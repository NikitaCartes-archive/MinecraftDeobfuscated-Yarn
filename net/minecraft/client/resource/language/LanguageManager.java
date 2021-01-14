/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.resource.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class LanguageManager
implements SynchronousResourceReloader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final LanguageDefinition field_25291 = new LanguageDefinition("en_us", "US", "English", false);
    private Map<String, LanguageDefinition> languageDefs = ImmutableMap.of("en_us", field_25291);
    private String currentLanguageCode;
    private LanguageDefinition language = field_25291;

    public LanguageManager(String string) {
        this.currentLanguageCode = string;
    }

    private static Map<String, LanguageDefinition> method_29393(Stream<ResourcePack> stream) {
        HashMap map = Maps.newHashMap();
        stream.forEach(resourcePack -> {
            try {
                LanguageResourceMetadata languageResourceMetadata = resourcePack.parseMetadata(LanguageResourceMetadata.READER);
                if (languageResourceMetadata != null) {
                    for (LanguageDefinition languageDefinition : languageResourceMetadata.getLanguageDefinitions()) {
                        map.putIfAbsent(languageDefinition.getCode(), languageDefinition);
                    }
                }
            } catch (IOException | RuntimeException exception) {
                LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", (Object)resourcePack.getName(), (Object)exception);
            }
        });
        return ImmutableMap.copyOf(map);
    }

    @Override
    public void reload(ResourceManager manager) {
        this.languageDefs = LanguageManager.method_29393(manager.streamResourcePacks());
        LanguageDefinition languageDefinition = this.languageDefs.getOrDefault("en_us", field_25291);
        this.language = this.languageDefs.getOrDefault(this.currentLanguageCode, languageDefinition);
        ArrayList<LanguageDefinition> list = Lists.newArrayList(languageDefinition);
        if (this.language != languageDefinition) {
            list.add(this.language);
        }
        TranslationStorage translationStorage = TranslationStorage.load(manager, list);
        I18n.method_29391(translationStorage);
        Language.setInstance(translationStorage);
    }

    public void setLanguage(LanguageDefinition language) {
        this.currentLanguageCode = language.getCode();
        this.language = language;
    }

    public LanguageDefinition getLanguage() {
        return this.language;
    }

    public SortedSet<LanguageDefinition> getAllLanguages() {
        return Sets.newTreeSet(this.languageDefs.values());
    }

    public LanguageDefinition getLanguage(String code) {
        return this.languageDefs.get(code);
    }
}

