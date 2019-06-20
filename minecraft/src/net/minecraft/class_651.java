package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_651 extends class_4003 {
	private class_651(class_1937 arg, double d, double e, double f, class_1935 arg2) {
		super(arg, d, e, f);
		this.method_18141(class_310.method_1551().method_1480().method_4012().method_3307(arg2));
		this.field_3844 = 0.0F;
		this.field_3847 = 80;
		this.field_3862 = false;
	}

	@Override
	public class_3999 method_18122() {
		return class_3999.field_17827;
	}

	@Override
	public float method_18132(float f) {
		return 0.5F;
	}

	@Environment(EnvType.CLIENT)
	public static class class_652 implements class_707<class_2400> {
		public class_703 method_3010(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_651(arg2, d, e, f, class_2246.field_10499.method_8389());
		}
	}
}
