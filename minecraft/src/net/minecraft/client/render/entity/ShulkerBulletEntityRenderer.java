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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ShulkerBulletEntityRenderer extends EntityRenderer<ShulkerBulletEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/shulker/spark.png");
	private static final RenderLayer LAYER = RenderLayer.getEntityTranslucent(TEXTURE);
	private final ShulkerBulletEntityModel<ShulkerBulletEntity> model = new ShulkerBulletEntityModel<>();

	public ShulkerBulletEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	protected int method_24091(ShulkerBulletEntity shulkerBulletEntity, BlockPos blockPos) {
		return 15;
	}

	public void method_4103(
		ShulkerBulletEntity shulkerBulletEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i
	) {
		matrixStack.push();
		float h = MathHelper.lerpAngle(shulkerBulletEntity.prevYaw, shulkerBulletEntity.yaw, g);
		float j = MathHelper.lerp(g, shulkerBulletEntity.prevPitch, shulkerBulletEntity.pitch);
		float k = (float)shulkerBulletEntity.age + g;
		matrixStack.translate(0.0, 0.15F, 0.0);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.sin(k * 0.1F) * 180.0F));
		matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(MathHelper.cos(k * 0.1F) * 180.0F));
		matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.sin(k * 0.15F) * 360.0F));
		matrixStack.scale(-0.5F, -0.5F, 0.5F);
		this.model.setAngles(shulkerBulletEntity, 0.0F, 0.0F, 0.0F, h, j);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(TEXTURE));
		this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
		matrixStack.scale(1.5F, 1.5F, 1.5F);
		VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(LAYER);
		this.model.render(matrixStack, vertexConsumer2, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 0.15F);
		matrixStack.pop();
		super.render(shulkerBulletEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	public Identifier method_4105(ShulkerBulletEntity shulkerBulletEntity) {
		return TEXTURE;
	}
}
