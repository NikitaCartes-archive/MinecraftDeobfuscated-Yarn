package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_877 extends class_922<class_1531, class_548> {
	public static final class_2960 field_4642 = new class_2960("textures/entity/armorstand/wood.png");

	public class_877(class_898 arg) {
		super(arg, new class_551(), 0.0F);
		this.method_4046(new class_987<>(this, new class_548(0.5F), new class_548(1.0F)));
		this.method_4046(new class_989<>(this));
		this.method_4046(new class_979<>(this));
		this.method_4046(new class_976<>(this));
	}

	protected class_2960 method_3880(class_1531 arg) {
		return field_4642;
	}

	protected void method_3877(class_1531 arg, float f, float g, float h) {
		GlStateManager.rotatef(180.0F - g, 0.0F, 1.0F, 0.0F);
		float i = (float)(arg.field_6002.method_8510() - arg.field_7112) + h;
		if (i < 5.0F) {
			GlStateManager.rotatef(class_3532.method_15374(i / 1.5F * (float) Math.PI) * 3.0F, 0.0F, 1.0F, 0.0F);
		}
	}

	protected boolean method_3878(class_1531 arg) {
		return arg.method_5807();
	}

	public void method_3876(class_1531 arg, double d, double e, double f, float g, float h) {
		if (arg.method_6912()) {
			this.field_4739 = true;
		}

		super.method_4054(arg, d, e, f, g, h);
		if (arg.method_6912()) {
			this.field_4739 = false;
		}
	}
}
