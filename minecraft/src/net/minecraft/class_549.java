package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_549<T extends class_1496> extends class_583<T> {
	protected final class_630 field_3305;
	protected final class_630 field_3307;
	private final class_630 field_3306;
	private final class_630 field_3303;
	private final class_630 field_3302;
	private final class_630 field_3308;
	private final class_630 field_3300;
	private final class_630[] field_3304;
	private final class_630[] field_3301;

	public class_549(float f) {
		this.field_17138 = 64;
		this.field_17139 = 64;
		this.field_3305 = new class_630(this, 0, 32);
		this.field_3305.method_2856(-5.0F, -8.0F, -17.0F, 10, 10, 22, 0.05F);
		this.field_3305.method_2851(0.0F, 11.0F, 5.0F);
		this.field_3307 = new class_630(this, 0, 35);
		this.field_3307.method_2844(-2.05F, -6.0F, -2.0F, 4, 12, 7);
		this.field_3307.field_3654 = (float) (Math.PI / 6);
		class_630 lv = new class_630(this, 0, 13);
		lv.method_2856(-3.0F, -11.0F, -2.0F, 6, 5, 7, f);
		class_630 lv2 = new class_630(this, 56, 36);
		lv2.method_2856(-1.0F, -11.0F, 5.01F, 2, 16, 2, f);
		class_630 lv3 = new class_630(this, 0, 25);
		lv3.method_2856(-2.0F, -11.0F, -7.0F, 4, 5, 5, f);
		this.field_3307.method_2845(lv);
		this.field_3307.method_2845(lv2);
		this.field_3307.method_2845(lv3);
		this.method_2789(this.field_3307);
		this.field_3306 = new class_630(this, 48, 21);
		this.field_3306.field_3666 = true;
		this.field_3306.method_2856(-3.0F, -1.01F, -1.0F, 4, 11, 4, f);
		this.field_3306.method_2851(4.0F, 14.0F, 7.0F);
		this.field_3303 = new class_630(this, 48, 21);
		this.field_3303.method_2856(-1.0F, -1.01F, -1.0F, 4, 11, 4, f);
		this.field_3303.method_2851(-4.0F, 14.0F, 7.0F);
		this.field_3302 = new class_630(this, 48, 21);
		this.field_3302.field_3666 = true;
		this.field_3302.method_2856(-3.0F, -1.01F, -1.9F, 4, 11, 4, f);
		this.field_3302.method_2851(4.0F, 6.0F, -12.0F);
		this.field_3308 = new class_630(this, 48, 21);
		this.field_3308.method_2856(-1.0F, -1.01F, -1.9F, 4, 11, 4, f);
		this.field_3308.method_2851(-4.0F, 6.0F, -12.0F);
		this.field_3300 = new class_630(this, 42, 36);
		this.field_3300.method_2856(-1.5F, 0.0F, 0.0F, 3, 14, 4, f);
		this.field_3300.method_2851(0.0F, -5.0F, 2.0F);
		this.field_3300.field_3654 = (float) (Math.PI / 6);
		this.field_3305.method_2845(this.field_3300);
		class_630 lv4 = new class_630(this, 26, 0);
		lv4.method_2856(-5.0F, -8.0F, -9.0F, 10, 9, 9, 0.5F);
		this.field_3305.method_2845(lv4);
		class_630 lv5 = new class_630(this, 29, 5);
		lv5.method_2856(2.0F, -9.0F, -6.0F, 1, 2, 2, f);
		this.field_3307.method_2845(lv5);
		class_630 lv6 = new class_630(this, 29, 5);
		lv6.method_2856(-3.0F, -9.0F, -6.0F, 1, 2, 2, f);
		this.field_3307.method_2845(lv6);
		class_630 lv7 = new class_630(this, 32, 2);
		lv7.method_2856(3.1F, -6.0F, -8.0F, 0, 3, 16, f);
		lv7.field_3654 = (float) (-Math.PI / 6);
		this.field_3307.method_2845(lv7);
		class_630 lv8 = new class_630(this, 32, 2);
		lv8.method_2856(-3.1F, -6.0F, -8.0F, 0, 3, 16, f);
		lv8.field_3654 = (float) (-Math.PI / 6);
		this.field_3307.method_2845(lv8);
		class_630 lv9 = new class_630(this, 1, 1);
		lv9.method_2856(-3.0F, -11.0F, -1.9F, 6, 5, 6, 0.2F);
		this.field_3307.method_2845(lv9);
		class_630 lv10 = new class_630(this, 19, 0);
		lv10.method_2856(-2.0F, -11.0F, -4.0F, 4, 5, 2, 0.2F);
		this.field_3307.method_2845(lv10);
		this.field_3304 = new class_630[]{lv4, lv5, lv6, lv9, lv10};
		this.field_3301 = new class_630[]{lv7, lv8};
	}

	protected void method_2789(class_630 arg) {
		class_630 lv = new class_630(this, 19, 16);
		lv.method_2856(0.55F, -13.0F, 4.0F, 2, 3, 1, -0.001F);
		class_630 lv2 = new class_630(this, 19, 16);
		lv2.method_2856(-2.55F, -13.0F, 4.0F, 2, 3, 1, -0.001F);
		arg.method_2845(lv);
		arg.method_2845(lv2);
	}

	public void method_17085(T arg, float f, float g, float h, float i, float j, float k) {
		boolean bl = arg.method_6109();
		float l = arg.method_17825();
		boolean bl2 = arg.method_6725();
		boolean bl3 = arg.method_5782();

		for (class_630 lv : this.field_3304) {
			lv.field_3665 = bl2;
		}

		for (class_630 lv : this.field_3301) {
			lv.field_3665 = bl3 && bl2;
		}

		if (bl) {
			GlStateManager.pushMatrix();
			GlStateManager.scalef(l, 0.5F + l * 0.5F, l);
			GlStateManager.translatef(0.0F, 0.95F * (1.0F - l), 0.0F);
		}

		this.field_3306.method_2846(k);
		this.field_3303.method_2846(k);
		this.field_3302.method_2846(k);
		this.field_3308.method_2846(k);
		if (bl) {
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(l, l, l);
			GlStateManager.translatef(0.0F, 2.3F * (1.0F - l), 0.0F);
		}

		this.field_3305.method_2846(k);
		if (bl) {
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			float m = l + 0.1F * l;
			GlStateManager.scalef(m, m, m);
			GlStateManager.translatef(0.0F, 2.25F * (1.0F - m), 0.1F * (1.4F - m));
		}

		this.field_3307.method_2846(k);
		if (bl) {
			GlStateManager.popMatrix();
		}
	}

	public void method_17084(T arg, float f, float g, float h) {
		super.method_2816(arg, f, g, h);
		float i = this.method_2790(arg.field_6220, arg.field_6283, h);
		float j = this.method_2790(arg.field_6259, arg.field_6241, h);
		float k = class_3532.method_16439(h, arg.field_6004, arg.field_5965);
		float l = j - i;
		float m = k * (float) (Math.PI / 180.0);
		if (l > 20.0F) {
			l = 20.0F;
		}

		if (l < -20.0F) {
			l = -20.0F;
		}

		if (g > 0.2F) {
			m += class_3532.method_15362(f * 0.4F) * 0.15F * g;
		}

		float n = arg.method_6739(h);
		float o = arg.method_6767(h);
		float p = 1.0F - o;
		float q = arg.method_6772(h);
		boolean bl = arg.field_6957 != 0;
		float r = (float)arg.field_6012 + h;
		this.field_3307.field_3656 = 4.0F;
		this.field_3307.field_3655 = -12.0F;
		this.field_3305.field_3654 = 0.0F;
		this.field_3307.field_3654 = (float) (Math.PI / 6) + m;
		this.field_3307.field_3675 = l * (float) (Math.PI / 180.0);
		float s = arg.method_5799() ? 0.2F : 1.0F;
		float t = class_3532.method_15362(s * f * 0.6662F + (float) Math.PI);
		float u = t * 0.8F * g;
		float v = (1.0F - Math.max(o, n)) * ((float) (Math.PI / 6) + m + q * class_3532.method_15374(r) * 0.05F);
		this.field_3307.field_3654 = o * ((float) (Math.PI / 12) + m) + n * (2.1816616F + class_3532.method_15374(r) * 0.05F) + v;
		this.field_3307.field_3675 = o * l * (float) (Math.PI / 180.0) + (1.0F - Math.max(o, n)) * this.field_3307.field_3675;
		this.field_3307.field_3656 = o * -4.0F + n * 11.0F + (1.0F - Math.max(o, n)) * this.field_3307.field_3656;
		this.field_3307.field_3655 = o * -4.0F + n * -12.0F + (1.0F - Math.max(o, n)) * this.field_3307.field_3655;
		this.field_3305.field_3654 = o * (float) (-Math.PI / 4) + p * this.field_3305.field_3654;
		float w = (float) (Math.PI / 12) * o;
		float x = class_3532.method_15362(r * 0.6F + (float) Math.PI);
		this.field_3302.field_3656 = 2.0F * o + 14.0F * p;
		this.field_3302.field_3655 = -6.0F * o - 10.0F * p;
		this.field_3308.field_3656 = this.field_3302.field_3656;
		this.field_3308.field_3655 = this.field_3302.field_3655;
		float y = ((float) (-Math.PI / 3) + x) * o + u * p;
		float z = ((float) (-Math.PI / 3) - x) * o - u * p;
		this.field_3306.field_3654 = w - t * 0.5F * g * p;
		this.field_3303.field_3654 = w + t * 0.5F * g * p;
		this.field_3302.field_3654 = y;
		this.field_3308.field_3654 = z;
		this.field_3300.field_3654 = (float) (Math.PI / 6) + g * 0.75F;
		this.field_3300.field_3656 = -5.0F + g;
		this.field_3300.field_3655 = 2.0F + g * 2.0F;
		if (bl) {
			this.field_3300.field_3675 = class_3532.method_15362(r * 0.7F);
		} else {
			this.field_3300.field_3675 = 0.0F;
		}
	}

	private float method_2790(float f, float g, float h) {
		float i = g - f;

		while (i < -180.0F) {
			i += 360.0F;
		}

		while (i >= 180.0F) {
			i -= 360.0F;
		}

		return f + h * i;
	}
}
