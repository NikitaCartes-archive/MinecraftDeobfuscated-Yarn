package net.minecraft.client.render.entity.feature;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class HeldItemFeatureRenderer<S extends LivingEntityRenderState, M extends EntityModel<S> & ModelWithArms> extends FeatureRenderer<S, M> {
	private final ItemRenderer itemRenderer;

	public HeldItemFeatureRenderer(FeatureRendererContext<S, M> context, ItemRenderer itemRenderer) {
		super(context);
		this.itemRenderer = itemRenderer;
	}

	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, S livingEntityRenderState, float f, float g) {
		this.renderItem(
			livingEntityRenderState,
			livingEntityRenderState.rightHandItemModel,
			livingEntityRenderState.rightHandStack,
			ModelTransformationMode.THIRD_PERSON_RIGHT_HAND,
			Arm.RIGHT,
			matrixStack,
			vertexConsumerProvider,
			i
		);
		this.renderItem(
			livingEntityRenderState,
			livingEntityRenderState.leftHandItemModel,
			livingEntityRenderState.leftHandStack,
			ModelTransformationMode.THIRD_PERSON_LEFT_HAND,
			Arm.LEFT,
			matrixStack,
			vertexConsumerProvider,
			i
		);
	}

	protected void renderItem(
		S state,
		@Nullable BakedModel model,
		ItemStack stack,
		ModelTransformationMode modelTransformation,
		Arm arm,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light
	) {
		if (model != null && !stack.isEmpty()) {
			matrices.push();
			this.getContextModel().setArmAngle(arm, matrices);
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
			boolean bl = arm == Arm.LEFT;
			matrices.translate((float)(bl ? -1 : 1) / 16.0F, 0.125F, -0.625F);
			this.itemRenderer.renderItem(stack, modelTransformation, bl, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, model);
			matrices.pop();
		}
	}
}
