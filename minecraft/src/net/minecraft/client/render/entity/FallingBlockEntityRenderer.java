package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class FallingBlockEntityRenderer extends EntityRenderer<FallingBlockEntity> {
	private final BlockRenderManager blockRenderManager;

	public FallingBlockEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.shadowRadius = 0.5F;
		this.blockRenderManager = context.getBlockRenderManager();
	}

	public void render(FallingBlockEntity fallingBlockEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		BlockState blockState = fallingBlockEntity.getBlockState();
		if (blockState.getRenderType() == BlockRenderType.MODEL) {
			World world = fallingBlockEntity.getWorld();
			if (blockState != world.getBlockState(fallingBlockEntity.getBlockPos()) && blockState.getRenderType() != BlockRenderType.INVISIBLE) {
				matrixStack.push();
				BlockPos blockPos = BlockPos.ofFloored(fallingBlockEntity.getX(), fallingBlockEntity.getBoundingBox().maxY, fallingBlockEntity.getZ());
				matrixStack.translate(-0.5, 0.0, -0.5);
				this.blockRenderManager
					.getModelRenderer()
					.render(
						world,
						this.blockRenderManager.getModel(blockState),
						blockState,
						blockPos,
						matrixStack,
						vertexConsumerProvider.getBuffer(RenderLayers.getMovingBlockLayer(blockState)),
						false,
						Random.create(),
						blockState.getRenderingSeed(fallingBlockEntity.getFallingBlockPos()),
						OverlayTexture.DEFAULT_UV
					);
				matrixStack.pop();
				super.render(fallingBlockEntity, f, g, matrixStack, vertexConsumerProvider, i);
			}
		}
	}

	public Identifier getTexture(FallingBlockEntity fallingBlockEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}
}
