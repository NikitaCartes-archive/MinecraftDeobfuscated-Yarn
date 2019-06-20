package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_4262 extends class_2373 {
	protected class_4262(class_2248.class_2251 arg) {
		super(arg);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public float method_9575(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return 1.0F;
	}

	@Override
	public boolean method_9579(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return true;
	}

	@Override
	public boolean method_16362(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return false;
	}

	@Override
	public boolean method_9521(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return false;
	}

	@Override
	public boolean method_9523(class_2680 arg, class_1922 arg2, class_2338 arg3, class_1299<?> arg4) {
		return false;
	}
}
