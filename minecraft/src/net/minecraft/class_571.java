package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_571 extends class_583<class_1584> {
	private final class_630 field_3386;
	private final class_630 field_3388;
	private final class_630 field_3387;
	private final class_630 field_3385;
	private final class_630 field_3383;
	private final class_630 field_3389;
	private final class_630 field_3382;
	private final class_630 field_3384;

	public class_571() {
		this.field_17138 = 128;
		this.field_17139 = 128;
		int i = 16;
		float f = 0.0F;
		this.field_3384 = new class_630(this);
		this.field_3384.method_2851(0.0F, -7.0F, -1.5F);
		this.field_3384.method_2850(68, 73).method_2856(-5.0F, -1.0F, -18.0F, 10, 10, 18, 0.0F);
		this.field_3386 = new class_630(this);
		this.field_3386.method_2851(0.0F, 16.0F, -17.0F);
		this.field_3386.method_2850(0, 0).method_2856(-8.0F, -20.0F, -14.0F, 16, 20, 16, 0.0F);
		this.field_3386.method_2850(0, 0).method_2856(-2.0F, -6.0F, -18.0F, 4, 8, 4, 0.0F);
		class_630 lv = new class_630(this);
		lv.method_2851(-10.0F, -14.0F, -8.0F);
		lv.method_2850(74, 55).method_2856(0.0F, -14.0F, -2.0F, 2, 14, 4, 0.0F);
		lv.field_3654 = 1.0995574F;
		this.field_3386.method_2845(lv);
		class_630 lv2 = new class_630(this);
		lv2.field_3666 = true;
		lv2.method_2851(8.0F, -14.0F, -8.0F);
		lv2.method_2850(74, 55).method_2856(0.0F, -14.0F, -2.0F, 2, 14, 4, 0.0F);
		lv2.field_3654 = 1.0995574F;
		this.field_3386.method_2845(lv2);
		this.field_3388 = new class_630(this);
		this.field_3388.method_2851(0.0F, -2.0F, 2.0F);
		this.field_3388.method_2850(0, 36).method_2856(-8.0F, 0.0F, -16.0F, 16, 3, 16, 0.0F);
		this.field_3386.method_2845(this.field_3388);
		this.field_3384.method_2845(this.field_3386);
		this.field_3387 = new class_630(this);
		this.field_3387.method_2850(0, 55).method_2856(-7.0F, -10.0F, -7.0F, 14, 16, 20, 0.0F);
		this.field_3387.method_2850(0, 91).method_2856(-6.0F, 6.0F, -7.0F, 12, 13, 18, 0.0F);
		this.field_3387.method_2851(0.0F, 1.0F, 2.0F);
		this.field_3385 = new class_630(this, 96, 0);
		this.field_3385.method_2856(-4.0F, 0.0F, -4.0F, 8, 37, 8, 0.0F);
		this.field_3385.method_2851(-8.0F, -13.0F, 18.0F);
		this.field_3383 = new class_630(this, 96, 0);
		this.field_3383.field_3666 = true;
		this.field_3383.method_2856(-4.0F, 0.0F, -4.0F, 8, 37, 8, 0.0F);
		this.field_3383.method_2851(8.0F, -13.0F, 18.0F);
		this.field_3389 = new class_630(this, 64, 0);
		this.field_3389.method_2856(-4.0F, 0.0F, -4.0F, 8, 37, 8, 0.0F);
		this.field_3389.method_2851(-8.0F, -13.0F, -5.0F);
		this.field_3382 = new class_630(this, 64, 0);
		this.field_3382.field_3666 = true;
		this.field_3382.method_2856(-4.0F, 0.0F, -4.0F, 8, 37, 8, 0.0F);
		this.field_3382.method_2851(8.0F, -13.0F, -5.0F);
	}

	public void method_17090(class_1584 arg, float f, float g, float h, float i, float j, float k) {
		this.method_17091(arg, f, g, h, i, j, k);
		this.field_3384.method_2846(k);
		this.field_3387.method_2846(k);
		this.field_3385.method_2846(k);
		this.field_3383.method_2846(k);
		this.field_3389.method_2846(k);
		this.field_3382.method_2846(k);
	}

	public void method_17091(class_1584 arg, float f, float g, float h, float i, float j, float k) {
		this.field_3386.field_3654 = j * (float) (Math.PI / 180.0);
		this.field_3386.field_3675 = i * (float) (Math.PI / 180.0);
		this.field_3387.field_3654 = (float) (Math.PI / 2);
		float l = 0.4F * g;
		this.field_3385.field_3654 = class_3532.method_15362(f * 0.6662F) * l;
		this.field_3383.field_3654 = class_3532.method_15362(f * 0.6662F + (float) Math.PI) * l;
		this.field_3389.field_3654 = class_3532.method_15362(f * 0.6662F + (float) Math.PI) * l;
		this.field_3382.field_3654 = class_3532.method_15362(f * 0.6662F) * l;
	}

	public void method_17089(class_1584 arg, float f, float g, float h) {
		super.method_2816(arg, f, g, h);
		int i = arg.method_7074();
		int j = arg.method_7072();
		int k = 20;
		int l = arg.method_7070();
		int m = 10;
		if (l > 0) {
			float n = this.method_2801((float)l - h, 10.0F);
			float o = (1.0F + n) * 0.5F;
			float p = o * o * o * 12.0F;
			float q = p * class_3532.method_15374(this.field_3384.field_3654);
			this.field_3384.field_3655 = -6.5F + p;
			this.field_3384.field_3656 = -7.0F - q;
			float r = class_3532.method_15374(((float)l - h) / 10.0F * (float) Math.PI * 0.25F);
			this.field_3388.field_3654 = (float) (Math.PI / 2) * r;
			if (l > 5) {
				this.field_3388.field_3654 = class_3532.method_15374(((float)(-4 + l) - h) / 4.0F) * (float) Math.PI * 0.4F;
			} else {
				this.field_3388.field_3654 = (float) (Math.PI / 20) * class_3532.method_15374((float) Math.PI * ((float)l - h) / 10.0F);
			}
		} else {
			float n = -1.0F;
			float o = -1.0F * class_3532.method_15374(this.field_3384.field_3654);
			this.field_3384.field_3657 = 0.0F;
			this.field_3384.field_3656 = -7.0F - o;
			this.field_3384.field_3655 = 5.5F;
			boolean bl = i > 0;
			this.field_3384.field_3654 = bl ? 0.21991149F : 0.0F;
			this.field_3388.field_3654 = (float) Math.PI * (bl ? 0.05F : 0.01F);
			if (bl) {
				double d = (double)i / 40.0;
				this.field_3384.field_3657 = (float)Math.sin(d * 10.0) * 3.0F;
			} else if (j > 0) {
				float q = class_3532.method_15374(((float)(20 - j) - h) / 20.0F * (float) Math.PI * 0.25F);
				this.field_3388.field_3654 = (float) (Math.PI / 2) * q;
			}
		}
	}

	private float method_2801(float f, float g) {
		return (Math.abs(f % g - g * 0.5F) - g * 0.25F) / (g * 0.25F);
	}
}
