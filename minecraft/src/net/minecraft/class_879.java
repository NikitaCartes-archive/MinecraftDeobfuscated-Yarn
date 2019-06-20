package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_879 extends class_927<class_1420, class_553> {
	private static final class_2960 field_4645 = new class_2960("textures/entity/bat.png");

	public class_879(class_898 arg) {
		super(arg, new class_553(), 0.25F);
	}

	protected class_2960 method_3883(class_1420 arg) {
		return field_4645;
	}

	protected void method_3884(class_1420 arg, float f) {
		GlStateManager.scalef(0.35F, 0.35F, 0.35F);
	}

	protected void method_3882(class_1420 arg, float f, float g, float h) {
		if (arg.method_6450()) {
			GlStateManager.translatef(0.0F, -0.1F, 0.0F);
		} else {
			GlStateManager.translatef(0.0F, class_3532.method_15362(f * 0.3F) * 0.1F, 0.0F);
		}

		super.method_4058(arg, f, g, h);
	}
}
