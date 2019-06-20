package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_966 extends class_897<class_1687> {
	private static final class_2960 field_4817 = new class_2960("textures/entity/wither/wither_invulnerable.png");
	private static final class_2960 field_4815 = new class_2960("textures/entity/wither/wither.png");
	private final class_607 field_4816 = new class_607();

	public class_966(class_898 arg) {
		super(arg);
	}

	private float method_4158(float f, float g, float h) {
		float i = g - f;

		while (i < -180.0F) {
			i += 360.0F;
		}

		while (i >= 180.0F) {
			i -= 360.0F;
		}

		return f + h * i;
	}

	public void method_4159(class_1687 arg, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		float i = this.method_4158(arg.field_5982, arg.field_6031, h);
		float j = class_3532.method_16439(h, arg.field_6004, arg.field_5965);
		GlStateManager.translatef((float)d, (float)e, (float)f);
		float k = 0.0625F;
		GlStateManager.enableRescaleNormal();
		GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
		GlStateManager.enableAlphaTest();
		this.method_3925(arg);
		if (this.field_4674) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.method_3929(arg));
		}

		this.field_4816.method_2821(0.0F, 0.0F, 0.0F, i, j, 0.0625F);
		if (this.field_4674) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();
		super.method_3936(arg, d, e, f, g, h);
	}

	protected class_2960 method_4160(class_1687 arg) {
		return arg.method_7503() ? field_4817 : field_4815;
	}
}
