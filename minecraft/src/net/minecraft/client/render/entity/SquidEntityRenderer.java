package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class SquidEntityRenderer<T extends SquidEntity> extends MobEntityRenderer<T, SquidEntityModel<T>> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/squid/squid.png");

	public SquidEntityRenderer(EntityRendererFactory.Context ctx, SquidEntityModel<T> model) {
		super(ctx, model, 0.7F);
	}

	public Identifier getTexture(T squidEntity) {
		return TEXTURE;
	}

	protected void setupTransforms(T squidEntity, MatrixStack matrixStack, float f, float g, float h, float i) {
		float j = MathHelper.lerp(h, squidEntity.prevTiltAngle, squidEntity.tiltAngle);
		float k = MathHelper.lerp(h, squidEntity.prevRollAngle, squidEntity.rollAngle);
		matrixStack.translate(0.0F, 0.5F, 0.0F);
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - g));
		matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(j));
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(k));
		matrixStack.translate(0.0F, -1.2F, 0.0F);
	}

	protected float getAnimationProgress(T squidEntity, float f) {
		return MathHelper.lerp(f, squidEntity.prevTentacleAngle, squidEntity.tentacleAngle);
	}
}
