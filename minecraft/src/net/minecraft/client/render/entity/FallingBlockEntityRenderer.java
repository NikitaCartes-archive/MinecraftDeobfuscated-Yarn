package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
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

	public void method_3965(FallingBlockEntity fallingBlockEntity, double d, double e, double f, float g, float h) {
		BlockState blockState = fallingBlockEntity.getBlockState();
		if (blockState.getRenderType() == BlockRenderType.MODEL) {
			World world = fallingBlockEntity.getWorldClient();
			if (blockState != world.getBlockState(new BlockPos(fallingBlockEntity)) && blockState.getRenderType() != BlockRenderType.INVISIBLE) {
				this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
				RenderSystem.pushMatrix();
				RenderSystem.disableLighting();
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
				if (this.renderOutlines) {
					RenderSystem.enableColorMaterial();
					RenderSystem.setupSolidRenderingTextureCombine(this.getOutlineColor(fallingBlockEntity));
				}

				bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_UV_NORMAL);
				BlockPos blockPos = new BlockPos(fallingBlockEntity.x, fallingBlockEntity.getBoundingBox().maxY, fallingBlockEntity.z);
				RenderSystem.translatef((float)(d - (double)blockPos.getX() - 0.5), (float)(e - (double)blockPos.getY()), (float)(f - (double)blockPos.getZ() - 0.5));
				BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
				blockRenderManager.getModelRenderer()
					.tesselate(
						world,
						blockRenderManager.getModel(blockState),
						blockState,
						blockPos,
						bufferBuilder,
						false,
						new Random(),
						blockState.getRenderingSeed(fallingBlockEntity.getFallingBlockPos())
					);
				tessellator.draw();
				if (this.renderOutlines) {
					RenderSystem.tearDownSolidRenderingTextureCombine();
					RenderSystem.disableColorMaterial();
				}

				RenderSystem.enableLighting();
				RenderSystem.popMatrix();
				super.render(fallingBlockEntity, d, e, f, g, h);
			}
		}
	}

	protected Identifier method_3964(FallingBlockEntity fallingBlockEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}
