package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public abstract class class_4593<E extends Entity> extends class_4592<E> {
	private float field_20923 = 1.0F;
	private float field_20924 = 1.0F;
	private float field_20925 = 1.0F;

	public void method_22955(float f, float g, float h) {
		this.field_20923 = f;
		this.field_20924 = g;
		this.field_20925 = h;
	}

	@Override
	public void method_17116(class_4587 arg, class_4588 arg2, int i, float f, float g, float h) {
		super.method_17116(arg, arg2, i, this.field_20923 * f, this.field_20924 * g, this.field_20925 * h);
	}
}
