package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public abstract class TintableCompositeModel<E extends Entity> extends CompositeEntityModel<E> {
	private float redMultiplier = 1.0F;
	private float greenMultiplier = 1.0F;
	private float blueMultiplier = 1.0F;

	public void setColorMultiplier(float red, float green, float blue) {
		this.redMultiplier = red;
		this.greenMultiplier = green;
		this.blueMultiplier = blue;
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float r, float g, float b) {
		super.render(matrixStack, vertexConsumer, i, j, this.redMultiplier * r, this.greenMultiplier * g, this.blueMultiplier * b);
	}
}
