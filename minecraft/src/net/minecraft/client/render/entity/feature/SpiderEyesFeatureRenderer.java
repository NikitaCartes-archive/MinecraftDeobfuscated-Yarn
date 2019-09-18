package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.entity.model.SpiderEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SpiderEyesFeatureRenderer<T extends Entity, M extends SpiderEntityModel<T>> extends FeatureRenderer<T, M> {
	private static final Identifier SKIN = new Identifier("textures/entity/spider_eyes.png");

	public SpiderEyesFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
		super(featureRendererContext);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k, float l) {
		this.bindTexture(SKIN);
		RenderSystem.enableBlend();
		RenderSystem.disableAlphaTest();
		RenderSystem.blendFunc(GlStateManager.class_4535.ONE, GlStateManager.class_4534.ONE);
		if (entity.isInvisible()) {
			RenderSystem.depthMask(false);
		} else {
			RenderSystem.depthMask(true);
		}

		int m = 61680;
		int n = m % 65536;
		int o = m / 65536;
		RenderSystem.glMultiTexCoord2f(33985, (float)n, (float)o);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		BackgroundRenderer.setFogBlack(true);
		this.getModel().render(entity, f, g, i, j, k, l);
		BackgroundRenderer.setFogBlack(false);
		m = entity.getLightmapCoordinates();
		n = m % 65536;
		o = m / 65536;
		RenderSystem.glMultiTexCoord2f(33985, (float)n, (float)o);
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
