package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_881 extends class_897<class_1690> {
	private static final class_2960[] field_4648 = new class_2960[]{
		new class_2960("textures/entity/boat/oak.png"),
		new class_2960("textures/entity/boat/spruce.png"),
		new class_2960("textures/entity/boat/birch.png"),
		new class_2960("textures/entity/boat/jungle.png"),
		new class_2960("textures/entity/boat/acacia.png"),
		new class_2960("textures/entity/boat/dark_oak.png")
	};
	protected final class_554 field_4647 = new class_554();

	public class_881(class_898 arg) {
		super(arg);
		this.field_4673 = 0.5F;
	}

	public void method_3888(class_1690 arg, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		this.method_3890(d, e, f);
		this.method_3889(arg, g, h);
		this.method_3925(arg);
		if (this.field_4674) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.method_3929(arg));
		}

		this.field_4647.method_17071(arg, h, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		if (this.field_4674) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();
		super.method_3936(arg, d, e, f, g, h);
	}

	public void method_3889(class_1690 arg, float f, float g) {
		GlStateManager.rotatef(180.0F - f, 0.0F, 1.0F, 0.0F);
		float h = (float)arg.method_7533() - g;
		float i = arg.method_7554() - g;
		if (i < 0.0F) {
			i = 0.0F;
		}

		if (h > 0.0F) {
			GlStateManager.rotatef(class_3532.method_15374(h) * h * i / 10.0F * (float)arg.method_7543(), 1.0F, 0.0F, 0.0F);
		}

		float j = arg.method_7547(g);
		if (!class_3532.method_15347(j, 0.0F)) {
			GlStateManager.rotatef(arg.method_7547(g), 1.0F, 0.0F, 1.0F);
		}

		GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
	}

	public void method_3890(double d, double e, double f) {
		GlStateManager.translatef((float)d, (float)e + 0.375F, (float)f);
	}

	protected class_2960 method_3891(class_1690 arg) {
		return field_4648[arg.method_7536().ordinal()];
	}

	@Override
	public boolean method_16894() {
		return true;
	}

	public void method_3887(class_1690 arg, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		this.method_3890(d, e, f);
		this.method_3889(arg, g, h);
		this.method_3925(arg);
		this.field_4647.method_2836(arg, h, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
	}
}
