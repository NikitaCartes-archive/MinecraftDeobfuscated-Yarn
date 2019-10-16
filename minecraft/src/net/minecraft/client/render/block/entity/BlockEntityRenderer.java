package net.minecraft.client.render.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public abstract class BlockEntityRenderer<T extends BlockEntity> {
	protected final BlockEntityRenderDispatcher field_20989;

	public BlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		this.field_20989 = blockEntityRenderDispatcher;
	}

	public abstract void method_3569(
		T blockEntity, double d, double e, double f, float g, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, int j
	);

	protected Sprite getSprite(Identifier identifier) {
		return MinecraftClient.getInstance().getSpriteAtlas().getSprite(identifier);
	}

	public boolean method_3563(T blockEntity) {
		return false;
	}
}
