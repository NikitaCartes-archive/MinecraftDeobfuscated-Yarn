package net.minecraft.client.resource.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class TranslationStorage extends Language {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Map<String, String> translations;
	private final boolean rightToLeft;

	private TranslationStorage(Map<String, String> translations, boolean rightToLeft) {
		this.translations = translations;
		this.rightToLeft = rightToLeft;
	}

	public static TranslationStorage load(ResourceManager resourceManager, List<LanguageDefinition> definitions) {
		Map<String, String> map = Maps.<String, String>newHashMap();
		boolean bl = false;

		for (LanguageDefinition languageDefinition : definitions) {
			bl |= languageDefinition.isRightToLeft();
			String string = String.format("lang/%s.json", languageDefinition.getCode());

			for (String string2 : resourceManager.getAllNamespaces()) {
				try {
					Identifier identifier = new Identifier(string2, string);
					load(resourceManager.getAllResources(identifier), map);
				} catch (FileNotFoundException var10) {
				} catch (Exception var11) {
					LOGGER.warn("Skipped language file: {}:{} ({})", string2, string, var11.toString());
				}
			}
		}

		return new TranslationStorage(ImmutableMap.copyOf(map), bl);
	}

	private static void load(List<Resource> resources, Map<String, String> translationMap) {
		for (Resource resource : resources) {
			try {
				InputStream inputStream = resource.getInputStream();
				Throwable var5 = null;

				try {
					Language.load(inputStream, translationMap::put);
				} catch (Throwable var15) {
					var5 = var15;
					throw var15;
				} finally {
					if (inputStream != null) {
						if (var5 != null) {
							try {
								inputStream.close();
							} catch (Throwable var14) {
								var5.addSuppressed(var14);
							}
						} else {
							inputStream.close();
						}
					}
				}
			} catch (IOException var17) {
				LOGGER.warn("Failed to load translations from {}", resource, var17);
			}
		}
	}

	@Override
	public String get(String key) {
		return (String)this.translations.getOrDefault(key, key);
	}

	@Override
	public boolean hasTranslation(String key) {
		return this.translations.containsKey(key);
	}

	@Override
	public boolean isRightToLeft() {
		return this.rightToLeft;
	}

	@Override
	public OrderedText reorder(StringVisitable text) {
		return ReorderingUtil.reorder(text, this.rightToLeft);
	}
}
