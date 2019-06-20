package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_888 extends class_927<class_1433, class_889<class_1433>> {
	private static final class_2960 field_4654 = new class_2960("textures/entity/dolphin.png");

	public class_888(class_898 arg) {
		super(arg, new class_889<>(), 0.7F);
		this.method_4046(new class_977(this));
	}

	protected class_2960 method_3903(class_1433 arg) {
		return field_4654;
	}

	protected void method_3901(class_1433 arg, float f) {
		float g = 1.0F;
		GlStateManager.scalef(1.0F, 1.0F, 1.0F);
	}

	protected void method_3902(class_1433 arg, float f, float g, float h) {
		super.method_4058(arg, f, g, h);
	}
}
