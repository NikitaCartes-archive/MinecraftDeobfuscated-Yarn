package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_963 extends class_927<class_1646, class_620<class_1646>> {
	private static final class_2960 field_4807 = new class_2960("textures/entity/villager/villager.png");

	public class_963(class_898 arg, class_3296 arg2) {
		super(arg, new class_620<>(0.0F), 0.5F);
		this.method_4046(new class_976<>(this));
		this.method_4046(new class_3885<>(this, arg2, "villager"));
		this.method_4046(new class_4004<>(this));
	}

	protected class_2960 method_4151(class_1646 arg) {
		return field_4807;
	}

	protected void method_4149(class_1646 arg, float f) {
		float g = 0.9375F;
		if (arg.method_6109()) {
			g = (float)((double)g * 0.5);
			this.field_4673 = 0.25F;
		} else {
			this.field_4673 = 0.5F;
		}

		GlStateManager.scalef(g, g, g);
	}
}
