package net.minecraft.text;

import java.util.function.Function;
import java.util.function.Supplier;

public class KeybindTranslations {
	static Function<String, Supplier<Text>> FACTORY = string -> () -> Text.literal(string);

	public static void setFactory(Function<String, Supplier<Text>> factory) {
		FACTORY = factory;
	}
}
