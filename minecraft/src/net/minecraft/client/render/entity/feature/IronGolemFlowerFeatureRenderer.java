package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
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

	public void render(IronGolemEntity ironGolemEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (ironGolemEntity.method_6502() != 0) {
			GlStateManager.enableRescaleNormal();
			GlStateManager.pushMatrix();
			GlStateManager.rotatef(5.0F + 180.0F * this.getModel().method_2809().pitch / (float) Math.PI, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.translatef(-0.9375F, -0.625F, -0.9375F);
			float m = 0.5F;
			GlStateManager.scalef(0.5F, -0.5F, 0.5F);
			int n = ironGolemEntity.getLightmapCoordinates();
			int o = n % 65536;
			int p = n / 65536;
			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)o, (float)p);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(Blocks.field_10449.getDefaultState(), 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.disableRescaleNormal();
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
