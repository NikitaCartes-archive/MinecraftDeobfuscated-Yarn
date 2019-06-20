package net.minecraft;

import java.util.Collections;
import javax.annotation.Nullable;

public interface class_1732 {
	void method_7662(@Nullable class_1860<?> arg);

	@Nullable
	class_1860<?> method_7663();

	default void method_7664(class_1657 arg) {
		class_1860<?> lv = this.method_7663();
		if (lv != null && !lv.method_8118()) {
			arg.method_7254(Collections.singleton(lv));
			this.method_7662(null);
		}
	}

	default boolean method_7665(class_1937 arg, class_3222 arg2, class_1860<?> arg3) {
		if (!arg3.method_8118() && arg.method_8450().method_8355(class_1928.field_19407) && !arg2.method_14253().method_14878(arg3)) {
			return false;
		} else {
			this.method_7662(arg3);
			return true;
		}
	}
}
