package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_887 extends class_927<class_1548, class_562<class_1548>> {
	private static final class_2960 field_4653 = new class_2960("textures/entity/creeper/creeper.png");

	public class_887(class_898 arg) {
		super(arg, new class_562<>(), 0.5F);
		this.method_4046(new class_974(this));
		this.method_4046(new class_4298(this));
	}

	protected void method_3900(class_1548 arg, float f) {
		float g = arg.method_7003(f);
		float h = 1.0F + class_3532.method_15374(g * 100.0F) * g * 0.01F;
		g = class_3532.method_15363(g, 0.0F, 1.0F);
		g *= g;
		g *= g;
		float i = (1.0F + g * 0.4F) * h;
		float j = (1.0F + g * 0.1F) / h;
		GlStateManager.scalef(i, j, i);
	}

	protected int method_3898(class_1548 arg, float f, float g) {
		float h = arg.method_7003(g);
		if ((int)(h * 10.0F) % 2 == 0) {
			return 0;
		} else {
			int i = (int)(h * 0.2F * 255.0F);
			i = class_3532.method_15340(i, 0, 255);
			return i << 24 | 822083583;
		}
	}

	protected class_2960 method_3899(class_1548 arg) {
		return field_4653;
	}
}
