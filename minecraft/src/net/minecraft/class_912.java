package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_912 extends class_3886 {
	private static final class_2960 field_4716 = new class_2960("textures/entity/zombie/husk.png");

	public class_912(class_898 arg) {
		super(arg);
	}

	protected void method_3985(class_1642 arg, float f) {
		float g = 1.0625F;
		GlStateManager.scalef(1.0625F, 1.0625F, 1.0625F);
		super.method_4042(arg, f);
	}

	@Override
	protected class_2960 method_4163(class_1642 arg) {
		return field_4716;
	}
}
