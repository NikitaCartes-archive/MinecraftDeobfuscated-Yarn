package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.state.FallingBlockEntityRenderState;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

@Environment(EnvType.CLIENT)
public class FallingBlockEntityRenderer extends EntityRenderer<FallingBlockEntity, FallingBlockEntityRenderState> {
	private final BlockRenderManager blockRenderManager;

	public FallingBlockEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.shadowRadius = 0.5F;
		this.blockRenderManager = context.getBlockRenderManager();
	}

	public boolean shouldRender(FallingBlockEntity fallingBlockEntity, Frustum frustum, double d, double e, double f) {
		return !super.shouldRender(fallingBlockEntity, frustum, d, e, f)
			? false
			: fallingBlockEntity.getBlockState() != fallingBlockEntity.getWorld().getBlockState(fallingBlockEntity.getBlockPos());
	}

	public void render(FallingBlockEntityRenderState fallingBlockEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		BlockState blockState = fallingBlockEntityRenderState.blockState;
		if (blockState.getRenderType() == BlockRenderType.MODEL) {
			matrixStack.push();
			matrixStack.translate(-0.5, 0.0, -0.5);
			this.blockRenderManager
				.getModelRenderer()
				.render(
					fallingBlockEntityRenderState,
					this.blockRenderManager.getModel(blockState),
					blockState,
					fallingBlockEntityRenderState.currentPos,
					matrixStack,
					vertexConsumerProvider.getBuffer(RenderLayers.getMovingBlockLayer(blockState)),
					false,
					Random.create(),
					blockState.getRenderingSeed(fallingBlockEntityRenderState.fallingBlockPos),
					OverlayTexture.DEFAULT_UV
				);
			matrixStack.pop();
			super.render(fallingBlockEntityRenderState, matrixStack, vertexConsumerProvider, i);
		}
	}

	public Identifier getTexture(FallingBlockEntityRenderState fallingBlockEntityRenderState) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}

	public FallingBlockEntityRenderState getRenderState() {
		return new FallingBlockEntityRenderState();
	}

	public void updateRenderState(FallingBlockEntity fallingBlockEntity, FallingBlockEntityRenderState fallingBlockEntityRenderState, float f) {
		super.updateRenderState(fallingBlockEntity, fallingBlockEntityRenderState, f);
		BlockPos blockPos = BlockPos.ofFloored(fallingBlockEntity.getX(), fallingBlockEntity.getBoundingBox().maxY, fallingBlockEntity.getZ());
		fallingBlockEntityRenderState.fallingBlockPos = fallingBlockEntity.getFallingBlockPos();
		fallingBlockEntityRenderState.currentPos = blockPos;
		fallingBlockEntityRenderState.blockState = fallingBlockEntity.getBlockState();
		fallingBlockEntityRenderState.biome = fallingBlockEntity.getWorld().getBiome(blockPos);
		fallingBlockEntityRenderState.world = fallingBlockEntity.getWorld();
	}
}
