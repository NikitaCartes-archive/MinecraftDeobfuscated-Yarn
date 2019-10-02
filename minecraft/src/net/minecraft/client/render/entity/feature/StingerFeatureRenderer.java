package net.minecraft.client.render.entity.feature;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class StingerFeatureRenderer<T extends LivingEntity, M extends PlayerEntityModel<T>> extends StickingOutThingsFeatureRenderer<T, M> {
	private static final Identifier field_20529 = new Identifier("textures/entity/bee/bee_stinger.png");

	public StingerFeatureRenderer(LivingEntityRenderer<T, M> livingEntityRenderer) {
		super(livingEntityRenderer);
	}

	@Override
	protected int getThingCount(T livingEntity) {
		return livingEntity.getStingerCount();
	}

	@Override
	protected void renderThing(
		MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, Entity entity, float f, float g, float h, float i
	) {
		float j = MathHelper.sqrt(f * f + h * h);
		float k = (float)(Math.atan2((double)f, (double)h) * 180.0F / (float)Math.PI);
		float l = (float)(Math.atan2((double)g, (double)j) * 180.0F / (float)Math.PI);
		matrixStack.translate(0.0, 0.0, 0.0);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(k - 90.0F, true));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(l, true));
		float m = 0.0F;
		float n = 0.125F;
		float o = 0.0F;
		float p = 0.0625F;
		float q = 0.03125F;
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(45.0F, true));
		matrixStack.scale(0.03125F, 0.03125F, 0.03125F);
		matrixStack.translate(2.5, 0.0, 0.0);
		int r = entity.getLightmapCoordinates();
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23017(field_20529));
		OverlayTexture.clearDefaultOverlay(vertexConsumer);

		for (int s = 0; s < 4; s++) {
			matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(90.0F, true));
			Matrix4f matrix4f = matrixStack.peek();
			method_23295(vertexConsumer, matrix4f, -4.5F, -1, 0.0F, 0.0F, r);
			method_23295(vertexConsumer, matrix4f, 4.5F, -1, 0.125F, 0.0F, r);
			method_23295(vertexConsumer, matrix4f, 4.5F, 1, 0.125F, 0.0625F, r);
			method_23295(vertexConsumer, matrix4f, -4.5F, 1, 0.0F, 0.0625F, r);
		}

		vertexConsumer.clearDefaultOverlay();
	}

	private static void method_23295(VertexConsumer vertexConsumer, Matrix4f matrix4f, float f, int i, float g, float h, int j) {
		vertexConsumer.vertex(matrix4f, f, (float)i, 0.0F).color(255, 255, 255, 255).texture(g, h).light(j).normal(0.0F, 1.0F, 0.0F).next();
	}
}
