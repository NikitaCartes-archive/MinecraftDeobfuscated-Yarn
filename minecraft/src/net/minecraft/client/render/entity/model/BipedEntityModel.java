package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3881;
import net.minecraft.class_3882;
import net.minecraft.client.model.Cuboid;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.sortme.OptionMainHand;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BipedEntityModel<T extends LivingEntity> extends EntityModel<T> implements class_3881, class_3882 {
	public Cuboid field_3398;
	public Cuboid field_3394;
	public Cuboid field_3391;
	public Cuboid field_3401;
	public Cuboid field_3390;
	public Cuboid field_3392;
	public Cuboid field_3397;
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
		this.field_3398 = new Cuboid(this, 0, 0);
		this.field_3398.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, f);
		this.field_3398.setRotationPoint(0.0F, 0.0F + g, 0.0F);
		this.field_3394 = new Cuboid(this, 32, 0);
		this.field_3394.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, f + 0.5F);
		this.field_3394.setRotationPoint(0.0F, 0.0F + g, 0.0F);
		this.field_3391 = new Cuboid(this, 16, 16);
		this.field_3391.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, f);
		this.field_3391.setRotationPoint(0.0F, 0.0F + g, 0.0F);
		this.field_3401 = new Cuboid(this, 40, 16);
		this.field_3401.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, f);
		this.field_3401.setRotationPoint(-5.0F, 2.0F + g, 0.0F);
		this.field_3390 = new Cuboid(this, 40, 16);
		this.field_3390.mirror = true;
		this.field_3390.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, f);
		this.field_3390.setRotationPoint(5.0F, 2.0F + g, 0.0F);
		this.field_3392 = new Cuboid(this, 0, 16);
		this.field_3392.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.field_3392.setRotationPoint(-1.9F, 12.0F + g, 0.0F);
		this.field_3397 = new Cuboid(this, 0, 16);
		this.field_3397.mirror = true;
		this.field_3397.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, f);
		this.field_3397.setRotationPoint(1.9F, 12.0F + g, 0.0F);
	}

	public void method_17088(T livingEntity, float f, float g, float h, float i, float j, float k) {
		this.method_17087(livingEntity, f, g, h, i, j, k);
		GlStateManager.pushMatrix();
		if (this.isChild) {
			float l = 2.0F;
			GlStateManager.scalef(0.75F, 0.75F, 0.75F);
			GlStateManager.translatef(0.0F, 16.0F * k, 0.0F);
			this.field_3398.render(k);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scalef(0.5F, 0.5F, 0.5F);
			GlStateManager.translatef(0.0F, 24.0F * k, 0.0F);
			this.field_3391.render(k);
			this.field_3401.render(k);
			this.field_3390.render(k);
			this.field_3392.render(k);
			this.field_3397.render(k);
			this.field_3394.render(k);
		} else {
			if (livingEntity.isSneaking()) {
				GlStateManager.translatef(0.0F, 0.2F, 0.0F);
			}

			this.field_3398.render(k);
			this.field_3391.render(k);
			this.field_3401.render(k);
			this.field_3390.render(k);
			this.field_3392.render(k);
			this.field_3397.render(k);
			this.field_3394.render(k);
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
		boolean bl2 = livingEntity.isSwimming();
		this.field_3398.yaw = i * (float) (Math.PI / 180.0);
		if (bl) {
			this.field_3398.pitch = (float) (-Math.PI / 4);
		} else if (this.field_3396 > 0.0F) {
			if (bl2) {
				this.field_3398.pitch = this.method_2804(this.field_3398.pitch, (float) (-Math.PI / 4), this.field_3396);
			} else {
				this.field_3398.pitch = this.method_2804(this.field_3398.pitch, j * (float) (Math.PI / 180.0), this.field_3396);
			}
		} else {
			this.field_3398.pitch = j * (float) (Math.PI / 180.0);
		}

		this.field_3391.yaw = 0.0F;
		this.field_3401.rotationPointZ = 0.0F;
		this.field_3401.rotationPointX = -5.0F;
		this.field_3390.rotationPointZ = 0.0F;
		this.field_3390.rotationPointX = 5.0F;
		float m;
		if (bl) {
			float l = (float)livingEntity.method_18798().lengthSquared();
			m = Math.min(l * l * l * 125.0F, 1.0F);
		} else {
			m = 1.0F;
		}

		this.field_3401.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 2.0F * g * 0.5F / m;
		this.field_3390.pitch = MathHelper.cos(f * 0.6662F) * 2.0F * g * 0.5F / m;
		this.field_3401.roll = 0.0F;
		this.field_3390.roll = 0.0F;
		this.field_3392.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g / m;
		this.field_3397.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g / m;
		this.field_3392.yaw = 0.0F;
		this.field_3397.yaw = 0.0F;
		this.field_3392.roll = 0.0F;
		this.field_3397.roll = 0.0F;
		if (this.isRiding) {
			this.field_3401.pitch += (float) (-Math.PI / 5);
			this.field_3390.pitch += (float) (-Math.PI / 5);
			this.field_3392.pitch = -1.4137167F;
			this.field_3392.yaw = (float) (Math.PI / 10);
			this.field_3392.roll = 0.07853982F;
			this.field_3397.pitch = -1.4137167F;
			this.field_3397.yaw = (float) (-Math.PI / 10);
			this.field_3397.roll = -0.07853982F;
		}

		this.field_3401.yaw = 0.0F;
		this.field_3401.roll = 0.0F;
		switch (this.armPoseLeft) {
			case field_3409:
				this.field_3390.yaw = 0.0F;
				break;
			case field_3406:
				this.field_3390.pitch = this.field_3390.pitch * 0.5F - 0.9424779F;
				this.field_3390.yaw = (float) (Math.PI / 6);
				break;
			case field_3410:
				this.field_3390.pitch = this.field_3390.pitch * 0.5F - (float) (Math.PI / 10);
				this.field_3390.yaw = 0.0F;
		}

		switch (this.armPoseRight) {
			case field_3409:
				this.field_3401.yaw = 0.0F;
				break;
			case field_3406:
				this.field_3401.pitch = this.field_3401.pitch * 0.5F - 0.9424779F;
				this.field_3401.yaw = (float) (-Math.PI / 6);
				break;
			case field_3410:
				this.field_3401.pitch = this.field_3401.pitch * 0.5F - (float) (Math.PI / 10);
				this.field_3401.yaw = 0.0F;
				break;
			case field_3407:
				this.field_3401.pitch = this.field_3401.pitch * 0.5F - (float) Math.PI;
				this.field_3401.yaw = 0.0F;
		}

		if (this.armPoseLeft == BipedEntityModel.ArmPose.field_3407
			&& this.armPoseRight != BipedEntityModel.ArmPose.field_3406
			&& this.armPoseRight != BipedEntityModel.ArmPose.field_3407
			&& this.armPoseRight != BipedEntityModel.ArmPose.field_3403) {
			this.field_3390.pitch = this.field_3390.pitch * 0.5F - (float) Math.PI;
			this.field_3390.yaw = 0.0F;
		}

		if (this.swingProgress > 0.0F) {
			OptionMainHand optionMainHand = this.getPreferedHand(livingEntity);
			Cuboid cuboid = this.method_2808(optionMainHand);
			float n = this.swingProgress;
			this.field_3391.yaw = MathHelper.sin(MathHelper.sqrt(n) * (float) (Math.PI * 2)) * 0.2F;
			if (optionMainHand == OptionMainHand.field_6182) {
				this.field_3391.yaw *= -1.0F;
			}

			this.field_3401.rotationPointZ = MathHelper.sin(this.field_3391.yaw) * 5.0F;
			this.field_3401.rotationPointX = -MathHelper.cos(this.field_3391.yaw) * 5.0F;
			this.field_3390.rotationPointZ = -MathHelper.sin(this.field_3391.yaw) * 5.0F;
			this.field_3390.rotationPointX = MathHelper.cos(this.field_3391.yaw) * 5.0F;
			this.field_3401.yaw = this.field_3401.yaw + this.field_3391.yaw;
			this.field_3390.yaw = this.field_3390.yaw + this.field_3391.yaw;
			this.field_3390.pitch = this.field_3390.pitch + this.field_3391.yaw;
			n = 1.0F - this.swingProgress;
			n *= n;
			n *= n;
			n = 1.0F - n;
			float o = MathHelper.sin(n * (float) Math.PI);
			float p = MathHelper.sin(this.swingProgress * (float) Math.PI) * -(this.field_3398.pitch - 0.7F) * 0.75F;
			cuboid.pitch = (float)((double)cuboid.pitch - ((double)o * 1.2 + (double)p));
			cuboid.yaw = cuboid.yaw + this.field_3391.yaw * 2.0F;
			cuboid.roll = cuboid.roll + MathHelper.sin(this.swingProgress * (float) Math.PI) * -0.4F;
		}

		if (this.isSneaking) {
			this.field_3391.pitch = 0.5F;
			this.field_3401.pitch += 0.4F;
			this.field_3390.pitch += 0.4F;
			this.field_3392.rotationPointZ = 4.0F;
			this.field_3397.rotationPointZ = 4.0F;
			this.field_3392.rotationPointY = 9.0F;
			this.field_3397.rotationPointY = 9.0F;
			this.field_3398.rotationPointY = 1.0F;
		} else {
			this.field_3391.pitch = 0.0F;
			this.field_3392.rotationPointZ = 0.1F;
			this.field_3397.rotationPointZ = 0.1F;
			this.field_3392.rotationPointY = 12.0F;
			this.field_3397.rotationPointY = 12.0F;
			this.field_3398.rotationPointY = 0.0F;
		}

		this.field_3401.roll = this.field_3401.roll + MathHelper.cos(h * 0.09F) * 0.05F + 0.05F;
		this.field_3390.roll = this.field_3390.roll - (MathHelper.cos(h * 0.09F) * 0.05F + 0.05F);
		this.field_3401.pitch = this.field_3401.pitch + MathHelper.sin(h * 0.067F) * 0.05F;
		this.field_3390.pitch = this.field_3390.pitch - MathHelper.sin(h * 0.067F) * 0.05F;
		if (this.armPoseRight == BipedEntityModel.ArmPose.field_3403) {
			this.field_3401.yaw = -0.1F + this.field_3398.yaw;
			this.field_3390.yaw = 0.1F + this.field_3398.yaw + 0.4F;
			this.field_3401.pitch = (float) (-Math.PI / 2) + this.field_3398.pitch;
			this.field_3390.pitch = (float) (-Math.PI / 2) + this.field_3398.pitch;
		} else if (this.armPoseLeft == BipedEntityModel.ArmPose.field_3403
			&& this.armPoseRight != BipedEntityModel.ArmPose.field_3407
			&& this.armPoseRight != BipedEntityModel.ArmPose.field_3406) {
			this.field_3401.yaw = -0.1F + this.field_3398.yaw - 0.4F;
			this.field_3390.yaw = 0.1F + this.field_3398.yaw;
			this.field_3401.pitch = (float) (-Math.PI / 2) + this.field_3398.pitch;
			this.field_3390.pitch = (float) (-Math.PI / 2) + this.field_3398.pitch;
		}

		float l = (float)CrossbowItem.method_7775(livingEntity.method_6030());
		if (this.armPoseRight == BipedEntityModel.ArmPose.field_3405) {
			this.field_3401.yaw = -0.8F;
			this.field_3401.pitch = -0.97079635F;
			this.field_3390.pitch = -0.97079635F;
			float q = MathHelper.clamp(this.field_3393, 0.0F, l);
			this.field_3390.yaw = MathHelper.lerp(q / l, 0.4F, 0.85F);
			this.field_3390.pitch = MathHelper.lerp(q / l, this.field_3390.pitch, (float) (-Math.PI / 2));
		} else if (this.armPoseLeft == BipedEntityModel.ArmPose.field_3405) {
			this.field_3390.yaw = 0.8F;
			this.field_3401.pitch = -0.97079635F;
			this.field_3390.pitch = -0.97079635F;
			float q = MathHelper.clamp(this.field_3393, 0.0F, l);
			this.field_3401.yaw = MathHelper.lerp(q / l, -0.4F, -0.85F);
			this.field_3401.pitch = MathHelper.lerp(q / l, this.field_3401.pitch, (float) (-Math.PI / 2));
		}

		if (this.armPoseRight == BipedEntityModel.ArmPose.field_3408 && this.swingProgress <= 0.0F) {
			this.field_3401.yaw = -0.3F + this.field_3398.yaw;
			this.field_3390.yaw = 0.6F + this.field_3398.yaw;
			this.field_3401.pitch = (float) (-Math.PI / 2) + this.field_3398.pitch + 0.1F;
			this.field_3390.pitch = -1.5F + this.field_3398.pitch;
		} else if (this.armPoseLeft == BipedEntityModel.ArmPose.field_3408) {
			this.field_3401.yaw = -0.6F + this.field_3398.yaw;
			this.field_3390.yaw = 0.3F + this.field_3398.yaw;
			this.field_3401.pitch = -1.5F + this.field_3398.pitch;
			this.field_3390.pitch = (float) (-Math.PI / 2) + this.field_3398.pitch + 0.1F;
		}

		if (this.field_3396 > 0.0F) {
			float q = f % 26.0F;
			float n = this.swingProgress > 0.0F ? 0.0F : this.field_3396;
			if (q < 14.0F) {
				this.field_3390.pitch = this.method_2804(this.field_3390.pitch, 0.0F, this.field_3396);
				this.field_3401.pitch = MathHelper.lerp(n, this.field_3401.pitch, 0.0F);
				this.field_3390.yaw = this.method_2804(this.field_3390.yaw, (float) Math.PI, this.field_3396);
				this.field_3401.yaw = MathHelper.lerp(n, this.field_3401.yaw, (float) Math.PI);
				this.field_3390.roll = this.method_2804(this.field_3390.roll, (float) Math.PI + 1.8707964F * this.method_2807(q) / this.method_2807(14.0F), this.field_3396);
				this.field_3401.roll = MathHelper.lerp(n, this.field_3401.roll, (float) Math.PI - 1.8707964F * this.method_2807(q) / this.method_2807(14.0F));
			} else if (q >= 14.0F && q < 22.0F) {
				float o = (q - 14.0F) / 8.0F;
				this.field_3390.pitch = this.method_2804(this.field_3390.pitch, (float) (Math.PI / 2) * o, this.field_3396);
				this.field_3401.pitch = MathHelper.lerp(n, this.field_3401.pitch, (float) (Math.PI / 2) * o);
				this.field_3390.yaw = this.method_2804(this.field_3390.yaw, (float) Math.PI, this.field_3396);
				this.field_3401.yaw = MathHelper.lerp(n, this.field_3401.yaw, (float) Math.PI);
				this.field_3390.roll = this.method_2804(this.field_3390.roll, 5.012389F - 1.8707964F * o, this.field_3396);
				this.field_3401.roll = MathHelper.lerp(n, this.field_3401.roll, 1.2707963F + 1.8707964F * o);
			} else if (q >= 22.0F && q < 26.0F) {
				float o = (q - 22.0F) / 4.0F;
				this.field_3390.pitch = this.method_2804(this.field_3390.pitch, (float) (Math.PI / 2) - (float) (Math.PI / 2) * o, this.field_3396);
				this.field_3401.pitch = MathHelper.lerp(n, this.field_3401.pitch, (float) (Math.PI / 2) - (float) (Math.PI / 2) * o);
				this.field_3390.yaw = this.method_2804(this.field_3390.yaw, (float) Math.PI, this.field_3396);
				this.field_3401.yaw = MathHelper.lerp(n, this.field_3401.yaw, (float) Math.PI);
				this.field_3390.roll = this.method_2804(this.field_3390.roll, (float) Math.PI, this.field_3396);
				this.field_3401.roll = MathHelper.lerp(n, this.field_3401.roll, (float) Math.PI);
			}

			float o = 0.3F;
			float p = 0.33333334F;
			this.field_3397.pitch = MathHelper.lerp(this.field_3396, this.field_3397.pitch, 0.3F * MathHelper.cos(f * 0.33333334F + (float) Math.PI));
			this.field_3392.pitch = MathHelper.lerp(this.field_3396, this.field_3392.pitch, 0.3F * MathHelper.cos(f * 0.33333334F));
		}

		this.field_3394.copyRotation(this.field_3398);
	}

	protected float method_2804(float f, float g, float h) {
		float i = g - f;

		while (i < (float) -Math.PI) {
			i += (float) (Math.PI * 2);
		}

		while (i >= (float) Math.PI) {
			i -= (float) (Math.PI * 2);
		}

		return f + h * i;
	}

	private float method_2807(float f) {
		return -65.0F * f + f * f;
	}

	public void setAttributes(BipedEntityModel<T> bipedEntityModel) {
		super.method_17081(bipedEntityModel);
		bipedEntityModel.armPoseLeft = this.armPoseLeft;
		bipedEntityModel.armPoseRight = this.armPoseRight;
		bipedEntityModel.isSneaking = this.isSneaking;
	}

	public void setVisible(boolean bl) {
		this.field_3398.visible = bl;
		this.field_3394.visible = bl;
		this.field_3391.visible = bl;
		this.field_3401.visible = bl;
		this.field_3390.visible = bl;
		this.field_3392.visible = bl;
		this.field_3397.visible = bl;
	}

	@Override
	public void method_2803(float f, OptionMainHand optionMainHand) {
		this.method_2808(optionMainHand).applyTransform(f);
	}

	protected Cuboid method_2808(OptionMainHand optionMainHand) {
		return optionMainHand == OptionMainHand.field_6182 ? this.field_3390 : this.field_3401;
	}

	@Override
	public Cuboid getHead() {
		return this.field_3398;
	}

	protected OptionMainHand getPreferedHand(T livingEntity) {
		OptionMainHand optionMainHand = livingEntity.getMainHand();
		return livingEntity.preferredHand == Hand.MAIN ? optionMainHand : optionMainHand.getOpposite();
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
