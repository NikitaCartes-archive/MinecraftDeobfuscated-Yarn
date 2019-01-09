package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_925<T extends class_1688> extends class_897<T> {
	private static final class_2960 field_4746 = new class_2960("textures/entity/minecart.png");
	protected final class_583<T> field_4747 = new class_580<>();

	public class_925(class_898 arg) {
		super(arg);
		this.field_4673 = 0.5F;
	}

	public void method_4063(T arg, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		this.method_3925(arg);
		long l = (long)arg.method_5628() * 493286711L;
		l = l * l * 4392167121L + l * 98761L;
		float i = (((float)(l >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float j = (((float)(l >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float k = (((float)(l >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		GlStateManager.translatef(i, j, k);
		double m = class_3532.method_16436((double)h, arg.field_6038, arg.field_5987);
		double n = class_3532.method_16436((double)h, arg.field_5971, arg.field_6010);
		double o = class_3532.method_16436((double)h, arg.field_5989, arg.field_6035);
		double p = 0.3F;
		class_243 lv = arg.method_7508(m, n, o);
		float q = class_3532.method_16439(h, arg.field_6004, arg.field_5965);
		if (lv != null) {
			class_243 lv2 = arg.method_7505(m, n, o, 0.3F);
			class_243 lv3 = arg.method_7505(m, n, o, -0.3F);
			if (lv2 == null) {
				lv2 = lv;
			}

			if (lv3 == null) {
				lv3 = lv;
			}

			d += lv.field_1352 - m;
			e += (lv2.field_1351 + lv3.field_1351) / 2.0 - n;
			f += lv.field_1350 - o;
			class_243 lv4 = lv3.method_1031(-lv2.field_1352, -lv2.field_1351, -lv2.field_1350);
			if (lv4.method_1033() != 0.0) {
				lv4 = lv4.method_1029();
				g = (float)(Math.atan2(lv4.field_1350, lv4.field_1352) * 180.0 / Math.PI);
				q = (float)(Math.atan(lv4.field_1351) * 73.0);
			}
		}

		GlStateManager.translatef((float)d, (float)e + 0.375F, (float)f);
		GlStateManager.rotatef(180.0F - g, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(-q, 0.0F, 0.0F, 1.0F);
		float r = (float)arg.method_7507() - h;
		float s = arg.method_7521() - h;
		if (s < 0.0F) {
			s = 0.0F;
		}

		if (r > 0.0F) {
			GlStateManager.rotatef(class_3532.method_15374(r) * r * s / 10.0F * (float)arg.method_7522(), 1.0F, 0.0F, 0.0F);
		}

		int t = arg.method_7514();
		if (this.field_4674) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.method_3929(arg));
		}

		class_2680 lv5 = arg.method_7519();
		if (lv5.method_11610() != class_2464.field_11455) {
			GlStateManager.pushMatrix();
			this.method_3924(class_1059.field_5275);
			float u = 0.75F;
			GlStateManager.scalef(0.75F, 0.75F, 0.75F);
			GlStateManager.translatef(-0.5F, (float)(t - 8) / 16.0F, 0.5F);
			this.method_4064(arg, h, lv5);
			GlStateManager.popMatrix();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.method_3925(arg);
		}

		GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
		this.field_4747.method_2819(arg, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
		if (this.field_4674) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		super.method_3936(arg, d, e, f, g, h);
	}

	protected class_2960 method_4065(T arg) {
		return field_4746;
	}

	protected void method_4064(T arg, float f, class_2680 arg2) {
		GlStateManager.pushMatrix();
		class_310.method_1551().method_1541().method_3353(arg2, arg.method_5718());
		GlStateManager.popMatrix();
	}
}
