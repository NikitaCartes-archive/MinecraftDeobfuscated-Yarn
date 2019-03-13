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
		this.field_3398.pitch = (float) (Math.PI / 180.0) * armorStandEntity.method_6921().getX();
		this.field_3398.yaw = (float) (Math.PI / 180.0) * armorStandEntity.method_6921().getY();
		this.field_3398.roll = (float) (Math.PI / 180.0) * armorStandEntity.method_6921().getZ();
		this.field_3398.setRotationPoint(0.0F, 1.0F, 0.0F);
		this.field_3391.pitch = (float) (Math.PI / 180.0) * armorStandEntity.method_6923().getX();
		this.field_3391.yaw = (float) (Math.PI / 180.0) * armorStandEntity.method_6923().getY();
		this.field_3391.roll = (float) (Math.PI / 180.0) * armorStandEntity.method_6923().getZ();
		this.field_3390.pitch = (float) (Math.PI / 180.0) * armorStandEntity.method_6930().getX();
		this.field_3390.yaw = (float) (Math.PI / 180.0) * armorStandEntity.method_6930().getY();
		this.field_3390.roll = (float) (Math.PI / 180.0) * armorStandEntity.method_6930().getZ();
		this.field_3401.pitch = (float) (Math.PI / 180.0) * armorStandEntity.method_6903().getX();
		this.field_3401.yaw = (float) (Math.PI / 180.0) * armorStandEntity.method_6903().getY();
		this.field_3401.roll = (float) (Math.PI / 180.0) * armorStandEntity.method_6903().getZ();
		this.field_3397.pitch = (float) (Math.PI / 180.0) * armorStandEntity.method_6917().getX();
		this.field_3397.yaw = (float) (Math.PI / 180.0) * armorStandEntity.method_6917().getY();
		this.field_3397.roll = (float) (Math.PI / 180.0) * armorStandEntity.method_6917().getZ();
		this.field_3397.setRotationPoint(1.9F, 11.0F, 0.0F);
		this.field_3392.pitch = (float) (Math.PI / 180.0) * armorStandEntity.method_6900().getX();
		this.field_3392.yaw = (float) (Math.PI / 180.0) * armorStandEntity.method_6900().getY();
		this.field_3392.roll = (float) (Math.PI / 180.0) * armorStandEntity.method_6900().getZ();
		this.field_3392.setRotationPoint(-1.9F, 11.0F, 0.0F);
		this.field_3394.copyRotation(this.field_3398);
	}
}
