package net.minecraft.client.resource.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
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

	public static TranslationStorage load(ResourceManager resourceManager, List<String> definitions, boolean rightToLeft) {
		Map<String, String> map = Maps.<String, String>newHashMap();

		for (String string : definitions) {
			String string2 = String.format(Locale.ROOT, "lang/%s.json", string);

			for (String string3 : resourceManager.getAllNamespaces()) {
				try {
					Identifier identifier = Identifier.of(string3, string2);
					load(string, resourceManager.getAllResources(identifier), map);
				} catch (Exception var10) {
					LOGGER.warn("Skipped language file: {}:{} ({})", string3, string2, var10.toString());
				}
			}
		}

		return new TranslationStorage(ImmutableMap.copyOf(map), rightToLeft);
	}

	private static void load(String langCode, List<Resource> resourceRefs, Map<String, String> translations) {
		for (Resource resource : resourceRefs) {
			try {
				InputStream inputStream = resource.getInputStream();

				try {
					Language.load(inputStream, translations::put);
				} catch (Throwable var9) {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (Throwable var8) {
							var9.addSuppressed(var8);
						}
					}

					throw var9;
				}

				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException var10) {
				LOGGER.warn("Failed to load translations for {} from pack {}", langCode, resource.getPackId(), var10);
			}
		}
	}

	@Override
	public String get(String key, String fallback) {
		return (String)this.translations.getOrDefault(key, fallback);
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
