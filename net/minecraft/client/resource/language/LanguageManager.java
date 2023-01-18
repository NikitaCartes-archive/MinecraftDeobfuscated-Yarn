/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.resource.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.TranslationStorage;
import net.minecraft.client.resource.metadata.LanguageResourceMetadata;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class LanguageManager
implements SynchronousResourceReloader {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String DEFAULT_LANGUAGE_CODE = "en_us";
    private static final LanguageDefinition ENGLISH_US = new LanguageDefinition("US", "English", false);
    private Map<String, LanguageDefinition> languageDefs = ImmutableMap.of("en_us", ENGLISH_US);
    private String currentLanguageCode;

    public LanguageManager(String languageCode) {
        this.currentLanguageCode = languageCode;
    }

    private static Map<String, LanguageDefinition> loadAvailableLanguages(Stream<ResourcePack> packs) {
        HashMap map = Maps.newHashMap();
        packs.forEach(pack -> {
            try {
                LanguageResourceMetadata languageResourceMetadata = pack.parseMetadata(LanguageResourceMetadata.SERIALIZER);
                if (languageResourceMetadata != null) {
                    languageResourceMetadata.definitions().forEach(map::putIfAbsent);
                }
            } catch (IOException | RuntimeException exception) {
                LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", (Object)pack.getName(), (Object)exception);
            }
        });
        return ImmutableMap.copyOf(map);
    }

    @Override
    public void reload(ResourceManager manager) {
        LanguageDefinition languageDefinition;
        this.languageDefs = LanguageManager.loadAvailableLanguages(manager.streamResourcePacks());
        ArrayList<String> list = new ArrayList<String>(2);
        boolean bl = ENGLISH_US.rightToLeft();
        list.add(DEFAULT_LANGUAGE_CODE);
        if (!this.currentLanguageCode.equals(DEFAULT_LANGUAGE_CODE) && (languageDefinition = this.languageDefs.get(this.currentLanguageCode)) != null) {
            list.add(this.currentLanguageCode);
            bl = languageDefinition.rightToLeft();
        }
        TranslationStorage translationStorage = TranslationStorage.load(manager, list, bl);
        I18n.setLanguage(translationStorage);
        Language.setInstance(translationStorage);
    }

    public void setLanguage(String languageCode) {
        this.currentLanguageCode = languageCode;
    }

    public String getLanguage() {
        return this.currentLanguageCode;
    }

    public SortedMap<String, LanguageDefinition> getAllLanguages() {
        return new TreeMap<String, LanguageDefinition>(this.languageDefs);
    }

    @Nullable
    public LanguageDefinition getLanguage(String code) {
        return this.languageDefs.get(code);
    }
}

