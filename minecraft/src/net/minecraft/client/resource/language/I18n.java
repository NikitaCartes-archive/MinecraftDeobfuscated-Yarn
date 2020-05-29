package net.minecraft.client.resource.language;

import java.util.IllegalFormatException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Language;

@Environment(EnvType.CLIENT)
public class I18n {
	private static volatile Language field_25290 = Language.getInstance();

	static void method_29391(Language language) {
		field_25290 = language;
	}

	public static String translate(String key, Object... args) {
		String string = field_25290.get(key);

		try {
			return String.format(string, args);
		} catch (IllegalFormatException var4) {
			return "Format error: " + string;
		}
	}

	public static boolean hasTranslation(String key) {
		return field_25290.hasTranslation(key);
	}
}
