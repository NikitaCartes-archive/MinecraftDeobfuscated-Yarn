package net.minecraft.client.render.entity.feature;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PlayerHeldItemFeatureRenderer<S extends PlayerEntityRenderState, M extends EntityModel<S> & ModelWithArms & ModelWithHead>
	extends HeldItemFeatureRenderer<S, M> {
	private final ItemRenderer playerHeldItemRenderer;
	private static final float HEAD_YAW = (float) (-Math.PI / 6);
	private static final float HEAD_ROLL = (float) (Math.PI / 2);

	public PlayerHeldItemFeatureRenderer(FeatureRendererContext<S, M> featureRendererContext, ItemRenderer itemRenderer) {
		super(featureRendererContext, itemRenderer);
		this.playerHeldItemRenderer = itemRenderer;
	}

	protected void renderItem(
		S playerEntityRenderState,
		@Nullable BakedModel bakedModel,
		ItemStack itemStack,
		ModelTransformationMode modelTransformationMode,
		Arm arm,
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i
	) {
		if (bakedModel != null) {
			Hand hand = arm == playerEntityRenderState.mainArm ? Hand.MAIN_HAND : Hand.OFF_HAND;
			if (playerEntityRenderState.isUsingItem
				&& playerEntityRenderState.activeHand == hand
				&& playerEntityRenderState.handSwingProgress < 1.0E-5F
				&& itemStack.isOf(Items.SPYGLASS)) {
				this.renderSpyglass(bakedModel, itemStack, arm, matrixStack, vertexConsumerProvider, i);
			} else {
				super.renderItem(playerEntityRenderState, bakedModel, itemStack, modelTransformationMode, arm, matrixStack, vertexConsumerProvider, i);
			}
		}
	}

	private void renderSpyglass(BakedModel model, ItemStack stack, Arm arm, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		matrices.push();
		this.getContextModel().getRootPart().rotate(matrices);
		ModelPart modelPart = this.getContextModel().getHead();
		float f = modelPart.pitch;
		modelPart.pitch = MathHelper.clamp(modelPart.pitch, (float) (-Math.PI / 6), (float) (Math.PI / 2));
		modelPart.rotate(matrices);
		modelPart.pitch = f;
		HeadFeatureRenderer.translate(matrices, HeadFeatureRenderer.HeadTransformation.DEFAULT);
		boolean bl = arm == Arm.LEFT;
		matrices.translate((bl ? -2.5F : 2.5F) / 16.0F, -0.0625F, 0.0F);
		this.playerHeldItemRenderer.renderItem(stack, ModelTransformationMode.HEAD, false, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV, model);
		matrices.pop();
	}
}
