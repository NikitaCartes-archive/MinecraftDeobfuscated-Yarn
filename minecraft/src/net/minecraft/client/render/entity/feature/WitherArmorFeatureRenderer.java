package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.entity.model.WitherEntityModel;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WitherArmorFeatureRenderer extends FeatureRenderer<WitherEntity, WitherEntityModel<WitherEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/wither/wither_armor.png");
	private final WitherEntityModel<WitherEntity> field_4909 = new WitherEntityModel<>(0.5F);

	public WitherArmorFeatureRenderer(FeatureRendererContext<WitherEntity, WitherEntityModel<WitherEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4207(WitherEntity witherEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (witherEntity.isAtHalfHealth()) {
			GlStateManager.depthMask(!witherEntity.isInvisible());
			this.bindTexture(SKIN);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			float m = (float)witherEntity.age + h;
			float n = MathHelper.cos(m * 0.02F) * 3.0F;
			float o = m * 0.01F;
			GlStateManager.translatef(n, o, 0.0F);
			GlStateManager.matrixMode(5888);
			GlStateManager.enableBlend();
			float p = 0.5F;
			GlStateManager.color4f(0.5F, 0.5F, 0.5F, 1.0F);
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
			this.field_4909.method_17128(witherEntity, f, g, h);
			this.getModel().method_17081(this.field_4909);
			GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
			gameRenderer.method_3201(true);
			this.field_4909.method_17129(witherEntity, f, g, i, j, k, l);
			gameRenderer.method_3201(false);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			GlStateManager.matrixMode(5888);
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
		}
	}

	@Override
	public boolean method_4200() {
		return false;
	}
}
