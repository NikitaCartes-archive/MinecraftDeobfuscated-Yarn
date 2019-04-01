package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_3729<T extends class_1543> extends class_927<T, class_575<T>> {
	protected class_3729(class_898 arg, class_575<T> arg2, float f) {
		super(arg, arg2, f);
		this.method_4046(new class_976<>(this));
	}

	public class_3729(class_898 arg) {
		super(arg, new class_575<>(0.0F, 0.0F, 64, 64), 0.5F);
		this.method_4046(new class_976<>(this));
	}

	protected void method_16460(T arg, float f) {
		float g = 0.9375F;
		GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
	}
}
