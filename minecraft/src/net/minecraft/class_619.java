package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_619<T extends class_1642> extends class_572<T> implements class_3884 {
	private class_630 field_17144;

	public class_619() {
		this(0.0F, false);
	}

	public class_619(float f, boolean bl) {
		super(f, 0.0F, 64, bl ? 32 : 64);
		if (bl) {
			this.field_3398 = new class_630(this, 0, 0);
			this.field_3398.method_2856(-4.0F, -10.0F, -4.0F, 8, 8, 8, f);
			this.field_3391 = new class_630(this, 16, 16);
			this.field_3391.method_2856(-4.0F, 0.0F, -2.0F, 8, 12, 4, f + 0.1F);
			this.field_3392 = new class_630(this, 0, 16);
			this.field_3392.method_2851(-2.0F, 12.0F, 0.0F);
			this.field_3392.method_2856(-2.0F, 0.0F, -2.0F, 4, 12, 4, f + 0.1F);
			this.field_3397 = new class_630(this, 0, 16);
			this.field_3397.field_3666 = true;
			this.field_3397.method_2851(2.0F, 12.0F, 0.0F);
			this.field_3397.method_2856(-2.0F, 0.0F, -2.0F, 4, 12, 4, f + 0.1F);
		} else {
			this.field_3398 = new class_630(this, 0, 0);
			this.field_3398.method_2850(0, 0).method_2856(-4.0F, -10.0F, -4.0F, 8, 10, 8, f);
			this.field_3398.method_2850(24, 0).method_2856(-1.0F, -3.0F, -6.0F, 2, 4, 2, f);
			this.field_3394 = new class_630(this, 32, 0);
			this.field_3394.method_2856(-4.0F, -10.0F, -4.0F, 8, 10, 8, f + 0.5F);
			this.field_17144 = new class_630(this);
			this.field_17144.method_2850(30, 47).method_2856(-8.0F, -8.0F, -6.0F, 16, 16, 1, f);
			this.field_17144.field_3654 = (float) (-Math.PI / 2);
			this.field_3394.method_2845(this.field_17144);
			this.field_3391 = new class_630(this, 16, 20);
			this.field_3391.method_2856(-4.0F, 0.0F, -3.0F, 8, 12, 6, f);
			this.field_3391.method_2850(0, 38).method_2856(-4.0F, 0.0F, -3.0F, 8, 18, 6, f + 0.05F);
			this.field_3401 = new class_630(this, 44, 22);
			this.field_3401.method_2856(-3.0F, -2.0F, -2.0F, 4, 12, 4, f);
			this.field_3401.method_2851(-5.0F, 2.0F, 0.0F);
			this.field_3390 = new class_630(this, 44, 22);
			this.field_3390.field_3666 = true;
			this.field_3390.method_2856(-1.0F, -2.0F, -2.0F, 4, 12, 4, f);
			this.field_3390.method_2851(5.0F, 2.0F, 0.0F);
			this.field_3392 = new class_630(this, 0, 22);
			this.field_3392.method_2851(-2.0F, 12.0F, 0.0F);
			this.field_3392.method_2856(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
			this.field_3397 = new class_630(this, 0, 22);
			this.field_3397.field_3666 = true;
			this.field_3397.method_2851(2.0F, 12.0F, 0.0F);
			this.field_3397.method_2856(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		}
	}

	public void method_17135(T arg, float f, float g, float h, float i, float j, float k) {
		super.method_17087(arg, f, g, h, i, j, k);
		float l = class_3532.method_15374(this.field_3447 * (float) Math.PI);
		float m = class_3532.method_15374((1.0F - (1.0F - this.field_3447) * (1.0F - this.field_3447)) * (float) Math.PI);
		this.field_3401.field_3674 = 0.0F;
		this.field_3390.field_3674 = 0.0F;
		this.field_3401.field_3675 = -(0.1F - l * 0.6F);
		this.field_3390.field_3675 = 0.1F - l * 0.6F;
		float n = (float) -Math.PI / (arg.method_6999() ? 1.5F : 2.25F);
		this.field_3401.field_3654 = n;
		this.field_3390.field_3654 = n;
		this.field_3401.field_3654 += l * 1.2F - m * 0.4F;
		this.field_3390.field_3654 += l * 1.2F - m * 0.4F;
		this.field_3401.field_3674 = this.field_3401.field_3674 + class_3532.method_15362(h * 0.09F) * 0.05F + 0.05F;
		this.field_3390.field_3674 = this.field_3390.field_3674 - (class_3532.method_15362(h * 0.09F) * 0.05F + 0.05F);
		this.field_3401.field_3654 = this.field_3401.field_3654 + class_3532.method_15374(h * 0.067F) * 0.05F;
		this.field_3390.field_3654 = this.field_3390.field_3654 - class_3532.method_15374(h * 0.067F) * 0.05F;
	}

	@Override
	public void method_17150(boolean bl) {
		this.field_3398.field_3665 = bl;
		this.field_3394.field_3665 = bl;
		this.field_17144.field_3665 = bl;
	}
}
