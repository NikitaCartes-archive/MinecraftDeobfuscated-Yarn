package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public interface BlockEntityRenderer<T extends BlockEntity> {
	void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay);

	default boolean rendersOutsideBoundingBox(T blockEntity) {
		return false;
	}
}
