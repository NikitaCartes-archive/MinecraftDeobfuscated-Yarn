package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_937 extends class_927<class_1456, class_590<class_1456>> {
	private static final class_2960 field_4766 = new class_2960("textures/entity/bear/polarbear.png");

	public class_937(class_898 arg) {
		super(arg, new class_590<>(), 0.7F);
	}

	protected class_2960 method_4097(class_1456 arg) {
		return field_4766;
	}

	protected void method_4099(class_1456 arg, float f) {
		GlStateManager.scalef(1.2F, 1.2F, 1.2F);
		super.method_4042(arg, f);
	}
}
