package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TntMinecartEntityRenderer extends MinecartEntityRenderer<TntMinecartEntity> {
	private final BlockRenderManager blockRenderManager;

	public TntMinecartEntityRenderer(EntityRendererFactory.Context context) {
		super(context, EntityModelLayers.TNT_MINECART);
		this.blockRenderManager = context.getBlockRenderManager();
	}

	protected void renderBlock(
		TntMinecartEntity tntMinecartEntity, float f, BlockState blockState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i
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

		renderFlashingBlock(this.blockRenderManager, blockState, matrixStack, vertexConsumerProvider, i, j > -1 && j / 5 % 2 == 0);
	}

	/**
	 * Renders a given block state into the given buffers either normally or with a bright white overlay.
	 * Used for rendering primed TNT either standalone or as part of a TNT minecart.
	 * 
	 * @param drawFlash whether a white semi-transparent overlay is added to the block to indicate the flash
	 */
	public static void renderFlashingBlock(
		BlockRenderManager blockRenderManager, BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, boolean drawFlash
	) {
		int i;
		if (drawFlash) {
			i = OverlayTexture.packUv(OverlayTexture.getU(1.0F), 10);
		} else {
			i = OverlayTexture.DEFAULT_UV;
		}

		blockRenderManager.renderBlockAsEntity(state, matrices, vertexConsumers, light, i);
	}
}
