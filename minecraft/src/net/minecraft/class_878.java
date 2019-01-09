package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_878 extends class_927<class_1545, class_555<class_1545>> {
	private static final class_2960 field_4644 = new class_2960("textures/entity/blaze.png");

	public class_878(class_898 arg) {
		super(arg, new class_555<>(), 0.5F);
	}

	protected class_2960 method_3881(class_1545 arg) {
		return field_4644;
	}
}
