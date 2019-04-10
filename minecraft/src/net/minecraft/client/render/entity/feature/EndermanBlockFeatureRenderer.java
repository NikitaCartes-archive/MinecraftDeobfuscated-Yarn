package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.mob.EndermanEntity;

@Environment(EnvType.CLIENT)
public class EndermanBlockFeatureRenderer extends FeatureRenderer<EndermanEntity, EndermanEntityModel<EndermanEntity>> {
	public EndermanBlockFeatureRenderer(FeatureRendererContext<EndermanEntity, EndermanEntityModel<EndermanEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4179(EndermanEntity endermanEntity, float f, float g, float h, float i, float j, float k, float l) {
		BlockState blockState = endermanEntity.getCarriedBlock();
		if (blockState != null) {
			GlStateManager.enableRescaleNormal();
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, 0.6875F, -0.75F);
			GlStateManager.rotatef(20.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(45.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.translatef(0.25F, 0.1875F, 0.25F);
			float m = 0.5F;
			GlStateManager.scalef(-0.5F, -0.5F, 0.5F);
			int n = endermanEntity.getLightmapCoordinates();
			int o = n % 65536;
			int p = n / 65536;
			GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)o, (float)p);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(blockState, 1.0F);
			GlStateManager.popMatrix();
			GlStateManager.disableRescaleNormal();
		}
	}

	@Override
	public boolean hasHurtOverlay() {
		return false;
	}
}
