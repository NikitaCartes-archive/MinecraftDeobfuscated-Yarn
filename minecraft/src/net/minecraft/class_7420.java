package net.minecraft;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.text.Text;

public class class_7420 {
	public static Function<String, Supplier<Text>> field_39013 = string -> () -> Text.method_43470(string);

	public static void method_43482(Function<String, Supplier<Text>> function) {
		field_39013 = function;
	}
}
