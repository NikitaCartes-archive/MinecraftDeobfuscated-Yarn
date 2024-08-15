package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.TropicalFishColorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LargeTropicalFishEntityModel;
import net.minecraft.client.render.entity.model.SmallTropicalFishEntityModel;
import net.minecraft.client.render.entity.state.TropicalFishEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class TropicalFishEntityRenderer
	extends MobEntityRenderer<TropicalFishEntity, TropicalFishEntityRenderState, EntityModel<TropicalFishEntityRenderState>> {
	private final EntityModel<TropicalFishEntityRenderState> smallModel = this.getModel();
	private final EntityModel<TropicalFishEntityRenderState> largeModel;
	private static final Identifier A_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_a.png");
	private static final Identifier B_TEXTURE = Identifier.ofVanilla("textures/entity/fish/tropical_b.png");

	public TropicalFishEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new SmallTropicalFishEntityModel(context.getPart(EntityModelLayers.TROPICAL_FISH_SMALL)), 0.15F);
		this.largeModel = new LargeTropicalFishEntityModel(context.getPart(EntityModelLayers.TROPICAL_FISH_LARGE));
		this.addFeature(new TropicalFishColorFeatureRenderer(this, context.getModelLoader()));
	}

	public Identifier getTexture(TropicalFishEntityRenderState tropicalFishEntityRenderState) {
		return switch (tropicalFishEntityRenderState.variety.getSize()) {
			case SMALL -> A_TEXTURE;
			case LARGE -> B_TEXTURE;
		};
	}

	public TropicalFishEntityRenderState getRenderState() {
		return new TropicalFishEntityRenderState();
	}

	public void updateRenderState(TropicalFishEntity tropicalFishEntity, TropicalFishEntityRenderState tropicalFishEntityRenderState, float f) {
		super.updateRenderState(tropicalFishEntity, tropicalFishEntityRenderState, f);
		tropicalFishEntityRenderState.variety = tropicalFishEntity.getVariant();
		tropicalFishEntityRenderState.baseColor = tropicalFishEntity.getBaseColorComponents().getEntityColor();
		tropicalFishEntityRenderState.patternColor = tropicalFishEntity.getPatternColorComponents().getEntityColor();
	}

	public void render(TropicalFishEntityRenderState tropicalFishEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		this.model = switch (tropicalFishEntityRenderState.variety.getSize()) {
			case SMALL -> this.smallModel;
			case LARGE -> this.largeModel;
		};
		super.render(tropicalFishEntityRenderState, matrixStack, vertexConsumerProvider, i);
	}

	protected int getMixColor(TropicalFishEntityRenderState tropicalFishEntityRenderState) {
		return tropicalFishEntityRenderState.baseColor;
	}

	protected void setupTransforms(TropicalFishEntityRenderState tropicalFishEntityRenderState, MatrixStack matrixStack, float f, float g) {
		super.setupTransforms(tropicalFishEntityRenderState, matrixStack, f, g);
		float h = 4.3F * MathHelper.sin(0.6F * tropicalFishEntityRenderState.age);
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(h));
		if (!tropicalFishEntityRenderState.touchingWater) {
			matrixStack.translate(0.2F, 0.1F, 0.0F);
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
		}
	}
}
