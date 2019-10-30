package net.minecraft.client.render.entity;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class FallingBlockEntityRenderer extends EntityRenderer<FallingBlockEntity> {
	public FallingBlockEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.field_4673 = 0.5F;
	}

	public void method_3965(
		FallingBlockEntity fallingBlockEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider
	) {
		BlockState blockState = fallingBlockEntity.getBlockState();
		if (blockState.getRenderType() == BlockRenderType.MODEL) {
			World world = fallingBlockEntity.getWorldClient();
			if (blockState != world.getBlockState(new BlockPos(fallingBlockEntity)) && blockState.getRenderType() != BlockRenderType.INVISIBLE) {
				matrixStack.push();
				BlockPos blockPos = new BlockPos(fallingBlockEntity.getX(), fallingBlockEntity.getBoundingBox().y2, fallingBlockEntity.getZ());
				matrixStack.translate(-0.5, 0.0, -0.5);
				BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
				blockRenderManager.getModelRenderer()
					.render(
						world,
						blockRenderManager.getModel(blockState),
						blockState,
						blockPos,
						matrixStack,
						vertexConsumerProvider.getBuffer(RenderLayers.getBlockLayer(blockState)),
						false,
						new Random(),
						blockState.getRenderingSeed(fallingBlockEntity.getFallingBlockPos()),
						OverlayTexture.DEFAULT_UV
					);
				matrixStack.pop();
				super.render(fallingBlockEntity, d, e, f, g, h, matrixStack, vertexConsumerProvider);
			}
		}
	}

	public Identifier method_3964(FallingBlockEntity fallingBlockEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}
