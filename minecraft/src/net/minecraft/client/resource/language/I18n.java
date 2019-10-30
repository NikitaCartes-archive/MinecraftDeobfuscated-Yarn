package net.minecraft.client.resource.language;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class I18n {
	private static TranslationStorage storage;

	static void setLanguage(TranslationStorage storage) {
		I18n.storage = storage;
	}

	public static String translate(String key, Object... args) {
		return storage.translate(key, args);
	}

	public static boolean hasTranslation(String key) {
		return storage.containsKey(key);
	}
}
