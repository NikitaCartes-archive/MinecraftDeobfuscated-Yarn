package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3975 extends class_3972 {
	public class_3975(class_2960 arg, String string, class_1856 arg2, class_1799 arg3) {
		super(class_3956.field_17641, class_1865.field_17640, arg, string, arg2, arg3);
	}

	@Override
	public boolean method_8115(class_1263 arg, class_1937 arg2) {
		return this.field_17642.method_8093(arg.method_5438(0));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1799 method_17447() {
		return new class_1799(class_2246.field_16335);
	}
}
