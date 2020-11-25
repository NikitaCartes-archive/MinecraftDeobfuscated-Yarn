package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.TropicalFishColorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LargeTropicalFishEntityModel;
import net.minecraft.client.render.entity.model.SmallTropicalFishEntityModel;
import net.minecraft.client.render.entity.model.TintableCompositeModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class TropicalFishEntityRenderer extends MobEntityRenderer<TropicalFishEntity, TintableCompositeModel<TropicalFishEntity>> {
	private final TintableCompositeModel<TropicalFishEntity> smallModel = this.getModel();
	private final TintableCompositeModel<TropicalFishEntity> largeModel;

	public TropicalFishEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new SmallTropicalFishEntityModel<>(context.getPart(EntityModelLayers.TROPICAL_FISH_SMALL)), 0.15F);
		this.largeModel = new LargeTropicalFishEntityModel<>(context.getPart(EntityModelLayers.TROPICAL_FISH_LARGE));
		this.addFeature(new TropicalFishColorFeatureRenderer(this, context.getModelLoader()));
	}

	public Identifier getTexture(TropicalFishEntity tropicalFishEntity) {
		return tropicalFishEntity.getShapeId();
	}

	public void render(TropicalFishEntity tropicalFishEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		TintableCompositeModel<TropicalFishEntity> tintableCompositeModel = tropicalFishEntity.getShape() == 0 ? this.smallModel : this.largeModel;
		this.model = tintableCompositeModel;
		float[] fs = tropicalFishEntity.getBaseColorComponents();
		tintableCompositeModel.setColorMultiplier(fs[0], fs[1], fs[2]);
		super.render(tropicalFishEntity, f, g, matrixStack, vertexConsumerProvider, i);
		tintableCompositeModel.setColorMultiplier(1.0F, 1.0F, 1.0F);
	}

	protected void setupTransforms(TropicalFishEntity tropicalFishEntity, MatrixStack matrixStack, float f, float g, float h) {
		super.setupTransforms(tropicalFishEntity, matrixStack, f, g, h);
		float i = 4.3F * MathHelper.sin(0.6F * f);
		matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(i));
		if (!tropicalFishEntity.isTouchingWater()) {
			matrixStack.translate(0.2F, 0.1F, 0.0);
			matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
		}
	}
}
