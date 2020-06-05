package net.minecraft.client.resource.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class TranslationStorage extends Language {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Pattern field_25288 = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z])");
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
	public String reorder(String string, boolean allowTokens) {
		if (!this.rightToLeft) {
			return string;
		} else {
			if (allowTokens && string.indexOf(37) != -1) {
				string = method_29389(string);
			}

			return this.reorder(string);
		}
	}

	public static String method_29389(String string) {
		Matcher matcher = field_25288.matcher(string);
		StringBuffer stringBuffer = new StringBuffer();
		int i = 1;

		while (matcher.find()) {
			String string2 = matcher.group(1);
			String string3 = string2 != null ? string2 : Integer.toString(i++);
			String string4 = matcher.group(2);
			String string5 = Matcher.quoteReplacement("\u2066%" + string3 + "$" + string4 + "\u2069");
			matcher.appendReplacement(stringBuffer, string5);
		}

		matcher.appendTail(stringBuffer);
		return stringBuffer.toString();
	}

	private String reorder(String string) {
		try {
			Bidi bidi = new Bidi(new ArabicShaping(8).shape(string), 127);
			bidi.setReorderingMode(0);
			return bidi.writeReordered(10);
		} catch (ArabicShapingException var3) {
			return string;
		}
	}
}
