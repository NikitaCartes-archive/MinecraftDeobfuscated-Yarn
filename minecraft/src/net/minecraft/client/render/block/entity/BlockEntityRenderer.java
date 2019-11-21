package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public abstract class BlockEntityRenderer<T extends BlockEntity> {
	protected final BlockEntityRenderDispatcher blockEntityRenderDispatcher;

	public BlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		this.blockEntityRenderDispatcher = blockEntityRenderDispatcher;
	}

	public abstract void render(T blockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j);

	public boolean method_3563(T blockEntity) {
		return false;
	}
}
