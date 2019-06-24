package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Arm;

@Environment(EnvType.CLIENT)
public class ArmorStandEntityModel extends ArmorStandArmorEntityModel {
	private final Cuboid field_3314;
	private final Cuboid field_3315;
	private final Cuboid field_3313;
	private final Cuboid field_3312;

	public ArmorStandEntityModel() {
		this(0.0F);
	}

	public ArmorStandEntityModel(float f) {
		super(f, 64, 64);
		this.head = new Cuboid(this, 0, 0);
		this.head.addBox(-1.0F, -7.0F, -1.0F, 2, 7, 2, f);
		this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.body = new Cuboid(this, 0, 26);
		this.body.addBox(-6.0F, 0.0F, -1.5F, 12, 3, 3, f);
		this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.rightArm = new Cuboid(this, 24, 0);
		this.rightArm.addBox(-2.0F, -2.0F, -1.0F, 2, 12, 2, f);
		this.rightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
		this.leftArm = new Cuboid(this, 32, 16);
		this.leftArm.mirror = true;
		this.leftArm.addBox(0.0F, -2.0F, -1.0F, 2, 12, 2, f);
		this.leftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
		this.rightLeg = new Cuboid(this, 8, 0);
		this.rightLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 11, 2, f);
		this.rightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		this.leftLeg = new Cuboid(this, 40, 16);
		this.leftLeg.mirror = true;
		this.leftLeg.addBox(-1.0F, 0.0F, -1.0F, 2, 11, 2, f);
		this.leftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		this.field_3314 = new Cuboid(this, 16, 0);
		this.field_3314.addBox(-3.0F, 3.0F, -1.0F, 2, 7, 2, f);
		this.field_3314.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.field_3314.visible = true;
		this.field_3315 = new Cuboid(this, 48, 16);
		this.field_3315.addBox(1.0F, 3.0F, -1.0F, 2, 7, 2, f);
		this.field_3315.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.field_3313 = new Cuboid(this, 0, 48);
		this.field_3313.addBox(-4.0F, 10.0F, -1.0F, 8, 2, 2, f);
		this.field_3313.setRotationPoint(0.0F, 0.0F, 0.0F);
		this.field_3312 = new Cuboid(this, 0, 32);
		this.field_3312.addBox(-6.0F, 11.0F, -6.0F, 12, 1, 12, f);
		this.field_3312.setRotationPoint(0.0F, 12.0F, 0.0F);
		this.headwear.visible = false;
	}

	@Override
	public void method_17066(ArmorStandEntity armorStandEntity, float f, float g, float h, float i, float j, float k) {
		super.method_17066(armorStandEntity, f, g, h, i, j, k);
		this.leftArm.visible = armorStandEntity.shouldShowArms();
		this.rightArm.visible = armorStandEntity.shouldShowArms();
		this.field_3312.visible = !armorStandEntity.shouldHideBasePlate();
		this.leftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
		this.rightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
		this.field_3314.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getPitch();
		this.field_3314.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getYaw();
		this.field_3314.roll = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getRoll();
		this.field_3315.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getPitch();
		this.field_3315.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getYaw();
		this.field_3315.roll = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getRoll();
		this.field_3313.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getPitch();
		this.field_3313.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getYaw();
		this.field_3313.roll = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getRoll();
		this.field_3312.pitch = 0.0F;
		this.field_3312.yaw = (float) (Math.PI / 180.0) * -armorStandEntity.yaw;
		this.field_3312.roll = 0.0F;
	}

	public void method_17067(ArmorStandEntity armorStandEntity, float f, float g, float h, float i, float j, float k) {
		super.method_17088(armorStandEntity, f, g, h, i, j, k);
		GlStateManager.pushMatrix();
		if (this.isChild) {
			float l = 2.0F;
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * k, 0.0F);
			this.field_3314.render(k);
			this.field_3315.render(k);
			this.field_3313.render(k);
			this.field_3312.render(k);
		} else {
			if (armorStandEntity.isSneaking()) {
				GlStateManager.translatef(0.0F, 0.2F, 0.0F);
			}

			this.field_3314.render(k);
			this.field_3315.render(k);
			this.field_3313.render(k);
			this.field_3312.render(k);
		}

		GlStateManager.popMatrix();
	}

	@Override
	public void setArmAngle(float f, Arm arm) {
		Cuboid cuboid = this.getArm(arm);
		boolean bl = cuboid.visible;
		cuboid.visible = true;
		super.setArmAngle(f, arm);
		cuboid.visible = bl;
	}
}
