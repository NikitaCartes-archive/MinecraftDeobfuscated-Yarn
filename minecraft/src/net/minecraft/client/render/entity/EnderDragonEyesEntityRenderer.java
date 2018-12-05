package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EnderDragonEyesEntityRenderer implements LayerEntityRenderer<EnderDragonEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/enderdragon/dragon_eyes.png");
	private final EnderDragonEntityRenderer field_4856;

	public EnderDragonEyesEntityRenderer(EnderDragonEntityRenderer enderDragonEntityRenderer) {
		this.field_4856 = enderDragonEntityRenderer;
	}

	public void render(EnderDragonEntity enderDragonEntity, float f, float g, float h, float i, float j, float k, float l) {
		this.field_4856.bindTexture(SKIN);
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFunc(GlStateManager.SrcBlendFactor.ONE, GlStateManager.DstBlendFactor.ONE);
		GlStateManager.disableLighting();
		GlStateManager.depthFunc(514);
		int m = 61680;
		int n = 61680;
		int o = 0;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 61680.0F, 0.0F);
		GlStateManager.enableLighting();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().worldRenderer.method_3201(true);
		this.field_4856.method_4038().render(enderDragonEntity, f, g, i, j, k, l);
		MinecraftClient.getInstance().worldRenderer.method_3201(false);
		this.field_4856.method_4070(enderDragonEntity);
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
		GlStateManager.depthFunc(515);
	}

	@Override
	public boolean shouldMergeTextures() {
		return false;
	}
}
