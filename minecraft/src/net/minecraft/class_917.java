package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_917 extends class_927<class_1589, class_576<class_1589>> {
	private static final class_2960 field_4727 = new class_2960("textures/entity/slime/magmacube.png");

	public class_917(class_898 arg) {
		super(arg, new class_576<>(), 0.25F);
	}

	protected class_2960 method_4001(class_1589 arg) {
		return field_4727;
	}

	protected void method_4000(class_1589 arg, float f) {
		int i = arg.method_7152();
		float g = class_3532.method_16439(f, arg.field_7387, arg.field_7388) / ((float)i * 0.5F + 1.0F);
		float h = 1.0F / (g + 1.0F);
		GlStateManager.scalef(h * (float)i, 1.0F / h * (float)i, h * (float)i);
	}
}
