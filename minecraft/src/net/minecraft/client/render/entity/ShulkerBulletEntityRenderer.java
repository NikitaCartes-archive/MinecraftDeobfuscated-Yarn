package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.ShulkerBulletEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class ShulkerBulletEntityRenderer extends EntityRenderer<ShulkerBulletEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/shulker/spark.png");
	private final ShulkerBulletEntityModel<ShulkerBulletEntity> model = new ShulkerBulletEntityModel<>();

	public ShulkerBulletEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_4103(
		ShulkerBulletEntity shulkerBulletEntity,
		double d,
		double e,
		double f,
		float g,
		float h,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		matrixStack.push();
		float i = MathHelper.method_22859(shulkerBulletEntity.prevYaw, shulkerBulletEntity.yaw, h);
		float j = MathHelper.lerp(h, shulkerBulletEntity.prevPitch, shulkerBulletEntity.pitch);
		float k = (float)shulkerBulletEntity.age + h;
		matrixStack.translate(0.0, 0.15F, 0.0);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(MathHelper.sin(k * 0.1F) * 180.0F, true));
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(MathHelper.cos(k * 0.1F) * 180.0F, true));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(MathHelper.sin(k * 0.15F) * 360.0F, true));
		float l = 0.03125F;
		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		int m = shulkerBulletEntity.getLightmapCoordinates();
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23017(SKIN));
		OverlayTexture.clearDefaultOverlay(vertexConsumer);
		this.model.setAngles(shulkerBulletEntity, 0.0F, 0.0F, 0.0F, i, j, 0.03125F);
		this.model.method_22957(matrixStack, vertexConsumer, m);
		vertexConsumer.clearDefaultOverlay();
		matrixStack.scale(1.5F, 1.5F, 1.5F);
		VertexConsumer vertexConsumer2 = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23019(SKIN, true, true, false));
		OverlayTexture.clearDefaultOverlay(vertexConsumer);
		this.model.method_22957(matrixStack, vertexConsumer2, m);
		vertexConsumer.clearDefaultOverlay();
		matrixStack.pop();
		super.render(shulkerBulletEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
	}

	public Identifier method_4105(ShulkerBulletEntity shulkerBulletEntity) {
		return SKIN;
	}
}
