package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_941 extends class_927<class_1472, class_601<class_1472>> {
	private static final class_2960 field_4778 = new class_2960("textures/entity/sheep/sheep.png");

	public class_941(class_898 arg) {
		super(arg, new class_601<>(), 0.7F);
		this.method_4046(new class_994(this));
	}

	protected class_2960 method_4106(class_1472 arg) {
		return field_4778;
	}
}
