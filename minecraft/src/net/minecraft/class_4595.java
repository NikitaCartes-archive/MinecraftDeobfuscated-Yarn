package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public abstract class class_4595<E extends Entity> extends EntityModel<E> {
	@Override
	public void method_17116(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, float f, float g, float h) {
		this.getParts().forEach(modelPart -> modelPart.render(matrixStack, vertexConsumer, 0.0625F, i, null, f, g, h));
	}

	public abstract Iterable<ModelPart> getParts();
}
