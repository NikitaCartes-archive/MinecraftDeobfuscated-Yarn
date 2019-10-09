package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MatrixStack;

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
	public void renderItem(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h) {
		super.renderItem(matrixStack, vertexConsumer, i, j, this.field_20923 * f, this.field_20924 * g, this.field_20925 * h);
	}
}
