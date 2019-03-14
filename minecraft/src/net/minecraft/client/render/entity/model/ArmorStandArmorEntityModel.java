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

	protected ArmorStandArmorEntityModel(float f, int i, int j) {
		super(f, 0.0F, i, j);
	}

	public void method_17066(ArmorStandEntity armorStandEntity, float f, float g, float h, float i, float j, float k) {
		this.head.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getHeadRotation().getX();
		this.head.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getHeadRotation().getY();
		this.head.roll = (float) (Math.PI / 180.0) * armorStandEntity.getHeadRotation().getZ();
		this.head.setRotationPoint(0.0F, 1.0F, 0.0F);
		this.body.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getX();
		this.body.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getY();
		this.body.roll = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getZ();
		this.armLeft.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getLeftArmRotation().getX();
		this.armLeft.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getLeftArmRotation().getY();
		this.armLeft.roll = (float) (Math.PI / 180.0) * armorStandEntity.getLeftArmRotation().getZ();
		this.armRight.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getRightArmRotation().getX();
		this.armRight.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getRightArmRotation().getY();
		this.armRight.roll = (float) (Math.PI / 180.0) * armorStandEntity.getRightArmRotation().getZ();
		this.legLeft.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getLeftLegRotation().getX();
		this.legLeft.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getLeftLegRotation().getY();
		this.legLeft.roll = (float) (Math.PI / 180.0) * armorStandEntity.getLeftLegRotation().getZ();
		this.legLeft.setRotationPoint(1.9F, 11.0F, 0.0F);
		this.legRight.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getRightLegRotation().getX();
		this.legRight.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getRightLegRotation().getY();
		this.legRight.roll = (float) (Math.PI / 180.0) * armorStandEntity.getRightLegRotation().getZ();
		this.legRight.setRotationPoint(-1.9F, 11.0F, 0.0F);
		this.headwear.copyRotation(this.head);
	}
}
