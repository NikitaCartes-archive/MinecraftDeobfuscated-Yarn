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
	default int method_8312(class_1944 arg, class_2338 arg2) {
		if (this.method_8320(arg2).method_11593(this, arg2)) {
			int i = this.method_8314(arg, arg2.method_10084());
			int j = this.method_8314(arg, arg2.method_10078());
			int k = this.method_8314(arg, arg2.method_10067());
			int l = this.method_8314(arg, arg2.method_10072());
			int m = this.method_8314(arg, arg2.method_10095());
			if (j > i) {
				i = j;
			}

			if (k > i) {
				i = k;
			}

			if (l > i) {
				i = l;
			}

			if (m > i) {
				i = m;
			}

			return i;
		} else {
			return this.method_8314(arg, arg2);
		}
	}

	@Environment(EnvType.CLIENT)
	default int method_8313(class_2338 arg, int i) {
		int j = this.method_8312(class_1944.field_9284, arg);
		int k = this.method_8312(class_1944.field_9282, arg);
		if (k < i) {
			k = i;
		}

		return j << 20 | k << 4;
	}
}
