package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_566<T extends class_1309> extends class_572<T> {
	public boolean field_3371;
	public boolean field_3370;

	public class_566(float f) {
		super(0.0F, -14.0F, 64, 32);
		float g = -14.0F;
		this.field_3394 = new class_630(this, 0, 16);
		this.field_3394.method_2856(-4.0F, -8.0F, -4.0F, 8, 8, 8, f - 0.5F);
		this.field_3394.method_2851(0.0F, -14.0F, 0.0F);
		this.field_3391 = new class_630(this, 32, 16);
		this.field_3391.method_2856(-4.0F, 0.0F, -2.0F, 8, 12, 4, f);
		this.field_3391.method_2851(0.0F, -14.0F, 0.0F);
		this.field_3401 = new class_630(this, 56, 0);
		this.field_3401.method_2856(-1.0F, -2.0F, -1.0F, 2, 30, 2, f);
		this.field_3401.method_2851(-3.0F, -12.0F, 0.0F);
		this.field_3390 = new class_630(this, 56, 0);
		this.field_3390.field_3666 = true;
		this.field_3390.method_2856(-1.0F, -2.0F, -1.0F, 2, 30, 2, f);
		this.field_3390.method_2851(5.0F, -12.0F, 0.0F);
		this.field_3392 = new class_630(this, 56, 0);
		this.field_3392.method_2856(-1.0F, 0.0F, -1.0F, 2, 30, 2, f);
		this.field_3392.method_2851(-2.0F, -2.0F, 0.0F);
		this.field_3397 = new class_630(this, 56, 0);
		this.field_3397.field_3666 = true;
		this.field_3397.method_2856(-1.0F, 0.0F, -1.0F, 2, 30, 2, f);
		this.field_3397.method_2851(2.0F, -2.0F, 0.0F);
	}

	@Override
	public void method_17087(T arg, float f, float g, float h, float i, float j, float k) {
		super.method_17087(arg, f, g, h, i, j, k);
		this.field_3398.field_3665 = true;
		float l = -14.0F;
		this.field_3391.field_3654 = 0.0F;
		this.field_3391.field_3656 = -14.0F;
		this.field_3391.field_3655 = -0.0F;
		this.field_3392.field_3654 -= 0.0F;
		this.field_3397.field_3654 -= 0.0F;
		this.field_3401.field_3654 = (float)((double)this.field_3401.field_3654 * 0.5);
		this.field_3390.field_3654 = (float)((double)this.field_3390.field_3654 * 0.5);
		this.field_3392.field_3654 = (float)((double)this.field_3392.field_3654 * 0.5);
		this.field_3397.field_3654 = (float)((double)this.field_3397.field_3654 * 0.5);
		float m = 0.4F;
		if (this.field_3401.field_3654 > 0.4F) {
			this.field_3401.field_3654 = 0.4F;
		}

		if (this.field_3390.field_3654 > 0.4F) {
			this.field_3390.field_3654 = 0.4F;
		}

		if (this.field_3401.field_3654 < -0.4F) {
			this.field_3401.field_3654 = -0.4F;
		}

		if (this.field_3390.field_3654 < -0.4F) {
			this.field_3390.field_3654 = -0.4F;
		}

		if (this.field_3392.field_3654 > 0.4F) {
			this.field_3392.field_3654 = 0.4F;
		}

		if (this.field_3397.field_3654 > 0.4F) {
			this.field_3397.field_3654 = 0.4F;
		}

		if (this.field_3392.field_3654 < -0.4F) {
			this.field_3392.field_3654 = -0.4F;
		}

		if (this.field_3397.field_3654 < -0.4F) {
			this.field_3397.field_3654 = -0.4F;
		}

		if (this.field_3371) {
			this.field_3401.field_3654 = -0.5F;
			this.field_3390.field_3654 = -0.5F;
			this.field_3401.field_3674 = 0.05F;
			this.field_3390.field_3674 = -0.05F;
		}

		this.field_3401.field_3655 = 0.0F;
		this.field_3390.field_3655 = 0.0F;
		this.field_3392.field_3655 = 0.0F;
		this.field_3397.field_3655 = 0.0F;
		this.field_3392.field_3656 = -5.0F;
		this.field_3397.field_3656 = -5.0F;
		this.field_3398.field_3655 = -0.0F;
		this.field_3398.field_3656 = -13.0F;
		this.field_3394.field_3657 = this.field_3398.field_3657;
		this.field_3394.field_3656 = this.field_3398.field_3656;
		this.field_3394.field_3655 = this.field_3398.field_3655;
		this.field_3394.field_3654 = this.field_3398.field_3654;
		this.field_3394.field_3675 = this.field_3398.field_3675;
		this.field_3394.field_3674 = this.field_3398.field_3674;
		if (this.field_3370) {
			float n = 1.0F;
			this.field_3398.field_3656 -= 5.0F;
		}
	}
}
