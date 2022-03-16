package net.minecraft.client.resource.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceRef;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class TranslationStorage extends Language {
	private static final Logger LOGGER = LogUtils.getLogger();
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
			String string = languageDefinition.getCode();
			String string2 = String.format("lang/%s.json", string);

			for (String string3 : resourceManager.getAllNamespaces()) {
				try {
					Identifier identifier = new Identifier(string3, string2);
					load(string, resourceManager.getAllResources(identifier), map);
				} catch (FileNotFoundException var11) {
				} catch (Exception var12) {
					LOGGER.warn("Skipped language file: {}:{} ({})", string3, string2, var12.toString());
				}
			}
		}

		return new TranslationStorage(ImmutableMap.copyOf(map), bl);
	}

	private static void load(String langCode, List<ResourceRef> resourceRefs, Map<String, String> translations) {
		for (ResourceRef resourceRef : resourceRefs) {
			try {
				Resource resource = resourceRef.open();

				try {
					InputStream inputStream = resource.getInputStream();

					try {
						Language.load(inputStream, translations::put);
					} catch (Throwable var11) {
						if (inputStream != null) {
							try {
								inputStream.close();
							} catch (Throwable var10) {
								var11.addSuppressed(var10);
							}
						}

						throw var11;
					}

					if (inputStream != null) {
						inputStream.close();
					}
				} catch (Throwable var12) {
					if (resource != null) {
						try {
							resource.close();
						} catch (Throwable var9) {
							var12.addSuppressed(var9);
						}
					}

					throw var12;
				}

				if (resource != null) {
					resource.close();
				}
			} catch (IOException var13) {
				LOGGER.warn("Failed to load translations for {} from pack {}", langCode, resourceRef.getPackName(), var13);
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
