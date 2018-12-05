package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LayerEntityRenderer;
import net.minecraft.client.render.entity.PhantomEntityRenderer;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class class_993 implements LayerEntityRenderer<PhantomEntity> {
	private static final Identifier field_4890 = new Identifier("textures/entity/phantom_eyes.png");
	private final PhantomEntityRenderer field_4889;

	public class_993(PhantomEntityRenderer phantomEntityRenderer) {
		this.field_4889 = phantomEntityRenderer;
	}

	public void render(PhantomEntity phantomEntity, float f, float g, float h, float i, float j, float k, float l) {
		this.field_4889.bindTexture(field_4890);
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFunc(GlStateManager.SrcBlendFactor.ONE, GlStateManager.DstBlendFactor.ONE);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(!phantomEntity.isInvisible());
		int m = 61680;
		int n = 61680;
		int o = 0;
		GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 61680.0F, 0.0F);
		GlStateManager.enableLighting();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		MinecraftClient.getInstance().worldRenderer.method_3201(true);
		this.field_4889.method_4038().render(phantomEntity, f, g, i, j, k, l);
		MinecraftClient.getInstance().worldRenderer.method_3201(false);
		this.field_4889.method_4070(phantomEntity);
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
	}

	@Override
	public boolean shouldMergeTextures() {
		return false;
	}
}
