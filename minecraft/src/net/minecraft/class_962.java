package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_962 extends class_3729<class_1632> {
	private static final class_2960 field_4804 = new class_2960("textures/entity/illager/vindicator.png");

	public class_962(class_898 arg) {
		super(arg, new class_575<>(0.0F, 0.0F, 64, 64), 0.5F);
		this.method_4046(new class_989<class_1632, class_575<class_1632>>(this) {
			public void method_17156(class_1632 arg, float f, float g, float h, float i, float j, float k, float l) {
				if (arg.method_7169()) {
					super.method_17162(arg, f, g, h, i, j, k, l);
				}
			}
		});
	}

	protected class_2960 method_4147(class_1632 arg) {
		return field_4804;
	}
}
