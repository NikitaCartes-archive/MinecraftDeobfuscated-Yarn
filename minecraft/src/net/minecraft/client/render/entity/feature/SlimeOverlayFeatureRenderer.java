package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class SlimeOverlayFeatureRenderer<T extends Entity> extends FeatureRenderer<T, SlimeEntityModel<T>> {
	private final EntityModel<T> model = new SlimeEntityModel<>(0);

	public SlimeOverlayFeatureRenderer(FeatureRendererContext<T, SlimeEntityModel<T>> featureRendererContext) {
		super(featureRendererContext);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k, float l) {
		if (!entity.isInvisible()) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			RenderSystem.enableNormalize();
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA);
			this.getModel().copyStateTo(this.model);
			this.model.render(entity, f, g, i, j, k, l);
			RenderSystem.disableBlend();
			RenderSystem.disableNormalize();
		}
	}

	@Override
	public boolean hasHurtOverlay() {
		return true;
	}
}
