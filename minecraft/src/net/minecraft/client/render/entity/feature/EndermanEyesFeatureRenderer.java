package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4493;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class EndermanEyesFeatureRenderer<T extends LivingEntity> extends FeatureRenderer<T, EndermanEntityModel<T>> {
	private static final Identifier SKIN = new Identifier("textures/entity/enderman/enderman_eyes.png");

	public EndermanEyesFeatureRenderer(FeatureRendererContext<T, EndermanEntityModel<T>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4187(T livingEntity, float f, float g, float h, float i, float j, float k, float l) {
		this.bindTexture(SKIN);
		RenderSystem.enableBlend();
		RenderSystem.disableAlphaTest();
		RenderSystem.blendFunc(class_4493.class_4535.ONE, class_4493.class_4534.ONE);
		RenderSystem.disableLighting();
		RenderSystem.depthMask(!livingEntity.isInvisible());
		int m = 61680;
		int n = 61680;
		int o = 0;
		RenderSystem.glMultiTexCoord2f(33985, 61680.0F, 0.0F);
		RenderSystem.enableLighting();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
		gameRenderer.setFogBlack(true);
		this.getModel().method_17088(livingEntity, f, g, i, j, k, l);
		gameRenderer.setFogBlack(false);
		this.applyLightmapCoordinates(livingEntity);
		RenderSystem.depthMask(true);
		RenderSystem.disableBlend();
		RenderSystem.enableAlphaTest();
	}

	@Override
	public boolean hasHurtOverlay() {
		return false;
	}
}
