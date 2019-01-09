package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_903 extends class_897<class_1671> {
	private final class_918 field_4703;

	public class_903(class_898 arg, class_918 arg2) {
		super(arg);
		this.field_4703 = arg2;
	}

	public void method_3968(class_1671 arg, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)d, (float)e, (float)f);
		GlStateManager.enableRescaleNormal();
		GlStateManager.rotatef(-this.field_4676.field_4679, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef((float)(this.field_4676.field_4692.field_1850 == 2 ? -1 : 1) * this.field_4676.field_4677, 1.0F, 0.0F, 0.0F);
		if (arg.method_7477()) {
			GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
		} else {
			GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
		}

		this.method_3924(class_1059.field_5275);
		if (this.field_4674) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.method_3929(arg));
		}

		this.field_4703.method_4009(arg.method_7495(), class_809.class_811.field_4318);
		if (this.field_4674) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		super.method_3936(arg, d, e, f, g, h);
	}

	protected class_2960 method_3969(class_1671 arg) {
		return class_1059.field_5275;
	}
}
