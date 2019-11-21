package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.TropicalFishSomethingFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.TintableCompositeModel;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelA;
import net.minecraft.client.render.entity.model.TropicalFishEntityModelB;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TropicalFishEntityRenderer extends MobEntityRenderer<TropicalFishEntity, EntityModel<TropicalFishEntity>> {
	private final TropicalFishEntityModelA<TropicalFishEntity> field_4800 = new TropicalFishEntityModelA<>(0.0F);
	private final TropicalFishEntityModelB<TropicalFishEntity> field_4799 = new TropicalFishEntityModelB<>(0.0F);

	public TropicalFishEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new TropicalFishEntityModelA<>(0.0F), 0.15F);
		this.addFeature(new TropicalFishSomethingFeatureRenderer(this));
	}

	public Identifier getTexture(TropicalFishEntity tropicalFishEntity) {
		return tropicalFishEntity.getShapeId();
	}

	public void render(TropicalFishEntity tropicalFishEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		TintableCompositeModel<TropicalFishEntity> tintableCompositeModel = (TintableCompositeModel<TropicalFishEntity>)(tropicalFishEntity.getShape() == 0
			? this.field_4800
			: this.field_4799);
		this.model = tintableCompositeModel;
		float[] fs = tropicalFishEntity.getBaseColorComponents();
		tintableCompositeModel.setColorMultiplier(fs[0], fs[1], fs[2]);
		super.render(tropicalFishEntity, f, g, matrixStack, vertexConsumerProvider, i);
		tintableCompositeModel.setColorMultiplier(1.0F, 1.0F, 1.0F);
	}

	protected void setupTransforms(TropicalFishEntity tropicalFishEntity, MatrixStack matrixStack, float f, float g, float h) {
		super.setupTransforms(tropicalFishEntity, matrixStack, f, g, h);
		float i = 4.3F * MathHelper.sin(0.6F * f);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(i));
		if (!tropicalFishEntity.isInsideWater()) {
			matrixStack.translate(0.2F, 0.1F, 0.0);
			matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
		}
	}
}
