package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.util.math.Dilation;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinActivity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PiglinEntityModel<T extends MobEntity> extends PlayerEntityModel<T> {
	public final ModelPart rightEar = this.head.getChild("right_ear");
	private final ModelPart leftEar = this.head.getChild("left_ear");
	private final ModelTransform torsoRotation = this.torso.getTransform();
	private final ModelTransform headRotation = this.head.getTransform();
	private final ModelTransform leftArmRotation = this.leftArm.getTransform();
	private final ModelTransform rightArmRotation = this.rightArm.getTransform();

	public PiglinEntityModel(ModelPart modelPart) {
		super(modelPart, false);
	}

	public static ModelData getModelData(Dilation dilation) {
		ModelData modelData = PlayerEntityModel.getTexturedModelData(dilation, false);
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild("body", ModelPartBuilder.create().uv(16, 16).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, dilation), ModelTransform.NONE);
		ModelPartData modelPartData2 = modelPartData.addChild(
			"head",
			ModelPartBuilder.create()
				.uv(0, 0)
				.cuboid(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, dilation)
				.uv(31, 1)
				.cuboid(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 1.0F, dilation)
				.uv(2, 4)
				.cuboid(2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, dilation)
				.uv(2, 0)
				.cuboid(-3.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, dilation),
			ModelTransform.NONE
		);
		modelPartData2.addChild(
			"left_ear",
			ModelPartBuilder.create().uv(51, 6).cuboid(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, dilation),
			ModelTransform.of(4.5F, -6.0F, 0.0F, 0.0F, 0.0F, (float) (-Math.PI / 6))
		);
		modelPartData2.addChild(
			"right_ear",
			ModelPartBuilder.create().uv(39, 6).cuboid(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, dilation),
			ModelTransform.of(-4.5F, -6.0F, 0.0F, 0.0F, 0.0F, (float) (Math.PI / 6))
		);
		modelPartData.addChild("hat", ModelPartBuilder.create(), ModelTransform.NONE);
		return modelData;
	}

	public void setAngles(T mobEntity, float f, float g, float h, float i, float j) {
		this.torso.setTransform(this.torsoRotation);
		this.head.setTransform(this.headRotation);
		this.leftArm.setTransform(this.leftArmRotation);
		this.rightArm.setTransform(this.rightArmRotation);
		super.setAngles(mobEntity, f, g, h, i, j);
		float k = (float) (Math.PI / 6);
		float l = h * 0.1F + f * 0.5F;
		float m = 0.08F + g * 0.4F;
		this.leftEar.roll = (float) (-Math.PI / 6) - MathHelper.cos(l * 1.2F) * m;
		this.rightEar.roll = (float) (Math.PI / 6) + MathHelper.cos(l) * m;
		if (mobEntity instanceof AbstractPiglinEntity) {
			AbstractPiglinEntity abstractPiglinEntity = (AbstractPiglinEntity)mobEntity;
			PiglinActivity piglinActivity = abstractPiglinEntity.getActivity();
			if (piglinActivity == PiglinActivity.DANCING) {
				float n = h / 60.0F;
				this.rightEar.roll = (float) (Math.PI / 6) + (float) (Math.PI / 180.0) * MathHelper.sin(n * 30.0F) * 10.0F;
				this.leftEar.roll = (float) (-Math.PI / 6) - (float) (Math.PI / 180.0) * MathHelper.cos(n * 30.0F) * 10.0F;
				this.head.pivotX = MathHelper.sin(n * 10.0F);
				this.head.pivotY = MathHelper.sin(n * 40.0F) + 0.4F;
				this.rightArm.roll = (float) (Math.PI / 180.0) * (70.0F + MathHelper.cos(n * 40.0F) * 10.0F);
				this.leftArm.roll = this.rightArm.roll * -1.0F;
				this.rightArm.pivotY = MathHelper.sin(n * 40.0F) * 0.5F + 1.5F;
				this.leftArm.pivotY = MathHelper.sin(n * 40.0F) * 0.5F + 1.5F;
				this.torso.pivotY = MathHelper.sin(n * 40.0F) * 0.35F;
			} else if (piglinActivity == PiglinActivity.ATTACKING_WITH_MELEE_WEAPON && this.handSwingProgress == 0.0F) {
				this.method_29354(mobEntity);
			} else if (piglinActivity == PiglinActivity.CROSSBOW_HOLD) {
				CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, !mobEntity.isLeftHanded());
			} else if (piglinActivity == PiglinActivity.CROSSBOW_CHARGE) {
				CrossbowPosing.charge(this.rightArm, this.leftArm, mobEntity, !mobEntity.isLeftHanded());
			} else if (piglinActivity == PiglinActivity.ADMIRING_ITEM) {
				this.head.pitch = 0.5F;
				this.head.yaw = 0.0F;
				if (mobEntity.isLeftHanded()) {
					this.rightArm.yaw = -0.5F;
					this.rightArm.pitch = -0.9F;
				} else {
					this.leftArm.yaw = 0.5F;
					this.leftArm.pitch = -0.9F;
				}
			}
		} else if (mobEntity.getType() == EntityType.ZOMBIFIED_PIGLIN) {
			CrossbowPosing.method_29352(this.leftArm, this.rightArm, mobEntity.isAttacking(), this.handSwingProgress, h);
		}

		this.leftPantLeg.copyTransform(this.leftLeg);
		this.rightPantLeg.copyTransform(this.rightLeg);
		this.leftSleeve.copyTransform(this.leftArm);
		this.rightSleeve.copyTransform(this.rightArm);
		this.jacket.copyTransform(this.torso);
		this.helmet.copyTransform(this.head);
	}

	protected void method_29353(T mobEntity, float f) {
		if (this.handSwingProgress > 0.0F
			&& mobEntity instanceof PiglinEntity
			&& ((PiglinEntity)mobEntity).getActivity() == PiglinActivity.ATTACKING_WITH_MELEE_WEAPON) {
			CrossbowPosing.method_29351(this.rightArm, this.leftArm, mobEntity, this.handSwingProgress, f);
		} else {
			super.method_29353(mobEntity, f);
		}
	}

	private void method_29354(T mobEntity) {
		if (mobEntity.isLeftHanded()) {
			this.leftArm.pitch = -1.8F;
		} else {
			this.rightArm.pitch = -1.8F;
		}
	}
}
