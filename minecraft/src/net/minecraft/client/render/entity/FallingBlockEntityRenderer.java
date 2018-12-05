package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.RenderTypeBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexBuffer;
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
		if (blockState.getRenderType() == RenderTypeBlock.MODEL) {
			World world = fallingBlockEntity.getWorldClient();
			if (blockState != world.getBlockState(new BlockPos(fallingBlockEntity)) && blockState.getRenderType() != RenderTypeBlock.NONE) {
				this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
				GlStateManager.pushMatrix();
				GlStateManager.disableLighting();
				Tessellator tessellator = Tessellator.getInstance();
				VertexBuffer vertexBuffer = tessellator.getVertexBuffer();
				if (this.field_4674) {
					GlStateManager.enableColorMaterial();
					GlStateManager.setupSolidRenderingTextureCombine(this.method_3929(fallingBlockEntity));
				}

				vertexBuffer.begin(7, VertexFormats.field_1582);
				BlockPos blockPos = new BlockPos(fallingBlockEntity.x, fallingBlockEntity.getBoundingBox().maxY, fallingBlockEntity.z);
				GlStateManager.translatef((float)(d - (double)blockPos.getX() - 0.5), (float)(e - (double)blockPos.getY()), (float)(f - (double)blockPos.getZ() - 0.5));
				BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
				blockRenderManager.getRenderer()
					.tesselate(
						world,
						blockRenderManager.getModel(blockState),
						blockState,
						blockPos,
						vertexBuffer,
						false,
						new Random(),
						blockState.getPosRandom(fallingBlockEntity.getFallingBlockPos())
					);
				tessellator.draw();
				if (this.field_4674) {
					GlStateManager.tearDownSolidRenderingTextureCombine();
					GlStateManager.disableColorMaterial();
				}

				GlStateManager.enableLighting();
				GlStateManager.popMatrix();
				super.method_3936(fallingBlockEntity, d, e, f, g, h);
			}
		}
	}

	protected Identifier getTexture(FallingBlockEntity fallingBlockEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}
}
