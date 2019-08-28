package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.passive.MooshroomEntity;

@Environment(EnvType.CLIENT)
public class MooshroomMushroomFeatureRenderer<T extends MooshroomEntity> extends FeatureRenderer<T, CowEntityModel<T>> {
	public MooshroomMushroomFeatureRenderer(FeatureRendererContext<T, CowEntityModel<T>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4195(T mooshroomEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (!mooshroomEntity.isBaby() && !mooshroomEntity.isInvisible()) {
			BlockState blockState = mooshroomEntity.getMooshroomType().getMushroomState();
			this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			RenderSystem.enableCull();
			RenderSystem.cullFace(GlStateManager.FaceSides.FRONT);
			RenderSystem.pushMatrix();
			RenderSystem.scalef(1.0F, -1.0F, 1.0F);
			RenderSystem.translatef(0.2F, 0.35F, 0.5F);
			RenderSystem.rotatef(42.0F, 0.0F, 1.0F, 0.0F);
			BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
			RenderSystem.pushMatrix();
			RenderSystem.translatef(-0.5F, -0.5F, 0.5F);
			blockRenderManager.renderDynamic(blockState, 1.0F);
			RenderSystem.popMatrix();
			RenderSystem.pushMatrix();
			RenderSystem.translatef(0.1F, 0.0F, -0.6F);
			RenderSystem.rotatef(42.0F, 0.0F, 1.0F, 0.0F);
			RenderSystem.translatef(-0.5F, -0.5F, 0.5F);
			blockRenderManager.renderDynamic(blockState, 1.0F);
			RenderSystem.popMatrix();
			RenderSystem.popMatrix();
			RenderSystem.pushMatrix();
			this.getModel().method_2800().applyTransform(0.0625F);
			RenderSystem.scalef(1.0F, -1.0F, 1.0F);
			RenderSystem.translatef(0.0F, 0.7F, -0.2F);
			RenderSystem.rotatef(12.0F, 0.0F, 1.0F, 0.0F);
			RenderSystem.translatef(-0.5F, -0.5F, 0.5F);
			blockRenderManager.renderDynamic(blockState, 1.0F);
			RenderSystem.popMatrix();
			RenderSystem.cullFace(GlStateManager.FaceSides.BACK);
			RenderSystem.disableCull();
		}
	}

	@Override
	public boolean hasHurtOverlay() {
		return true;
	}
}
