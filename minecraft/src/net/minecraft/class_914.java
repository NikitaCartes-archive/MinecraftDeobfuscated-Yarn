package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_914 extends class_3729<class_1581> {
	private static final class_2960 field_4718 = new class_2960("textures/entity/illager/illusioner.png");

	public class_914(class_898 arg) {
		super(arg, new class_575<>(0.0F, 0.0F, 64, 64), 0.5F);
		this.method_4046(new class_989<class_1581, class_575<class_1581>>(this) {
			public void method_17149(class_1581 arg, float f, float g, float h, float i, float j, float k, float l) {
				if (arg.method_7137() || arg.method_6510()) {
					super.method_17162(arg, f, g, h, i, j, k, l);
				}
			}
		});
		this.field_4737.method_2812().field_3665 = true;
	}

	protected class_2960 method_3990(class_1581 arg) {
		return field_4718;
	}

	public void method_3991(class_1581 arg, double d, double e, double f, float g, float h) {
		if (arg.method_5767()) {
			class_243[] lvs = arg.method_7065(h);
			float i = this.method_4045(arg, h);

			for (int j = 0; j < lvs.length; j++) {
				super.method_4072(
					arg,
					d + lvs[j].field_1352 + (double)class_3532.method_15362((float)j + i * 0.5F) * 0.025,
					e + lvs[j].field_1351 + (double)class_3532.method_15362((float)j + i * 0.75F) * 0.0125,
					f + lvs[j].field_1350 + (double)class_3532.method_15362((float)j + i * 0.7F) * 0.025,
					g,
					h
				);
			}
		} else {
			super.method_4072(arg, d, e, f, g, h);
		}
	}

	protected boolean method_3988(class_1581 arg) {
		return true;
	}
}
