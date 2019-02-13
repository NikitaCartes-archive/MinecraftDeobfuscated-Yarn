package net.minecraft.client.resource.language;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.metadata.LanguageResourceMetadata;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class LanguageManager implements SynchronousResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	protected static final TranslationStorage STORAGE = new TranslationStorage();
	private String currentLanguageCode;
	private final Map<String, LanguageDefinition> languageDefs = Maps.<String, LanguageDefinition>newHashMap();

	public LanguageManager(String string) {
		this.currentLanguageCode = string;
		I18n.setLanguage(STORAGE);
	}

	public void reloadResources(List<ResourcePack> list) {
		this.languageDefs.clear();

		for (ResourcePack resourcePack : list) {
			try {
				LanguageResourceMetadata languageResourceMetadata = resourcePack.parseMetadata(LanguageResourceMetadata.READER);
				if (languageResourceMetadata != null) {
					for (LanguageDefinition languageDefinition : languageResourceMetadata.getLanguageDefinitions()) {
						if (!this.languageDefs.containsKey(languageDefinition.getCode())) {
							this.languageDefs.put(languageDefinition.getCode(), languageDefinition);
						}
					}
				}
			} catch (IOException | RuntimeException var7) {
				LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", resourcePack.getName(), var7);
			}
		}
	}

	@Override
	public void apply(ResourceManager resourceManager) {
		List<String> list = Lists.<String>newArrayList("en_us");
		if (!"en_us".equals(this.currentLanguageCode)) {
			list.add(this.currentLanguageCode);
		}

		STORAGE.load(resourceManager, list);
		Language.load(STORAGE.translations);
	}

	public boolean isRightToLeft() {
		return this.getLanguage() != null && this.getLanguage().isRightToLeft();
	}

	public void setLanguage(LanguageDefinition languageDefinition) {
		this.currentLanguageCode = languageDefinition.getCode();
	}

	public LanguageDefinition getLanguage() {
		String string = this.languageDefs.containsKey(this.currentLanguageCode) ? this.currentLanguageCode : "en_us";
		return (LanguageDefinition)this.languageDefs.get(string);
	}

	public SortedSet<LanguageDefinition> getAllLanguages() {
		return Sets.<LanguageDefinition>newTreeSet(this.languageDefs.values());
	}

	public LanguageDefinition method_4668(String string) {
		return (LanguageDefinition)this.languageDefs.get(string);
	}
}
