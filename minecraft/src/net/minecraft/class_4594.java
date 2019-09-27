package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public abstract class class_4594<E extends Entity> extends class_4595<E> {
	private float field_20926 = 1.0F;
	private float field_20927 = 1.0F;
	private float field_20928 = 1.0F;

	public void method_22956(float f, float g, float h) {
		this.field_20926 = f;
		this.field_20927 = g;
		this.field_20928 = h;
	}

	@Override
	public void method_17116(class_4587 arg, class_4588 arg2, int i, float f, float g, float h) {
		super.method_17116(arg, arg2, i, this.field_20926 * f, this.field_20927 * g, this.field_20928 * h);
	}
}
