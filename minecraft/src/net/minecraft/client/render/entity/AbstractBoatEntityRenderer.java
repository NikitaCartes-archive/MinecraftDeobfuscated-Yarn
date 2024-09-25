package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.BoatEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

@Environment(EnvType.CLIENT)
public abstract class AbstractBoatEntityRenderer extends EntityRenderer<AbstractBoatEntity, BoatEntityRenderState> {
	public AbstractBoatEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.shadowRadius = 0.8F;
	}

	public void render(BoatEntityRenderState boatEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		matrixStack.translate(0.0F, 0.375F, 0.0F);
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - boatEntityRenderState.yaw));
		float f = boatEntityRenderState.damageWobbleTicks;
		if (f > 0.0F) {
			matrixStack.multiply(
				RotationAxis.POSITIVE_X
					.rotationDegrees(MathHelper.sin(f) * f * boatEntityRenderState.damageWobbleStrength / 10.0F * (float)boatEntityRenderState.damageWobbleSide)
			);
		}

		if (!MathHelper.approximatelyEquals(boatEntityRenderState.bubbleWobble, 0.0F)) {
			matrixStack.multiply(new Quaternionf().setAngleAxis(boatEntityRenderState.bubbleWobble * (float) (Math.PI / 180.0), 1.0F, 0.0F, 1.0F));
		}

		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
		EntityModel<BoatEntityRenderState> entityModel = this.getModel();
		entityModel.setAngles(boatEntityRenderState);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.getRenderLayer());
		entityModel.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
		this.renderWaterMask(boatEntityRenderState, matrixStack, vertexConsumerProvider, i);
		matrixStack.pop();
		super.render(boatEntityRenderState, matrixStack, vertexConsumerProvider, i);
	}

	protected void renderWaterMask(BoatEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
	}

	protected abstract EntityModel<BoatEntityRenderState> getModel();

	protected abstract RenderLayer getRenderLayer();

	public BoatEntityRenderState getRenderState() {
		return new BoatEntityRenderState();
	}

	public void updateRenderState(AbstractBoatEntity abstractBoatEntity, BoatEntityRenderState boatEntityRenderState, float f) {
		super.updateRenderState(abstractBoatEntity, boatEntityRenderState, f);
		boatEntityRenderState.yaw = abstractBoatEntity.getLerpedYaw(f);
		boatEntityRenderState.damageWobbleTicks = (float)abstractBoatEntity.getDamageWobbleTicks() - f;
		boatEntityRenderState.damageWobbleSide = abstractBoatEntity.getDamageWobbleSide();
		boatEntityRenderState.damageWobbleStrength = Math.max(abstractBoatEntity.getDamageWobbleStrength() - f, 0.0F);
		boatEntityRenderState.bubbleWobble = abstractBoatEntity.lerpBubbleWobble(f);
		boatEntityRenderState.submergedInWater = abstractBoatEntity.isSubmergedInWater();
		boatEntityRenderState.leftPaddleAngle = abstractBoatEntity.lerpPaddlePhase(0, f);
		boatEntityRenderState.rightPaddleAngle = abstractBoatEntity.lerpPaddlePhase(1, f);
	}
}
