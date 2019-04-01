package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3992 extends class_927<class_3989, class_620<class_3989>> {
	private static final class_2960 field_17739 = new class_2960("textures/entity/wandering_trader.png");

	public class_3992(class_898 arg) {
		super(arg, new class_620<>(0.0F), 0.5F);
		this.method_4046(new class_976<>(this));
		this.method_4046(new class_4004<>(this));
	}

	protected class_2960 method_18045(class_3989 arg) {
		return field_17739;
	}

	protected void method_18046(class_3989 arg, float f) {
		float g = 0.9375F;
		GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
	}
}
