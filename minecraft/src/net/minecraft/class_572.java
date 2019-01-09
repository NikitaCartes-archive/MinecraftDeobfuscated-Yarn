package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_572<T extends class_1309> extends class_583<T> implements class_3881, class_3882 {
	public class_630 field_3398;
	public class_630 field_3394;
	public class_630 field_3391;
	public class_630 field_3401;
	public class_630 field_3390;
	public class_630 field_3392;
	public class_630 field_3397;
	public class_572.class_573 field_3399 = class_572.class_573.field_3409;
	public class_572.class_573 field_3395 = class_572.class_573.field_3409;
	public boolean field_3400;
	public float field_3396;
	private float field_3393;

	public class_572() {
		this(0.0F);
	}

	public class_572(float f) {
		this(f, 0.0F, 64, 32);
	}

	public class_572(float f, float g, int i, int j) {
		this.field_17138 = i;
		this.field_17139 = j;
		this.field_3398 = new class_630(this, 0, 0);
		this.field_3398.method_2856(-4.0F, -8.0F, -4.0F, 8, 8, 8, f);
		this.field_3398.method_2851(0.0F, 0.0F + g, 0.0F);
		this.field_3394 = new class_630(this, 32, 0);
		this.field_3394.method_2856(-4.0F, -8.0F, -4.0F, 8, 8, 8, f + 0.5F);
		this.field_3394.method_2851(0.0F, 0.0F + g, 0.0F);
		this.field_3391 = new class_630(this, 16, 16);
		this.field_3391.method_2856(-4.0F, 0.0F, -2.0F, 8, 12, 4, f);
		this.field_3391.method_2851(0.0F, 0.0F + g, 0.0F);
		this.field_3401 = new class_630(this, 40, 16);
		this.field_3401.method_2856(-3.0F, -2.0F, -2.0F, 4, 12, 4, f);
		this.field_3401.method_2851(-5.0F, 2.0F + g, 0.0F);
		this.field_3390 = new class_630(this, 40, 16);
		this.field_3390.field_3666 = true;
		this.field_3390.method_2856(-1.0F, -2.0F, -2.0F, 4, 12, 4, f);
		this.field_3390.method_2851(5.0F, 2.0F + g, 0.0F);
		this.field_3392 = new class_630(this, 0, 16);
		this.field_3392.method_2856(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.field_3392.method_2851(-1.9F, 12.0F + g, 0.0F);
		this.field_3397 = new class_630(this, 0, 16);
		this.field_3397.field_3666 = true;
		this.field_3397.method_2856(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.field_3397.method_2851(1.9F, 12.0F + g, 0.0F);
	}

	public void method_17088(T arg, float f, float g, float h, float i, float j, float k) {
		this.method_17087(arg, f, g, h, i, j, k);
		GlStateManager.pushMatrix();
		if (this.field_3448) {
			float l = 2.0F;
			GlStateManager.scalef(0.75F, 0.75F, 0.75F);
			GlStateManager.translatef(0.0F, 16.0F * k, 0.0F);
			this.field_3398.method_2846(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * k, 0.0F);
			this.field_3391.method_2846(k);
			this.field_3401.method_2846(k);
			this.field_3390.method_2846(k);
			this.field_3392.method_2846(k);
			this.field_3397.method_2846(k);
			this.field_3394.method_2846(k);
		} else {
			if (arg.method_5715()) {
				GlStateManager.translatef(0.0F, 0.2F, 0.0F);
			}

			this.field_3398.method_2846(k);
			this.field_3391.method_2846(k);
			this.field_3401.method_2846(k);
			this.field_3390.method_2846(k);
			this.field_3392.method_2846(k);
			this.field_3397.method_2846(k);
			this.field_3394.method_2846(k);
		}

		GlStateManager.popMatrix();
	}

	public void method_17086(T arg, float f, float g, float h) {
		this.field_3396 = arg.method_6024(h);
		this.field_3393 = (float)arg.method_6048();
		super.method_2816(arg, f, g, h);
	}

	public void method_17087(T arg, float f, float g, float h, float i, float j, float k) {
		boolean bl = arg.method_6003() > 4;
		boolean bl2 = arg.method_5681();
		this.field_3398.field_3675 = i * (float) (Math.PI / 180.0);
		if (bl) {
			this.field_3398.field_3654 = (float) (-Math.PI / 4);
		} else if (this.field_3396 > 0.0F) {
			if (bl2) {
				this.field_3398.field_3654 = this.method_2804(this.field_3398.field_3654, (float) (-Math.PI / 4), this.field_3396);
			} else {
				this.field_3398.field_3654 = this.method_2804(this.field_3398.field_3654, j * (float) (Math.PI / 180.0), this.field_3396);
			}
		} else {
			this.field_3398.field_3654 = j * (float) (Math.PI / 180.0);
		}

		this.field_3391.field_3675 = 0.0F;
		this.field_3401.field_3655 = 0.0F;
		this.field_3401.field_3657 = -5.0F;
		this.field_3390.field_3655 = 0.0F;
		this.field_3390.field_3657 = 5.0F;
		float l = 1.0F;
		if (bl) {
			l = (float)(arg.field_5967 * arg.field_5967 + arg.field_5984 * arg.field_5984 + arg.field_6006 * arg.field_6006);
			l /= 0.2F;
			l *= l * l;
		}

		if (l < 1.0F) {
			l = 1.0F;
		}

		this.field_3401.field_3654 = class_3532.method_15362(f * 0.6662F + (float) Math.PI) * 2.0F * g * 0.5F / l;
		this.field_3390.field_3654 = class_3532.method_15362(f * 0.6662F) * 2.0F * g * 0.5F / l;
		this.field_3401.field_3674 = 0.0F;
		this.field_3390.field_3674 = 0.0F;
		this.field_3392.field_3654 = class_3532.method_15362(f * 0.6662F) * 1.4F * g / l;
		this.field_3397.field_3654 = class_3532.method_15362(f * 0.6662F + (float) Math.PI) * 1.4F * g / l;
		this.field_3392.field_3675 = 0.0F;
		this.field_3397.field_3675 = 0.0F;
		this.field_3392.field_3674 = 0.0F;
		this.field_3397.field_3674 = 0.0F;
		if (this.field_3449) {
			this.field_3401.field_3654 += (float) (-Math.PI / 5);
			this.field_3390.field_3654 += (float) (-Math.PI / 5);
			this.field_3392.field_3654 = -1.4137167F;
			this.field_3392.field_3675 = (float) (Math.PI / 10);
			this.field_3392.field_3674 = 0.07853982F;
			this.field_3397.field_3654 = -1.4137167F;
			this.field_3397.field_3675 = (float) (-Math.PI / 10);
			this.field_3397.field_3674 = -0.07853982F;
		}

		this.field_3401.field_3675 = 0.0F;
		this.field_3401.field_3674 = 0.0F;
		switch (this.field_3399) {
			case field_3409:
				this.field_3390.field_3675 = 0.0F;
				break;
			case field_3406:
				this.field_3390.field_3654 = this.field_3390.field_3654 * 0.5F - 0.9424779F;
				this.field_3390.field_3675 = (float) (Math.PI / 6);
				break;
			case field_3410:
				this.field_3390.field_3654 = this.field_3390.field_3654 * 0.5F - (float) (Math.PI / 10);
				this.field_3390.field_3675 = 0.0F;
		}

		switch (this.field_3395) {
			case field_3409:
				this.field_3401.field_3675 = 0.0F;
				break;
			case field_3406:
				this.field_3401.field_3654 = this.field_3401.field_3654 * 0.5F - 0.9424779F;
				this.field_3401.field_3675 = (float) (-Math.PI / 6);
				break;
			case field_3410:
				this.field_3401.field_3654 = this.field_3401.field_3654 * 0.5F - (float) (Math.PI / 10);
				this.field_3401.field_3675 = 0.0F;
				break;
			case field_3407:
				this.field_3401.field_3654 = this.field_3401.field_3654 * 0.5F - (float) Math.PI;
				this.field_3401.field_3675 = 0.0F;
		}

		if (this.field_3399 == class_572.class_573.field_3407
			&& this.field_3395 != class_572.class_573.field_3406
			&& this.field_3395 != class_572.class_573.field_3407
			&& this.field_3395 != class_572.class_573.field_3403) {
			this.field_3390.field_3654 = this.field_3390.field_3654 * 0.5F - (float) Math.PI;
			this.field_3390.field_3675 = 0.0F;
		}

		if (this.field_3447 > 0.0F) {
			class_1306 lv = this.method_2806(arg);
			class_630 lv2 = this.method_2808(lv);
			float m = this.field_3447;
			this.field_3391.field_3675 = class_3532.method_15374(class_3532.method_15355(m) * (float) (Math.PI * 2)) * 0.2F;
			if (lv == class_1306.field_6182) {
				this.field_3391.field_3675 *= -1.0F;
			}

			this.field_3401.field_3655 = class_3532.method_15374(this.field_3391.field_3675) * 5.0F;
			this.field_3401.field_3657 = -class_3532.method_15362(this.field_3391.field_3675) * 5.0F;
			this.field_3390.field_3655 = -class_3532.method_15374(this.field_3391.field_3675) * 5.0F;
			this.field_3390.field_3657 = class_3532.method_15362(this.field_3391.field_3675) * 5.0F;
			this.field_3401.field_3675 = this.field_3401.field_3675 + this.field_3391.field_3675;
			this.field_3390.field_3675 = this.field_3390.field_3675 + this.field_3391.field_3675;
			this.field_3390.field_3654 = this.field_3390.field_3654 + this.field_3391.field_3675;
			m = 1.0F - this.field_3447;
			m *= m;
			m *= m;
			m = 1.0F - m;
			float n = class_3532.method_15374(m * (float) Math.PI);
			float o = class_3532.method_15374(this.field_3447 * (float) Math.PI) * -(this.field_3398.field_3654 - 0.7F) * 0.75F;
			lv2.field_3654 = (float)((double)lv2.field_3654 - ((double)n * 1.2 + (double)o));
			lv2.field_3675 = lv2.field_3675 + this.field_3391.field_3675 * 2.0F;
			lv2.field_3674 = lv2.field_3674 + class_3532.method_15374(this.field_3447 * (float) Math.PI) * -0.4F;
		}

		if (this.field_3400) {
			this.field_3391.field_3654 = 0.5F;
			this.field_3401.field_3654 += 0.4F;
			this.field_3390.field_3654 += 0.4F;
			this.field_3392.field_3655 = 4.0F;
			this.field_3397.field_3655 = 4.0F;
			this.field_3392.field_3656 = 9.0F;
			this.field_3397.field_3656 = 9.0F;
			this.field_3398.field_3656 = 1.0F;
		} else {
			this.field_3391.field_3654 = 0.0F;
			this.field_3392.field_3655 = 0.1F;
			this.field_3397.field_3655 = 0.1F;
			this.field_3392.field_3656 = 12.0F;
			this.field_3397.field_3656 = 12.0F;
			this.field_3398.field_3656 = 0.0F;
		}

		this.field_3401.field_3674 = this.field_3401.field_3674 + class_3532.method_15362(h * 0.09F) * 0.05F + 0.05F;
		this.field_3390.field_3674 = this.field_3390.field_3674 - (class_3532.method_15362(h * 0.09F) * 0.05F + 0.05F);
		this.field_3401.field_3654 = this.field_3401.field_3654 + class_3532.method_15374(h * 0.067F) * 0.05F;
		this.field_3390.field_3654 = this.field_3390.field_3654 - class_3532.method_15374(h * 0.067F) * 0.05F;
		if (this.field_3395 == class_572.class_573.field_3403) {
			this.field_3401.field_3675 = -0.1F + this.field_3398.field_3675;
			this.field_3390.field_3675 = 0.1F + this.field_3398.field_3675 + 0.4F;
			this.field_3401.field_3654 = (float) (-Math.PI / 2) + this.field_3398.field_3654;
			this.field_3390.field_3654 = (float) (-Math.PI / 2) + this.field_3398.field_3654;
		} else if (this.field_3399 == class_572.class_573.field_3403
			&& this.field_3395 != class_572.class_573.field_3407
			&& this.field_3395 != class_572.class_573.field_3406) {
			this.field_3401.field_3675 = -0.1F + this.field_3398.field_3675 - 0.4F;
			this.field_3390.field_3675 = 0.1F + this.field_3398.field_3675;
			this.field_3401.field_3654 = (float) (-Math.PI / 2) + this.field_3398.field_3654;
			this.field_3390.field_3654 = (float) (-Math.PI / 2) + this.field_3398.field_3654;
		}

		float p = (float)class_1764.method_7775(arg.method_6030());
		if (this.field_3395 == class_572.class_573.field_3405) {
			this.field_3401.field_3675 = -0.8F;
			this.field_3401.field_3654 = -0.97079635F;
			this.field_3390.field_3654 = -0.97079635F;
			float q = class_3532.method_15363(this.field_3393, 0.0F, p);
			this.field_3390.field_3675 = class_3532.method_16439(q / p, 0.4F, 0.85F);
			this.field_3390.field_3654 = class_3532.method_16439(q / p, this.field_3390.field_3654, (float) (-Math.PI / 2));
		} else if (this.field_3399 == class_572.class_573.field_3405) {
			this.field_3390.field_3675 = 0.8F;
			this.field_3401.field_3654 = -0.97079635F;
			this.field_3390.field_3654 = -0.97079635F;
			float q = class_3532.method_15363(this.field_3393, 0.0F, p);
			this.field_3401.field_3675 = class_3532.method_16439(q / p, -0.4F, -0.85F);
			this.field_3401.field_3654 = class_3532.method_16439(q / p, this.field_3401.field_3654, (float) (-Math.PI / 2));
		}

		if (this.field_3395 == class_572.class_573.field_3408 && this.field_3447 <= 0.0F) {
			this.field_3401.field_3675 = -0.3F + this.field_3398.field_3675;
			this.field_3390.field_3675 = 0.6F + this.field_3398.field_3675;
			this.field_3401.field_3654 = (float) (-Math.PI / 2) + this.field_3398.field_3654 + 0.1F;
			this.field_3390.field_3654 = -1.5F + this.field_3398.field_3654;
		} else if (this.field_3399 == class_572.class_573.field_3408) {
			this.field_3401.field_3675 = -0.6F + this.field_3398.field_3675;
			this.field_3390.field_3675 = 0.3F + this.field_3398.field_3675;
			this.field_3401.field_3654 = -1.5F + this.field_3398.field_3654;
			this.field_3390.field_3654 = (float) (-Math.PI / 2) + this.field_3398.field_3654 + 0.1F;
		}

		if (this.field_3396 > 0.0F) {
			float q = f % 26.0F;
			float m = this.field_3447 > 0.0F ? 0.0F : this.field_3396;
			if (q < 14.0F) {
				this.field_3390.field_3654 = this.method_2804(this.field_3390.field_3654, 0.0F, this.field_3396);
				this.field_3401.field_3654 = class_3532.method_16439(m, this.field_3401.field_3654, 0.0F);
				this.field_3390.field_3675 = this.method_2804(this.field_3390.field_3675, (float) Math.PI, this.field_3396);
				this.field_3401.field_3675 = class_3532.method_16439(m, this.field_3401.field_3675, (float) Math.PI);
				this.field_3390.field_3674 = this.method_2804(
					this.field_3390.field_3674, (float) Math.PI + 1.8707964F * this.method_2807(q) / this.method_2807(14.0F), this.field_3396
				);
				this.field_3401.field_3674 = class_3532.method_16439(
					m, this.field_3401.field_3674, (float) Math.PI - 1.8707964F * this.method_2807(q) / this.method_2807(14.0F)
				);
			} else if (q >= 14.0F && q < 22.0F) {
				float n = (q - 14.0F) / 8.0F;
				this.field_3390.field_3654 = this.method_2804(this.field_3390.field_3654, (float) (Math.PI / 2) * n, this.field_3396);
				this.field_3401.field_3654 = class_3532.method_16439(m, this.field_3401.field_3654, (float) (Math.PI / 2) * n);
				this.field_3390.field_3675 = this.method_2804(this.field_3390.field_3675, (float) Math.PI, this.field_3396);
				this.field_3401.field_3675 = class_3532.method_16439(m, this.field_3401.field_3675, (float) Math.PI);
				this.field_3390.field_3674 = this.method_2804(this.field_3390.field_3674, 5.012389F - 1.8707964F * n, this.field_3396);
				this.field_3401.field_3674 = class_3532.method_16439(m, this.field_3401.field_3674, 1.2707963F + 1.8707964F * n);
			} else if (q >= 22.0F && q < 26.0F) {
				float n = (q - 22.0F) / 4.0F;
				this.field_3390.field_3654 = this.method_2804(this.field_3390.field_3654, (float) (Math.PI / 2) - (float) (Math.PI / 2) * n, this.field_3396);
				this.field_3401.field_3654 = class_3532.method_16439(m, this.field_3401.field_3654, (float) (Math.PI / 2) - (float) (Math.PI / 2) * n);
				this.field_3390.field_3675 = this.method_2804(this.field_3390.field_3675, (float) Math.PI, this.field_3396);
				this.field_3401.field_3675 = class_3532.method_16439(m, this.field_3401.field_3675, (float) Math.PI);
				this.field_3390.field_3674 = this.method_2804(this.field_3390.field_3674, (float) Math.PI, this.field_3396);
				this.field_3401.field_3674 = class_3532.method_16439(m, this.field_3401.field_3674, (float) Math.PI);
			}

			float n = 0.3F;
			float o = 0.33333334F;
			this.field_3397.field_3654 = class_3532.method_16439(
				this.field_3396, this.field_3397.field_3654, 0.3F * class_3532.method_15362(f * 0.33333334F + (float) Math.PI)
			);
			this.field_3392.field_3654 = class_3532.method_16439(this.field_3396, this.field_3392.field_3654, 0.3F * class_3532.method_15362(f * 0.33333334F));
		}

		this.field_3394.method_17138(this.field_3398);
	}

	protected float method_2804(float f, float g, float h) {
		float i = g - f;

		while (i < (float) -Math.PI) {
			i += (float) (Math.PI * 2);
		}

		while (i >= (float) Math.PI) {
			i -= (float) (Math.PI * 2);
		}

		return f + h * i;
	}

	private float method_2807(float f) {
		return -65.0F * f + f * f;
	}

	public void method_2818(class_572<T> arg) {
		super.method_17081(arg);
		arg.field_3399 = this.field_3399;
		arg.field_3395 = this.field_3395;
		arg.field_3400 = this.field_3400;
	}

	public void method_2805(boolean bl) {
		this.field_3398.field_3665 = bl;
		this.field_3394.field_3665 = bl;
		this.field_3391.field_3665 = bl;
		this.field_3401.field_3665 = bl;
		this.field_3390.field_3665 = bl;
		this.field_3392.field_3665 = bl;
		this.field_3397.field_3665 = bl;
	}

	@Override
	public void method_2803(float f, class_1306 arg) {
		this.method_2808(arg).method_2847(f);
	}

	protected class_630 method_2808(class_1306 arg) {
		return arg == class_1306.field_6182 ? this.field_3390 : this.field_3401;
	}

	@Override
	public class_630 method_2838() {
		return this.field_3398;
	}

	protected class_1306 method_2806(T arg) {
		class_1306 lv = arg.method_6068();
		return arg.field_6266 == class_1268.field_5808 ? lv : lv.method_5928();
	}

	@Environment(EnvType.CLIENT)
	public static enum class_573 {
		field_3409,
		field_3410,
		field_3406,
		field_3403,
		field_3407,
		field_3405,
		field_3408;
	}
}
