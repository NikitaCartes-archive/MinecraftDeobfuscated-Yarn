package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.decoration.ArmorStandEntity;

@Environment(EnvType.CLIENT)
public class ArmorStandArmorEntityModel extends BipedEntityModel<ArmorStandEntity> {
	public ArmorStandArmorEntityModel() {
		this(0.0F);
	}

	public ArmorStandArmorEntityModel(float f) {
		this(f, 64, 32);
	}

	protected ArmorStandArmorEntityModel(float scale, int textureWidth, int textureHeight) {
		super(scale, 0.0F, textureWidth, textureHeight);
	}

	public void setAngles(ArmorStandEntity armorStandEntity, float f, float g, float h, float i, float j, float k) {
		this.head.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getHeadRotation().getPitch();
		this.head.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getHeadRotation().getYaw();
		this.head.roll = (float) (Math.PI / 180.0) * armorStandEntity.getHeadRotation().getRoll();
		this.head.setPivot(0.0F, 1.0F, 0.0F);
		this.torso.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getPitch();
		this.torso.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getYaw();
		this.torso.roll = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getRoll();
		this.leftArm.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getLeftArmRotation().getPitch();
		this.leftArm.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getLeftArmRotation().getYaw();
		this.leftArm.roll = (float) (Math.PI / 180.0) * armorStandEntity.getLeftArmRotation().getRoll();
		this.rightArm.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getRightArmRotation().getPitch();
		this.rightArm.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getRightArmRotation().getYaw();
		this.rightArm.roll = (float) (Math.PI / 180.0) * armorStandEntity.getRightArmRotation().getRoll();
		this.leftLeg.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getLeftLegRotation().getPitch();
		this.leftLeg.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getLeftLegRotation().getYaw();
		this.leftLeg.roll = (float) (Math.PI / 180.0) * armorStandEntity.getLeftLegRotation().getRoll();
		this.leftLeg.setPivot(1.9F, 11.0F, 0.0F);
		this.rightLeg.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getRightLegRotation().getPitch();
		this.rightLeg.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getRightLegRotation().getYaw();
		this.rightLeg.roll = (float) (Math.PI / 180.0) * armorStandEntity.getRightLegRotation().getRoll();
		this.rightLeg.setPivot(-1.9F, 11.0F, 0.0F);
		this.helmet.copyPositionAndRotation(this.head);
	}
}
