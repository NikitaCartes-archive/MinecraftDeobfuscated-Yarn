package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.SlimeEntityModel;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class SlimeOverlayFeatureRenderer<T extends Entity> extends FeatureRenderer<T, SlimeEntityModel<T>> {
	private final EntityModel<T> model = new SlimeEntityModel<>(0);

	public SlimeOverlayFeatureRenderer(FeatureRendererContext<T, SlimeEntityModel<T>> context) {
		super(context);
	}

	@Override
	public void render(T entity, float f, float g, float h, float i, float j, float k, float l) {
		if (!entity.isInvisible()) {
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableNormalize();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			this.getContextModel().copyStateTo(this.model);
			this.model.render(entity, f, g, i, j, k, l);
			GlStateManager.disableBlend();
			GlStateManager.disableNormalize();
		}
	}

	@Override
	public boolean hasHurtOverlay() {
		return true;
	}
}
