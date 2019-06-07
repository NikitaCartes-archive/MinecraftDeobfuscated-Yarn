package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class EvilVillagerEntityModel<T extends IllagerEntity> extends EntityModel<T> implements ModelWithArms, ModelWithHead {
	protected final Cuboid field_3422;
	private final Cuboid field_3419;
	protected final Cuboid field_3425;
	protected final Cuboid field_3423;
	protected final Cuboid field_3420;
	protected final Cuboid field_3418;
	private final Cuboid field_3421;
	protected final Cuboid field_3426;
	protected final Cuboid field_3417;
	private float field_3424;

	public EvilVillagerEntityModel(float f, float g, int i, int j) {
		this.field_3422 = new Cuboid(this).setTextureSize(i, j);
		this.field_3422.setRotationPoint(0.0F, 0.0F + g, 0.0F);
		this.field_3422.setTextureOffset(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, f);
		this.field_3419 = new Cuboid(this, 32, 0).setTextureSize(i, j);
		this.field_3419.addBox(-4.0F, -10.0F, -4.0F, 8, 12, 8, f + 0.45F);
		this.field_3422.addChild(this.field_3419);
		this.field_3419.visible = false;
		this.field_3421 = new Cuboid(this).setTextureSize(i, j);
		this.field_3421.setRotationPoint(0.0F, g - 2.0F, 0.0F);
		this.field_3421.setTextureOffset(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2, 4, 2, f);
		this.field_3422.addChild(this.field_3421);
		this.field_3425 = new Cuboid(this).setTextureSize(i, j);
		this.field_3425.setRotationPoint(0.0F, 0.0F + g, 0.0F);
		this.field_3425.setTextureOffset(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8, 12, 6, f);
		this.field_3425.setTextureOffset(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8, 18, 6, f + 0.5F);
		this.field_3423 = new Cuboid(this).setTextureSize(i, j);
		this.field_3423.setRotationPoint(0.0F, 0.0F + g + 2.0F, 0.0F);
		this.field_3423.setTextureOffset(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4, 8, 4, f);
		Cuboid cuboid = new Cuboid(this, 44, 22).setTextureSize(i, j);
		cuboid.mirror = true;
		cuboid.addBox(4.0F, -2.0F, -2.0F, 4, 8, 4, f);
		this.field_3423.addChild(cuboid);
		this.field_3423.setTextureOffset(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8, 4, 4, f);
		this.field_3420 = new Cuboid(this, 0, 22).setTextureSize(i, j);
		this.field_3420.setRotationPoint(-2.0F, 12.0F + g, 0.0F);
		this.field_3420.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.field_3418 = new Cuboid(this, 0, 22).setTextureSize(i, j);
		this.field_3418.mirror = true;
		this.field_3418.setRotationPoint(2.0F, 12.0F + g, 0.0F);
		this.field_3418.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.field_3426 = new Cuboid(this, 40, 46).setTextureSize(i, j);
		this.field_3426.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, f);
		this.field_3426.setRotationPoint(-5.0F, 2.0F + g, 0.0F);
		this.field_3417 = new Cuboid(this, 40, 46).setTextureSize(i, j);
		this.field_3417.mirror = true;
		this.field_3417.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, f);
		this.field_3417.setRotationPoint(5.0F, 2.0F + g, 0.0F);
	}

	public void method_17093(T illagerEntity, float f, float g, float h, float i, float j, float k) {
		this.method_17094(illagerEntity, f, g, h, i, j, k);
		this.field_3422.render(k);
		this.field_3425.render(k);
		this.field_3420.render(k);
		this.field_3418.render(k);
		if (illagerEntity.getState() == IllagerEntity.State.field_7207) {
			this.field_3423.render(k);
		} else {
			this.field_3426.render(k);
			this.field_3417.render(k);
		}
	}

	public void method_17094(T illagerEntity, float f, float g, float h, float i, float j, float k) {
		this.field_3422.yaw = i * (float) (Math.PI / 180.0);
		this.field_3422.pitch = j * (float) (Math.PI / 180.0);
		this.field_3423.rotationPointY = 3.0F;
		this.field_3423.rotationPointZ = -1.0F;
		this.field_3423.pitch = -0.75F;
		if (this.isRiding) {
			this.field_3426.pitch = (float) (-Math.PI / 5);
			this.field_3426.yaw = 0.0F;
			this.field_3426.roll = 0.0F;
			this.field_3417.pitch = (float) (-Math.PI / 5);
			this.field_3417.yaw = 0.0F;
			this.field_3417.roll = 0.0F;
			this.field_3420.pitch = -1.4137167F;
			this.field_3420.yaw = (float) (Math.PI / 10);
			this.field_3420.roll = 0.07853982F;
			this.field_3418.pitch = -1.4137167F;
			this.field_3418.yaw = (float) (-Math.PI / 10);
			this.field_3418.roll = -0.07853982F;
		} else {
			this.field_3426.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 2.0F * g * 0.5F;
			this.field_3426.yaw = 0.0F;
			this.field_3426.roll = 0.0F;
			this.field_3417.pitch = MathHelper.cos(f * 0.6662F) * 2.0F * g * 0.5F;
			this.field_3417.yaw = 0.0F;
			this.field_3417.roll = 0.0F;
			this.field_3420.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g * 0.5F;
			this.field_3420.yaw = 0.0F;
			this.field_3420.roll = 0.0F;
			this.field_3418.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g * 0.5F;
			this.field_3418.yaw = 0.0F;
			this.field_3418.roll = 0.0F;
		}

		IllagerEntity.State state = illagerEntity.getState();
		if (state == IllagerEntity.State.field_7211) {
			float l = MathHelper.sin(this.handSwingProgress * (float) Math.PI);
			float m = MathHelper.sin((1.0F - (1.0F - this.handSwingProgress) * (1.0F - this.handSwingProgress)) * (float) Math.PI);
			this.field_3426.roll = 0.0F;
			this.field_3417.roll = 0.0F;
			this.field_3426.yaw = (float) (Math.PI / 20);
			this.field_3417.yaw = (float) (-Math.PI / 20);
			if (illagerEntity.getMainArm() == Arm.field_6183) {
				this.field_3426.pitch = -1.8849558F + MathHelper.cos(h * 0.09F) * 0.15F;
				this.field_3417.pitch = -0.0F + MathHelper.cos(h * 0.19F) * 0.5F;
				this.field_3426.pitch += l * 2.2F - m * 0.4F;
				this.field_3417.pitch += l * 1.2F - m * 0.4F;
			} else {
				this.field_3426.pitch = -0.0F + MathHelper.cos(h * 0.19F) * 0.5F;
				this.field_3417.pitch = -1.8849558F + MathHelper.cos(h * 0.09F) * 0.15F;
				this.field_3426.pitch += l * 1.2F - m * 0.4F;
				this.field_3417.pitch += l * 2.2F - m * 0.4F;
			}

			this.field_3426.roll = this.field_3426.roll + MathHelper.cos(h * 0.09F) * 0.05F + 0.05F;
			this.field_3417.roll = this.field_3417.roll - (MathHelper.cos(h * 0.09F) * 0.05F + 0.05F);
			this.field_3426.pitch = this.field_3426.pitch + MathHelper.sin(h * 0.067F) * 0.05F;
			this.field_3417.pitch = this.field_3417.pitch - MathHelper.sin(h * 0.067F) * 0.05F;
		} else if (state == IllagerEntity.State.field_7212) {
			this.field_3426.rotationPointZ = 0.0F;
			this.field_3426.rotationPointX = -5.0F;
			this.field_3417.rotationPointZ = 0.0F;
			this.field_3417.rotationPointX = 5.0F;
			this.field_3426.pitch = MathHelper.cos(h * 0.6662F) * 0.25F;
			this.field_3417.pitch = MathHelper.cos(h * 0.6662F) * 0.25F;
			this.field_3426.roll = (float) (Math.PI * 3.0 / 4.0);
			this.field_3417.roll = (float) (-Math.PI * 3.0 / 4.0);
			this.field_3426.yaw = 0.0F;
			this.field_3417.yaw = 0.0F;
		} else if (state == IllagerEntity.State.field_7208) {
			this.field_3426.yaw = -0.1F + this.field_3422.yaw;
			this.field_3426.pitch = (float) (-Math.PI / 2) + this.field_3422.pitch;
			this.field_3417.pitch = -0.9424779F + this.field_3422.pitch;
			this.field_3417.yaw = this.field_3422.yaw - 0.4F;
			this.field_3417.roll = (float) (Math.PI / 2);
		} else if (state == IllagerEntity.State.field_7213) {
			this.field_3426.yaw = -0.3F + this.field_3422.yaw;
			this.field_3417.yaw = 0.6F + this.field_3422.yaw;
			this.field_3426.pitch = (float) (-Math.PI / 2) + this.field_3422.pitch + 0.1F;
			this.field_3417.pitch = -1.5F + this.field_3422.pitch;
		} else if (state == IllagerEntity.State.field_7210) {
			this.field_3426.yaw = -0.8F;
			this.field_3426.pitch = -0.97079635F;
			this.field_3417.pitch = -0.97079635F;
			float l = MathHelper.clamp(this.field_3424, 0.0F, 25.0F);
			this.field_3417.yaw = MathHelper.lerp(l / 25.0F, 0.4F, 0.85F);
			this.field_3417.pitch = MathHelper.lerp(l / 25.0F, this.field_3417.pitch, (float) (-Math.PI / 2));
		} else if (state == IllagerEntity.State.field_19012) {
			this.field_3426.rotationPointZ = 0.0F;
			this.field_3426.rotationPointX = -5.0F;
			this.field_3426.pitch = MathHelper.cos(h * 0.6662F) * 0.05F;
			this.field_3426.roll = 2.670354F;
			this.field_3426.yaw = 0.0F;
			this.field_3417.rotationPointZ = 0.0F;
			this.field_3417.rotationPointX = 5.0F;
			this.field_3417.pitch = MathHelper.cos(h * 0.6662F) * 0.05F;
			this.field_3417.roll = (float) (-Math.PI * 3.0 / 4.0);
			this.field_3417.yaw = 0.0F;
		}
	}

	public void method_17092(T illagerEntity, float f, float g, float h) {
		this.field_3424 = (float)illagerEntity.getItemUseTime();
		super.animateModel(illagerEntity, f, g, h);
	}

	private Cuboid method_2813(Arm arm) {
		return arm == Arm.field_6182 ? this.field_3417 : this.field_3426;
	}

	public Cuboid method_2812() {
		return this.field_3419;
	}

	@Override
	public Cuboid getHead() {
		return this.field_3422;
	}

	@Override
	public void setArmAngle(float f, Arm arm) {
		this.method_2813(arm).applyTransform(0.0625F);
	}
}
