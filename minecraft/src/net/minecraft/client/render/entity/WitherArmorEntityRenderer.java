package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WitherArmorEntityRenderer implements LayerEntityRenderer<EntityWither> {
	private static final Identifier SKIN = new Identifier("textures/entity/wither/wither_armor.png");
	private final WitherEntityRenderer field_4911;
	private final WitherEntityModel field_4909 = new WitherEntityModel(0.5F);

	public WitherArmorEntityRenderer(WitherEntityRenderer witherEntityRenderer) {
		this.field_4911 = witherEntityRenderer;
	}

	public void render(EntityWither entityWither, float f, float g, float h, float i, float j, float k, float l) {
		if (entityWither.isAtHalfHealth()) {
			GlStateManager.depthMask(!entityWither.isInvisible());
			this.field_4911.bindTexture(SKIN);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			float m = (float)entityWither.age + h;
			float n = MathHelper.cos(m * 0.02F) * 3.0F;
			float o = m * 0.01F;
			GlStateManager.translatef(n, o, 0.0F);
			GlStateManager.matrixMode(5888);
			GlStateManager.enableBlend();
			float p = 0.5F;
			GlStateManager.color4f(0.5F, 0.5F, 0.5F, 1.0F);
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(GlStateManager.SrcBlendFactor.ONE, GlStateManager.DstBlendFactor.ONE);
			this.field_4909.animateModel(entityWither, f, g, h);
			this.field_4909.setAttributes(this.field_4911.method_4038());
			MinecraftClient.getInstance().worldRenderer.method_3201(true);
			this.field_4909.render(entityWither, f, g, i, j, k, l);
			MinecraftClient.getInstance().worldRenderer.method_3201(false);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			GlStateManager.matrixMode(5888);
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
		}
	}

	@Override
	public boolean shouldMergeTextures() {
		return false;
	}
}
