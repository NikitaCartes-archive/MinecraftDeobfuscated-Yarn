package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.entity.model.PhantomEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PhantomEyesFeatureRenderer<T extends Entity> extends FeatureRenderer<T, PhantomEntityModel<T>> {
	private static final Identifier SKIN = new Identifier("textures/entity/phantom_eyes.png");

	public PhantomEyesFeatureRenderer(FeatureRendererContext<T, PhantomEntityModel<T>> featureRendererContext) {
		super(featureRendererContext);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k, float l) {
		this.bindTexture(SKIN);
		RenderSystem.enableBlend();
		RenderSystem.disableAlphaTest();
		RenderSystem.blendFunc(GlStateManager.class_4535.ONE, GlStateManager.class_4534.ONE);
		RenderSystem.disableLighting();
		RenderSystem.depthMask(!entity.isInvisible());
		int m = 61680;
		int n = 61680;
		int o = 0;
		RenderSystem.glMultiTexCoord2f(33985, 61680.0F, 0.0F);
		RenderSystem.enableLighting();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		BackgroundRenderer.setFogBlack(true);
		this.getModel().render(entity, f, g, i, j, k, l);
		BackgroundRenderer.setFogBlack(false);
		this.applyLightmapCoordinates(entity);
		RenderSystem.depthMask(true);
		RenderSystem.disableBlend();
		RenderSystem.enableAlphaTest();
	}

	@Override
	public boolean hasHurtOverlay() {
		return false;
	}
}
