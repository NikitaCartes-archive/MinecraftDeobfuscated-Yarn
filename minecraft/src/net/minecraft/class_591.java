package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_591<T extends class_1309> extends class_572<T> {
	public final class_630 field_3484;
	public final class_630 field_3486;
	public final class_630 field_3482;
	public final class_630 field_3479;
	public final class_630 field_3483;
	private final class_630 field_3485;
	private final class_630 field_3481;
	private final boolean field_3480;

	public class_591(float f, boolean bl) {
		super(f, 0.0F, 64, 64);
		this.field_3480 = bl;
		this.field_3481 = new class_630(this, 24, 0);
		this.field_3481.method_2856(-3.0F, -6.0F, -1.0F, 6, 6, 1, f);
		this.field_3485 = new class_630(this, 0, 0);
		this.field_3485.method_2853(64, 32);
		this.field_3485.method_2856(-5.0F, 0.0F, -1.0F, 10, 16, 1, f);
		if (bl) {
			this.field_3390 = new class_630(this, 32, 48);
			this.field_3390.method_2856(-1.0F, -2.0F, -2.0F, 3, 12, 4, f);
			this.field_3390.method_2851(5.0F, 2.5F, 0.0F);
			this.field_3401 = new class_630(this, 40, 16);
			this.field_3401.method_2856(-2.0F, -2.0F, -2.0F, 3, 12, 4, f);
			this.field_3401.method_2851(-5.0F, 2.5F, 0.0F);
			this.field_3484 = new class_630(this, 48, 48);
			this.field_3484.method_2856(-1.0F, -2.0F, -2.0F, 3, 12, 4, f + 0.25F);
			this.field_3484.method_2851(5.0F, 2.5F, 0.0F);
			this.field_3486 = new class_630(this, 40, 32);
			this.field_3486.method_2856(-2.0F, -2.0F, -2.0F, 3, 12, 4, f + 0.25F);
			this.field_3486.method_2851(-5.0F, 2.5F, 10.0F);
		} else {
			this.field_3390 = new class_630(this, 32, 48);
			this.field_3390.method_2856(-1.0F, -2.0F, -2.0F, 4, 12, 4, f);
			this.field_3390.method_2851(5.0F, 2.0F, 0.0F);
			this.field_3484 = new class_630(this, 48, 48);
			this.field_3484.method_2856(-1.0F, -2.0F, -2.0F, 4, 12, 4, f + 0.25F);
			this.field_3484.method_2851(5.0F, 2.0F, 0.0F);
			this.field_3486 = new class_630(this, 40, 32);
			this.field_3486.method_2856(-3.0F, -2.0F, -2.0F, 4, 12, 4, f + 0.25F);
			this.field_3486.method_2851(-5.0F, 2.0F, 10.0F);
		}

		this.field_3397 = new class_630(this, 16, 48);
		this.field_3397.method_2856(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.field_3397.method_2851(1.9F, 12.0F, 0.0F);
		this.field_3482 = new class_630(this, 0, 48);
		this.field_3482.method_2856(-2.0F, 0.0F, -2.0F, 4, 12, 4, f + 0.25F);
		this.field_3482.method_2851(1.9F, 12.0F, 0.0F);
		this.field_3479 = new class_630(this, 0, 32);
		this.field_3479.method_2856(-2.0F, 0.0F, -2.0F, 4, 12, 4, f + 0.25F);
		this.field_3479.method_2851(-1.9F, 12.0F, 0.0F);
		this.field_3483 = new class_630(this, 16, 32);
		this.field_3483.method_2856(-4.0F, 0.0F, -2.0F, 8, 12, 4, f + 0.25F);
		this.field_3483.method_2851(0.0F, 0.0F, 0.0F);
	}

	@Override
	public void method_17088(T arg, float f, float g, float h, float i, float j, float k) {
		super.method_17088(arg, f, g, h, i, j, k);
		GlStateManager.pushMatrix();
		if (this.field_3448) {
			float l = 2.0F;
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * k, 0.0F);
			this.field_3482.method_2846(k);
			this.field_3479.method_2846(k);
			this.field_3484.method_2846(k);
			this.field_3486.method_2846(k);
			this.field_3483.method_2846(k);
		} else {
			if (arg.method_5715()) {
				GlStateManager.translatef(0.0F, 0.2F, 0.0F);
			}

			this.field_3482.method_2846(k);
			this.field_3479.method_2846(k);
			this.field_3484.method_2846(k);
			this.field_3486.method_2846(k);
			this.field_3483.method_2846(k);
		}

		GlStateManager.popMatrix();
	}

	public void method_2824(float f) {
		this.field_3481.method_17138(this.field_3398);
		this.field_3481.field_3657 = 0.0F;
		this.field_3481.field_3656 = 0.0F;
		this.field_3481.method_2846(f);
	}

	public void method_2823(float f) {
		this.field_3485.method_2846(f);
	}

	@Override
	public void method_17087(T arg, float f, float g, float h, float i, float j, float k) {
		super.method_17087(arg, f, g, h, i, j, k);
		this.field_3482.method_17138(this.field_3397);
		this.field_3479.method_17138(this.field_3392);
		this.field_3484.method_17138(this.field_3390);
		this.field_3486.method_17138(this.field_3401);
		this.field_3483.method_17138(this.field_3391);
		if (arg.method_5715()) {
			this.field_3485.field_3656 = 2.0F;
		} else {
			this.field_3485.field_3656 = 0.0F;
		}
	}

	@Override
	public void method_2805(boolean bl) {
		super.method_2805(bl);
		this.field_3484.field_3665 = bl;
		this.field_3486.field_3665 = bl;
		this.field_3482.field_3665 = bl;
		this.field_3479.field_3665 = bl;
		this.field_3483.field_3665 = bl;
		this.field_3485.field_3665 = bl;
		this.field_3481.field_3665 = bl;
	}

	@Override
	public void method_2803(float f, class_1306 arg) {
		class_630 lv = this.method_2808(arg);
		if (this.field_3480) {
			float g = 0.5F * (float)(arg == class_1306.field_6183 ? 1 : -1);
			lv.field_3657 += g;
			lv.method_2847(f);
			lv.field_3657 -= g;
		} else {
			lv.method_2847(f);
		}
	}
}
