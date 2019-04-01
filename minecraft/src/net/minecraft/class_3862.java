package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3862 extends class_1874 {
	public class_3862(class_2960 arg, String string, class_1856 arg2, class_1799 arg3, float f, int i) {
		super(class_3956.field_17548, arg, string, arg2, arg3, f, i);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1799 method_17447() {
		return new class_1799(class_2246.field_16334);
	}

	@Override
	public class_1865<?> method_8119() {
		return class_1865.field_17085;
	}
}
