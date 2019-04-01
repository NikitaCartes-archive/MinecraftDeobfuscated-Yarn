package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_617 extends class_572<class_1634> {
	private final class_630 field_3601;
	private final class_630 field_3602;

	public class_617() {
		this(0.0F);
	}

	public class_617(float f) {
		super(f, 0.0F, 64, 64);
		this.field_3397.field_3665 = false;
		this.field_3394.field_3665 = false;
		this.field_3392 = new class_630(this, 32, 0);
		this.field_3392.method_2856(-1.0F, -1.0F, -2.0F, 6, 10, 4, 0.0F);
		this.field_3392.method_2851(-1.9F, 12.0F, 0.0F);
		this.field_3602 = new class_630(this, 0, 32);
		this.field_3602.method_2844(-20.0F, 0.0F, 0.0F, 20, 12, 1);
		this.field_3601 = new class_630(this, 0, 32);
		this.field_3601.field_3666 = true;
		this.field_3601.method_2844(0.0F, 0.0F, 0.0F, 20, 12, 1);
	}

	public void method_17126(class_1634 arg, float f, float g, float h, float i, float j, float k) {
		super.method_17088(arg, f, g, h, i, j, k);
		this.field_3602.method_2846(k);
		this.field_3601.method_2846(k);
	}

	public void method_17127(class_1634 arg, float f, float g, float h, float i, float j, float k) {
		super.method_17087(arg, f, g, h, i, j, k);
		if (arg.method_7176()) {
			if (arg.method_6068() == class_1306.field_6183) {
				this.field_3401.field_3654 = 3.7699115F;
			} else {
				this.field_3390.field_3654 = 3.7699115F;
			}
		}

		this.field_3392.field_3654 += (float) (Math.PI / 5);
		this.field_3602.field_3655 = 2.0F;
		this.field_3601.field_3655 = 2.0F;
		this.field_3602.field_3656 = 1.0F;
		this.field_3601.field_3656 = 1.0F;
		this.field_3602.field_3675 = 0.47123894F + class_3532.method_15362(h * 0.8F) * (float) Math.PI * 0.05F;
		this.field_3601.field_3675 = -this.field_3602.field_3675;
		this.field_3601.field_3674 = -0.47123894F;
		this.field_3601.field_3654 = 0.47123894F;
		this.field_3602.field_3654 = 0.47123894F;
		this.field_3602.field_3674 = 0.47123894F;
	}
}
