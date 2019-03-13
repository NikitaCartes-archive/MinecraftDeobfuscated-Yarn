package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CreeperChargeFeatureRenderer extends FeatureRenderer<CreeperEntity, CreeperEntityModel<CreeperEntity>> {
	private static final Identifier field_4842 = new Identifier("textures/entity/creeper/creeper_armor.png");
	private final CreeperEntityModel<CreeperEntity> field_4844 = new CreeperEntityModel<>(2.0F);

	public CreeperChargeFeatureRenderer(FeatureRendererContext<CreeperEntity, CreeperEntityModel<CreeperEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4178(CreeperEntity creeperEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (creeperEntity.isCharged()) {
			boolean bl = creeperEntity.isInvisible();
			GlStateManager.depthMask(!bl);
			this.method_17164(field_4842);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			float m = (float)creeperEntity.age + h;
			GlStateManager.translatef(m * 0.01F, m * 0.01F, 0.0F);
			GlStateManager.matrixMode(5888);
			GlStateManager.enableBlend();
			float n = 0.5F;
			GlStateManager.color4f(0.5F, 0.5F, 0.5F, 1.0F);
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
			this.getModel().method_17081(this.field_4844);
			GameRenderer gameRenderer = MinecraftClient.getInstance().field_1773;
			gameRenderer.method_3201(true);
			this.field_4844.render(creeperEntity, f, g, i, j, k, l);
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
