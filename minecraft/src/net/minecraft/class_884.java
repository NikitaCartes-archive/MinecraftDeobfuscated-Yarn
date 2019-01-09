package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_884 extends class_927<class_1430, class_560<class_1430>> {
	private static final class_2960 field_4651 = new class_2960("textures/entity/cow/cow.png");

	public class_884(class_898 arg) {
		super(arg, new class_560<>(), 0.7F);
	}

	protected class_2960 method_3895(class_1430 arg) {
		return field_4651;
	}
}
