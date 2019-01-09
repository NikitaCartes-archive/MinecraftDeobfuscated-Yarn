package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_893 extends class_907 {
	private static final class_2960 field_4665 = new class_2960("textures/entity/guardian_elder.png");

	public class_893(class_898 arg) {
		super(arg);
	}

	protected void method_3910(class_1577 arg, float f) {
		GlStateManager.scalef(2.35F, 2.35F, 2.35F);
	}

	@Override
	protected class_2960 method_3976(class_1577 arg) {
		return field_4665;
	}
}
