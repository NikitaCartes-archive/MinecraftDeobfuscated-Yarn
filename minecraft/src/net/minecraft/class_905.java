package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_905 extends class_927<class_1571, class_567<class_1571>> {
	private static final class_2960 field_4705 = new class_2960("textures/entity/ghast/ghast.png");
	private static final class_2960 field_4706 = new class_2960("textures/entity/ghast/ghast_shooting.png");

	public class_905(class_898 arg) {
		super(arg, new class_567<>(), 0.5F);
	}

	protected class_2960 method_3972(class_1571 arg) {
		return arg.method_7050() ? field_4706 : field_4705;
	}

	protected void method_3973(class_1571 arg, float f) {
		float g = 1.0F;
		float h = 4.5F;
		float i = 4.5F;
		GlStateManager.scalef(4.5F, 4.5F, 4.5F);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}
}
