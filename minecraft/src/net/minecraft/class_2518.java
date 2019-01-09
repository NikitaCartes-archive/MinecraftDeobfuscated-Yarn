package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2518 extends class_2248 {
	private static final class_265 field_11589 = class_2248.method_9541(5.0, 5.0, 5.0, 11.0, 11.0, 11.0);

	protected class_2518(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11455;
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return field_11589;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public float method_9575(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return 1.0F;
	}

	@Override
	public class_3619 method_9527(class_2680 arg) {
		return class_3619.field_15971;
	}
}
