package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2213 extends class_2248 {
	protected class_2213(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public boolean method_9579(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return true;
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11455;
	}

	@Override
	public boolean method_9601(class_2680 arg) {
		return false;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public float method_9575(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return 1.0F;
	}

	@Override
	public boolean method_9523(class_2680 arg, class_1922 arg2, class_2338 arg3, class_1299<?> arg4) {
		return false;
	}
}
