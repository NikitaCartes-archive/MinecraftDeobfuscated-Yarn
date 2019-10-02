package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelA;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelB;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class TropicalFishSomethingFeatureRenderer extends FeatureRenderer<TropicalFishEntity, EntityModel<TropicalFishEntity>> {
	private final TropicalFishEntityModelA<TropicalFishEntity> modelA = new TropicalFishEntityModelA<>(0.008F);
	private final TropicalFishEntityModelB<TropicalFishEntity> modelB = new TropicalFishEntityModelB<>(0.008F);

	public TropicalFishSomethingFeatureRenderer(FeatureRendererContext<TropicalFishEntity, EntityModel<TropicalFishEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4205(
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		TropicalFishEntity tropicalFishEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l,
		float m
	) {
		EntityModel<TropicalFishEntity> entityModel = (EntityModel<TropicalFishEntity>)(tropicalFishEntity.getShape() == 0 ? this.modelA : this.modelB);
		float[] fs = tropicalFishEntity.getPatternColorComponents();
		method_23196(
			this.getModel(),
			entityModel,
			tropicalFishEntity.getVarietyId(),
			matrixStack,
			layeredVertexConsumerStorage,
			i,
			tropicalFishEntity,
			f,
			g,
			j,
			k,
			l,
			m,
			h,
			fs[0],
			fs[1],
			fs[2]
		);
	}
}
