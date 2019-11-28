package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.IronGolemEntity;

@Environment(EnvType.CLIENT)
public class IronGolemFlowerFeatureRenderer extends FeatureRenderer<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> {
	public IronGolemFlowerFeatureRenderer(FeatureRendererContext<IronGolemEntity, IronGolemEntityModel<IronGolemEntity>> context) {
		super(context);
	}

	public void render(
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i,
		IronGolemEntity ironGolemEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l
	) {
		if (ironGolemEntity.method_6502() != 0) {
			matrixStack.push();
			ModelPart modelPart = this.getContextModel().getRightArm();
			modelPart.rotate(matrixStack);
			matrixStack.translate(-1.1875, 1.0625, -0.9375);
			matrixStack.translate(0.5, 0.5, 0.5);
			float m = 0.5F;
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
			matrixStack.translate(-0.5, -0.5, -0.5);
			MinecraftClient.getInstance()
				.getBlockRenderManager()
				.renderBlockAsEntity(Blocks.POPPY.getDefaultState(), matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV);
			matrixStack.pop();
		}
	}
}
