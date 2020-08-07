package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public abstract class BlockEntityRenderer<T extends BlockEntity> {
	protected final BlockEntityRenderDispatcher dispatcher;

	public BlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public abstract void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay);

	public boolean rendersOutsideBoundingBox(T blockEntity) {
		return false;
	}
}
