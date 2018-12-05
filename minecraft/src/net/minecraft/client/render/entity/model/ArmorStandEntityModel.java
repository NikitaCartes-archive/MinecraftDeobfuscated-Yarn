package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.sortme.OptionMainHand;

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
		this.armRight = new Cuboid(this, 24, 0);
		this.armRight.addBox(-2.0F, -2.0F, -1.0F, 2, 12, 2, f);
		this.armRight.setRotationPoint(-5.0F, 2.0F, 0.0F);
		this.armLeft = new Cuboid(this, 32, 16);
		this.armLeft.mirror = true;
		this.armLeft.addBox(0.0F, -2.0F, -1.0F, 2, 12, 2, f);
		this.armLeft.setRotationPoint(5.0F, 2.0F, 0.0F);
		this.legRight = new Cuboid(this, 8, 0);
		this.legRight.addBox(-1.0F, 0.0F, -1.0F, 2, 11, 2, f);
		this.legRight.setRotationPoint(-1.9F, 12.0F, 0.0F);
		this.legLeft = new Cuboid(this, 40, 16);
		this.legLeft.mirror = true;
		this.legLeft.addBox(-1.0F, 0.0F, -1.0F, 2, 11, 2, f);
		this.legLeft.setRotationPoint(1.9F, 12.0F, 0.0F);
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
	public void setRotationAngles(float f, float g, float h, float i, float j, float k, Entity entity) {
		super.setRotationAngles(f, g, h, i, j, k, entity);
		if (entity instanceof ArmorStandEntity) {
			ArmorStandEntity armorStandEntity = (ArmorStandEntity)entity;
			this.armLeft.visible = armorStandEntity.shouldShowArms();
			this.armRight.visible = armorStandEntity.shouldShowArms();
			this.field_3312.visible = !armorStandEntity.shouldHideBasePlate();
			this.legLeft.setRotationPoint(1.9F, 12.0F, 0.0F);
			this.legRight.setRotationPoint(-1.9F, 12.0F, 0.0F);
			this.field_3314.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getX();
			this.field_3314.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getY();
			this.field_3314.roll = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getZ();
			this.field_3315.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getX();
			this.field_3315.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getY();
			this.field_3315.roll = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getZ();
			this.field_3313.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getX();
			this.field_3313.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getY();
			this.field_3313.roll = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getZ();
			this.field_3312.pitch = 0.0F;
			this.field_3312.yaw = (float) (Math.PI / 180.0) * -entity.yaw;
			this.field_3312.roll = 0.0F;
		}
	}

	@Override
	public void render(Entity entity, float f, float g, float h, float i, float j, float k) {
		super.render(entity, f, g, h, i, j, k);
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
			if (entity.isSneaking()) {
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
	public void method_2803(float f, OptionMainHand optionMainHand) {
		Cuboid cuboid = this.getArm(optionMainHand);
		boolean bl = cuboid.visible;
		cuboid.visible = true;
		super.method_2803(f, optionMainHand);
		cuboid.visible = bl;
	}
}
