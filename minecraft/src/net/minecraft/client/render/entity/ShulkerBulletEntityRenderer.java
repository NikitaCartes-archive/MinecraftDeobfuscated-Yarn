package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.ShulkerBulletEntityModel;
import net.minecraft.client.render.entity.state.ShulkerBulletEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class ShulkerBulletEntityRenderer extends EntityRenderer<ShulkerBulletEntity, ShulkerBulletEntityRenderState> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/shulker/spark.png");
	private static final RenderLayer LAYER = RenderLayer.getEntityTranslucent(TEXTURE);
	private final ShulkerBulletEntityModel model;

	public ShulkerBulletEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.model = new ShulkerBulletEntityModel(context.getPart(EntityModelLayers.SHULKER_BULLET));
	}

	protected int getBlockLight(ShulkerBulletEntity shulkerBulletEntity, BlockPos blockPos) {
		return 15;
	}

	public void render(
		ShulkerBulletEntityRenderState shulkerBulletEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i
	) {
		matrixStack.push();
		float f = shulkerBulletEntityRenderState.age;
		matrixStack.translate(0.0F, 0.15F, 0.0F);
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.sin(f * 0.1F) * 180.0F));
		matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.cos(f * 0.1F) * 180.0F));
		matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.sin(f * 0.15F) * 360.0F));
		matrixStack.scale(-0.5F, -0.5F, 0.5F);
		this.model.setAngles(shulkerBulletEntityRenderState);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(TEXTURE));
		this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
		matrixStack.scale(1.5F, 1.5F, 1.5F);
		VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(LAYER);
		this.model.render(matrixStack, vertexConsumer2, i, OverlayTexture.DEFAULT_UV, 654311423);
		matrixStack.pop();
		super.render(shulkerBulletEntityRenderState, matrixStack, vertexConsumerProvider, i);
	}

	public ShulkerBulletEntityRenderState createRenderState() {
		return new ShulkerBulletEntityRenderState();
	}

	public void updateRenderState(ShulkerBulletEntity shulkerBulletEntity, ShulkerBulletEntityRenderState shulkerBulletEntityRenderState, float f) {
		super.updateRenderState(shulkerBulletEntity, shulkerBulletEntityRenderState, f);
		shulkerBulletEntityRenderState.yaw = shulkerBulletEntity.getLerpedYaw(f);
		shulkerBulletEntityRenderState.pitch = shulkerBulletEntity.getLerpedPitch(f);
	}
}
