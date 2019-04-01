package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_920 extends class_897<class_1532> {
	private static final class_2960 field_4734 = new class_2960("textures/entity/lead_knot.png");
	private final class_579<class_1532> field_4735 = new class_579<>();

	public class_920(class_898 arg) {
		super(arg);
	}

	public void method_4035(class_1532 arg, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		GlStateManager.translatef((float)d, (float)e, (float)f);
		float i = 0.0625F;
		GlStateManager.enableRescaleNormal();
		GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
		GlStateManager.enableAlphaTest();
		this.method_3925(arg);
		if (this.field_4674) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.method_3929(arg));
		}

		this.field_4735.method_2819(arg, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		if (this.field_4674) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();
		super.method_3936(arg, d, e, f, g, h);
	}

	protected class_2960 method_4036(class_1532 arg) {
		return field_4734;
	}
}
