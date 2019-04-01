package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_951 extends class_927<class_1477, class_610<class_1477>> {
	private static final class_2960 field_4791 = new class_2960("textures/entity/squid.png");

	public class_951(class_898 arg) {
		super(arg, new class_610<>(), 0.7F);
	}

	protected class_2960 method_4127(class_1477 arg) {
		return field_4791;
	}

	protected void method_4126(class_1477 arg, float f, float g, float h) {
		float i = class_3532.method_16439(h, arg.field_6905, arg.field_6907);
		float j = class_3532.method_16439(h, arg.field_6906, arg.field_6903);
		GlStateManager.translatef(0.0F, 0.5F, 0.0F);
		GlStateManager.rotatef(180.0F - g, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotatef(i, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(j, 0.0F, 1.0F, 0.0F);
		GlStateManager.translatef(0.0F, -1.2F, 0.0F);
	}

	protected float method_4125(class_1477 arg, float f) {
		return class_3532.method_16439(f, arg.field_6900, arg.field_6904);
	}
}
