package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1108 extends class_1101 {
	private final class_1688 field_5458;
	private float field_5459 = 0.0F;

	public class_1108(class_1688 arg) {
		super(class_3417.field_14784, class_3419.field_15254);
		this.field_5458 = arg;
		this.field_5446 = true;
		this.field_5451 = 0;
		this.field_5442 = 0.0F;
		this.field_5439 = (float)arg.field_5987;
		this.field_5450 = (float)arg.field_6010;
		this.field_5449 = (float)arg.field_6035;
	}

	@Override
	public boolean method_4785() {
		return true;
	}

	@Override
	public void method_16896() {
		if (this.field_5458.field_5988) {
			this.field_5438 = true;
		} else {
			this.field_5439 = (float)this.field_5458.field_5987;
			this.field_5450 = (float)this.field_5458.field_6010;
			this.field_5449 = (float)this.field_5458.field_6035;
			float f = class_3532.method_15368(this.field_5458.field_5967 * this.field_5458.field_5967 + this.field_5458.field_6006 * this.field_5458.field_6006);
			if ((double)f >= 0.01) {
				this.field_5459 = class_3532.method_15363(this.field_5459 + 0.0025F, 0.0F, 1.0F);
				this.field_5442 = class_3532.method_16439(class_3532.method_15363(f, 0.0F, 0.5F), 0.0F, 0.7F);
			} else {
				this.field_5459 = 0.0F;
				this.field_5442 = 0.0F;
			}
		}
	}
}
