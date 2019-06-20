package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_953<T extends class_1297 & class_3856> extends class_897<T> {
	private final class_918 field_4792;
	private final float field_17147;

	public class_953(class_898 arg, class_918 arg2, float f) {
		super(arg);
		this.field_4792 = arg2;
		this.field_17147 = f;
	}

	public class_953(class_898 arg, class_918 arg2) {
		this(arg, arg2, 1.0F);
	}

	@Override
	public void method_3936(T arg, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)d, (float)e, (float)f);
		GlStateManager.enableRescaleNormal();
		GlStateManager.scalef(this.field_17147, this.field_17147, this.field_17147);
		GlStateManager.rotatef(-this.field_4676.field_4679, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef((float)(this.field_4676.field_4692.field_1850 == 2 ? -1 : 1) * this.field_4676.field_4677, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
		this.method_3924(class_1059.field_5275);
		if (this.field_4674) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.method_3929(arg));
		}

		this.field_4792.method_4009(arg.method_7495(), class_809.class_811.field_4318);
		if (this.field_4674) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		super.method_3936(arg, d, e, f, g, h);
	}

	@Override
	protected class_2960 method_3931(class_1297 arg) {
		return class_1059.field_5275;
	}
}
