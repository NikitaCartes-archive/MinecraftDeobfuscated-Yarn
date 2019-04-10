package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.AbsoluteHand;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BipedEntityModel<T extends LivingEntity> extends EntityModel<T> implements ModelWithArms, ModelWithHead {
	public Cuboid head;
	public Cuboid headwear;
	public Cuboid body;
	public Cuboid armRight;
	public Cuboid armLeft;
	public Cuboid legRight;
	public Cuboid legLeft;
	public BipedEntityModel.ArmPose armPoseLeft = BipedEntityModel.ArmPose.field_3409;
	public BipedEntityModel.ArmPose armPoseRight = BipedEntityModel.ArmPose.field_3409;
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
		this.armRight = new Cuboid(this, 40, 16);
		this.armRight.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, f);
		this.armRight.setRotationPoint(-5.0F, 2.0F + g, 0.0F);
		this.armLeft = new Cuboid(this, 40, 16);
		this.armLeft.mirror = true;
		this.armLeft.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, f);
		this.armLeft.setRotationPoint(5.0F, 2.0F + g, 0.0F);
		this.legRight = new Cuboid(this, 0, 16);
		this.legRight.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.legRight.setRotationPoint(-1.9F, 12.0F + g, 0.0F);
		this.legLeft = new Cuboid(this, 0, 16);
		this.legLeft.mirror = true;
		this.legLeft.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.legLeft.setRotationPoint(1.9F, 12.0F + g, 0.0F);
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
			this.armRight.render(k);
			this.armLeft.render(k);
			this.legRight.render(k);
			this.legLeft.render(k);
			this.headwear.render(k);
		} else {
			if (livingEntity.isInSneakingPose()) {
				GlStateManager.translatef(0.0F, 0.2F, 0.0F);
			}

			this.head.render(k);
			this.body.render(k);
			this.armRight.render(k);
			this.armLeft.render(k);
			this.legRight.render(k);
			this.legLeft.render(k);
			this.headwear.render(k);
		}

		GlStateManager.popMatrix();
	}

	public void method_17086(T livingEntity, float f, float g, float h) {
		this.field_3396 = livingEntity.method_6024(h);
		this.field_3393 = (float)livingEntity.method_6048();
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
		this.armRight.rotationPointZ = 0.0F;
		this.armRight.rotationPointX = -5.0F;
		this.armLeft.rotationPointZ = 0.0F;
		this.armLeft.rotationPointX = 5.0F;
		float l = 1.0F;
		if (bl) {
			l = (float)livingEntity.getVelocity().lengthSquared();
			l /= 0.2F;
			l *= l * l;
		}

		if (l < 1.0F) {
			l = 1.0F;
		}

		this.armRight.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 2.0F * g * 0.5F / l;
		this.armLeft.pitch = MathHelper.cos(f * 0.6662F) * 2.0F * g * 0.5F / l;
		this.armRight.roll = 0.0F;
		this.armLeft.roll = 0.0F;
		this.legRight.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g / l;
		this.legLeft.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g / l;
		this.legRight.yaw = 0.0F;
		this.legLeft.yaw = 0.0F;
		this.legRight.roll = 0.0F;
		this.legLeft.roll = 0.0F;
		if (this.isRiding) {
			this.armRight.pitch += (float) (-Math.PI / 5);
			this.armLeft.pitch += (float) (-Math.PI / 5);
			this.legRight.pitch = -1.4137167F;
			this.legRight.yaw = (float) (Math.PI / 10);
			this.legRight.roll = 0.07853982F;
			this.legLeft.pitch = -1.4137167F;
			this.legLeft.yaw = (float) (-Math.PI / 10);
			this.legLeft.roll = -0.07853982F;
		}

		this.armRight.yaw = 0.0F;
		this.armRight.roll = 0.0F;
		switch (this.armPoseLeft) {
			case field_3409:
				this.armLeft.yaw = 0.0F;
				break;
			case field_3406:
				this.armLeft.pitch = this.armLeft.pitch * 0.5F - 0.9424779F;
				this.armLeft.yaw = (float) (Math.PI / 6);
				break;
			case field_3410:
				this.armLeft.pitch = this.armLeft.pitch * 0.5F - (float) (Math.PI / 10);
				this.armLeft.yaw = 0.0F;
		}

		switch (this.armPoseRight) {
			case field_3409:
				this.armRight.yaw = 0.0F;
				break;
			case field_3406:
				this.armRight.pitch = this.armRight.pitch * 0.5F - 0.9424779F;
				this.armRight.yaw = (float) (-Math.PI / 6);
				break;
			case field_3410:
				this.armRight.pitch = this.armRight.pitch * 0.5F - (float) (Math.PI / 10);
				this.armRight.yaw = 0.0F;
				break;
			case field_3407:
				this.armRight.pitch = this.armRight.pitch * 0.5F - (float) Math.PI;
				this.armRight.yaw = 0.0F;
		}

		if (this.armPoseLeft == BipedEntityModel.ArmPose.field_3407
			&& this.armPoseRight != BipedEntityModel.ArmPose.field_3406
			&& this.armPoseRight != BipedEntityModel.ArmPose.field_3407
			&& this.armPoseRight != BipedEntityModel.ArmPose.field_3403) {
			this.armLeft.pitch = this.armLeft.pitch * 0.5F - (float) Math.PI;
			this.armLeft.yaw = 0.0F;
		}

		if (this.handSwingProgress > 0.0F) {
			AbsoluteHand absoluteHand = this.getPreferedHand(livingEntity);
			Cuboid cuboid = this.getArm(absoluteHand);
			float m = this.handSwingProgress;
			this.body.yaw = MathHelper.sin(MathHelper.sqrt(m) * (float) (Math.PI * 2)) * 0.2F;
			if (absoluteHand == AbsoluteHand.field_6182) {
				this.body.yaw *= -1.0F;
			}

			this.armRight.rotationPointZ = MathHelper.sin(this.body.yaw) * 5.0F;
			this.armRight.rotationPointX = -MathHelper.cos(this.body.yaw) * 5.0F;
			this.armLeft.rotationPointZ = -MathHelper.sin(this.body.yaw) * 5.0F;
			this.armLeft.rotationPointX = MathHelper.cos(this.body.yaw) * 5.0F;
			this.armRight.yaw = this.armRight.yaw + this.body.yaw;
			this.armLeft.yaw = this.armLeft.yaw + this.body.yaw;
			this.armLeft.pitch = this.armLeft.pitch + this.body.yaw;
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
			this.armRight.pitch += 0.4F;
			this.armLeft.pitch += 0.4F;
			this.legRight.rotationPointZ = 4.0F;
			this.legLeft.rotationPointZ = 4.0F;
			this.legRight.rotationPointY = 9.0F;
			this.legLeft.rotationPointY = 9.0F;
			this.head.rotationPointY = 1.0F;
		} else {
			this.body.pitch = 0.0F;
			this.legRight.rotationPointZ = 0.1F;
			this.legLeft.rotationPointZ = 0.1F;
			this.legRight.rotationPointY = 12.0F;
			this.legLeft.rotationPointY = 12.0F;
			this.head.rotationPointY = 0.0F;
		}

		this.armRight.roll = this.armRight.roll + MathHelper.cos(h * 0.09F) * 0.05F + 0.05F;
		this.armLeft.roll = this.armLeft.roll - (MathHelper.cos(h * 0.09F) * 0.05F + 0.05F);
		this.armRight.pitch = this.armRight.pitch + MathHelper.sin(h * 0.067F) * 0.05F;
		this.armLeft.pitch = this.armLeft.pitch - MathHelper.sin(h * 0.067F) * 0.05F;
		if (this.armPoseRight == BipedEntityModel.ArmPose.field_3403) {
			this.armRight.yaw = -0.1F + this.head.yaw;
			this.armLeft.yaw = 0.1F + this.head.yaw + 0.4F;
			this.armRight.pitch = (float) (-Math.PI / 2) + this.head.pitch;
			this.armLeft.pitch = (float) (-Math.PI / 2) + this.head.pitch;
		} else if (this.armPoseLeft == BipedEntityModel.ArmPose.field_3403
			&& this.armPoseRight != BipedEntityModel.ArmPose.field_3407
			&& this.armPoseRight != BipedEntityModel.ArmPose.field_3406) {
			this.armRight.yaw = -0.1F + this.head.yaw - 0.4F;
			this.armLeft.yaw = 0.1F + this.head.yaw;
			this.armRight.pitch = (float) (-Math.PI / 2) + this.head.pitch;
			this.armLeft.pitch = (float) (-Math.PI / 2) + this.head.pitch;
		}

		float p = (float)CrossbowItem.getPullTime(livingEntity.getActiveItem());
		if (this.armPoseRight == BipedEntityModel.ArmPose.field_3405) {
			this.armRight.yaw = -0.8F;
			this.armRight.pitch = -0.97079635F;
			this.armLeft.pitch = -0.97079635F;
			float q = MathHelper.clamp(this.field_3393, 0.0F, p);
			this.armLeft.yaw = MathHelper.lerp(q / p, 0.4F, 0.85F);
			this.armLeft.pitch = MathHelper.lerp(q / p, this.armLeft.pitch, (float) (-Math.PI / 2));
		} else if (this.armPoseLeft == BipedEntityModel.ArmPose.field_3405) {
			this.armLeft.yaw = 0.8F;
			this.armRight.pitch = -0.97079635F;
			this.armLeft.pitch = -0.97079635F;
			float q = MathHelper.clamp(this.field_3393, 0.0F, p);
			this.armRight.yaw = MathHelper.lerp(q / p, -0.4F, -0.85F);
			this.armRight.pitch = MathHelper.lerp(q / p, this.armRight.pitch, (float) (-Math.PI / 2));
		}

		if (this.armPoseRight == BipedEntityModel.ArmPose.field_3408 && this.handSwingProgress <= 0.0F) {
			this.armRight.yaw = -0.3F + this.head.yaw;
			this.armLeft.yaw = 0.6F + this.head.yaw;
			this.armRight.pitch = (float) (-Math.PI / 2) + this.head.pitch + 0.1F;
			this.armLeft.pitch = -1.5F + this.head.pitch;
		} else if (this.armPoseLeft == BipedEntityModel.ArmPose.field_3408) {
			this.armRight.yaw = -0.6F + this.head.yaw;
			this.armLeft.yaw = 0.3F + this.head.yaw;
			this.armRight.pitch = -1.5F + this.head.pitch;
			this.armLeft.pitch = (float) (-Math.PI / 2) + this.head.pitch + 0.1F;
		}

		if (this.field_3396 > 0.0F) {
			float q = f % 26.0F;
			float m = this.handSwingProgress > 0.0F ? 0.0F : this.field_3396;
			if (q < 14.0F) {
				this.armLeft.pitch = this.method_2804(this.armLeft.pitch, 0.0F, this.field_3396);
				this.armRight.pitch = MathHelper.lerp(m, this.armRight.pitch, 0.0F);
				this.armLeft.yaw = this.method_2804(this.armLeft.yaw, (float) Math.PI, this.field_3396);
				this.armRight.yaw = MathHelper.lerp(m, this.armRight.yaw, (float) Math.PI);
				this.armLeft.roll = this.method_2804(this.armLeft.roll, (float) Math.PI + 1.8707964F * this.method_2807(q) / this.method_2807(14.0F), this.field_3396);
				this.armRight.roll = MathHelper.lerp(m, this.armRight.roll, (float) Math.PI - 1.8707964F * this.method_2807(q) / this.method_2807(14.0F));
			} else if (q >= 14.0F && q < 22.0F) {
				float n = (q - 14.0F) / 8.0F;
				this.armLeft.pitch = this.method_2804(this.armLeft.pitch, (float) (Math.PI / 2) * n, this.field_3396);
				this.armRight.pitch = MathHelper.lerp(m, this.armRight.pitch, (float) (Math.PI / 2) * n);
				this.armLeft.yaw = this.method_2804(this.armLeft.yaw, (float) Math.PI, this.field_3396);
				this.armRight.yaw = MathHelper.lerp(m, this.armRight.yaw, (float) Math.PI);
				this.armLeft.roll = this.method_2804(this.armLeft.roll, 5.012389F - 1.8707964F * n, this.field_3396);
				this.armRight.roll = MathHelper.lerp(m, this.armRight.roll, 1.2707963F + 1.8707964F * n);
			} else if (q >= 22.0F && q < 26.0F) {
				float n = (q - 22.0F) / 4.0F;
				this.armLeft.pitch = this.method_2804(this.armLeft.pitch, (float) (Math.PI / 2) - (float) (Math.PI / 2) * n, this.field_3396);
				this.armRight.pitch = MathHelper.lerp(m, this.armRight.pitch, (float) (Math.PI / 2) - (float) (Math.PI / 2) * n);
				this.armLeft.yaw = this.method_2804(this.armLeft.yaw, (float) Math.PI, this.field_3396);
				this.armRight.yaw = MathHelper.lerp(m, this.armRight.yaw, (float) Math.PI);
				this.armLeft.roll = this.method_2804(this.armLeft.roll, (float) Math.PI, this.field_3396);
				this.armRight.roll = MathHelper.lerp(m, this.armRight.roll, (float) Math.PI);
			}

			float n = 0.3F;
			float o = 0.33333334F;
			this.legLeft.pitch = MathHelper.lerp(this.field_3396, this.legLeft.pitch, 0.3F * MathHelper.cos(f * 0.33333334F + (float) Math.PI));
			this.legRight.pitch = MathHelper.lerp(this.field_3396, this.legRight.pitch, 0.3F * MathHelper.cos(f * 0.33333334F));
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
		bipedEntityModel.armPoseLeft = this.armPoseLeft;
		bipedEntityModel.armPoseRight = this.armPoseRight;
		bipedEntityModel.isSneaking = this.isSneaking;
	}

	public void setVisible(boolean bl) {
		this.head.visible = bl;
		this.headwear.visible = bl;
		this.body.visible = bl;
		this.armRight.visible = bl;
		this.armLeft.visible = bl;
		this.legRight.visible = bl;
		this.legLeft.visible = bl;
	}

	@Override
	public void setArmAngle(float f, AbsoluteHand absoluteHand) {
		this.getArm(absoluteHand).applyTransform(f);
	}

	protected Cuboid getArm(AbsoluteHand absoluteHand) {
		return absoluteHand == AbsoluteHand.field_6182 ? this.armLeft : this.armRight;
	}

	@Override
	public Cuboid getHead() {
		return this.head;
	}

	protected AbsoluteHand getPreferedHand(T livingEntity) {
		AbsoluteHand absoluteHand = livingEntity.getMainHand();
		return livingEntity.preferredHand == Hand.MAIN ? absoluteHand : absoluteHand.getOpposite();
	}

	@Environment(EnvType.CLIENT)
	public static enum ArmPose {
		field_3409,
		field_3410,
		field_3406,
		field_3403,
		field_3407,
		field_3405,
		field_3408;
	}
}
