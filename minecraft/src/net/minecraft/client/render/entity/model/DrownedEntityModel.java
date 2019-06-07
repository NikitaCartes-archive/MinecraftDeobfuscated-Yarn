package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
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
		this.rightArm = new Cuboid(this, 32, 48);
		this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, f);
		this.rightArm.setRotationPoint(-5.0F, 2.0F + g, 0.0F);
		this.rightLeg = new Cuboid(this, 16, 48);
		this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.rightLeg.setRotationPoint(-1.9F, 12.0F + g, 0.0F);
	}

	public DrownedEntityModel(float f, boolean bl) {
		super(f, 0.0F, 64, bl ? 32 : 64);
	}

	public void method_17077(T zombieEntity, float f, float g, float h) {
		this.rightArmPose = BipedEntityModel.ArmPose.field_3409;
		this.leftArmPose = BipedEntityModel.ArmPose.field_3409;
		ItemStack itemStack = zombieEntity.getStackInHand(Hand.field_5808);
		if (itemStack.getItem() == Items.field_8547 && zombieEntity.isAttacking()) {
			if (zombieEntity.getMainArm() == Arm.field_6183) {
				this.rightArmPose = BipedEntityModel.ArmPose.field_3407;
			} else {
				this.leftArmPose = BipedEntityModel.ArmPose.field_3407;
			}
		}

		super.method_17086(zombieEntity, f, g, h);
	}

	public void method_17134(T zombieEntity, float f, float g, float h, float i, float j, float k) {
		super.method_17791(zombieEntity, f, g, h, i, j, k);
		if (this.leftArmPose == BipedEntityModel.ArmPose.field_3407) {
			this.leftArm.pitch = this.leftArm.pitch * 0.5F - (float) Math.PI;
			this.leftArm.yaw = 0.0F;
		}

		if (this.rightArmPose == BipedEntityModel.ArmPose.field_3407) {
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
