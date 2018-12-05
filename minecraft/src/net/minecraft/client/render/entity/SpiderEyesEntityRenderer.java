package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SpiderEyesEntityRenderer<T extends SpiderEntity> implements LayerEntityRenderer<T> {
	private static final Identifier SKIN = new Identifier("textures/entity/spider_eyes.png");
	private final SpiderEntityRenderer<T> field_4901;

	public SpiderEyesEntityRenderer(SpiderEntityRenderer<T> spiderEntityRenderer) {
		this.field_4901 = spiderEntityRenderer;
	}

	public void render(T spiderEntity, float f, float g, float h, float i, float j, float k, float l) {
		this.field_4901.bindTexture(SKIN);
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFunc(GlStateManager.SrcBlendFactor.ONE, GlStateManager.DstBlendFactor.ONE);
		if (spiderEntity.isInvisible()) {
			GlStateManager.depthMask(false);
		} else {
			GlStateManager.depthMask(true);
		}

		int m = 61680;
		int n = m % 65536;
		int o = m / 65536;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)n, (float)o);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().worldRenderer.method_3201(true);
		this.field_4901.method_4038().render(spiderEntity, f, g, i, j, k, l);
		MinecraftClient.getInstance().worldRenderer.method_3201(false);
		m = spiderEntity.getLightmapCoordinates();
		n = m % 65536;
		o = m / 65536;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, (float)n, (float)o);
		this.field_4901.method_4070(spiderEntity);
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
	}

	@Override
	public boolean shouldMergeTextures() {
		return false;
	}
}
