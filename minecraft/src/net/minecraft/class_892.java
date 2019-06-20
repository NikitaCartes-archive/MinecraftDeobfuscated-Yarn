package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_892 extends class_897<class_1511> {
	private static final class_2960 field_4663 = new class_2960("textures/entity/end_crystal/end_crystal.png");
	private final class_583<class_1511> field_4662 = new class_627<>(0.0F, true);
	private final class_583<class_1511> field_4664 = new class_627<>(0.0F, false);

	public class_892(class_898 arg) {
		super(arg);
		this.field_4673 = 0.5F;
	}

	public void method_3908(class_1511 arg, double d, double e, double f, float g, float h) {
		float i = (float)arg.field_7034 + h;
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)d, (float)e, (float)f);
		this.method_3924(field_4663);
		float j = class_3532.method_15374(i * 0.2F) / 2.0F + 0.5F;
		j = j * j + j;
		if (this.field_4674) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.method_3929(arg));
		}

		if (arg.method_6836()) {
			this.field_4662.method_2819(arg, 0.0F, i * 3.0F, j * 0.2F, 0.0F, 0.0F, 0.0625F);
		} else {
			this.field_4664.method_2819(arg, 0.0F, i * 3.0F, j * 0.2F, 0.0F, 0.0F, 0.0625F);
		}

		if (this.field_4674) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();
		class_2338 lv = arg.method_6838();
		if (lv != null) {
			this.method_3924(class_895.field_4668);
			float k = (float)lv.method_10263() + 0.5F;
			float l = (float)lv.method_10264() + 0.5F;
			float m = (float)lv.method_10260() + 0.5F;
			double n = (double)k - arg.field_5987;
			double o = (double)l - arg.field_6010;
			double p = (double)m - arg.field_6035;
			class_895.method_3917(
				d + n, e - 0.3 + (double)(j * 0.4F) + o, f + p, h, (double)k, (double)l, (double)m, arg.field_7034, arg.field_5987, arg.field_6010, arg.field_6035
			);
		}

		super.method_3936(arg, d, e, f, g, h);
	}

	protected class_2960 method_3909(class_1511 arg) {
		return field_4663;
	}

	public boolean method_3907(class_1511 arg, class_856 arg2, double d, double e, double f) {
		return super.method_3933(arg, arg2, d, e, f) || arg.method_6838() != null;
	}
}
