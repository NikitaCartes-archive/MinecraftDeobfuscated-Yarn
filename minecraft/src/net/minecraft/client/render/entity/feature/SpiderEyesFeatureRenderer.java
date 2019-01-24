package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
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
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
		if (entity.isInvisible()) {
			GlStateManager.depthMask(false);
		} else {
			GlStateManager.depthMask(true);
		}

		int m = 61680;
		int n = m % 65536;
		int o = m / 65536;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)n, (float)o);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
		gameRenderer.method_3201(true);
		this.getModel().render(entity, f, g, i, j, k, l);
		gameRenderer.method_3201(false);
		m = entity.getLightmapCoordinates();
		n = m % 65536;
		o = m / 65536;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)n, (float)o);
		this.applyLightmapCoordinates(entity);
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
