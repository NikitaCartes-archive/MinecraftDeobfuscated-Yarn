package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MatrixStack;

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
	public void method_17116(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, float f, float g, float h) {
		super.method_17116(matrixStack, vertexConsumer, i, this.field_20926 * f, this.field_20927 * g, this.field_20928 * h);
	}
}
