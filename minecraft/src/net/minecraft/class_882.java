package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_882 extends class_927<class_1428, class_558<class_1428>> {
	private static final class_2960 field_4649 = new class_2960("textures/entity/chicken.png");

	public class_882(class_898 arg) {
		super(arg, new class_558<>(), 0.3F);
	}

	protected class_2960 method_3892(class_1428 arg) {
		return field_4649;
	}

	protected float method_3893(class_1428 arg, float f) {
		float g = class_3532.method_16439(f, arg.field_6736, arg.field_6741);
		float h = class_3532.method_16439(f, arg.field_6738, arg.field_6743);
		return (class_3532.method_15374(g) + 1.0F) * h;
	}
}
