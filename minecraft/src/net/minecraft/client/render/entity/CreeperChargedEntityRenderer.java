package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.CreeperEntityModel;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CreeperChargedEntityRenderer implements LayerEntityRenderer<CreeperEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/creeper/creeper_armor.png");
	private final CreeperEntityRenderer field_4843;
	private final CreeperEntityModel field_4844 = new CreeperEntityModel(2.0F);

	public CreeperChargedEntityRenderer(CreeperEntityRenderer creeperEntityRenderer) {
		this.field_4843 = creeperEntityRenderer;
	}

	public void render(CreeperEntity creeperEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (creeperEntity.isCharged()) {
			boolean bl = creeperEntity.isInvisible();
			GlStateManager.depthMask(!bl);
			this.field_4843.bindTexture(SKIN);
			GlStateManager.matrixMode(5890);
			GlStateManager.loadIdentity();
			float m = (float)creeperEntity.age + h;
			GlStateManager.translatef(m * 0.01F, m * 0.01F, 0.0F);
			GlStateManager.matrixMode(5888);
			GlStateManager.enableBlend();
			float n = 0.5F;
			GlStateManager.color4f(0.5F, 0.5F, 0.5F, 1.0F);
			GlStateManager.disableLighting();
			GlStateManager.blendFunc(GlStateManager.SrcBlendFactor.ONE, GlStateManager.DstBlendFactor.ONE);
			this.field_4844.setAttributes(this.field_4843.method_4038());
			MinecraftClient.getInstance().worldRenderer.method_3201(true);
			this.field_4844.render(creeperEntity, f, g, i, j, k, l);
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
