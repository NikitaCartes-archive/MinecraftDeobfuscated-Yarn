package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.entity.mob.SlimeEntity;

@Environment(EnvType.CLIENT)
public class SlimeOverlayEntityRenderer implements LayerEntityRenderer<SlimeEntity> {
	private final SlimeEntityRenderer renderer;
	private final Model model = new SlimeEntityModel(0);

	public SlimeOverlayEntityRenderer(SlimeEntityRenderer slimeEntityRenderer) {
		this.renderer = slimeEntityRenderer;
	}

	public void render(SlimeEntity slimeEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (!slimeEntity.isInvisible()) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableNormalize();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SrcBlendFactor.SRC_ALPHA, GlStateManager.DstBlendFactor.ONE_MINUS_SRC_ALPHA);
			this.model.setAttributes(this.renderer.method_4038());
			this.model.render(slimeEntity, f, g, i, j, k, l);
			GlStateManager.disableBlend();
			GlStateManager.disableNormalize();
		}
	}

	@Override
	public boolean shouldMergeTextures() {
		return true;
	}
}
