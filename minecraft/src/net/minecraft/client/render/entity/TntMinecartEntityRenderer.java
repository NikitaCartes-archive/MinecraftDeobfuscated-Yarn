package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.TntMinecartEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TntMinecartEntityRenderer extends AbstractMinecartEntityRenderer<TntMinecartEntity, TntMinecartEntityRenderState> {
	private final BlockRenderManager tntBlockRenderManager;

	public TntMinecartEntityRenderer(EntityRendererFactory.Context context) {
		super(context, EntityModelLayers.TNT_MINECART);
		this.tntBlockRenderManager = context.getBlockRenderManager();
	}

	protected void renderBlock(
		TntMinecartEntityRenderState tntMinecartEntityRenderState,
		BlockState blockState,
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i
	) {
		float f = tntMinecartEntityRenderState.fuseTicks;
		if (f > -1.0F && f < 10.0F) {
			float g = 1.0F - f / 10.0F;
			g = MathHelper.clamp(g, 0.0F, 1.0F);
			g *= g;
			g *= g;
			float h = 1.0F + g * 0.3F;
			matrixStack.scale(h, h, h);
		}

		renderFlashingBlock(this.tntBlockRenderManager, blockState, matrixStack, vertexConsumerProvider, i, f > -1.0F && (int)f / 5 % 2 == 0);
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

	public TntMinecartEntityRenderState createRenderState() {
		return new TntMinecartEntityRenderState();
	}

	public void updateRenderState(TntMinecartEntity tntMinecartEntity, TntMinecartEntityRenderState tntMinecartEntityRenderState, float f) {
		super.updateRenderState(tntMinecartEntity, tntMinecartEntityRenderState, f);
		tntMinecartEntityRenderState.fuseTicks = tntMinecartEntity.getFuseTicks() > -1 ? (float)tntMinecartEntity.getFuseTicks() - f + 1.0F : -1.0F;
	}
}
