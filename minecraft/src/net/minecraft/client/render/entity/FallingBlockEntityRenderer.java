package net.minecraft.client.render.entity;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
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

	public void method_3965(FallingBlockEntity fallingBlockEntity, double d, double e, double f, float g, float h, class_4587 arg, class_4597 arg2) {
		BlockState blockState = fallingBlockEntity.getBlockState();
		if (blockState.getRenderType() == BlockRenderType.MODEL) {
			World world = fallingBlockEntity.getWorldClient();
			if (blockState != world.getBlockState(new BlockPos(fallingBlockEntity)) && blockState.getRenderType() != BlockRenderType.INVISIBLE) {
				arg.method_22903();
				BlockPos blockPos = new BlockPos(fallingBlockEntity.x, fallingBlockEntity.getBoundingBox().maxY, fallingBlockEntity.z);
				arg.method_22904((double)(-(blockPos.getX() & 15)) - 0.5, (double)(-(blockPos.getY() & 15)), (double)(-(blockPos.getZ() & 15)) - 0.5);
				BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
				blockRenderManager.getModelRenderer()
					.tesselate(
						world,
						blockRenderManager.getModel(blockState),
						blockState,
						blockPos,
						arg,
						arg2.getBuffer(BlockRenderLayer.method_22715(blockState)),
						false,
						new Random(),
						blockState.getRenderingSeed(fallingBlockEntity.getFallingBlockPos())
					);
				arg.method_22909();
				super.render(fallingBlockEntity, d, e, f, g, h, arg, arg2);
			}
		}
	}

	public Identifier method_3964(FallingBlockEntity fallingBlockEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}
