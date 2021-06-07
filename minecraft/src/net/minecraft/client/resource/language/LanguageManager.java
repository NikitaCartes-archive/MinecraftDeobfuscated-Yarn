package net.minecraft.client.resource.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.LanguageResourceMetadata;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class LanguageManager implements SynchronousResourceReloader {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final String DEFAULT_LANGUAGE_CODE = "en_us";
	private static final LanguageDefinition ENGLISH_US = new LanguageDefinition("en_us", "US", "English", false);
	private Map<String, LanguageDefinition> languageDefs = ImmutableMap.of("en_us", ENGLISH_US);
	private String currentLanguageCode;
	private LanguageDefinition language = ENGLISH_US;

	public LanguageManager(String languageCode) {
		this.currentLanguageCode = languageCode;
	}

	private static Map<String, LanguageDefinition> loadAvailableLanguages(Stream<ResourcePack> packs) {
		Map<String, LanguageDefinition> map = Maps.<String, LanguageDefinition>newHashMap();
		packs.forEach(pack -> {
			try {
				LanguageResourceMetadata languageResourceMetadata = pack.parseMetadata(LanguageResourceMetadata.READER);
				if (languageResourceMetadata != null) {
					for (LanguageDefinition languageDefinition : languageResourceMetadata.getLanguageDefinitions()) {
						map.putIfAbsent(languageDefinition.getCode(), languageDefinition);
					}
				}
			} catch (IOException | RuntimeException var5) {
				LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", pack.getName(), var5);
			}
		});
		return ImmutableMap.copyOf(map);
	}

	@Override
	public void reload(ResourceManager manager) {
		this.languageDefs = loadAvailableLanguages(manager.streamResourcePacks());
		LanguageDefinition languageDefinition = (LanguageDefinition)this.languageDefs.getOrDefault("en_us", ENGLISH_US);
		this.language = (LanguageDefinition)this.languageDefs.getOrDefault(this.currentLanguageCode, languageDefinition);
		List<LanguageDefinition> list = Lists.<LanguageDefinition>newArrayList(languageDefinition);
		if (this.language != languageDefinition) {
			list.add(this.language);
		}

		TranslationStorage translationStorage = TranslationStorage.load(manager, list);
		I18n.setLanguage(translationStorage);
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
		return Sets.<LanguageDefinition>newTreeSet(this.languageDefs.values());
	}

	public LanguageDefinition getLanguage(String code) {
		return (LanguageDefinition)this.languageDefs.get(code);
	}
}
