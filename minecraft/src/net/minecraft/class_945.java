package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_945 extends class_927<class_1621, class_609<class_1621>> {
	private static final class_2960 field_4784 = new class_2960("textures/entity/slime/slime.png");

	public class_945(class_898 arg) {
		super(arg, new class_609<>(16), 0.25F);
		this.method_4046(new class_997<>(this));
	}

	public void method_4117(class_1621 arg, double d, double e, double f, float g, float h) {
		this.field_4673 = 0.25F * (float)arg.method_7152();
		super.method_4072(arg, d, e, f, g, h);
	}

	protected void method_4118(class_1621 arg, float f) {
		float g = 0.999F;
		GlStateManager.scalef(0.999F, 0.999F, 0.999F);
		float h = (float)arg.method_7152();
		float i = class_3532.method_16439(f, arg.field_7387, arg.field_7388) / (h * 0.5F + 1.0F);
		float j = 1.0F / (i + 1.0F);
		GlStateManager.scalef(j * h, 1.0F / j * h, j * h);
	}

	protected class_2960 method_4116(class_1621 arg) {
		return field_4784;
	}
}
