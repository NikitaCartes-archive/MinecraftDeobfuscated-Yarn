package net.minecraft.client.resource.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.LanguageResourceMetadata;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Language;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class LanguageManager implements SynchronousResourceReloader {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final LanguageDefinition ENGLISH_US = new LanguageDefinition("US", "English", false);
	private Map<String, LanguageDefinition> languageDefs = ImmutableMap.of("en_us", ENGLISH_US);
	private String currentLanguageCode;
	private final Consumer<TranslationStorage> field_51830;

	public LanguageManager(String languageCode, Consumer<TranslationStorage> consumer) {
		this.currentLanguageCode = languageCode;
		this.field_51830 = consumer;
	}

	private static Map<String, LanguageDefinition> loadAvailableLanguages(Stream<ResourcePack> packs) {
		Map<String, LanguageDefinition> map = Maps.<String, LanguageDefinition>newHashMap();
		packs.forEach(pack -> {
			try {
				LanguageResourceMetadata languageResourceMetadata = pack.parseMetadata(LanguageResourceMetadata.SERIALIZER);
				if (languageResourceMetadata != null) {
					languageResourceMetadata.definitions().forEach(map::putIfAbsent);
				}
			} catch (IOException | RuntimeException var3) {
				LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", pack.getId(), var3);
			}
		});
		return ImmutableMap.copyOf(map);
	}

	@Override
	public void reload(ResourceManager manager) {
		this.languageDefs = loadAvailableLanguages(manager.streamResourcePacks());
		List<String> list = new ArrayList(2);
		boolean bl = ENGLISH_US.rightToLeft();
		list.add("en_us");
		if (!this.currentLanguageCode.equals("en_us")) {
			LanguageDefinition languageDefinition = (LanguageDefinition)this.languageDefs.get(this.currentLanguageCode);
			if (languageDefinition != null) {
				list.add(this.currentLanguageCode);
				bl = languageDefinition.rightToLeft();
			}
		}

		TranslationStorage translationStorage = TranslationStorage.load(manager, list, bl);
		I18n.setLanguage(translationStorage);
		Language.setInstance(translationStorage);
		this.field_51830.accept(translationStorage);
	}

	public void setLanguage(String languageCode) {
		this.currentLanguageCode = languageCode;
	}

	public String getLanguage() {
		return this.currentLanguageCode;
	}

	public SortedMap<String, LanguageDefinition> getAllLanguages() {
		return new TreeMap(this.languageDefs);
	}

	@Nullable
	public LanguageDefinition getLanguage(String code) {
		return (LanguageDefinition)this.languageDefs.get(code);
	}
}
