package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface class_1920 extends class_1922 {
	class_1959 method_8310(class_2338 arg);

	int method_8314(class_1944 arg, class_2338 arg2);

	default boolean method_8311(class_2338 arg) {
		return this.method_8314(class_1944.field_9284, arg) >= this.method_8315();
	}

	@Environment(EnvType.CLIENT)
	default int method_8313(class_2338 arg, int i) {
		int j = this.method_8314(class_1944.field_9284, arg);
		int k = this.method_8314(class_1944.field_9282, arg);
		if (k < i) {
			k = i;
		}

		return j << 20 | k << 4;
	}
}
