package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_960 extends class_909<class_1634, class_617> {
	private static final class_2960 field_4801 = new class_2960("textures/entity/illager/vex.png");
	private static final class_2960 field_4802 = new class_2960("textures/entity/illager/vex_charging.png");

	public class_960(class_898 arg) {
		super(arg, new class_617(), 0.3F);
	}

	protected class_2960 method_4144(class_1634 arg) {
		return arg.method_7176() ? field_4802 : field_4801;
	}

	protected void method_4143(class_1634 arg, float f) {
		GlStateManager.scalef(0.4F, 0.4F, 0.4F);
	}
}
