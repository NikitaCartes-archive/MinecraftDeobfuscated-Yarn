package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class TntMinecartEntityRenderer extends MinecartEntityRenderer<TntMinecartEntity> {
	public TntMinecartEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	protected void method_4137(
		TntMinecartEntity tntMinecartEntity,
		float f,
		BlockState blockState,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i
	) {
		int j = tntMinecartEntity.getFuseTicks();
		if (j > -1 && (float)j - f + 1.0F < 10.0F) {
			float g = 1.0F - ((float)j - f + 1.0F) / 10.0F;
			g = MathHelper.clamp(g, 0.0F, 1.0F);
			g *= g;
			g *= g;
			float h = 1.0F + g * 0.3F;
			matrixStack.scale(h, h, h);
		}

		if (j > -1 && j / 5 % 2 == 0) {
			method_23190(blockState, matrixStack, layeredVertexConsumerStorage, i);
		} else {
			MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(blockState, matrixStack, layeredVertexConsumerStorage, i, 0, 10);
		}
	}

	public static void method_23190(BlockState blockState, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i) {
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23017(SpriteAtlasTexture.BLOCK_ATLAS_TEX));
		vertexConsumer.defaultOverlay(OverlayTexture.getU(1.0F), 10);
		MinecraftClient.getInstance()
			.getBlockRenderManager()
			.renderDynamic(
				blockState, matrixStack, renderLayer -> renderLayer == RenderLayer.SOLID ? vertexConsumer : layeredVertexConsumerStorage.getBuffer(renderLayer), i, 0, 10
			);
		vertexConsumer.clearDefaultOverlay();
	}
}
