package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class StrayEntityModel<T extends MobEntity & RangedAttackMob> extends BipedEntityModel<T> {
	public StrayEntityModel() {
		this(0.0F, false);
	}

	public StrayEntityModel(float f, boolean bl) {
		super(f, 0.0F, 64, 32);
		if (!bl) {
			this.rightArm = new Cuboid(this, 40, 16);
			this.rightArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, f);
			this.rightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
			this.leftArm = new Cuboid(this, 40, 16);
			this.leftArm.mirror = true;
			this.leftArm.addBox(-1.0F, -2.0F, -1.0F, 2, 12, 2, f);
			this.leftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
			this.rightLeg = new Cuboid(this, 0, 16);
			this.rightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, f);
			this.rightLeg.setRotationPoint(-2.0F, 12.0F, 0.0F);
			this.leftLeg = new Cuboid(this, 0, 16);
			this.leftLeg.mirror = true;
			this.leftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 12, 2, f);
			this.leftLeg.setRotationPoint(2.0F, 12.0F, 0.0F);
		}
	}

	public void method_19689(T mobEntity, float f, float g, float h) {
		this.rightArmPose = BipedEntityModel.ArmPose.EMPTY;
		this.leftArmPose = BipedEntityModel.ArmPose.EMPTY;
		ItemStack itemStack = mobEntity.getStackInHand(Hand.MAIN_HAND);
		if (itemStack.getItem() == Items.BOW && mobEntity.isAttacking()) {
			if (mobEntity.getMainArm() == Arm.RIGHT) {
				this.rightArmPose = BipedEntityModel.ArmPose.BOW_AND_ARROW;
			} else {
				this.leftArmPose = BipedEntityModel.ArmPose.BOW_AND_ARROW;
			}
		}

		super.method_17086(mobEntity, f, g, h);
	}

	public void method_19690(T mobEntity, float f, float g, float h, float i, float j, float k) {
		super.method_17087(mobEntity, f, g, h, i, j, k);
		ItemStack itemStack = mobEntity.getMainHandStack();
		if (mobEntity.isAttacking() && (itemStack.isEmpty() || itemStack.getItem() != Items.BOW)) {
			float l = MathHelper.sin(this.handSwingProgress * (float) Math.PI);
			float m = MathHelper.sin((1.0F - (1.0F - this.handSwingProgress) * (1.0F - this.handSwingProgress)) * (float) Math.PI);
			this.rightArm.roll = 0.0F;
			this.leftArm.roll = 0.0F;
			this.rightArm.yaw = -(0.1F - l * 0.6F);
			this.leftArm.yaw = 0.1F - l * 0.6F;
			this.rightArm.pitch = (float) (-Math.PI / 2);
			this.leftArm.pitch = (float) (-Math.PI / 2);
			this.rightArm.pitch -= l * 1.2F - m * 0.4F;
			this.leftArm.pitch -= l * 1.2F - m * 0.4F;
			this.rightArm.roll = this.rightArm.roll + MathHelper.cos(h * 0.09F) * 0.05F + 0.05F;
			this.leftArm.roll = this.leftArm.roll - (MathHelper.cos(h * 0.09F) * 0.05F + 0.05F);
			this.rightArm.pitch = this.rightArm.pitch + MathHelper.sin(h * 0.067F) * 0.05F;
			this.leftArm.pitch = this.leftArm.pitch - MathHelper.sin(h * 0.067F) * 0.05F;
		}
	}

	@Override
	public void setArmAngle(float f, Arm arm) {
		float g = arm == Arm.RIGHT ? 1.0F : -1.0F;
		Cuboid cuboid = this.getArm(arm);
		cuboid.rotationPointX += g;
		cuboid.applyTransform(f);
		cuboid.rotationPointX -= g;
	}
}
