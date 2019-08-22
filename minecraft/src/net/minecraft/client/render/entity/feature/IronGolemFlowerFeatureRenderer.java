package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.passive.IronGolemEntity;

@Environment(EnvType.CLIENT)
public class IronGolemFlowerFeatureRenderer extends FeatureRenderer<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> {
	public IronGolemFlowerFeatureRenderer(FeatureRendererContext<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4188(IronGolemEntity ironGolemEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (ironGolemEntity.method_6502() != 0) {
			RenderSystem.enableRescaleNormal();
			RenderSystem.pushMatrix();
			RenderSystem.rotatef(5.0F + 180.0F * this.getModel().method_2809().pitch / (float) Math.PI, 1.0F, 0.0F, 0.0F);
			RenderSystem.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
			RenderSystem.translatef(-0.9375F, -0.625F, -0.9375F);
			float m = 0.5F;
			RenderSystem.scalef(0.5F, -0.5F, 0.5F);
			int n = ironGolemEntity.getLightmapCoordinates();
			int o = n % 65536;
			int p = n / 65536;
			RenderSystem.glMultiTexCoord2f(33985, (float)o, (float)p);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(Blocks.POPPY.getDefaultState(), 1.0F);
			RenderSystem.popMatrix();
			RenderSystem.disableRescaleNormal();
		}
	}

	@Override
	public boolean hasHurtOverlay() {
		return false;
	}
}
