package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CreeperChargeFeatureRenderer extends FeatureRenderer<CreeperEntity, CreeperEntityModel<CreeperEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/creeper/creeper_armor.png");
	private final CreeperEntityModel<CreeperEntity> model = new CreeperEntityModel<>(2.0F);

	public CreeperChargeFeatureRenderer(FeatureRendererContext<CreeperEntity, CreeperEntityModel<CreeperEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4178(CreeperEntity creeperEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (creeperEntity.isCharged()) {
			boolean bl = creeperEntity.isInvisible();
			RenderSystem.depthMask(!bl);
			this.bindTexture(SKIN);
			RenderSystem.matrixMode(5890);
			RenderSystem.loadIdentity();
			float m = (float)creeperEntity.age + h;
			RenderSystem.translatef(m * 0.01F, m * 0.01F, 0.0F);
			RenderSystem.matrixMode(5888);
			RenderSystem.enableBlend();
			float n = 0.5F;
			RenderSystem.color4f(0.5F, 0.5F, 0.5F, 1.0F);
			RenderSystem.disableLighting();
			RenderSystem.blendFunc(GlStateManager.class_4535.ONE, GlStateManager.class_4534.ONE);
			this.getModel().copyStateTo(this.model);
			GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
			gameRenderer.setFogBlack(true);
			this.model.render(creeperEntity, f, g, i, j, k, l);
			gameRenderer.setFogBlack(false);
			RenderSystem.matrixMode(5890);
			RenderSystem.loadIdentity();
			RenderSystem.matrixMode(5888);
			RenderSystem.enableLighting();
			RenderSystem.disableBlend();
			RenderSystem.depthMask(true);
		}
	}

	@Override
	public boolean hasHurtOverlay() {
		return false;
	}
}
