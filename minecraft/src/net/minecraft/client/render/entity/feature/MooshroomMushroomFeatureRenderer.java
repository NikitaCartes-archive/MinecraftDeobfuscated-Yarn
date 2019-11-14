package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.MooshroomEntity;

@Environment(EnvType.CLIENT)
public class MooshroomMushroomFeatureRenderer<T extends MooshroomEntity> extends FeatureRenderer<T, CowEntityModel<T>> {
	public MooshroomMushroomFeatureRenderer(FeatureRendererContext<T, CowEntityModel<T>> context) {
		super(context);
	}

	public void method_4195(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T mooshroomEntity, float f, float g, float h, float j, float k, float l
	) {
		if (!mooshroomEntity.isBaby() && !mooshroomEntity.isInvisible()) {
			BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
			BlockState blockState = mooshroomEntity.getMooshroomType().getMushroomState();
			int m = LivingEntityRenderer.method_23622(mooshroomEntity, 0.0F);
			matrixStack.push();
			matrixStack.translate(0.2F, -0.35F, 0.5);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-48.0F));
			matrixStack.scale(-1.0F, -1.0F, 1.0F);
			matrixStack.translate(-0.5, -0.5, -0.5);
			blockRenderManager.renderBlockAsEntity(blockState, matrixStack, vertexConsumerProvider, i, m);
			matrixStack.pop();
			matrixStack.push();
			matrixStack.translate(0.2F, -0.35F, 0.5);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(42.0F));
			matrixStack.translate(0.1F, 0.0, -0.6F);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-48.0F));
			matrixStack.scale(-1.0F, -1.0F, 1.0F);
			matrixStack.translate(-0.5, -0.5, -0.5);
			blockRenderManager.renderBlockAsEntity(blockState, matrixStack, vertexConsumerProvider, i, m);
			matrixStack.pop();
			matrixStack.push();
			this.getModel().getHead().rotate(matrixStack);
			matrixStack.translate(0.0, -0.7F, -0.2F);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-78.0F));
			matrixStack.scale(-1.0F, -1.0F, 1.0F);
			matrixStack.translate(-0.5, -0.5, -0.5);
			blockRenderManager.renderBlockAsEntity(blockState, matrixStack, vertexConsumerProvider, i, m);
			matrixStack.pop();
		}
	}
}
