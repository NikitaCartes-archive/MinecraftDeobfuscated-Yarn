package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.entity.model.DragonEntityModel;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EnderDragonEyesFeatureRenderer extends FeatureRenderer<EnderDragonEntity, DragonEntityModel> {
	private static final Identifier SKIN = new Identifier("textures/entity/enderdragon/dragon_eyes.png");

	public EnderDragonEyesFeatureRenderer(FeatureRendererContext<EnderDragonEntity, DragonEntityModel> featureRendererContext) {
		super(featureRendererContext);
	}

	public void render(EnderDragonEntity enderDragonEntity, float f, float g, float h, float i, float j, float k, float l) {
		this.bindTexture(SKIN);
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFunc(GlStateManager.class_1033.ONE, GlStateManager.class_1027.ONE);
		GlStateManager.disableLighting();
		GlStateManager.depthFunc(514);
		int m = 61680;
		int n = 61680;
		int o = 0;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 61680.0F, 0.0F);
		GlStateManager.enableLighting();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
		gameRenderer.method_3201(true);
		this.getModel().method_17137(enderDragonEntity, f, g, i, j, k, l);
		gameRenderer.method_3201(false);
		this.applyLightmapCoordinates(enderDragonEntity);
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
		GlStateManager.depthFunc(515);
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
