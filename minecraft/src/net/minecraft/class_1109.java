package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1109 extends class_1102 {
	public class_1109(class_3414 arg, class_3419 arg2, float f, float g, class_2338 arg3) {
		this(arg, arg2, f, g, (float)arg3.method_10263() + 0.5F, (float)arg3.method_10264() + 0.5F, (float)arg3.method_10260() + 0.5F);
	}

	public static class_1109 method_4758(class_3414 arg, float f) {
		return method_4757(arg, f, 0.25F);
	}

	public static class_1109 method_4757(class_3414 arg, float f, float g) {
		return new class_1109(arg, class_3419.field_15250, g, f, false, 0, class_1113.class_1114.field_5478, 0.0F, 0.0F, 0.0F);
	}

	public static class_1109 method_4759(class_3414 arg) {
		return new class_1109(arg, class_3419.field_15253, 1.0F, 1.0F, false, 0, class_1113.class_1114.field_5478, 0.0F, 0.0F, 0.0F);
	}

	public static class_1109 method_4760(class_3414 arg, float f, float g, float h) {
		return new class_1109(arg, class_3419.field_15247, 4.0F, 1.0F, false, 0, class_1113.class_1114.field_5476, f, g, h);
	}

	public class_1109(class_3414 arg, class_3419 arg2, float f, float g, float h, float i, float j) {
		this(arg, arg2, f, g, false, 0, class_1113.class_1114.field_5476, h, i, j);
	}

	private class_1109(class_3414 arg, class_3419 arg2, float f, float g, boolean bl, int i, class_1113.class_1114 arg3, float h, float j, float k) {
		this(arg.method_14833(), arg2, f, g, bl, i, arg3, h, j, k);
	}

	public class_1109(class_2960 arg, class_3419 arg2, float f, float g, boolean bl, int i, class_1113.class_1114 arg3, float h, float j, float k) {
		super(arg, arg2);
		this.field_5442 = f;
		this.field_5441 = g;
		this.field_5439 = h;
		this.field_5450 = j;
		this.field_5449 = k;
		this.field_5446 = bl;
		this.field_5451 = i;
		this.field_5440 = arg3;
	}
}
