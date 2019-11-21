package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TntMinecartEntityRenderer extends MinecartEntityRenderer<TntMinecartEntity> {
	public TntMinecartEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	protected void renderBlock(
		TntMinecartEntity tntMinecartEntity, float f, BlockState blockState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i
	) {
		int j = tntMinecartEntity.getFuseTicks();
		if (j > -1 && (float)j - f + 1.0F < 10.0F) {
			float g = 1.0F - ((float)j - f + 1.0F) / 10.0F;
			g = MathHelper.clamp(g, 0.0F, 1.0F);
			g *= g;
			g *= g;
			float h = 1.0F + g * 0.3F;
			matrixStack.scale(h, h, h);
		}

		method_23190(blockState, matrixStack, vertexConsumerProvider, i, j > -1 && j / 5 % 2 == 0);
	}

	public static void method_23190(BlockState blockState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, boolean bl) {
		int j;
		if (bl) {
			j = OverlayTexture.packUv(OverlayTexture.getU(1.0F), 10);
		} else {
			j = OverlayTexture.DEFAULT_UV;
		}

		matrixStack.push();
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0F));
		MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(blockState, matrixStack, vertexConsumerProvider, i, j);
		matrixStack.pop();
	}
}
