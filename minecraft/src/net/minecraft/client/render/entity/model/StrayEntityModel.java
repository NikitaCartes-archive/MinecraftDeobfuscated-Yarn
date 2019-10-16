package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
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
		super(f);
		if (!bl) {
			this.rightArm = new ModelPart(this, 40, 16);
			this.rightArm.addCuboid(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, f);
			this.rightArm.setPivot(-5.0F, 2.0F, 0.0F);
			this.leftArm = new ModelPart(this, 40, 16);
			this.leftArm.mirror = true;
			this.leftArm.addCuboid(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, f);
			this.leftArm.setPivot(5.0F, 2.0F, 0.0F);
			this.rightLeg = new ModelPart(this, 0, 16);
			this.rightLeg.addCuboid(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, f);
			this.rightLeg.setPivot(-2.0F, 12.0F, 0.0F);
			this.leftLeg = new ModelPart(this, 0, 16);
			this.leftLeg.mirror = true;
			this.leftLeg.addCuboid(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F, f);
			this.leftLeg.setPivot(2.0F, 12.0F, 0.0F);
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
	public void setArmAngle(float f, Arm arm, MatrixStack matrixStack) {
		float g = arm == Arm.RIGHT ? 1.0F : -1.0F;
		ModelPart modelPart = this.getArm(arm);
		modelPart.pivotX += g;
		modelPart.rotate(matrixStack, f);
		modelPart.pivotX -= g;
	}
}
