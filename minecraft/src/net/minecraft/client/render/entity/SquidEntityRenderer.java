package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.client.render.entity.state.SquidEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class SquidEntityRenderer<T extends SquidEntity> extends AgeableMobEntityRenderer<T, SquidEntityRenderState, SquidEntityModel> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/squid/squid.png");

	public SquidEntityRenderer(EntityRendererFactory.Context context, SquidEntityModel model, SquidEntityModel babyModel) {
		super(context, model, babyModel, 0.7F);
	}

	public Identifier getTexture(SquidEntityRenderState squidEntityRenderState) {
		return TEXTURE;
	}

	public SquidEntityRenderState getRenderState() {
		return new SquidEntityRenderState();
	}

	public void updateRenderState(T squidEntity, SquidEntityRenderState squidEntityRenderState, float f) {
		super.updateRenderState(squidEntity, squidEntityRenderState, f);
		squidEntityRenderState.tentacleAngle = MathHelper.lerp(f, squidEntity.prevTentacleAngle, squidEntity.tentacleAngle);
		squidEntityRenderState.tiltAngle = MathHelper.lerp(f, squidEntity.prevTiltAngle, squidEntity.tiltAngle);
		squidEntityRenderState.rollAngle = MathHelper.lerp(f, squidEntity.prevRollAngle, squidEntity.rollAngle);
	}

	protected void setupTransforms(SquidEntityRenderState squidEntityRenderState, MatrixStack matrixStack, float f, float g) {
		matrixStack.translate(0.0F, squidEntityRenderState.baby ? 0.25F : 0.5F, 0.0F);
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - f));
		matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(squidEntityRenderState.tiltAngle));
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(squidEntityRenderState.rollAngle));
		matrixStack.translate(0.0F, squidEntityRenderState.baby ? -0.6F : -1.2F, 0.0F);
	}
}
