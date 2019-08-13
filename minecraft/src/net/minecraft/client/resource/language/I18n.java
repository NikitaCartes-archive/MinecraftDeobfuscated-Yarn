package net.minecraft.client.resource.language;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class I18n {
	private static TranslationStorage storage;

	static void setLanguage(TranslationStorage translationStorage) {
		storage = translationStorage;
	}

	public static String translate(String string, Object... objects) {
		return storage.translate(string, objects);
	}

	public static boolean hasTranslation(String string) {
		return storage.containsKey(string);
	}
}
