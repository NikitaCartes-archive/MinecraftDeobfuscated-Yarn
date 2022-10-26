package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class DragonFireballEntityRenderer extends EntityRenderer<DragonFireballEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/enderdragon/dragon_fireball.png");
	private static final RenderLayer LAYER = RenderLayer.getEntityCutoutNoCull(TEXTURE);

	public DragonFireballEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	protected int getBlockLight(DragonFireballEntity dragonFireballEntity, BlockPos blockPos) {
		return 15;
	}

	public void render(DragonFireballEntity dragonFireballEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		matrixStack.scale(2.0F, 2.0F, 2.0F);
		matrixStack.multiply(this.dispatcher.getRotation());
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
		MatrixStack.Entry entry = matrixStack.peek();
		Matrix4f matrix4f = entry.getPositionMatrix();
		Matrix3f matrix3f = entry.getNormalMatrix();
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
		produceVertex(vertexConsumer, matrix4f, matrix3f, i, 0.0F, 0, 0, 1);
		produceVertex(vertexConsumer, matrix4f, matrix3f, i, 1.0F, 0, 1, 1);
		produceVertex(vertexConsumer, matrix4f, matrix3f, i, 1.0F, 1, 1, 0);
		produceVertex(vertexConsumer, matrix4f, matrix3f, i, 0.0F, 1, 0, 0);
		matrixStack.pop();
		super.render(dragonFireballEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	private static void produceVertex(
		VertexConsumer vertexConsumer, Matrix4f positionMatrix, Matrix3f normalMatrix, int light, float x, int y, int textureU, int textureV
	) {
		vertexConsumer.vertex(positionMatrix, x - 0.5F, (float)y - 0.25F, 0.0F)
			.color(255, 255, 255, 255)
			.texture((float)textureU, (float)textureV)
			.overlay(OverlayTexture.DEFAULT_UV)
			.light(light)
			.normal(normalMatrix, 0.0F, 1.0F, 0.0F)
			.next();
	}

	public Identifier getTexture(DragonFireballEntity dragonFireballEntity) {
		return TEXTURE;
	}
}
