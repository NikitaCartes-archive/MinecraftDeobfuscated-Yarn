package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_564<T extends class_1642> extends class_623<T> {
	public class_564(float f, float g, int i, int j) {
		super(f, g, i, j);
		this.field_3401 = new class_630(this, 32, 48);
		this.field_3401.method_2856(-3.0F, -2.0F, -2.0F, 4, 12, 4, f);
		this.field_3401.method_2851(-5.0F, 2.0F + g, 0.0F);
		this.field_3392 = new class_630(this, 16, 48);
		this.field_3392.method_2856(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.field_3392.method_2851(-1.9F, 12.0F + g, 0.0F);
	}

	public class_564(float f, boolean bl) {
		super(f, 0.0F, 64, bl ? 32 : 64);
	}

	public void method_17077(T arg, float f, float g, float h) {
		this.field_3395 = class_572.class_573.field_3409;
		this.field_3399 = class_572.class_573.field_3409;
		class_1799 lv = arg.method_5998(class_1268.field_5808);
		if (lv.method_7909() == class_1802.field_8547 && arg.method_6510()) {
			if (arg.method_6068() == class_1306.field_6183) {
				this.field_3395 = class_572.class_573.field_3407;
			} else {
				this.field_3399 = class_572.class_573.field_3407;
			}
		}

		super.method_17086(arg, f, g, h);
	}

	public void method_17134(T arg, float f, float g, float h, float i, float j, float k) {
		super.method_17791(arg, f, g, h, i, j, k);
		if (this.field_3399 == class_572.class_573.field_3407) {
			this.field_3390.field_3654 = this.field_3390.field_3654 * 0.5F - (float) Math.PI;
			this.field_3390.field_3675 = 0.0F;
		}

		if (this.field_3395 == class_572.class_573.field_3407) {
			this.field_3401.field_3654 = this.field_3401.field_3654 * 0.5F - (float) Math.PI;
			this.field_3401.field_3675 = 0.0F;
		}

		if (this.field_3396 > 0.0F) {
			this.field_3401.field_3654 = this.method_2804(this.field_3401.field_3654, (float) (-Math.PI * 4.0 / 5.0), this.field_3396)
				+ this.field_3396 * 0.35F * class_3532.method_15374(0.1F * h);
			this.field_3390.field_3654 = this.method_2804(this.field_3390.field_3654, (float) (-Math.PI * 4.0 / 5.0), this.field_3396)
				- this.field_3396 * 0.35F * class_3532.method_15374(0.1F * h);
			this.field_3401.field_3674 = this.method_2804(this.field_3401.field_3674, -0.15F, this.field_3396);
			this.field_3390.field_3674 = this.method_2804(this.field_3390.field_3674, 0.15F, this.field_3396);
			this.field_3397.field_3654 = this.field_3397.field_3654 - this.field_3396 * 0.55F * class_3532.method_15374(0.1F * h);
			this.field_3392.field_3654 = this.field_3392.field_3654 + this.field_3396 * 0.55F * class_3532.method_15374(0.1F * h);
			this.field_3398.field_3654 = 0.0F;
		}
	}
}
