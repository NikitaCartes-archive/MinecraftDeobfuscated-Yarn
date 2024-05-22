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
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class TropicalFishEntityRenderer extends MobEntityRenderer<TropicalFishEntity, TintableCompositeModel<TropicalFishEntity>> {
	private final TintableCompositeModel<TropicalFishEntity> smallModel = this.getModel();
	private final TintableCompositeModel<TropicalFishEntity> largeModel;
	private static final Identifier A_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_a.png");
	private static final Identifier B_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_b.png");

	public TropicalFishEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new SmallTropicalFishEntityModel<>(context.getPart(EntityModelLayers.TROPICAL_FISH_SMALL)), 0.15F);
		this.largeModel = new LargeTropicalFishEntityModel<>(context.getPart(EntityModelLayers.TROPICAL_FISH_LARGE));
		this.addFeature(new TropicalFishColorFeatureRenderer(this, context.getModelLoader()));
	}

	public Identifier getTexture(TropicalFishEntity tropicalFishEntity) {
		return switch (tropicalFishEntity.getVariant().getSize()) {
			case SMALL -> A_TEXTURE;
			case LARGE -> B_TEXTURE;
		};
	}

	public void render(TropicalFishEntity tropicalFishEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		TintableCompositeModel<TropicalFishEntity> tintableCompositeModel = switch (tropicalFishEntity.getVariant().getSize()) {
			case SMALL -> this.smallModel;
			case LARGE -> this.largeModel;
		};
		this.model = tintableCompositeModel;
		tintableCompositeModel.setColorMultiplier(tropicalFishEntity.getBaseColorComponents().getEntityColor());
		super.render(tropicalFishEntity, f, g, matrixStack, vertexConsumerProvider, i);
		tintableCompositeModel.setColorMultiplier(-1);
	}

	protected void setupTransforms(TropicalFishEntity tropicalFishEntity, MatrixStack matrixStack, float f, float g, float h, float i) {
		super.setupTransforms(tropicalFishEntity, matrixStack, f, g, h, i);
		float j = 4.3F * MathHelper.sin(0.6F * f);
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
		if (!tropicalFishEntity.isTouchingWater()) {
			matrixStack.translate(0.2F, 0.1F, 0.0F);
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
		}
	}
}
