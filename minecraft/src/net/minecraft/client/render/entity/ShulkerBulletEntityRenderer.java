package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ShulkerBulletEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class ShulkerBulletEntityRenderer extends EntityRenderer<ShulkerBulletEntity> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/shulker/spark.png");
	private static final RenderLayer LAYER = RenderLayer.getEntityTranslucent(TEXTURE);
	private final ShulkerBulletEntityModel<ShulkerBulletEntity> model;

	public ShulkerBulletEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.model = new ShulkerBulletEntityModel<>(context.getPart(EntityModelLayers.SHULKER_BULLET));
	}

	protected int getBlockLight(ShulkerBulletEntity shulkerBulletEntity, BlockPos blockPos) {
		return 15;
	}

	public void render(ShulkerBulletEntity shulkerBulletEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		float h = MathHelper.lerpAngleDegrees(g, shulkerBulletEntity.prevYaw, shulkerBulletEntity.getYaw());
		float j = MathHelper.lerp(g, shulkerBulletEntity.prevPitch, shulkerBulletEntity.getPitch());
		float k = (float)shulkerBulletEntity.age + g;
		matrixStack.translate(0.0F, 0.15F, 0.0F);
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.sin(k * 0.1F) * 180.0F));
		matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.cos(k * 0.1F) * 180.0F));
		matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.sin(k * 0.15F) * 360.0F));
		matrixStack.scale(-0.5F, -0.5F, 0.5F);
		this.model.setAngles(shulkerBulletEntity, 0.0F, 0.0F, 0.0F, h, j);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(TEXTURE));
		this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
		matrixStack.scale(1.5F, 1.5F, 1.5F);
		VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(LAYER);
		this.model.render(matrixStack, vertexConsumer2, i, OverlayTexture.DEFAULT_UV, 654311423);
		matrixStack.pop();
		super.render(shulkerBulletEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	public Identifier getTexture(ShulkerBulletEntity shulkerBulletEntity) {
		return TEXTURE;
	}
}
