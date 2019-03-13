package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
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
		BlockState blockState = fallingBlockEntity.method_6962();
		if (blockState.getRenderType() == BlockRenderType.field_11458) {
			World world = fallingBlockEntity.method_6966();
			if (blockState != world.method_8320(new BlockPos(fallingBlockEntity)) && blockState.getRenderType() != BlockRenderType.field_11455) {
				this.method_3924(SpriteAtlasTexture.field_5275);
				GlStateManager.pushMatrix();
				GlStateManager.disableLighting();
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
				if (this.renderOutlines) {
					GlStateManager.enableColorMaterial();
					GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(fallingBlockEntity));
				}

				bufferBuilder.method_1328(7, VertexFormats.field_1582);
				BlockPos blockPos = new BlockPos(fallingBlockEntity.x, fallingBlockEntity.method_5829().maxY, fallingBlockEntity.z);
				GlStateManager.translatef((float)(d - (double)blockPos.getX() - 0.5), (float)(e - (double)blockPos.getY()), (float)(f - (double)blockPos.getZ() - 0.5));
				BlockRenderManager blockRenderManager = MinecraftClient.getInstance().method_1541();
				blockRenderManager.method_3350()
					.method_3374(
						world,
						blockRenderManager.method_3349(blockState),
						blockState,
						blockPos,
						bufferBuilder,
						false,
						new Random(),
						blockState.method_11617(fallingBlockEntity.method_6964())
					);
				tessellator.draw();
				if (this.renderOutlines) {
					GlStateManager.tearDownSolidRenderingTextureCombine();
					GlStateManager.disableColorMaterial();
				}

				GlStateManager.enableLighting();
				GlStateManager.popMatrix();
				super.render(fallingBlockEntity, d, e, f, g, h);
			}
		}
	}

	protected Identifier method_3964(FallingBlockEntity fallingBlockEntity) {
		return SpriteAtlasTexture.field_5275;
	}
}
