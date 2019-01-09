package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3874 extends class_489<class_3706> {
	private static final class_2960 field_17128 = new class_2960("textures/gui/container/smoker.png");

	public class_3874(class_3706 arg, class_1661 arg2, class_2561 arg3) {
		super(arg, new class_3877(), arg2, arg3);
	}

	@Override
	protected class_2960 method_17045() {
		return field_17128;
	}
}
