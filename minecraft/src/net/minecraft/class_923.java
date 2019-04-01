package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_923 extends class_897<class_1673> {
	private static final class_2960 field_4745 = new class_2960("textures/entity/llama/spit.png");
	private final class_581<class_1673> field_4744 = new class_581<>();

	public class_923(class_898 arg) {
		super(arg);
	}

	public void method_4061(class_1673 arg, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)d, (float)e + 0.15F, (float)f);
		GlStateManager.rotatef(class_3532.method_16439(h, arg.field_5982, arg.field_6031) - 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(class_3532.method_16439(h, arg.field_6004, arg.field_5965), 0.0F, 0.0F, 1.0F);
		this.method_3925(arg);
		if (this.field_4674) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.method_3929(arg));
		}

		this.field_4744.method_2819(arg, h, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		if (this.field_4674) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();
		super.method_3936(arg, d, e, f, g, h);
	}

	protected class_2960 method_4062(class_1673 arg) {
		return field_4745;
	}
}
