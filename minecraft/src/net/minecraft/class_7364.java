package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class class_7364 extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	public class_7364(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void render(
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i,
		AbstractClientPlayerEntity abstractClientPlayerEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l
	) {
		BlockState blockState = abstractClientPlayerEntity.method_42800();
		if (blockState != null) {
			matrixStack.push();
			Item item = class_7323.method_42881(blockState);
			if (item != null) {
				ItemStack itemStack = item.getDefaultStack();
				MinecraftClient.getInstance()
					.getHeldItemRenderer()
					.renderItem(
						abstractClientPlayerEntity,
						itemStack,
						ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND,
						false,
						matrixStack,
						vertexConsumerProvider,
						OverlayTexture.DEFAULT_UV
					);
			} else {
				matrixStack.translate(0.0, 0.6875, -0.75);
				matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(20.0F));
				matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(45.0F));
				matrixStack.translate(0.125, 0.25, 0.5);
				float m = 0.625F;
				matrixStack.scale(-0.625F, -0.625F, 0.625F);
				matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
				MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(blockState, matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV);
			}

			matrixStack.pop();
		}
	}
}
