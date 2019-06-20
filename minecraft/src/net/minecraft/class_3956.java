package net.minecraft;

import java.util.Optional;

public interface class_3956<T extends class_1860<?>> {
	class_3956<class_3955> field_17545 = method_17726("crafting");
	class_3956<class_3861> field_17546 = method_17726("smelting");
	class_3956<class_3859> field_17547 = method_17726("blasting");
	class_3956<class_3862> field_17548 = method_17726("smoking");
	class_3956<class_3920> field_17549 = method_17726("campfire_cooking");
	class_3956<class_3975> field_17641 = method_17726("stonecutting");

	static <T extends class_1860<?>> class_3956<T> method_17726(String string) {
		return class_2378.method_10230(class_2378.field_17597, new class_2960(string), new class_3956<T>() {
			public String toString() {
				return string;
			}
		});
	}

	default <C extends class_1263> Optional<T> method_17725(class_1860<C> arg, class_1937 arg2, C arg3) {
		return arg.method_8115(arg3, arg2) ? Optional.of(arg) : Optional.empty();
	}
}
