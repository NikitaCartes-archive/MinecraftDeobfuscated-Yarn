package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_930 extends class_927<class_1453, class_584> {
	public static final class_2960[] field_4754 = new class_2960[]{
		new class_2960("textures/entity/parrot/parrot_red_blue.png"),
		new class_2960("textures/entity/parrot/parrot_blue.png"),
		new class_2960("textures/entity/parrot/parrot_green.png"),
		new class_2960("textures/entity/parrot/parrot_yellow_blue.png"),
		new class_2960("textures/entity/parrot/parrot_grey.png")
	};

	public class_930(class_898 arg) {
		super(arg, new class_584(), 0.3F);
	}

	protected class_2960 method_4080(class_1453 arg) {
		return field_4754[arg.method_6584()];
	}

	public float method_4081(class_1453 arg, float f) {
		float g = class_3532.method_16439(f, arg.field_6829, arg.field_6818);
		float h = class_3532.method_16439(f, arg.field_6827, arg.field_6819);
		return (class_3532.method_15374(g) + 1.0F) * h;
	}
}
