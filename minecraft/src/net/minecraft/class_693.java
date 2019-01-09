package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_693 extends class_703 {
	private final class_1297 field_3823;
	private final class_1297 field_3821;
	private int field_3826;
	private final int field_3825;
	private final float field_3822;
	private final class_898 field_3824 = class_310.method_1551().method_1561();

	public class_693(class_1937 arg, class_1297 arg2, class_1297 arg3, float f) {
		super(arg, arg2.field_5987, arg2.field_6010, arg2.field_6035, arg2.field_5967, arg2.field_5984, arg2.field_6006);
		this.field_3823 = arg2;
		this.field_3821 = arg3;
		this.field_3825 = 3;
		this.field_3822 = f;
	}

	@Override
	public void method_3074(class_287 arg, class_1297 arg2, float f, float g, float h, float i, float j, float k) {
		float l = ((float)this.field_3826 + f) / (float)this.field_3825;
		l *= l;
		double d = this.field_3823.field_5987;
		double e = this.field_3823.field_6010;
		double m = this.field_3823.field_6035;
		double n = class_3532.method_16436((double)f, this.field_3821.field_6038, this.field_3821.field_5987);
		double o = class_3532.method_16436((double)f, this.field_3821.field_5971, this.field_3821.field_6010) + (double)this.field_3822;
		double p = class_3532.method_16436((double)f, this.field_3821.field_5989, this.field_3821.field_6035);
		double q = class_3532.method_16436((double)l, d, n);
		double r = class_3532.method_16436((double)l, e, o);
		double s = class_3532.method_16436((double)l, m, p);
		int t = this.method_3068(f);
		int u = t % 65536;
		int v = t / 65536;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)u, (float)v);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		q -= field_3873;
		r -= field_3853;
		s -= field_3870;
		GlStateManager.enableLighting();
		this.field_3824.method_3954(this.field_3823, q, r, s, this.field_3823.field_6031, f, false);
	}

	@Override
	public void method_3070() {
		this.field_3826++;
		if (this.field_3826 == this.field_3825) {
			this.method_3085();
		}
	}

	@Override
	public int method_3079() {
		return 3;
	}
}
