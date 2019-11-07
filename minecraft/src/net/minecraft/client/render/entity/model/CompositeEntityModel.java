package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public abstract class CompositeEntityModel<E extends Entity> extends EntityModel<E> {
	@Override
	public void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float r, float g, float b) {
		this.getParts().forEach(modelPart -> modelPart.render(matrixStack, vertexConsumer, i, j, null, r, g, b));
	}

	public abstract Iterable<ModelPart> getParts();
}
