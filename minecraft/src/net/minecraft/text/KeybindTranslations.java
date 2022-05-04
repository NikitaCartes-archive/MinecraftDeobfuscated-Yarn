package net.minecraft.text;

import java.util.function.Function;
import java.util.function.Supplier;

public class KeybindTranslations {
	static Function<String, Supplier<Text>> factory = key -> () -> Text.literal(key);

	public static void setFactory(Function<String, Supplier<Text>> factory) {
		KeybindTranslations.factory = factory;
	}
}
