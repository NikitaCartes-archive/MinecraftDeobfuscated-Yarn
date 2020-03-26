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
	public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
		this.getParts().forEach(modelPart -> modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha));
	}

	public abstract Iterable<ModelPart> getParts();
}
