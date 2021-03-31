package net.minecraft.client.resource.language;

import java.util.IllegalFormatException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Language;

@Environment(EnvType.CLIENT)
public class I18n {
	private static volatile Language language = Language.getInstance();

	private I18n() {
	}

	static void setLanguage(Language language) {
		I18n.language = language;
	}

	public static String translate(String key, Object... args) {
		String string = language.get(key);

		try {
			return String.format(string, args);
		} catch (IllegalFormatException var4) {
			return "Format error: " + string;
		}
	}

	public static boolean hasTranslation(String key) {
		return language.hasTranslation(key);
	}
}
