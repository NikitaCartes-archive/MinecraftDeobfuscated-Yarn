package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BipedEntityModel<T extends LivingEntity> extends AnimalModel<T> implements ModelWithArms, ModelWithHead {
	public ModelPart head;
	public ModelPart helmet;
	public ModelPart torso;
	public ModelPart rightArm;
	public ModelPart leftArm;
	public ModelPart rightLeg;
	public ModelPart leftLeg;
	public BipedEntityModel.ArmPose leftArmPose = BipedEntityModel.ArmPose.EMPTY;
	public BipedEntityModel.ArmPose rightArmPose = BipedEntityModel.ArmPose.EMPTY;
	public boolean isSneaking;
	public float field_3396;
	private float itemUsedTime;

	public BipedEntityModel(float scale) {
		this(RenderLayer::getEntityCutoutNoCull, scale, 0.0F, 64, 32);
	}

	protected BipedEntityModel(float scale, float f, int textureWidth, int textureHeight) {
		this(RenderLayer::getEntityCutoutNoCull, scale, f, textureWidth, textureHeight);
	}

	public BipedEntityModel(Function<Identifier, RenderLayer> texturedLayerFactory, float scale, float f, int textureWidth, int textureHeight) {
		super(texturedLayerFactory, true, 16.0F, 0.0F, 2.0F, 2.0F, 24.0F);
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, scale);
		this.head.setPivot(0.0F, 0.0F + f, 0.0F);
		this.helmet = new ModelPart(this, 32, 0);
		this.helmet.addCuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, scale + 0.5F);
		this.helmet.setPivot(0.0F, 0.0F + f, 0.0F);
		this.torso = new ModelPart(this, 16, 16);
		this.torso.addCuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, scale);
		this.torso.setPivot(0.0F, 0.0F + f, 0.0F);
		this.rightArm = new ModelPart(this, 40, 16);
		this.rightArm.addCuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale);
		this.rightArm.setPivot(-5.0F, 2.0F + f, 0.0F);
		this.leftArm = new ModelPart(this, 40, 16);
		this.leftArm.mirror = true;
		this.leftArm.addCuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale);
		this.leftArm.setPivot(5.0F, 2.0F + f, 0.0F);
		this.rightLeg = new ModelPart(this, 0, 16);
		this.rightLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale);
		this.rightLeg.setPivot(-1.9F, 12.0F + f, 0.0F);
		this.leftLeg = new ModelPart(this, 0, 16);
		this.leftLeg.mirror = true;
		this.leftLeg.addCuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, scale);
		this.leftLeg.setPivot(1.9F, 12.0F + f, 0.0F);
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of(this.head);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.torso, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg, this.helmet);
	}

	public void animateModel(T livingEntity, float f, float g, float h) {
		this.field_3396 = livingEntity.getLeaningPitch(h);
		this.itemUsedTime = (float)livingEntity.getItemUseTime();
		super.animateModel(livingEntity, f, g, h);
	}

	public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
		boolean bl = livingEntity.getRoll() > 4;
		boolean bl2 = livingEntity.isInSwimmingPose();
		this.head.yaw = i * (float) (Math.PI / 180.0);
		if (bl) {
			this.head.pitch = (float) (-Math.PI / 4);
		} else if (this.field_3396 > 0.0F) {
			if (bl2) {
				this.head.pitch = this.lerpAngle(this.head.pitch, (float) (-Math.PI / 4), this.field_3396);
			} else {
				this.head.pitch = this.lerpAngle(this.head.pitch, j * (float) (Math.PI / 180.0), this.field_3396);
			}
		} else {
			this.head.pitch = j * (float) (Math.PI / 180.0);
		}

		this.torso.yaw = 0.0F;
		this.rightArm.pivotZ = 0.0F;
		this.rightArm.pivotX = -5.0F;
		this.leftArm.pivotZ = 0.0F;
		this.leftArm.pivotX = 5.0F;
		float k = 1.0F;
		if (bl) {
			k = (float)livingEntity.getVelocity().lengthSquared();
			k /= 0.2F;
			k *= k * k;
		}

		if (k < 1.0F) {
			k = 1.0F;
		}

		this.rightArm.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 2.0F * g * 0.5F / k;
		this.leftArm.pitch = MathHelper.cos(f * 0.6662F) * 2.0F * g * 0.5F / k;
		this.rightArm.roll = 0.0F;
		this.leftArm.roll = 0.0F;
		this.rightLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g / k;
		this.leftLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g / k;
		this.rightLeg.yaw = 0.0F;
		this.leftLeg.yaw = 0.0F;
		this.rightLeg.roll = 0.0F;
		this.leftLeg.roll = 0.0F;
		if (this.riding) {
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
			ModelPart modelPart = this.getArm(arm);
			float l = this.handSwingProgress;
			this.torso.yaw = MathHelper.sin(MathHelper.sqrt(l) * (float) (Math.PI * 2)) * 0.2F;
			if (arm == Arm.LEFT) {
				this.torso.yaw *= -1.0F;
			}

			this.rightArm.pivotZ = MathHelper.sin(this.torso.yaw) * 5.0F;
			this.rightArm.pivotX = -MathHelper.cos(this.torso.yaw) * 5.0F;
			this.leftArm.pivotZ = -MathHelper.sin(this.torso.yaw) * 5.0F;
			this.leftArm.pivotX = MathHelper.cos(this.torso.yaw) * 5.0F;
			this.rightArm.yaw = this.rightArm.yaw + this.torso.yaw;
			this.leftArm.yaw = this.leftArm.yaw + this.torso.yaw;
			this.leftArm.pitch = this.leftArm.pitch + this.torso.yaw;
			l = 1.0F - this.handSwingProgress;
			l *= l;
			l *= l;
			l = 1.0F - l;
			float m = MathHelper.sin(l * (float) Math.PI);
			float n = MathHelper.sin(this.handSwingProgress * (float) Math.PI) * -(this.head.pitch - 0.7F) * 0.75F;
			modelPart.pitch = (float)((double)modelPart.pitch - ((double)m * 1.2 + (double)n));
			modelPart.yaw = modelPart.yaw + this.torso.yaw * 2.0F;
			modelPart.roll = modelPart.roll + MathHelper.sin(this.handSwingProgress * (float) Math.PI) * -0.4F;
		}

		if (this.isSneaking) {
			this.torso.pitch = 0.5F;
			this.rightArm.pitch += 0.4F;
			this.leftArm.pitch += 0.4F;
			this.rightLeg.pivotZ = 4.0F;
			this.leftLeg.pivotZ = 4.0F;
			this.rightLeg.pivotY = 12.2F;
			this.leftLeg.pivotY = 12.2F;
			this.head.pivotY = 4.2F;
			this.torso.pivotY = 3.2F;
			this.leftArm.pivotY = 5.2F;
			this.rightArm.pivotY = 5.2F;
		} else {
			this.torso.pitch = 0.0F;
			this.rightLeg.pivotZ = 0.1F;
			this.leftLeg.pivotZ = 0.1F;
			this.rightLeg.pivotY = 12.0F;
			this.leftLeg.pivotY = 12.0F;
			this.head.pivotY = 0.0F;
			this.torso.pivotY = 0.0F;
			this.leftArm.pivotY = 2.0F;
			this.rightArm.pivotY = 2.0F;
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

		float o = (float)CrossbowItem.getPullTime(livingEntity.getActiveItem());
		if (this.rightArmPose == BipedEntityModel.ArmPose.CROSSBOW_CHARGE) {
			this.rightArm.yaw = -0.8F;
			this.rightArm.pitch = -0.97079635F;
			this.leftArm.pitch = -0.97079635F;
			float p = MathHelper.clamp(this.itemUsedTime, 0.0F, o);
			this.leftArm.yaw = MathHelper.lerp(p / o, 0.4F, 0.85F);
			this.leftArm.pitch = MathHelper.lerp(p / o, this.leftArm.pitch, (float) (-Math.PI / 2));
		} else if (this.leftArmPose == BipedEntityModel.ArmPose.CROSSBOW_CHARGE) {
			this.leftArm.yaw = 0.8F;
			this.rightArm.pitch = -0.97079635F;
			this.leftArm.pitch = -0.97079635F;
			float p = MathHelper.clamp(this.itemUsedTime, 0.0F, o);
			this.rightArm.yaw = MathHelper.lerp(p / o, -0.4F, -0.85F);
			this.rightArm.pitch = MathHelper.lerp(p / o, this.rightArm.pitch, (float) (-Math.PI / 2));
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
			float p = f % 26.0F;
			float l = this.handSwingProgress > 0.0F ? 0.0F : this.field_3396;
			if (p < 14.0F) {
				this.leftArm.pitch = this.lerpAngle(this.leftArm.pitch, 0.0F, this.field_3396);
				this.rightArm.pitch = MathHelper.lerp(l, this.rightArm.pitch, 0.0F);
				this.leftArm.yaw = this.lerpAngle(this.leftArm.yaw, (float) Math.PI, this.field_3396);
				this.rightArm.yaw = MathHelper.lerp(l, this.rightArm.yaw, (float) Math.PI);
				this.leftArm.roll = this.lerpAngle(this.leftArm.roll, (float) Math.PI + 1.8707964F * this.method_2807(p) / this.method_2807(14.0F), this.field_3396);
				this.rightArm.roll = MathHelper.lerp(l, this.rightArm.roll, (float) Math.PI - 1.8707964F * this.method_2807(p) / this.method_2807(14.0F));
			} else if (p >= 14.0F && p < 22.0F) {
				float m = (p - 14.0F) / 8.0F;
				this.leftArm.pitch = this.lerpAngle(this.leftArm.pitch, (float) (Math.PI / 2) * m, this.field_3396);
				this.rightArm.pitch = MathHelper.lerp(l, this.rightArm.pitch, (float) (Math.PI / 2) * m);
				this.leftArm.yaw = this.lerpAngle(this.leftArm.yaw, (float) Math.PI, this.field_3396);
				this.rightArm.yaw = MathHelper.lerp(l, this.rightArm.yaw, (float) Math.PI);
				this.leftArm.roll = this.lerpAngle(this.leftArm.roll, 5.012389F - 1.8707964F * m, this.field_3396);
				this.rightArm.roll = MathHelper.lerp(l, this.rightArm.roll, 1.2707963F + 1.8707964F * m);
			} else if (p >= 22.0F && p < 26.0F) {
				float m = (p - 22.0F) / 4.0F;
				this.leftArm.pitch = this.lerpAngle(this.leftArm.pitch, (float) (Math.PI / 2) - (float) (Math.PI / 2) * m, this.field_3396);
				this.rightArm.pitch = MathHelper.lerp(l, this.rightArm.pitch, (float) (Math.PI / 2) - (float) (Math.PI / 2) * m);
				this.leftArm.yaw = this.lerpAngle(this.leftArm.yaw, (float) Math.PI, this.field_3396);
				this.rightArm.yaw = MathHelper.lerp(l, this.rightArm.yaw, (float) Math.PI);
				this.leftArm.roll = this.lerpAngle(this.leftArm.roll, (float) Math.PI, this.field_3396);
				this.rightArm.roll = MathHelper.lerp(l, this.rightArm.roll, (float) Math.PI);
			}

			float m = 0.3F;
			float n = 0.33333334F;
			this.leftLeg.pitch = MathHelper.lerp(this.field_3396, this.leftLeg.pitch, 0.3F * MathHelper.cos(f * 0.33333334F + (float) Math.PI));
			this.rightLeg.pitch = MathHelper.lerp(this.field_3396, this.rightLeg.pitch, 0.3F * MathHelper.cos(f * 0.33333334F));
		}

		this.helmet.copyPositionAndRotation(this.head);
	}

	protected float lerpAngle(float from, float to, float position) {
		float f = (to - from) % (float) (Math.PI * 2);
		if (f < (float) -Math.PI) {
			f += (float) (Math.PI * 2);
		}

		if (f >= (float) Math.PI) {
			f -= (float) (Math.PI * 2);
		}

		return from + position * f;
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

	public void setVisible(boolean visible) {
		this.head.visible = visible;
		this.helmet.visible = visible;
		this.torso.visible = visible;
		this.rightArm.visible = visible;
		this.leftArm.visible = visible;
		this.rightLeg.visible = visible;
		this.leftLeg.visible = visible;
	}

	@Override
	public void setArmAngle(Arm arm, MatrixStack matrixStack) {
		this.getArm(arm).rotate(matrixStack);
	}

	protected ModelPart getArm(Arm arm) {
		return arm == Arm.LEFT ? this.leftArm : this.rightArm;
	}

	@Override
	public ModelPart getHead() {
		return this.head;
	}

	protected Arm getPreferredArm(T entity) {
		Arm arm = entity.getMainArm();
		return entity.preferredHand == Hand.MAIN_HAND ? arm : arm.getOpposite();
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
