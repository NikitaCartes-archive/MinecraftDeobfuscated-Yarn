package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_899<T extends class_1617> extends class_3729<T> {
	private static final class_2960 field_4697 = new class_2960("textures/entity/illager/evoker.png");

	public class_899(class_898 arg) {
		super(arg, new class_575<>(0.0F, 0.0F, 64, 64), 0.5F);
		this.method_4046(new class_989<T, class_575<T>>(this) {
			public void method_17147(T arg, float f, float g, float h, float i, float j, float k, float l) {
				if (arg.method_7137()) {
					super.method_17162(arg, f, g, h, i, j, k, l);
				}
			}
		});
	}

	protected class_2960 method_3961(T arg) {
		return field_4697;
	}
}
