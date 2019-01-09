package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_967 extends class_946 {
	private static final class_2960 field_4818 = new class_2960("textures/entity/skeleton/wither_skeleton.png");

	public class_967(class_898 arg) {
		super(arg);
	}

	@Override
	protected class_2960 method_4119(class_1547 arg) {
		return field_4818;
	}

	protected void method_4161(class_1547 arg, float f) {
		GlStateManager.scalef(1.2F, 1.2F, 1.2F);
	}
}
