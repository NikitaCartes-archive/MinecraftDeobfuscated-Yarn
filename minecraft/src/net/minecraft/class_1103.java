package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_1103 extends class_1101 {
	private final class_746 field_5452;
	private int field_5453;

	public class_1103(class_746 arg) {
		super(class_3417.field_14572, class_3419.field_15248);
		this.field_5452 = arg;
		this.field_5446 = true;
		this.field_5451 = 0;
		this.field_5442 = 0.1F;
	}

	@Override
	public void method_16896() {
		this.field_5453++;
		if (!this.field_5452.field_5988 && (this.field_5453 <= 20 || this.field_5452.method_6128())) {
			this.field_5439 = (float)this.field_5452.field_5987;
			this.field_5450 = (float)this.field_5452.field_6010;
			this.field_5449 = (float)this.field_5452.field_6035;
			float f = class_3532.method_15368(
				this.field_5452.field_5967 * this.field_5452.field_5967
					+ this.field_5452.field_6006 * this.field_5452.field_6006
					+ this.field_5452.field_5984 * this.field_5452.field_5984
			);
			float g = f / 2.0F;
			if ((double)f >= 0.01) {
				this.field_5442 = class_3532.method_15363(g * g, 0.0F, 1.0F);
			} else {
				this.field_5442 = 0.0F;
			}

			if (this.field_5453 < 20) {
				this.field_5442 = 0.0F;
			} else if (this.field_5453 < 40) {
				this.field_5442 = (float)((double)this.field_5442 * ((double)(this.field_5453 - 20) / 20.0));
			}

			float h = 0.8F;
			if (this.field_5442 > 0.8F) {
				this.field_5441 = 1.0F + (this.field_5442 - 0.8F);
			} else {
				this.field_5441 = 1.0F;
			}
		} else {
			this.field_5438 = true;
		}
	}
}
