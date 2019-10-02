package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.model.CowEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class MooshroomMushroomFeatureRenderer<T extends MooshroomEntity> extends FeatureRenderer<T, CowEntityModel<T>> {
	public MooshroomMushroomFeatureRenderer(FeatureRendererContext<T, CowEntityModel<T>> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_4195(
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i,
		T mooshroomEntity,
		float f,
		float g,
		float h,
		float j,
		float k,
		float l,
		float m
	) {
		if (!mooshroomEntity.isBaby() && !mooshroomEntity.isInvisible()) {
			BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
			BlockState blockState = mooshroomEntity.getMooshroomType().getMushroomState();
			matrixStack.push();
			matrixStack.scale(-1.0F, -1.0F, 1.0F);
			matrixStack.translate(-0.2F, 0.35F, 0.5);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-42.0F, true));
			int n = OverlayTexture.getV(mooshroomEntity.hurtTime > 0 || mooshroomEntity.deathTime > 0);
			matrixStack.push();
			matrixStack.translate(-0.5, -0.5, 0.5);
			blockRenderManager.renderDynamic(blockState, matrixStack, layeredVertexConsumerStorage, i, 0, n);
			matrixStack.pop();
			matrixStack.push();
			matrixStack.translate(-0.1F, 0.0, -0.6F);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-42.0F, true));
			matrixStack.translate(-0.5, -0.5, 0.5);
			blockRenderManager.renderDynamic(blockState, matrixStack, layeredVertexConsumerStorage, i, 0, n);
			matrixStack.pop();
			matrixStack.pop();
			matrixStack.push();
			this.getModel().getHead().rotate(matrixStack, 0.0625F);
			matrixStack.scale(-1.0F, -1.0F, 1.0F);
			matrixStack.translate(0.0, 0.7F, -0.2F);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-12.0F, true));
			matrixStack.translate(-0.5, -0.5, 0.5);
			blockRenderManager.renderDynamic(blockState, matrixStack, layeredVertexConsumerStorage, i, 0, n);
			matrixStack.pop();
		}
	}
}
