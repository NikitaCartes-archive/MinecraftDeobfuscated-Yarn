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
	private final WitherEntityModel<WitherEntity> model = new WitherEntityModel<>(0.5F);

	public WitherArmorFeatureRenderer(FeatureRendererContext<WitherEntity, WitherEntityModel<WitherEntity>> context) {
		super(context);
	}

	public void render(WitherEntity witherEntity, float f, float g, float h, float i, float j, float k, float l) {
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
			this.model.animateModel(witherEntity, f, g, h);
			this.getContextModel().copyStateTo(this.model);
			GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
			gameRenderer.setFogBlack(true);
			this.model.render(witherEntity, f, g, i, j, k, l);
			gameRenderer.setFogBlack(false);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			GlStateManager.matrixMode(5888);
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
		}
	}

	@Override
	public boolean hasHurtOverlay() {
		return false;
	}
}
