package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BipedEntityModel<T extends LivingEntity> extends EntityModel<T> implements ModelWithArms, ModelWithHead {
	public Cuboid head;
	public Cuboid headwear;
	public Cuboid body;
	public Cuboid rightArm;
	public Cuboid leftArm;
	public Cuboid rightLeg;
	public Cuboid leftLeg;
	public BipedEntityModel.ArmPose leftArmPose = BipedEntityModel.ArmPose.EMPTY;
	public BipedEntityModel.ArmPose rightArmPose = BipedEntityModel.ArmPose.EMPTY;
	public boolean isSneaking;
	public float field_3396;
	private float field_3393;

	public BipedEntityModel() {
		this(0.0F);
	}

	public BipedEntityModel(float f) {
		this(f, 0.0F, 64, 32);
	}

	public BipedEntityModel(float f, float g, int i, int j) {
		this.textureWidth = i;
		this.textureHeight = j;
		this.head = new Cuboid(this, 0, 0);
		this.head.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, f);
		this.head.setRotationPoint(0.0F, 0.0F + g, 0.0F);
		this.headwear = new Cuboid(this, 32, 0);
		this.headwear.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, f + 0.5F);
		this.headwear.setRotationPoint(0.0F, 0.0F + g, 0.0F);
		this.body = new Cuboid(this, 16, 16);
		this.body.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, f);
		this.body.setRotationPoint(0.0F, 0.0F + g, 0.0F);
		this.rightArm = new Cuboid(this, 40, 16);
		this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, f);
		this.rightArm.setRotationPoint(-5.0F, 2.0F + g, 0.0F);
		this.leftArm = new Cuboid(this, 40, 16);
		this.leftArm.mirror = true;
		this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, f);
		this.leftArm.setRotationPoint(5.0F, 2.0F + g, 0.0F);
		this.rightLeg = new Cuboid(this, 0, 16);
		this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.rightLeg.setRotationPoint(-1.9F, 12.0F + g, 0.0F);
		this.leftLeg = new Cuboid(this, 0, 16);
		this.leftLeg.mirror = true;
		this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.leftLeg.setRotationPoint(1.9F, 12.0F + g, 0.0F);
	}

	public void method_17088(T livingEntity, float f, float g, float h, float i, float j, float k) {
		this.method_17087(livingEntity, f, g, h, i, j, k);
		GlStateManager.pushMatrix();
		if (this.isChild) {
			float l = 2.0F;
			GlStateManager.scalef(0.75F, 0.75F, 0.75F);
			GlStateManager.translatef(0.0F, 16.0F * k, 0.0F);
			this.head.render(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * k, 0.0F);
			this.body.render(k);
			this.rightArm.render(k);
			this.leftArm.render(k);
			this.rightLeg.render(k);
			this.leftLeg.render(k);
			this.headwear.render(k);
		} else {
			if (livingEntity.isInSneakingPose()) {
				GlStateManager.translatef(0.0F, 0.2F, 0.0F);
			}

			this.head.render(k);
			this.body.render(k);
			this.rightArm.render(k);
			this.leftArm.render(k);
			this.rightLeg.render(k);
			this.leftLeg.render(k);
			this.headwear.render(k);
		}

		GlStateManager.popMatrix();
	}

	public void method_17086(T livingEntity, float f, float g, float h) {
		this.field_3396 = livingEntity.method_6024(h);
		this.field_3393 = (float)livingEntity.getItemUseTime();
		super.animateModel(livingEntity, f, g, h);
	}

	public void method_17087(T livingEntity, float f, float g, float h, float i, float j, float k) {
		boolean bl = livingEntity.method_6003() > 4;
		boolean bl2 = livingEntity.isInSwimmingPose();
		this.head.yaw = i * (float) (Math.PI / 180.0);
		if (bl) {
			this.head.pitch = (float) (-Math.PI / 4);
		} else if (this.field_3396 > 0.0F) {
			if (bl2) {
				this.head.pitch = this.method_2804(this.head.pitch, (float) (-Math.PI / 4), this.field_3396);
			} else {
				this.head.pitch = this.method_2804(this.head.pitch, j * (float) (Math.PI / 180.0), this.field_3396);
			}
		} else {
			this.head.pitch = j * (float) (Math.PI / 180.0);
		}

		this.body.yaw = 0.0F;
		this.rightArm.rotationPointZ = 0.0F;
		this.rightArm.rotationPointX = -5.0F;
		this.leftArm.rotationPointZ = 0.0F;
		this.leftArm.rotationPointX = 5.0F;
		float l = 1.0F;
		if (bl) {
			l = (float)livingEntity.getVelocity().lengthSquared();
			l /= 0.2F;
			l *= l * l;
		}

		if (l < 1.0F) {
			l = 1.0F;
		}

		this.rightArm.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 2.0F * g * 0.5F / l;
		this.leftArm.pitch = MathHelper.cos(f * 0.6662F) * 2.0F * g * 0.5F / l;
		this.rightArm.roll = 0.0F;
		this.leftArm.roll = 0.0F;
		this.rightLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g / l;
		this.leftLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g / l;
		this.rightLeg.yaw = 0.0F;
		this.leftLeg.yaw = 0.0F;
		this.rightLeg.roll = 0.0F;
		this.leftLeg.roll = 0.0F;
		if (this.isRiding) {
			this.rightArm.pitch += (float) (-Math.PI / 5);
			this.leftArm.pitch += (float) (-Math.PI / 5);
			this.rightLeg.pitch = -1.4137167F;
			this.rightLeg.yaw = (float) (Math.PI / 10);
			this.rightLeg.roll = 0.07853982F;
			this.leftLeg.pitch = -1.4137167F;
			this.leftLeg.yaw = (float) (-Math.PI / 10);
			this.leftLeg.roll = -0.07853982F;
		}

		this.rightArm.yaw = 0.0F;
		this.rightArm.roll = 0.0F;
		switch (this.leftArmPose) {
			case EMPTY:
				this.leftArm.yaw = 0.0F;
				break;
			case BLOCK:
				this.leftArm.pitch = this.leftArm.pitch * 0.5F - 0.9424779F;
				this.leftArm.yaw = (float) (Math.PI / 6);
				break;
			case ITEM:
				this.leftArm.pitch = this.leftArm.pitch * 0.5F - (float) (Math.PI / 10);
				this.leftArm.yaw = 0.0F;
		}

		switch (this.rightArmPose) {
			case EMPTY:
				this.rightArm.yaw = 0.0F;
				break;
			case BLOCK:
				this.rightArm.pitch = this.rightArm.pitch * 0.5F - 0.9424779F;
				this.rightArm.yaw = (float) (-Math.PI / 6);
				break;
			case ITEM:
				this.rightArm.pitch = this.rightArm.pitch * 0.5F - (float) (Math.PI / 10);
				this.rightArm.yaw = 0.0F;
				break;
			case THROW_SPEAR:
				this.rightArm.pitch = this.rightArm.pitch * 0.5F - (float) Math.PI;
				this.rightArm.yaw = 0.0F;
		}

		if (this.leftArmPose == BipedEntityModel.ArmPose.THROW_SPEAR
			&& this.rightArmPose != BipedEntityModel.ArmPose.BLOCK
			&& this.rightArmPose != BipedEntityModel.ArmPose.THROW_SPEAR
			&& this.rightArmPose != BipedEntityModel.ArmPose.BOW_AND_ARROW) {
			this.leftArm.pitch = this.leftArm.pitch * 0.5F - (float) Math.PI;
			this.leftArm.yaw = 0.0F;
		}

		if (this.handSwingProgress > 0.0F) {
			Arm arm = this.getPreferredArm(livingEntity);
			Cuboid cuboid = this.getArm(arm);
			float m = this.handSwingProgress;
			this.body.yaw = MathHelper.sin(MathHelper.sqrt(m) * (float) (Math.PI * 2)) * 0.2F;
			if (arm == Arm.LEFT) {
				this.body.yaw *= -1.0F;
			}

			this.rightArm.rotationPointZ = MathHelper.sin(this.body.yaw) * 5.0F;
			this.rightArm.rotationPointX = -MathHelper.cos(this.body.yaw) * 5.0F;
			this.leftArm.rotationPointZ = -MathHelper.sin(this.body.yaw) * 5.0F;
			this.leftArm.rotationPointX = MathHelper.cos(this.body.yaw) * 5.0F;
			this.rightArm.yaw = this.rightArm.yaw + this.body.yaw;
			this.leftArm.yaw = this.leftArm.yaw + this.body.yaw;
			this.leftArm.pitch = this.leftArm.pitch + this.body.yaw;
			m = 1.0F - this.handSwingProgress;
			m *= m;
			m *= m;
			m = 1.0F - m;
			float n = MathHelper.sin(m * (float) Math.PI);
			float o = MathHelper.sin(this.handSwingProgress * (float) Math.PI) * -(this.head.pitch - 0.7F) * 0.75F;
			cuboid.pitch = (float)((double)cuboid.pitch - ((double)n * 1.2 + (double)o));
			cuboid.yaw = cuboid.yaw + this.body.yaw * 2.0F;
			cuboid.roll = cuboid.roll + MathHelper.sin(this.handSwingProgress * (float) Math.PI) * -0.4F;
		}

		if (this.isSneaking) {
			this.body.pitch = 0.5F;
			this.rightArm.pitch += 0.4F;
			this.leftArm.pitch += 0.4F;
			this.rightLeg.rotationPointZ = 4.0F;
			this.leftLeg.rotationPointZ = 4.0F;
			this.rightLeg.rotationPointY = 9.0F;
			this.leftLeg.rotationPointY = 9.0F;
			this.head.rotationPointY = 1.0F;
		} else {
			this.body.pitch = 0.0F;
			this.rightLeg.rotationPointZ = 0.1F;
			this.leftLeg.rotationPointZ = 0.1F;
			this.rightLeg.rotationPointY = 12.0F;
			this.leftLeg.rotationPointY = 12.0F;
			this.head.rotationPointY = 0.0F;
		}

		this.rightArm.roll = this.rightArm.roll + MathHelper.cos(h * 0.09F) * 0.05F + 0.05F;
		this.leftArm.roll = this.leftArm.roll - (MathHelper.cos(h * 0.09F) * 0.05F + 0.05F);
		this.rightArm.pitch = this.rightArm.pitch + MathHelper.sin(h * 0.067F) * 0.05F;
		this.leftArm.pitch = this.leftArm.pitch - MathHelper.sin(h * 0.067F) * 0.05F;
		if (this.rightArmPose == BipedEntityModel.ArmPose.BOW_AND_ARROW) {
			this.rightArm.yaw = -0.1F + this.head.yaw;
			this.leftArm.yaw = 0.1F + this.head.yaw + 0.4F;
			this.rightArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
			this.leftArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
		} else if (this.leftArmPose == BipedEntityModel.ArmPose.BOW_AND_ARROW
			&& this.rightArmPose != BipedEntityModel.ArmPose.THROW_SPEAR
			&& this.rightArmPose != BipedEntityModel.ArmPose.BLOCK) {
			this.rightArm.yaw = -0.1F + this.head.yaw - 0.4F;
			this.leftArm.yaw = 0.1F + this.head.yaw;
			this.rightArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
			this.leftArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
		}

		float p = (float)CrossbowItem.getPullTime(livingEntity.getActiveItem());
		if (this.rightArmPose == BipedEntityModel.ArmPose.CROSSBOW_CHARGE) {
			this.rightArm.yaw = -0.8F;
			this.rightArm.pitch = -0.97079635F;
			this.leftArm.pitch = -0.97079635F;
			float q = MathHelper.clamp(this.field_3393, 0.0F, p);
			this.leftArm.yaw = MathHelper.lerp(q / p, 0.4F, 0.85F);
			this.leftArm.pitch = MathHelper.lerp(q / p, this.leftArm.pitch, (float) (-Math.PI / 2));
		} else if (this.leftArmPose == BipedEntityModel.ArmPose.CROSSBOW_CHARGE) {
			this.leftArm.yaw = 0.8F;
			this.rightArm.pitch = -0.97079635F;
			this.leftArm.pitch = -0.97079635F;
			float q = MathHelper.clamp(this.field_3393, 0.0F, p);
			this.rightArm.yaw = MathHelper.lerp(q / p, -0.4F, -0.85F);
			this.rightArm.pitch = MathHelper.lerp(q / p, this.rightArm.pitch, (float) (-Math.PI / 2));
		}

		if (this.rightArmPose == BipedEntityModel.ArmPose.CROSSBOW_HOLD && this.handSwingProgress <= 0.0F) {
			this.rightArm.yaw = -0.3F + this.head.yaw;
			this.leftArm.yaw = 0.6F + this.head.yaw;
			this.rightArm.pitch = (float) (-Math.PI / 2) + this.head.pitch + 0.1F;
			this.leftArm.pitch = -1.5F + this.head.pitch;
		} else if (this.leftArmPose == BipedEntityModel.ArmPose.CROSSBOW_HOLD) {
			this.rightArm.yaw = -0.6F + this.head.yaw;
			this.leftArm.yaw = 0.3F + this.head.yaw;
			this.rightArm.pitch = -1.5F + this.head.pitch;
			this.leftArm.pitch = (float) (-Math.PI / 2) + this.head.pitch + 0.1F;
		}

		if (this.field_3396 > 0.0F) {
			float q = f % 26.0F;
			float m = this.handSwingProgress > 0.0F ? 0.0F : this.field_3396;
			if (q < 14.0F) {
				this.leftArm.pitch = this.method_2804(this.leftArm.pitch, 0.0F, this.field_3396);
				this.rightArm.pitch = MathHelper.lerp(m, this.rightArm.pitch, 0.0F);
				this.leftArm.yaw = this.method_2804(this.leftArm.yaw, (float) Math.PI, this.field_3396);
				this.rightArm.yaw = MathHelper.lerp(m, this.rightArm.yaw, (float) Math.PI);
				this.leftArm.roll = this.method_2804(this.leftArm.roll, (float) Math.PI + 1.8707964F * this.method_2807(q) / this.method_2807(14.0F), this.field_3396);
				this.rightArm.roll = MathHelper.lerp(m, this.rightArm.roll, (float) Math.PI - 1.8707964F * this.method_2807(q) / this.method_2807(14.0F));
			} else if (q >= 14.0F && q < 22.0F) {
				float n = (q - 14.0F) / 8.0F;
				this.leftArm.pitch = this.method_2804(this.leftArm.pitch, (float) (Math.PI / 2) * n, this.field_3396);
				this.rightArm.pitch = MathHelper.lerp(m, this.rightArm.pitch, (float) (Math.PI / 2) * n);
				this.leftArm.yaw = this.method_2804(this.leftArm.yaw, (float) Math.PI, this.field_3396);
				this.rightArm.yaw = MathHelper.lerp(m, this.rightArm.yaw, (float) Math.PI);
				this.leftArm.roll = this.method_2804(this.leftArm.roll, 5.012389F - 1.8707964F * n, this.field_3396);
				this.rightArm.roll = MathHelper.lerp(m, this.rightArm.roll, 1.2707963F + 1.8707964F * n);
			} else if (q >= 22.0F && q < 26.0F) {
				float n = (q - 22.0F) / 4.0F;
				this.leftArm.pitch = this.method_2804(this.leftArm.pitch, (float) (Math.PI / 2) - (float) (Math.PI / 2) * n, this.field_3396);
				this.rightArm.pitch = MathHelper.lerp(m, this.rightArm.pitch, (float) (Math.PI / 2) - (float) (Math.PI / 2) * n);
				this.leftArm.yaw = this.method_2804(this.leftArm.yaw, (float) Math.PI, this.field_3396);
				this.rightArm.yaw = MathHelper.lerp(m, this.rightArm.yaw, (float) Math.PI);
				this.leftArm.roll = this.method_2804(this.leftArm.roll, (float) Math.PI, this.field_3396);
				this.rightArm.roll = MathHelper.lerp(m, this.rightArm.roll, (float) Math.PI);
			}

			float n = 0.3F;
			float o = 0.33333334F;
			this.leftLeg.pitch = MathHelper.lerp(this.field_3396, this.leftLeg.pitch, 0.3F * MathHelper.cos(f * 0.33333334F + (float) Math.PI));
			this.rightLeg.pitch = MathHelper.lerp(this.field_3396, this.rightLeg.pitch, 0.3F * MathHelper.cos(f * 0.33333334F));
		}

		this.headwear.copyRotation(this.head);
	}

	protected float method_2804(float f, float g, float h) {
		float i = (g - f) % (float) (Math.PI * 2);
		if (i < (float) -Math.PI) {
			i += (float) (Math.PI * 2);
		}

		if (i >= (float) Math.PI) {
			i -= (float) (Math.PI * 2);
		}

		return f + h * i;
	}

	private float method_2807(float f) {
		return -65.0F * f + f * f;
	}

	public void setAttributes(BipedEntityModel<T> bipedEntityModel) {
		super.copyStateTo(bipedEntityModel);
		bipedEntityModel.leftArmPose = this.leftArmPose;
		bipedEntityModel.rightArmPose = this.rightArmPose;
		bipedEntityModel.isSneaking = this.isSneaking;
	}

	public void setVisible(boolean bl) {
		this.head.visible = bl;
		this.headwear.visible = bl;
		this.body.visible = bl;
		this.rightArm.visible = bl;
		this.leftArm.visible = bl;
		this.rightLeg.visible = bl;
		this.leftLeg.visible = bl;
	}

	@Override
	public void setArmAngle(float f, Arm arm) {
		this.getArm(arm).applyTransform(f);
	}

	protected Cuboid getArm(Arm arm) {
		return arm == Arm.LEFT ? this.leftArm : this.rightArm;
	}

	@Override
	public Cuboid getHead() {
		return this.head;
	}

	protected Arm getPreferredArm(T livingEntity) {
		Arm arm = livingEntity.getMainArm();
		return livingEntity.preferredHand == Hand.MAIN_HAND ? arm : arm.getOpposite();
	}

	@Environment(EnvType.CLIENT)
	public static enum ArmPose {
		EMPTY,
		ITEM,
		BLOCK,
		BOW_AND_ARROW,
		THROW_SPEAR,
		CROSSBOW_CHARGE,
		CROSSBOW_HOLD;
	}
}
