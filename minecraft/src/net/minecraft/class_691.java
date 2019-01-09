package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_691 extends class_703 {
	private static final class_2960 field_3815 = new class_2960("textures/entity/explosion.png");
	private static final class_293 field_3816 = new class_293()
		.method_1361(class_290.field_1587)
		.method_1361(class_290.field_1591)
		.method_1361(class_290.field_1581)
		.method_1361(class_290.field_1583)
		.method_1361(class_290.field_1579)
		.method_1361(class_290.field_1578);
	private int field_3820;
	private final int field_3819;
	private final class_1060 field_3817;
	private final float field_3818;

	protected class_691(class_1060 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
		super(arg2, d, e, f, 0.0, 0.0, 0.0);
		this.field_3817 = arg;
		this.field_3819 = 6 + this.field_3840.nextInt(4);
		float j = this.field_3840.nextFloat() * 0.6F + 0.4F;
		this.field_3861 = j;
		this.field_3842 = j;
		this.field_3859 = j;
		this.field_3818 = 1.0F - (float)g * 0.5F;
	}

	@Override
	public void method_3074(class_287 arg, class_1297 arg2, float f, float g, float h, float i, float j, float k) {
		int l = (int)(((float)this.field_3820 + f) * 15.0F / (float)this.field_3819);
		if (l <= 15) {
			this.field_3817.method_4618(field_3815);
			float m = (float)(l % 4) / 4.0F;
			float n = m + 0.24975F;
			float o = (float)(l / 4) / 4.0F;
			float p = o + 0.24975F;
			float q = 2.0F * this.field_3818;
			float r = (float)(class_3532.method_16436((double)f, this.field_3858, this.field_3874) - field_3873);
			float s = (float)(class_3532.method_16436((double)f, this.field_3838, this.field_3854) - field_3853);
			float t = (float)(class_3532.method_16436((double)f, this.field_3856, this.field_3871) - field_3870);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableLighting();
			class_308.method_1450();
			arg.method_1328(7, field_3816);
			arg.method_1315((double)(r - g * q - j * q), (double)(s - h * q), (double)(t - i * q - k * q))
				.method_1312((double)n, (double)p)
				.method_1336(this.field_3861, this.field_3842, this.field_3859, 1.0F)
				.method_1313(0, 240)
				.method_1318(0.0F, 1.0F, 0.0F)
				.method_1344();
			arg.method_1315((double)(r - g * q + j * q), (double)(s + h * q), (double)(t - i * q + k * q))
				.method_1312((double)n, (double)o)
				.method_1336(this.field_3861, this.field_3842, this.field_3859, 1.0F)
				.method_1313(0, 240)
				.method_1318(0.0F, 1.0F, 0.0F)
				.method_1344();
			arg.method_1315((double)(r + g * q + j * q), (double)(s + h * q), (double)(t + i * q + k * q))
				.method_1312((double)m, (double)o)
				.method_1336(this.field_3861, this.field_3842, this.field_3859, 1.0F)
				.method_1313(0, 240)
				.method_1318(0.0F, 1.0F, 0.0F)
				.method_1344();
			arg.method_1315((double)(r + g * q - j * q), (double)(s - h * q), (double)(t + i * q - k * q))
				.method_1312((double)m, (double)p)
				.method_1336(this.field_3861, this.field_3842, this.field_3859, 1.0F)
				.method_1313(0, 240)
				.method_1318(0.0F, 1.0F, 0.0F)
				.method_1344();
			class_289.method_1348().method_1350();
			GlStateManager.enableLighting();
		}
	}

	@Override
	public int method_3068(float f) {
		return 61680;
	}

	@Override
	public void method_3070() {
		this.field_3858 = this.field_3874;
		this.field_3838 = this.field_3854;
		this.field_3856 = this.field_3871;
		this.field_3820++;
		if (this.field_3820 == this.field_3819) {
			this.method_3085();
		}
	}

	@Override
	public int method_3079() {
		return 3;
	}

	@Environment(EnvType.CLIENT)
	public static class class_692 implements class_707<class_2400> {
		public class_703 method_3038(class_2400 arg, class_1937 arg2, double d, double e, double f, double g, double h, double i) {
			return new class_691(class_310.method_1551().method_1531(), arg2, d, e, f, g, h, i);
		}
	}
}
