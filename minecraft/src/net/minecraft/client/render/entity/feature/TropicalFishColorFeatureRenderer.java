package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.LargeTropicalFishEntityModel;
import net.minecraft.client.render.entity.model.SmallTropicalFishEntityModel;
import net.minecraft.client.render.entity.model.TintableCompositeModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TropicalFishEntity;

@Environment(EnvType.CLIENT)
public class TropicalFishColorFeatureRenderer extends FeatureRenderer<TropicalFishEntity, TintableCompositeModel<TropicalFishEntity>> {
	private final SmallTropicalFishEntityModel<TropicalFishEntity> smallModel;
	private final LargeTropicalFishEntityModel<TropicalFishEntity> largeModel;

	public TropicalFishColorFeatureRenderer(
		FeatureRendererContext<TropicalFishEntity, TintableCompositeModel<TropicalFishEntity>> featureRendererContext, EntityModelLoader entityModelLoader
	) {
		super(featureRendererContext);
		this.smallModel = new SmallTropicalFishEntityModel<>(entityModelLoader.getModelPart(EntityModelLayers.TROPICAL_FISH_SMALL_PATTERN));
		this.largeModel = new LargeTropicalFishEntityModel<>(entityModelLoader.getModelPart(EntityModelLayers.TROPICAL_FISH_LARGE_PATTERN));
	}

	public void render(
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i,
		TropicalFishEntity tropicalFishEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l
	) {
		EntityModel<TropicalFishEntity> entityModel = (EntityModel<TropicalFishEntity>)(tropicalFishEntity.getShape() == 0 ? this.smallModel : this.largeModel);
		float[] fs = tropicalFishEntity.getPatternColorComponents();
		render(
			this.getContextModel(),
			entityModel,
			tropicalFishEntity.getVarietyId(),
			matrixStack,
			vertexConsumerProvider,
			i,
			tropicalFishEntity,
			f,
			g,
			j,
			k,
			l,
			h,
			fs[0],
			fs[1],
			fs[2]
		);
	}
}
