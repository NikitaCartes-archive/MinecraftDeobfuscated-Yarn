package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1106 extends class_1101 {
	private final class_1297 field_5455;

	public class_1106(class_3414 arg, class_3419 arg2, class_1297 arg3) {
		this(arg, arg2, 1.0F, 1.0F, arg3);
	}

	public class_1106(class_3414 arg, class_3419 arg2, float f, float g, class_1297 arg3) {
		super(arg, arg2);
		this.field_5442 = f;
		this.field_5441 = g;
		this.field_5455 = arg3;
		this.field_5439 = (float)this.field_5455.field_5987;
		this.field_5450 = (float)this.field_5455.field_6010;
		this.field_5449 = (float)this.field_5455.field_6035;
	}

	@Override
	public void method_16896() {
		if (this.field_5455.field_5988) {
			this.field_5438 = true;
		} else {
			this.field_5439 = (float)this.field_5455.field_5987;
			this.field_5450 = (float)this.field_5455.field_6010;
			this.field_5449 = (float)this.field_5455.field_6035;
		}
	}
}
