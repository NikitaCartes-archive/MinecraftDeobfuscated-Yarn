package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SalmonEntityModel;
import net.minecraft.client.render.entity.state.SalmonEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class SalmonEntityRenderer extends MobEntityRenderer<SalmonEntity, SalmonEntityRenderState, SalmonEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/fish/salmon.png");
	private final SalmonEntityModel field_53198;
	private final SalmonEntityModel field_53199;
	private final SalmonEntityModel field_53200;

	public SalmonEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new SalmonEntityModel(context.getPart(EntityModelLayers.SALMON)), 0.4F);
		this.field_53198 = new SalmonEntityModel(context.getPart(EntityModelLayers.SALMON_SMALL));
		this.field_53199 = new SalmonEntityModel(context.getPart(EntityModelLayers.SALMON));
		this.field_53200 = new SalmonEntityModel(context.getPart(EntityModelLayers.SALMON_LARGE));
	}

	public void updateRenderState(SalmonEntity salmonEntity, SalmonEntityRenderState salmonEntityRenderState, float f) {
		super.updateRenderState(salmonEntity, salmonEntityRenderState, f);
		salmonEntityRenderState.variant = salmonEntity.getVariant();
	}

	public Identifier getTexture(SalmonEntityRenderState salmonEntityRenderState) {
		return TEXTURE;
	}

	public SalmonEntityRenderState getRenderState() {
		return new SalmonEntityRenderState();
	}

	protected void setupTransforms(SalmonEntityRenderState salmonEntityRenderState, MatrixStack matrixStack, float f, float g) {
		super.setupTransforms(salmonEntityRenderState, matrixStack, f, g);
		float h = 1.0F;
		float i = 1.0F;
		if (!salmonEntityRenderState.touchingWater) {
			h = 1.3F;
			i = 1.7F;
		}

		float j = h * 4.3F * MathHelper.sin(i * 0.6F * salmonEntityRenderState.age);
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(j));
		if (!salmonEntityRenderState.touchingWater) {
			matrixStack.translate(0.2F, 0.1F, 0.0F);
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(90.0F));
		}
	}

	public void render(SalmonEntityRenderState salmonEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		if (salmonEntityRenderState.variant == SalmonEntity.Variant.SMALL) {
			this.model = this.field_53198;
		} else if (salmonEntityRenderState.variant == SalmonEntity.Variant.LARGE) {
			this.model = this.field_53200;
		} else {
			this.model = this.field_53199;
		}

		super.render(salmonEntityRenderState, matrixStack, vertexConsumerProvider, i);
	}
}
