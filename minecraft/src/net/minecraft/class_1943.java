package net.minecraft;

import java.util.function.Function;
import javax.annotation.Nullable;

public interface class_1943 {
	@Nullable
	class_37 method_8646();

	@Nullable
	default <T extends class_18> T method_8648(class_2874 arg, Function<String, T> function, String string) {
		class_37 lv = this.method_8646();
		return lv == null ? null : lv.method_268(arg, function, string);
	}

	default void method_8647(class_2874 arg, String string, class_18 arg2) {
		class_37 lv = this.method_8646();
		if (lv != null) {
			lv.method_267(arg, string, arg2);
		}
	}

	default int method_8645(class_2874 arg, String string) {
		return this.method_8646().method_266(arg, string);
	}
}
