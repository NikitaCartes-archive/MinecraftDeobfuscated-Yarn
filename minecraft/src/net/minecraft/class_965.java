package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_965 extends class_927<class_1640, class_622<class_1640>> {
	private static final class_2960 field_4814 = new class_2960("textures/entity/witch.png");

	public class_965(class_898 arg) {
		super(arg, new class_622<>(0.0F), 0.5F);
		this.method_4046(new class_1005<>(this));
	}

	public void method_4155(class_1640 arg, double d, double e, double f, float g, float h) {
		this.field_4737.method_2840(!arg.method_6047().method_7960());
		super.method_4072(arg, d, e, f, g, h);
	}

	protected class_2960 method_4154(class_1640 arg) {
		return field_4814;
	}

	protected void method_4157(class_1640 arg, float f) {
		float g = 0.9375F;
		GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
	}
}
