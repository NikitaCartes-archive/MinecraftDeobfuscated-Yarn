package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_551 extends class_548 {
	private final class_630 field_3314;
	private final class_630 field_3315;
	private final class_630 field_3313;
	private final class_630 field_3312;

	public class_551() {
		this(0.0F);
	}

	public class_551(float f) {
		super(f, 64, 64);
		this.field_3398 = new class_630(this, 0, 0);
		this.field_3398.method_2856(-1.0F, -7.0F, -1.0F, 2, 7, 2, f);
		this.field_3398.method_2851(0.0F, 0.0F, 0.0F);
		this.field_3391 = new class_630(this, 0, 26);
		this.field_3391.method_2856(-6.0F, 0.0F, -1.5F, 12, 3, 3, f);
		this.field_3391.method_2851(0.0F, 0.0F, 0.0F);
		this.field_3401 = new class_630(this, 24, 0);
		this.field_3401.method_2856(-2.0F, -2.0F, -1.0F, 2, 12, 2, f);
		this.field_3401.method_2851(-5.0F, 2.0F, 0.0F);
		this.field_3390 = new class_630(this, 32, 16);
		this.field_3390.field_3666 = true;
		this.field_3390.method_2856(0.0F, -2.0F, -1.0F, 2, 12, 2, f);
		this.field_3390.method_2851(5.0F, 2.0F, 0.0F);
		this.field_3392 = new class_630(this, 8, 0);
		this.field_3392.method_2856(-1.0F, 0.0F, -1.0F, 2, 11, 2, f);
		this.field_3392.method_2851(-1.9F, 12.0F, 0.0F);
		this.field_3397 = new class_630(this, 40, 16);
		this.field_3397.field_3666 = true;
		this.field_3397.method_2856(-1.0F, 0.0F, -1.0F, 2, 11, 2, f);
		this.field_3397.method_2851(1.9F, 12.0F, 0.0F);
		this.field_3314 = new class_630(this, 16, 0);
		this.field_3314.method_2856(-3.0F, 3.0F, -1.0F, 2, 7, 2, f);
		this.field_3314.method_2851(0.0F, 0.0F, 0.0F);
		this.field_3314.field_3665 = true;
		this.field_3315 = new class_630(this, 48, 16);
		this.field_3315.method_2856(1.0F, 3.0F, -1.0F, 2, 7, 2, f);
		this.field_3315.method_2851(0.0F, 0.0F, 0.0F);
		this.field_3313 = new class_630(this, 0, 48);
		this.field_3313.method_2856(-4.0F, 10.0F, -1.0F, 8, 2, 2, f);
		this.field_3313.method_2851(0.0F, 0.0F, 0.0F);
		this.field_3312 = new class_630(this, 0, 32);
		this.field_3312.method_2856(-6.0F, 11.0F, -6.0F, 12, 1, 12, f);
		this.field_3312.method_2851(0.0F, 12.0F, 0.0F);
		this.field_3394.field_3665 = false;
	}

	@Override
	public void method_17066(class_1531 arg, float f, float g, float h, float i, float j, float k) {
		super.method_17066(arg, f, g, h, i, j, k);
		this.field_3390.field_3665 = arg.method_6929();
		this.field_3401.field_3665 = arg.method_6929();
		this.field_3312.field_3665 = !arg.method_6901();
		this.field_3397.method_2851(1.9F, 12.0F, 0.0F);
		this.field_3392.method_2851(-1.9F, 12.0F, 0.0F);
		this.field_3314.field_3654 = (float) (Math.PI / 180.0) * arg.method_6923().method_10256();
		this.field_3314.field_3675 = (float) (Math.PI / 180.0) * arg.method_6923().method_10257();
		this.field_3314.field_3674 = (float) (Math.PI / 180.0) * arg.method_6923().method_10258();
		this.field_3315.field_3654 = (float) (Math.PI / 180.0) * arg.method_6923().method_10256();
		this.field_3315.field_3675 = (float) (Math.PI / 180.0) * arg.method_6923().method_10257();
		this.field_3315.field_3674 = (float) (Math.PI / 180.0) * arg.method_6923().method_10258();
		this.field_3313.field_3654 = (float) (Math.PI / 180.0) * arg.method_6923().method_10256();
		this.field_3313.field_3675 = (float) (Math.PI / 180.0) * arg.method_6923().method_10257();
		this.field_3313.field_3674 = (float) (Math.PI / 180.0) * arg.method_6923().method_10258();
		this.field_3312.field_3654 = 0.0F;
		this.field_3312.field_3675 = (float) (Math.PI / 180.0) * -arg.field_6031;
		this.field_3312.field_3674 = 0.0F;
	}

	public void method_17067(class_1531 arg, float f, float g, float h, float i, float j, float k) {
		super.method_17088(arg, f, g, h, i, j, k);
		GlStateManager.pushMatrix();
		if (this.field_3448) {
			float l = 2.0F;
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * k, 0.0F);
			this.field_3314.method_2846(k);
			this.field_3315.method_2846(k);
			this.field_3313.method_2846(k);
			this.field_3312.method_2846(k);
		} else {
			if (arg.method_5715()) {
				GlStateManager.translatef(0.0F, 0.2F, 0.0F);
			}

			this.field_3314.method_2846(k);
			this.field_3315.method_2846(k);
			this.field_3313.method_2846(k);
			this.field_3312.method_2846(k);
		}

		GlStateManager.popMatrix();
	}

	@Override
	public void method_2803(float f, class_1306 arg) {
		class_630 lv = this.method_2808(arg);
		boolean bl = lv.field_3665;
		lv.field_3665 = true;
		super.method_2803(f, arg);
		lv.field_3665 = bl;
	}
}
