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
	public void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h) {
		this.getParts().forEach(modelPart -> modelPart.render(matrixStack, vertexConsumer, 0.0625F, i, j, null, f, g, h));
	}

	public abstract Iterable<ModelPart> getParts();
}
