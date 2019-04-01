package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_964 extends class_927<class_1528, class_621<class_1528>> {
	private static final class_2960 field_4812 = new class_2960("textures/entity/wither/wither_invulnerable.png");
	private static final class_2960 field_4813 = new class_2960("textures/entity/wither/wither.png");

	public class_964(class_898 arg) {
		super(arg, new class_621<>(0.0F), 1.0F);
		this.method_4046(new class_1004(this));
	}

	protected class_2960 method_4153(class_1528 arg) {
		int i = arg.method_6884();
		return i > 0 && (i > 80 || i / 5 % 2 != 1) ? field_4812 : field_4813;
	}

	protected void method_4152(class_1528 arg, float f) {
		float g = 2.0F;
		int i = arg.method_6884();
		if (i > 0) {
			g -= ((float)i - f) / 220.0F * 0.5F;
		}

		GlStateManager.scalef(g, g, g);
	}
}
