package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.entity.model.EndermanEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.mob.EndermanEntity;

@Environment(EnvType.CLIENT)
public class EndermanBlockFeatureRenderer extends FeatureRenderer<EndermanEntity, EndermanEntityModel<EndermanEntity>> {
	public EndermanBlockFeatureRenderer(FeatureRendererContext<EndermanEntity, EndermanEntityModel<EndermanEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4179(
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		EndermanEntity endermanEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l,
		float m
	) {
		BlockState blockState = endermanEntity.getCarriedBlock();
		if (blockState != null) {
			matrixStack.push();
			matrixStack.translate(0.0, 0.6875, -0.75);
			matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(20.0F));
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(45.0F));
			matrixStack.translate(0.25, 0.1875, 0.25);
			float n = 0.5F;
			matrixStack.scale(-0.5F, -0.5F, 0.5F);
			MinecraftClient.getInstance().getBlockRenderManager().renderOnEntity(blockState, matrixStack, layeredVertexConsumerStorage, i, OverlayTexture.DEFAULT_UV);
			matrixStack.pop();
		}
	}
}
