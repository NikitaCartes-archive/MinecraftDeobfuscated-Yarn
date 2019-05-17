package net.minecraft.client.render.entity.feature;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelA;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelB;
import net.minecraft.entity.passive.TropicalFishEntity;

@Environment(EnvType.CLIENT)
public class TropicalFishSomethingFeatureRenderer extends FeatureRenderer<TropicalFishEntity, EntityModel<TropicalFishEntity>> {
	private final TropicalFishEntityModelA<TropicalFishEntity> modelA = new TropicalFishEntityModelA<>(0.008F);
	private final TropicalFishEntityModelB<TropicalFishEntity> modelB = new TropicalFishEntityModelB<>(0.008F);

	public TropicalFishSomethingFeatureRenderer(FeatureRendererContext<TropicalFishEntity, EntityModel<TropicalFishEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4205(TropicalFishEntity tropicalFishEntity, float f, float g, float h, float i, float j, float k, float l) {
		if (!tropicalFishEntity.isInvisible()) {
			EntityModel<TropicalFishEntity> entityModel = (EntityModel<TropicalFishEntity>)(tropicalFishEntity.getShape() == 0 ? this.modelA : this.modelB);
			this.bindTexture(tropicalFishEntity.getVarietyId());
			float[] fs = tropicalFishEntity.getPatternColorComponents();
			GlStateManager.color3f(fs[0], fs[1], fs[2]);
			this.getModel().copyStateTo(entityModel);
			entityModel.animateModel(tropicalFishEntity, f, g, h);
			entityModel.render(tropicalFishEntity, f, g, i, j, k, l);
		}
	}

	@Override
	public boolean hasHurtOverlay() {
		return true;
	}
}
