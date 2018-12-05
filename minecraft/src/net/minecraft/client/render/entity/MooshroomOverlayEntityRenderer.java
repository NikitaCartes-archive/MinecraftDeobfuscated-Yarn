package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.passive.MooshroomEntity;

@Environment(EnvType.CLIENT)
public class MooshroomOverlayEntityRenderer implements LayerEntityRenderer<MooshroomEntity> {
	private final MooshroomEntityRenderer field_4885;

	public MooshroomOverlayEntityRenderer(MooshroomEntityRenderer mooshroomEntityRenderer) {
		this.field_4885 = mooshroomEntityRenderer;
	}

	public void render(MooshroomEntity mooshroomEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (!mooshroomEntity.isChild() && !mooshroomEntity.isInvisible()) {
			BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
			this.field_4885.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			GlStateManager.enableCull();
			GlStateManager.cullFace(GlStateManager.FaceSides.FRONT);
			GlStateManager.pushMatrix();
			GlStateManager.scalef(1.0F, -1.0F, 1.0F);
			GlStateManager.translatef(0.2F, 0.35F, 0.5F);
			GlStateManager.rotatef(42.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.pushMatrix();
			GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
			blockRenderManager.render(Blocks.field_10559.getDefaultState(), 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.1F, 0.0F, -0.6F);
			GlStateManager.rotatef(42.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
			blockRenderManager.render(Blocks.field_10559.getDefaultState(), 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			this.field_4885.method_4067().method_2800().method_2847(0.0625F);
			GlStateManager.scalef(1.0F, -1.0F, 1.0F);
			GlStateManager.translatef(0.0F, 0.7F, -0.2F);
			GlStateManager.rotatef(12.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
			blockRenderManager.render(Blocks.field_10559.getDefaultState(), 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.cullFace(GlStateManager.FaceSides.BACK);
			GlStateManager.disableCull();
		}
	}

	@Override
	public boolean shouldMergeTextures() {
		return true;
	}
}
