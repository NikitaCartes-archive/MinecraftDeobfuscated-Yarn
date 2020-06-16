package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PiglinEntityModel<T extends MobEntity> extends PlayerEntityModel<T> {
	/**
	 * Maybe the ears are swapped
	 */
	public final ModelPart rightEar;
	public final ModelPart leftEar;
	private final ModelPart field_25634;
	private final ModelPart field_25635;
	private final ModelPart field_25632;
	private final ModelPart field_25633;

	public PiglinEntityModel(float scale, int textureWidth, int textureHeight) {
		super(scale, false);
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.torso = new ModelPart(this, 16, 16);
		this.torso.addCuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, scale);
		this.head = new ModelPart(this);
		this.head.setTextureOffset(0, 0).addCuboid(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F, scale);
		this.head.setTextureOffset(31, 1).addCuboid(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 1.0F, scale);
		this.head.setTextureOffset(2, 4).addCuboid(2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, scale);
		this.head.setTextureOffset(2, 0).addCuboid(-3.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F, scale);
		this.rightEar = new ModelPart(this);
		this.rightEar.setPivot(4.5F, -6.0F, 0.0F);
		this.rightEar.setTextureOffset(51, 6).addCuboid(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, scale);
		this.head.addChild(this.rightEar);
		this.leftEar = new ModelPart(this);
		this.leftEar.setPivot(-4.5F, -6.0F, 0.0F);
		this.leftEar.setTextureOffset(39, 6).addCuboid(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F, scale);
		this.head.addChild(this.leftEar);
		this.helmet = new ModelPart(this);
		this.field_25634 = this.torso.method_29991();
		this.field_25635 = this.head.method_29991();
		this.field_25632 = this.leftArm.method_29991();
		this.field_25633 = this.leftArm.method_29991();
	}

	public void setAngles(T mobEntity, float f, float g, float h, float i, float j) {
		this.torso.copyPositionAndRotation(this.field_25634);
		this.head.copyPositionAndRotation(this.field_25635);
		this.leftArm.copyPositionAndRotation(this.field_25632);
		this.rightArm.copyPositionAndRotation(this.field_25633);
		super.setAngles(mobEntity, f, g, h, i, j);
		float k = (float) (Math.PI / 6);
		float l = h * 0.1F + f * 0.5F;
		float m = 0.08F + g * 0.4F;
		this.rightEar.roll = (float) (-Math.PI / 6) - MathHelper.cos(l * 1.2F) * m;
		this.leftEar.roll = (float) (Math.PI / 6) + MathHelper.cos(l) * m;
		if (mobEntity.getType() == EntityType.PIGLIN) {
			PiglinEntity piglinEntity = (PiglinEntity)mobEntity;
			PiglinEntity.Activity activity = piglinEntity.getActivity();
			if (activity == PiglinEntity.Activity.DANCING) {
				float n = h / 60.0F;
				this.leftEar.roll = (float) (Math.PI / 6) + (float) (Math.PI / 180.0) * MathHelper.sin(n * 30.0F) * 10.0F;
				this.rightEar.roll = (float) (-Math.PI / 6) - (float) (Math.PI / 180.0) * MathHelper.cos(n * 30.0F) * 10.0F;
				this.head.pivotX = MathHelper.sin(n * 10.0F);
				this.head.pivotY = MathHelper.sin(n * 40.0F) + 0.4F;
				this.rightArm.roll = (float) (Math.PI / 180.0) * (70.0F + MathHelper.cos(n * 40.0F) * 10.0F);
				this.leftArm.roll = this.rightArm.roll * -1.0F;
				this.rightArm.pivotY = MathHelper.sin(n * 40.0F) * 0.5F + 1.5F;
				this.leftArm.pivotY = MathHelper.sin(n * 40.0F) * 0.5F + 1.5F;
				this.torso.pivotY = MathHelper.sin(n * 40.0F) * 0.35F;
			} else if (activity == PiglinEntity.Activity.ATTACKING_WITH_MELEE_WEAPON && this.handSwingProgress == 0.0F) {
				this.method_29354(mobEntity);
			} else if (activity == PiglinEntity.Activity.CROSSBOW_HOLD) {
				CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, !mobEntity.isLeftHanded());
			} else if (activity == PiglinEntity.Activity.CROSSBOW_CHARGE) {
				CrossbowPosing.charge(this.rightArm, this.leftArm, mobEntity, !mobEntity.isLeftHanded());
			} else if (activity == PiglinEntity.Activity.ADMIRING_ITEM) {
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

		this.leftPantLeg.copyPositionAndRotation(this.leftLeg);
		this.rightPantLeg.copyPositionAndRotation(this.rightLeg);
		this.leftSleeve.copyPositionAndRotation(this.leftArm);
		this.rightSleeve.copyPositionAndRotation(this.rightArm);
		this.jacket.copyPositionAndRotation(this.torso);
	}

	protected void method_29353(T mobEntity, float f) {
		if (this.handSwingProgress > 0.0F
			&& mobEntity instanceof PiglinEntity
			&& ((PiglinEntity)mobEntity).getActivity() == PiglinEntity.Activity.ATTACKING_WITH_MELEE_WEAPON) {
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
