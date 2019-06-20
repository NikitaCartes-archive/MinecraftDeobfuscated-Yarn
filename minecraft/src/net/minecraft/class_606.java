package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_606<T extends class_1308 & class_1603> extends class_572<T> {
	public class_606() {
		this(0.0F, false);
	}

	public class_606(float f, boolean bl) {
		super(f, 0.0F, 64, 32);
		if (!bl) {
			this.field_3401 = new class_630(this, 40, 16);
			this.field_3401.method_2856(-1.0F, -2.0F, -1.0F, 2, 12, 2, f);
			this.field_3401.method_2851(-5.0F, 2.0F, 0.0F);
			this.field_3390 = new class_630(this, 40, 16);
			this.field_3390.field_3666 = true;
			this.field_3390.method_2856(-1.0F, -2.0F, -1.0F, 2, 12, 2, f);
			this.field_3390.method_2851(5.0F, 2.0F, 0.0F);
			this.field_3392 = new class_630(this, 0, 16);
			this.field_3392.method_2856(-1.0F, 0.0F, -1.0F, 2, 12, 2, f);
			this.field_3392.method_2851(-2.0F, 12.0F, 0.0F);
			this.field_3397 = new class_630(this, 0, 16);
			this.field_3397.field_3666 = true;
			this.field_3397.method_2856(-1.0F, 0.0F, -1.0F, 2, 12, 2, f);
			this.field_3397.method_2851(2.0F, 12.0F, 0.0F);
		}
	}

	public void method_19689(T arg, float f, float g, float h) {
		this.field_3395 = class_572.class_573.field_3409;
		this.field_3399 = class_572.class_573.field_3409;
		class_1799 lv = arg.method_5998(class_1268.field_5808);
		if (lv.method_7909() == class_1802.field_8102 && arg.method_6510()) {
			if (arg.method_6068() == class_1306.field_6183) {
				this.field_3395 = class_572.class_573.field_3403;
			} else {
				this.field_3399 = class_572.class_573.field_3403;
			}
		}

		super.method_17086(arg, f, g, h);
	}

	public void method_19690(T arg, float f, float g, float h, float i, float j, float k) {
		super.method_17087(arg, f, g, h, i, j, k);
		class_1799 lv = arg.method_6047();
		if (arg.method_6510() && (lv.method_7960() || lv.method_7909() != class_1802.field_8102)) {
			float l = class_3532.method_15374(this.field_3447 * (float) Math.PI);
			float m = class_3532.method_15374((1.0F - (1.0F - this.field_3447) * (1.0F - this.field_3447)) * (float) Math.PI);
			this.field_3401.field_3674 = 0.0F;
			this.field_3390.field_3674 = 0.0F;
			this.field_3401.field_3675 = -(0.1F - l * 0.6F);
			this.field_3390.field_3675 = 0.1F - l * 0.6F;
			this.field_3401.field_3654 = (float) (-Math.PI / 2);
			this.field_3390.field_3654 = (float) (-Math.PI / 2);
			this.field_3401.field_3654 -= l * 1.2F - m * 0.4F;
			this.field_3390.field_3654 -= l * 1.2F - m * 0.4F;
			this.field_3401.field_3674 = this.field_3401.field_3674 + class_3532.method_15362(h * 0.09F) * 0.05F + 0.05F;
			this.field_3390.field_3674 = this.field_3390.field_3674 - (class_3532.method_15362(h * 0.09F) * 0.05F + 0.05F);
			this.field_3401.field_3654 = this.field_3401.field_3654 + class_3532.method_15374(h * 0.067F) * 0.05F;
			this.field_3390.field_3654 = this.field_3390.field_3654 - class_3532.method_15374(h * 0.067F) * 0.05F;
		}
	}

	@Override
	public void method_2803(float f, class_1306 arg) {
		float g = arg == class_1306.field_6183 ? 1.0F : -1.0F;
		class_630 lv = this.method_2808(arg);
		lv.field_3657 += g;
		lv.method_2847(f);
		lv.field_3657 -= g;
	}
}
