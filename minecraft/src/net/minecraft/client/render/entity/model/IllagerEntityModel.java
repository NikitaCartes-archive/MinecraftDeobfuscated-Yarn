package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class IllagerEntityModel<T extends IllagerEntity> extends CompositeEntityModel<T> implements ModelWithArms, ModelWithHead {
	private final ModelPart head;
	private final ModelPart hat;
	private final ModelPart torso;
	private final ModelPart arms;
	private final ModelPart rightLeg;
	private final ModelPart leftLeg;
	private final ModelPart rightAttackingArm;
	private final ModelPart leftAttackingArm;
	private float field_3424;

	public IllagerEntityModel(float f, float g, int i, int j) {
		this.head = new ModelPart(this).setTextureSize(i, j);
		this.head.setPivot(0.0F, 0.0F + g, 0.0F);
		this.head.setTextureOffset(0, 0).addCuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, f);
		this.hat = new ModelPart(this, 32, 0).setTextureSize(i, j);
		this.hat.addCuboid(-4.0F, -10.0F, -4.0F, 8.0F, 12.0F, 8.0F, f + 0.45F);
		this.head.addChild(this.hat);
		this.hat.visible = false;
		ModelPart modelPart = new ModelPart(this).setTextureSize(i, j);
		modelPart.setPivot(0.0F, g - 2.0F, 0.0F);
		modelPart.setTextureOffset(24, 0).addCuboid(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, f);
		this.head.addChild(modelPart);
		this.torso = new ModelPart(this).setTextureSize(i, j);
		this.torso.setPivot(0.0F, 0.0F + g, 0.0F);
		this.torso.setTextureOffset(16, 20).addCuboid(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, f);
		this.torso.setTextureOffset(0, 38).addCuboid(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, f + 0.5F);
		this.arms = new ModelPart(this).setTextureSize(i, j);
		this.arms.setPivot(0.0F, 0.0F + g + 2.0F, 0.0F);
		this.arms.setTextureOffset(44, 22).addCuboid(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, f);
		ModelPart modelPart2 = new ModelPart(this, 44, 22).setTextureSize(i, j);
		modelPart2.mirror = true;
		modelPart2.addCuboid(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, f);
		this.arms.addChild(modelPart2);
		this.arms.setTextureOffset(40, 38).addCuboid(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, f);
		this.rightLeg = new ModelPart(this, 0, 22).setTextureSize(i, j);
		this.rightLeg.setPivot(-2.0F, 12.0F + g, 0.0F);
		this.rightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
		this.leftLeg = new ModelPart(this, 0, 22).setTextureSize(i, j);
		this.leftLeg.mirror = true;
		this.leftLeg.setPivot(2.0F, 12.0F + g, 0.0F);
		this.leftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
		this.rightAttackingArm = new ModelPart(this, 40, 46).setTextureSize(i, j);
		this.rightAttackingArm.addCuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
		this.rightAttackingArm.setPivot(-5.0F, 2.0F + g, 0.0F);
		this.leftAttackingArm = new ModelPart(this, 40, 46).setTextureSize(i, j);
		this.leftAttackingArm.mirror = true;
		this.leftAttackingArm.addCuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, f);
		this.leftAttackingArm.setPivot(5.0F, 2.0F + g, 0.0F);
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.head, this.torso, this.rightLeg, this.leftLeg, this.arms, this.rightAttackingArm, this.leftAttackingArm);
	}

	public void setAngles(T illagerEntity, float f, float g, float h, float i, float j) {
		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.head.pitch = j * (float) (Math.PI / 180.0);
		this.arms.pivotY = 3.0F;
		this.arms.pivotZ = -1.0F;
		this.arms.pitch = -0.75F;
		if (this.riding) {
			this.rightAttackingArm.pitch = (float) (-Math.PI / 5);
			this.rightAttackingArm.yaw = 0.0F;
			this.rightAttackingArm.roll = 0.0F;
			this.leftAttackingArm.pitch = (float) (-Math.PI / 5);
			this.leftAttackingArm.yaw = 0.0F;
			this.leftAttackingArm.roll = 0.0F;
			this.rightLeg.pitch = -1.4137167F;
			this.rightLeg.yaw = (float) (Math.PI / 10);
			this.rightLeg.roll = 0.07853982F;
			this.leftLeg.pitch = -1.4137167F;
			this.leftLeg.yaw = (float) (-Math.PI / 10);
			this.leftLeg.roll = -0.07853982F;
		} else {
			this.rightAttackingArm.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 2.0F * g * 0.5F;
			this.rightAttackingArm.yaw = 0.0F;
			this.rightAttackingArm.roll = 0.0F;
			this.leftAttackingArm.pitch = MathHelper.cos(f * 0.6662F) * 2.0F * g * 0.5F;
			this.leftAttackingArm.yaw = 0.0F;
			this.leftAttackingArm.roll = 0.0F;
			this.rightLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g * 0.5F;
			this.rightLeg.yaw = 0.0F;
			this.rightLeg.roll = 0.0F;
			this.leftLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g * 0.5F;
			this.leftLeg.yaw = 0.0F;
			this.leftLeg.roll = 0.0F;
		}

		IllagerEntity.State state = illagerEntity.getState();
		if (state == IllagerEntity.State.ATTACKING) {
			float k = MathHelper.sin(this.handSwingProgress * (float) Math.PI);
			float l = MathHelper.sin((1.0F - (1.0F - this.handSwingProgress) * (1.0F - this.handSwingProgress)) * (float) Math.PI);
			this.rightAttackingArm.roll = 0.0F;
			this.leftAttackingArm.roll = 0.0F;
			this.rightAttackingArm.yaw = (float) (Math.PI / 20);
			this.leftAttackingArm.yaw = (float) (-Math.PI / 20);
			if (illagerEntity.getMainArm() == Arm.RIGHT) {
				this.rightAttackingArm.pitch = -1.8849558F + MathHelper.cos(h * 0.09F) * 0.15F;
				this.leftAttackingArm.pitch = -0.0F + MathHelper.cos(h * 0.19F) * 0.5F;
				this.rightAttackingArm.pitch += k * 2.2F - l * 0.4F;
				this.leftAttackingArm.pitch += k * 1.2F - l * 0.4F;
			} else {
				this.rightAttackingArm.pitch = -0.0F + MathHelper.cos(h * 0.19F) * 0.5F;
				this.leftAttackingArm.pitch = -1.8849558F + MathHelper.cos(h * 0.09F) * 0.15F;
				this.rightAttackingArm.pitch += k * 1.2F - l * 0.4F;
				this.leftAttackingArm.pitch += k * 2.2F - l * 0.4F;
			}

			this.rightAttackingArm.roll = this.rightAttackingArm.roll + MathHelper.cos(h * 0.09F) * 0.05F + 0.05F;
			this.leftAttackingArm.roll = this.leftAttackingArm.roll - (MathHelper.cos(h * 0.09F) * 0.05F + 0.05F);
			this.rightAttackingArm.pitch = this.rightAttackingArm.pitch + MathHelper.sin(h * 0.067F) * 0.05F;
			this.leftAttackingArm.pitch = this.leftAttackingArm.pitch - MathHelper.sin(h * 0.067F) * 0.05F;
		} else if (state == IllagerEntity.State.SPELLCASTING) {
			this.rightAttackingArm.pivotZ = 0.0F;
			this.rightAttackingArm.pivotX = -5.0F;
			this.leftAttackingArm.pivotZ = 0.0F;
			this.leftAttackingArm.pivotX = 5.0F;
			this.rightAttackingArm.pitch = MathHelper.cos(h * 0.6662F) * 0.25F;
			this.leftAttackingArm.pitch = MathHelper.cos(h * 0.6662F) * 0.25F;
			this.rightAttackingArm.roll = (float) (Math.PI * 3.0 / 4.0);
			this.leftAttackingArm.roll = (float) (-Math.PI * 3.0 / 4.0);
			this.rightAttackingArm.yaw = 0.0F;
			this.leftAttackingArm.yaw = 0.0F;
		} else if (state == IllagerEntity.State.BOW_AND_ARROW) {
			this.rightAttackingArm.yaw = -0.1F + this.head.yaw;
			this.rightAttackingArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
			this.leftAttackingArm.pitch = -0.9424779F + this.head.pitch;
			this.leftAttackingArm.yaw = this.head.yaw - 0.4F;
			this.leftAttackingArm.roll = (float) (Math.PI / 2);
		} else if (state == IllagerEntity.State.CROSSBOW_HOLD) {
			this.rightAttackingArm.yaw = -0.3F + this.head.yaw;
			this.leftAttackingArm.yaw = 0.6F + this.head.yaw;
			this.rightAttackingArm.pitch = (float) (-Math.PI / 2) + this.head.pitch + 0.1F;
			this.leftAttackingArm.pitch = -1.5F + this.head.pitch;
		} else if (state == IllagerEntity.State.CROSSBOW_CHARGE) {
			this.rightAttackingArm.yaw = -0.8F;
			this.rightAttackingArm.pitch = -0.97079635F;
			this.leftAttackingArm.pitch = -0.97079635F;
			float k = MathHelper.clamp(this.field_3424, 0.0F, 25.0F);
			this.leftAttackingArm.yaw = MathHelper.lerp(k / 25.0F, 0.4F, 0.85F);
			this.leftAttackingArm.pitch = MathHelper.lerp(k / 25.0F, this.leftAttackingArm.pitch, (float) (-Math.PI / 2));
		} else if (state == IllagerEntity.State.CELEBRATING) {
			this.rightAttackingArm.pivotZ = 0.0F;
			this.rightAttackingArm.pivotX = -5.0F;
			this.rightAttackingArm.pitch = MathHelper.cos(h * 0.6662F) * 0.05F;
			this.rightAttackingArm.roll = 2.670354F;
			this.rightAttackingArm.yaw = 0.0F;
			this.leftAttackingArm.pivotZ = 0.0F;
			this.leftAttackingArm.pivotX = 5.0F;
			this.leftAttackingArm.pitch = MathHelper.cos(h * 0.6662F) * 0.05F;
			this.leftAttackingArm.roll = (float) (-Math.PI * 3.0 / 4.0);
			this.leftAttackingArm.yaw = 0.0F;
		}

		boolean bl = state == IllagerEntity.State.CROSSED;
		this.arms.visible = bl;
		this.leftAttackingArm.visible = !bl;
		this.rightAttackingArm.visible = !bl;
	}

	public void animateModel(T illagerEntity, float f, float g, float h) {
		this.field_3424 = (float)illagerEntity.getItemUseTime();
		super.animateModel(illagerEntity, f, g, h);
	}

	private ModelPart method_2813(Arm arm) {
		return arm == Arm.LEFT ? this.leftAttackingArm : this.rightAttackingArm;
	}

	public ModelPart method_2812() {
		return this.hat;
	}

	@Override
	public ModelPart getHead() {
		return this.head;
	}

	@Override
	public void setArmAngle(Arm arm, MatrixStack matrixStack) {
		this.method_2813(arm).rotate(matrixStack);
	}
}
