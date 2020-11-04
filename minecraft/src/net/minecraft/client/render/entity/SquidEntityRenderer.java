package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SquidEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SquidEntityRenderer extends MobEntityRenderer<SquidEntity, SquidEntityModel<SquidEntity>> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/squid.png");

	public SquidEntityRenderer(class_5617.class_5618 arg) {
		super(arg, new SquidEntityModel<>(arg.method_32167(EntityModelLayers.SQUID)), 0.7F);
	}

	public Identifier getTexture(SquidEntity squidEntity) {
		return TEXTURE;
	}

	protected void setupTransforms(SquidEntity squidEntity, MatrixStack matrixStack, float f, float g, float h) {
		float i = MathHelper.lerp(h, squidEntity.prevTiltAngle, squidEntity.tiltAngle);
		float j = MathHelper.lerp(h, squidEntity.prevRollAngle, squidEntity.rollAngle);
		matrixStack.translate(0.0, 0.5, 0.0);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F - g));
		matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(i));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(j));
		matrixStack.translate(0.0, -1.2F, 0.0);
	}

	protected float getAnimationProgress(SquidEntity squidEntity, float f) {
		return MathHelper.lerp(f, squidEntity.prevTentacleAngle, squidEntity.tentacleAngle);
	}
}
