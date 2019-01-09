package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2373 extends class_2248 {
	protected class_2373(class_2248.class_2251 arg) {
		super(arg);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_9522(class_2680 arg, class_2680 arg2, class_2350 arg3) {
		return arg2.method_11614() == this ? true : super.method_9522(arg, arg2, arg3);
	}
}
