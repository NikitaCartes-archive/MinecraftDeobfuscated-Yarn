package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1105 extends class_1101 {
	private final class_1577 field_5454;

	public class_1105(class_1577 arg) {
		super(class_3417.field_14880, class_3419.field_15251);
		this.field_5454 = arg;
		this.field_5440 = class_1113.class_1114.field_5478;
		this.field_5446 = true;
		this.field_5451 = 0;
	}

	@Override
	public void method_16896() {
		if (!this.field_5454.field_5988 && this.field_5454.method_7063()) {
			this.field_5439 = (float)this.field_5454.field_5987;
			this.field_5450 = (float)this.field_5454.field_6010;
			this.field_5449 = (float)this.field_5454.field_6035;
			float f = this.field_5454.method_7061(0.0F);
			this.field_5442 = 0.0F + 1.0F * f * f;
			this.field_5441 = 0.7F + 0.5F * f;
		} else {
			this.field_5438 = true;
		}
	}
}
