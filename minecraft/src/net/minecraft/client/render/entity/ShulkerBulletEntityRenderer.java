package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.ShulkerBulletEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

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
		VertexConsumerProvider vertexConsumerProvider
	) {
		matrixStack.push();
		float i = MathHelper.method_22859(shulkerBulletEntity.prevYaw, shulkerBulletEntity.yaw, h);
		float j = MathHelper.lerp(h, shulkerBulletEntity.prevPitch, shulkerBulletEntity.pitch);
		float k = (float)shulkerBulletEntity.age + h;
		matrixStack.translate(0.0, 0.15F, 0.0);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(MathHelper.sin(k * 0.1F) * 180.0F));
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(MathHelper.cos(k * 0.1F) * 180.0F));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(MathHelper.sin(k * 0.15F) * 360.0F));
		float l = 0.03125F;
		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		int m = shulkerBulletEntity.getLightmapCoordinates();
		this.model.setAngles(shulkerBulletEntity, 0.0F, 0.0F, 0.0F, i, j, 0.03125F);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(SKIN));
		this.model.render(matrixStack, vertexConsumer, m, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F);
		matrixStack.scale(1.5F, 1.5F, 1.5F);
		VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(RenderLayer.getEntityForceTranslucent(SKIN));
		this.model.render(matrixStack, vertexConsumer2, m, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F);
		matrixStack.pop();
		super.render(shulkerBulletEntity, d, e, f, g, h, matrixStack, vertexConsumerProvider);
	}

	public Identifier method_4105(ShulkerBulletEntity shulkerBulletEntity) {
		return SKIN;
	}
}
