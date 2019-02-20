package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
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
		if (!mooshroomEntity.isChild() && !mooshroomEntity.isInvisible()) {
			BlockState blockState = mooshroomEntity.method_18435().method_18437();
			this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			GlStateManager.enableCull();
			GlStateManager.cullFace(GlStateManager.FaceSides.field_5068);
			GlStateManager.pushMatrix();
			GlStateManager.scalef(1.0F, -1.0F, 1.0F);
			GlStateManager.translatef(0.2F, 0.35F, 0.5F);
			GlStateManager.rotatef(42.0F, 0.0F, 1.0F, 0.0F);
			BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
			GlStateManager.pushMatrix();
			GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
			blockRenderManager.renderDynamic(blockState, 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.1F, 0.0F, -0.6F);
			GlStateManager.rotatef(42.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
			blockRenderManager.renderDynamic(blockState, 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			this.getModel().method_2800().method_2847(0.0625F);
			GlStateManager.scalef(1.0F, -1.0F, 1.0F);
			GlStateManager.translatef(0.0F, 0.7F, -0.2F);
			GlStateManager.rotatef(12.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translatef(-0.5F, -0.5F, 0.5F);
			blockRenderManager.renderDynamic(blockState, 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.cullFace(GlStateManager.FaceSides.field_5070);
			GlStateManager.disableCull();
		}
	}

	@Override
	public boolean method_4200() {
		return true;
	}
}
