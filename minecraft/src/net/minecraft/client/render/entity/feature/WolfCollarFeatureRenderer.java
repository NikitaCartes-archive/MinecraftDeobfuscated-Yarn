package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WolfCollarFeatureRenderer extends FeatureRenderer<WolfEntity, WolfEntityModel<WolfEntity>> {
	private static final Identifier SKIN = new Identifier("textures/entity/wolf/wolf_collar.png");

	public WolfCollarFeatureRenderer(FeatureRendererContext<WolfEntity, WolfEntityModel<WolfEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4209(WolfEntity wolfEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (wolfEntity.isTamed() && !wolfEntity.isInvisible()) {
			this.bindTexture(SKIN);
			float[] fs = wolfEntity.getCollarColor().getColorComponents();
			GlStateManager.color3f(fs[0], fs[1], fs[2]);
			this.getModel().method_17132(wolfEntity, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean hasHurtOverlay() {
		return true;
	}
}
