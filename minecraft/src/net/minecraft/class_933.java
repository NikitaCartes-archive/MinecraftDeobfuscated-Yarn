package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_933 extends class_927<class_1593, class_588<class_1593>> {
	private static final class_2960 field_4756 = new class_2960("textures/entity/phantom.png");

	public class_933(class_898 arg) {
		super(arg, new class_588<>(), 0.75F);
		this.method_4046(new class_993<>(this));
	}

	protected class_2960 method_4090(class_1593 arg) {
		return field_4756;
	}

	protected void method_4088(class_1593 arg, float f) {
		int i = arg.method_7084();
		float g = 1.0F + 0.15F * (float)i;
		GlStateManager.scalef(g, g, g);
		GlStateManager.translatef(0.0F, 1.3125F, 0.1875F);
	}

	protected void method_4089(class_1593 arg, float f, float g, float h) {
		super.method_4058(arg, f, g, h);
		GlStateManager.rotatef(arg.field_5965, 1.0F, 0.0F, 0.0F);
	}
}
