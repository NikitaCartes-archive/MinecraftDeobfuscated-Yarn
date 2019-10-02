package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.ShulkerEntityRenderer;
import net.minecraft.client.render.entity.model.ShulkerEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class ShulkerSomethingFeatureRenderer extends FeatureRenderer<ShulkerEntity, ShulkerEntityModel<ShulkerEntity>> {
	public ShulkerSomethingFeatureRenderer(FeatureRendererContext<ShulkerEntity, ShulkerEntityModel<ShulkerEntity>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4115(
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		ShulkerEntity shulkerEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l,
		float m
	) {
		matrixStack.push();
		switch (shulkerEntity.getAttachedFace()) {
			case DOWN:
			default:
				break;
			case EAST:
				matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(90.0F, true));
				matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(90.0F, true));
				matrixStack.translate(1.0, -1.0, 0.0);
				matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0F, true));
				break;
			case WEST:
				matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(-90.0F, true));
				matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(90.0F, true));
				matrixStack.translate(-1.0, -1.0, 0.0);
				matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0F, true));
				break;
			case NORTH:
				matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(90.0F, true));
				matrixStack.translate(0.0, -1.0, -1.0);
				break;
			case SOUTH:
				matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(180.0F, true));
				matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(90.0F, true));
				matrixStack.translate(0.0, -1.0, 1.0);
				break;
			case UP:
				matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(180.0F, true));
				matrixStack.translate(0.0, -2.0, 0.0);
		}

		ModelPart modelPart = this.getModel().method_2830();
		modelPart.yaw = k * (float) (Math.PI / 180.0);
		modelPart.pitch = l * (float) (Math.PI / 180.0);
		DyeColor dyeColor = shulkerEntity.getColor();
		Identifier identifier;
		if (dyeColor == null) {
			identifier = ShulkerEntityRenderer.SKIN;
		} else {
			identifier = ShulkerEntityRenderer.SKIN_COLOR[dyeColor.getId()];
		}

		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23017(identifier));
		LivingEntityRenderer.method_23184(shulkerEntity, vertexConsumer, 0.0F);
		modelPart.render(matrixStack, vertexConsumer, m, i, null);
		vertexConsumer.clearDefaultOverlay();
		matrixStack.pop();
	}
}
