package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class class_968<T extends class_1642, M extends class_623<T>> extends class_909<T, M> {
	private static final class_2960 field_4819 = new class_2960("textures/entity/zombie/zombie.png");

	protected class_968(class_898 arg, M arg2, M arg3, M arg4) {
		super(arg, arg2, 0.5F);
		this.method_4046(new class_987<>(this, arg3, arg4));
	}

	protected class_2960 method_4163(class_1642 arg) {
		return field_4819;
	}

	protected void method_17144(T arg, float f, float g, float h) {
		if (arg.method_7206()) {
			g += (float)(Math.cos((double)arg.field_6012 * 3.25) * Math.PI * 0.25);
		}

		super.method_4058(arg, f, g, h);
	}
}
