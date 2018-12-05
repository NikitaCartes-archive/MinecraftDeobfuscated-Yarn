package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EndermanEyesEntityRenderer implements LayerEntityRenderer<EndermanEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/enderman/enderman_eyes.png");
	private final EndermanEntityRenderer field_4875;

	public EndermanEyesEntityRenderer(EndermanEntityRenderer endermanEntityRenderer) {
		this.field_4875 = endermanEntityRenderer;
	}

	public void render(EndermanEntity endermanEntity, float f, float g, float h, float i, float j, float k, float l) {
		this.field_4875.bindTexture(SKIN);
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFunc(GlStateManager.SrcBlendFactor.ONE, GlStateManager.DstBlendFactor.ONE);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(!endermanEntity.isInvisible());
		int m = 61680;
		int n = 61680;
		int o = 0;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 61680.0F, 0.0F);
		GlStateManager.enableLighting();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().worldRenderer.method_3201(true);
		this.field_4875.method_3913().render(endermanEntity, f, g, i, j, k, l);
		MinecraftClient.getInstance().worldRenderer.method_3201(false);
		this.field_4875.method_4070(endermanEntity);
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
	}

	@Override
	public boolean shouldMergeTextures() {
		return false;
	}
}
