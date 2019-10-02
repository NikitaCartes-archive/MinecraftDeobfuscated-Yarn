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
	public DrownedEntityModel(float f, float g, int i, int j) {
		super(f, g, i, j);
		this.rightArm = new ModelPart(this, 32, 48);
		this.rightArm.addCuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
		this.rightArm.setPivot(-5.0F, 2.0F + g, 0.0F);
		this.rightLeg = new ModelPart(this, 16, 48);
		this.rightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
		this.rightLeg.setPivot(-1.9F, 12.0F + g, 0.0F);
	}

	public DrownedEntityModel(float f, boolean bl) {
		super(f, 0.0F, 64, bl ? 32 : 64);
	}

	public void method_17077(T zombieEntity, float f, float g, float h) {
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

		super.method_17086(zombieEntity, f, g, h);
	}

	public void method_17134(T zombieEntity, float f, float g, float h, float i, float j, float k) {
		super.method_17791(zombieEntity, f, g, h, i, j, k);
		if (this.leftArmPose == BipedEntityModel.ArmPose.THROW_SPEAR) {
			this.leftArm.pitch = this.leftArm.pitch * 0.5F - (float) Math.PI;
			this.leftArm.yaw = 0.0F;
		}

		if (this.rightArmPose == BipedEntityModel.ArmPose.THROW_SPEAR) {
			this.rightArm.pitch = this.rightArm.pitch * 0.5F - (float) Math.PI;
			this.rightArm.yaw = 0.0F;
		}

		if (this.field_3396 > 0.0F) {
			this.rightArm.pitch = this.method_2804(this.rightArm.pitch, (float) (-Math.PI * 4.0 / 5.0), this.field_3396)
				+ this.field_3396 * 0.35F * MathHelper.sin(0.1F * h);
			this.leftArm.pitch = this.method_2804(this.leftArm.pitch, (float) (-Math.PI * 4.0 / 5.0), this.field_3396)
				- this.field_3396 * 0.35F * MathHelper.sin(0.1F * h);
			this.rightArm.roll = this.method_2804(this.rightArm.roll, -0.15F, this.field_3396);
			this.leftArm.roll = this.method_2804(this.leftArm.roll, 0.15F, this.field_3396);
			this.leftLeg.pitch = this.leftLeg.pitch - this.field_3396 * 0.55F * MathHelper.sin(0.1F * h);
			this.rightLeg.pitch = this.rightLeg.pitch + this.field_3396 * 0.55F * MathHelper.sin(0.1F * h);
			this.head.pitch = 0.0F;
		}
	}
}
