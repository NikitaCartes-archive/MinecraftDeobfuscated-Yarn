package net.minecraft.client.resource.language;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class I18n {
	private static TranslationStorage field_5319;

	static void method_4661(TranslationStorage translationStorage) {
		field_5319 = translationStorage;
	}

	public static String translate(String string, Object... objects) {
		return field_5319.translate(string, objects);
	}

	public static boolean hasTranslation(String string) {
		return field_5319.containsKey(string);
	}
}
