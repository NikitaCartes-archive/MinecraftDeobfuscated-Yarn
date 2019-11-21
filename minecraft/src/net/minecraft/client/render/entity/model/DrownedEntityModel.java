package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DrownedEntityModel<T extends ZombieEntity> extends ZombieEntityModel<T> {
	public DrownedEntityModel(float scale, float f, int textureWidth, int i) {
		super(scale, f, textureWidth, i);
		this.rightArm = new ModelPart(this, 32, 48);
		this.rightArm.addCuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale);
		this.rightArm.setPivot(-5.0F, 2.0F + f, 0.0F);
		this.rightLeg = new ModelPart(this, 16, 48);
		this.rightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale);
		this.rightLeg.setPivot(-1.9F, 12.0F + f, 0.0F);
	}

	public DrownedEntityModel(float f, boolean bl) {
		super(f, 0.0F, 64, bl ? 32 : 64);
	}

	public void animateModel(T zombieEntity, float f, float g, float h) {
		this.rightArmPose = BipedEntityModel.ArmPose.EMPTY;
		this.leftArmPose = BipedEntityModel.ArmPose.EMPTY;
		ItemStack itemStack = zombieEntity.getStackInHand(Hand.MAIN_HAND);
		if (itemStack.getItem() == Items.TRIDENT && zombieEntity.isAttacking()) {
			if (zombieEntity.getMainArm() == Arm.RIGHT) {
				this.rightArmPose = BipedEntityModel.ArmPose.THROW_SPEAR;
			} else {
				this.leftArmPose = BipedEntityModel.ArmPose.THROW_SPEAR;
			}
		}

		super.animateModel(zombieEntity, f, g, h);
	}

	public void setAngles(T zombieEntity, float f, float g, float h, float i, float j) {
		super.setAngles(zombieEntity, f, g, h, i, j);
		if (this.leftArmPose == BipedEntityModel.ArmPose.THROW_SPEAR) {
			this.leftArm.pitch = this.leftArm.pitch * 0.5F - (float) Math.PI;
			this.leftArm.yaw = 0.0F;
		}

		if (this.rightArmPose == BipedEntityModel.ArmPose.THROW_SPEAR) {
			this.rightArm.pitch = this.rightArm.pitch * 0.5F - (float) Math.PI;
			this.rightArm.yaw = 0.0F;
		}

		if (this.field_3396 > 0.0F) {
			this.rightArm.pitch = this.lerpAngle(this.rightArm.pitch, (float) (-Math.PI * 4.0 / 5.0), this.field_3396)
				+ this.field_3396 * 0.35F * MathHelper.sin(0.1F * h);
			this.leftArm.pitch = this.lerpAngle(this.leftArm.pitch, (float) (-Math.PI * 4.0 / 5.0), this.field_3396)
				- this.field_3396 * 0.35F * MathHelper.sin(0.1F * h);
			this.rightArm.roll = this.lerpAngle(this.rightArm.roll, -0.15F, this.field_3396);
			this.leftArm.roll = this.lerpAngle(this.leftArm.roll, 0.15F, this.field_3396);
			this.leftLeg.pitch = this.leftLeg.pitch - this.field_3396 * 0.55F * MathHelper.sin(0.1F * h);
			this.rightLeg.pitch = this.rightLeg.pitch + this.field_3396 * 0.55F * MathHelper.sin(0.1F * h);
			this.head.pitch = 0.0F;
		}
	}
}
