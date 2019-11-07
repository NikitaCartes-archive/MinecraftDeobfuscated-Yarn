package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class StingerFeatureRenderer<T extends LivingEntity, M extends PlayerEntityModel<T>> extends StickingOutThingsFeatureRenderer<T, M> {
	private static final Identifier field_20529 = new Identifier("textures/entity/bee/bee_stinger.png");

	public StingerFeatureRenderer(LivingEntityRenderer<T, M> livingEntityRenderer) {
		super(livingEntityRenderer);
	}

	@Override
	protected int getThingCount(T entity) {
		return entity.getStingerCount();
	}

	@Override
	protected void renderThing(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, Entity entity, float tickDelta, float f, float g, float h
	) {
		float j = MathHelper.sqrt(tickDelta * tickDelta + g * g);
		float k = (float)(Math.atan2((double)tickDelta, (double)g) * 180.0F / (float)Math.PI);
		float l = (float)(Math.atan2((double)f, (double)j) * 180.0F / (float)Math.PI);
		matrixStack.translate(0.0, 0.0, 0.0);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(k - 90.0F));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(l));
		float m = 0.0F;
		float n = 0.125F;
		float o = 0.0F;
		float p = 0.0625F;
		float q = 0.03125F;
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(45.0F));
		matrixStack.scale(0.03125F, 0.03125F, 0.03125F);
		matrixStack.translate(2.5, 0.0, 0.0);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(field_20529));

		for (int r = 0; r < 4; r++) {
			matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(90.0F));
			MatrixStack.Entry entry = matrixStack.method_23760();
			Matrix4f matrix4f = entry.method_23761();
			Matrix3f matrix3f = entry.method_23762();
			method_23295(vertexConsumer, matrix4f, matrix3f, -4.5F, -1, 0.0F, 0.0F, i);
			method_23295(vertexConsumer, matrix4f, matrix3f, 4.5F, -1, 0.125F, 0.0F, i);
			method_23295(vertexConsumer, matrix4f, matrix3f, 4.5F, 1, 0.125F, 0.0625F, i);
			method_23295(vertexConsumer, matrix4f, matrix3f, -4.5F, 1, 0.0F, 0.0625F, i);
		}
	}

	private static void method_23295(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, float f, int i, float g, float h, int j) {
		vertexConsumer.vertex(matrix4f, f, (float)i, 0.0F)
			.color(255, 255, 255, 255)
			.texture(g, h)
			.overlay(OverlayTexture.DEFAULT_UV)
			.light(j)
			.method_23763(matrix3f, 0.0F, 1.0F, 0.0F)
			.next();
	}
}
