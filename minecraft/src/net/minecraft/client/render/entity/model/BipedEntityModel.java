package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BipedEntityModel<T extends LivingEntity> extends AnimalModel<T> implements ModelWithArms, ModelWithHead {
	public final ModelPart head;
	public final ModelPart helmet;
	public final ModelPart torso;
	public final ModelPart rightArm;
	public final ModelPart field_27433;
	public final ModelPart rightLeg;
	public final ModelPart leftLeg;
	public BipedEntityModel.ArmPose leftArmPose = BipedEntityModel.ArmPose.EMPTY;
	public BipedEntityModel.ArmPose rightArmPose = BipedEntityModel.ArmPose.EMPTY;
	public boolean sneaking;
	public float leaningPitch;

	public BipedEntityModel(ModelPart modelPart) {
		this(modelPart, RenderLayer::getEntityCutoutNoCull);
	}

	public BipedEntityModel(ModelPart modelPart, Function<Identifier, RenderLayer> function) {
		super(function, true, 16.0F, 0.0F, 2.0F, 2.0F, 24.0F);
		this.head = modelPart.method_32086("head");
		this.helmet = modelPart.method_32086("hat");
		this.torso = modelPart.method_32086("body");
		this.rightArm = modelPart.method_32086("right_arm");
		this.field_27433 = modelPart.method_32086("left_arm");
		this.rightLeg = modelPart.method_32086("right_leg");
		this.leftLeg = modelPart.method_32086("left_leg");
	}

	public static class_5609 method_32011(class_5605 arg, float f) {
		class_5609 lv = new class_5609();
		class_5610 lv2 = lv.method_32111();
		lv2.method_32117(
			"head", class_5606.method_32108().method_32101(0, 0).method_32098(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, arg), class_5603.method_32090(0.0F, 0.0F + f, 0.0F)
		);
		lv2.method_32117(
			"hat",
			class_5606.method_32108().method_32101(32, 0).method_32098(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, arg.method_32094(0.5F)),
			class_5603.method_32090(0.0F, 0.0F + f, 0.0F)
		);
		lv2.method_32117(
			"body",
			class_5606.method_32108().method_32101(16, 16).method_32098(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, arg),
			class_5603.method_32090(0.0F, 0.0F + f, 0.0F)
		);
		lv2.method_32117(
			"right_arm",
			class_5606.method_32108().method_32101(40, 16).method_32098(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, arg),
			class_5603.method_32090(-5.0F, 2.0F + f, 0.0F)
		);
		lv2.method_32117(
			"left_arm",
			class_5606.method_32108().method_32101(40, 16).method_32096().method_32098(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, arg),
			class_5603.method_32090(5.0F, 2.0F + f, 0.0F)
		);
		lv2.method_32117(
			"right_leg",
			class_5606.method_32108().method_32101(0, 16).method_32098(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, arg),
			class_5603.method_32090(-1.9F, 12.0F + f, 0.0F)
		);
		lv2.method_32117(
			"left_leg",
			class_5606.method_32108().method_32101(0, 16).method_32096().method_32098(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, arg),
			class_5603.method_32090(1.9F, 12.0F + f, 0.0F)
		);
		return lv;
	}

	@Override
	protected Iterable<ModelPart> getHeadParts() {
		return ImmutableList.<ModelPart>of(this.head);
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return ImmutableList.<ModelPart>of(this.torso, this.rightArm, this.field_27433, this.rightLeg, this.leftLeg, this.helmet);
	}

	public void animateModel(T livingEntity, float f, float g, float h) {
		this.leaningPitch = livingEntity.getLeaningPitch(h);
		super.animateModel(livingEntity, f, g, h);
	}

	public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
		boolean bl = livingEntity.getRoll() > 4;
		boolean bl2 = livingEntity.isInSwimmingPose();
		this.head.yaw = i * (float) (Math.PI / 180.0);
		if (bl) {
			this.head.pitch = (float) (-Math.PI / 4);
		} else if (this.leaningPitch > 0.0F) {
			if (bl2) {
				this.head.pitch = this.lerpAngle(this.leaningPitch, this.head.pitch, (float) (-Math.PI / 4));
			} else {
				this.head.pitch = this.lerpAngle(this.leaningPitch, this.head.pitch, j * (float) (Math.PI / 180.0));
			}
		} else {
			this.head.pitch = j * (float) (Math.PI / 180.0);
		}

		this.torso.yaw = 0.0F;
		this.rightArm.pivotZ = 0.0F;
		this.rightArm.pivotX = -5.0F;
		this.field_27433.pivotZ = 0.0F;
		this.field_27433.pivotX = 5.0F;
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
		this.field_27433.pitch = MathHelper.cos(f * 0.6662F) * 2.0F * g * 0.5F / k;
		this.rightArm.roll = 0.0F;
		this.field_27433.roll = 0.0F;
		this.rightLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g / k;
		this.leftLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g / k;
		this.rightLeg.yaw = 0.0F;
		this.leftLeg.yaw = 0.0F;
		this.rightLeg.roll = 0.0F;
		this.leftLeg.roll = 0.0F;
		if (this.riding) {
			this.rightArm.pitch += (float) (-Math.PI / 5);
			this.field_27433.pitch += (float) (-Math.PI / 5);
			this.rightLeg.pitch = -1.4137167F;
			this.rightLeg.yaw = (float) (Math.PI / 10);
			this.rightLeg.roll = 0.07853982F;
			this.leftLeg.pitch = -1.4137167F;
			this.leftLeg.yaw = (float) (-Math.PI / 10);
			this.leftLeg.roll = -0.07853982F;
		}

		this.rightArm.yaw = 0.0F;
		this.field_27433.yaw = 0.0F;
		boolean bl3 = livingEntity.getMainArm() == Arm.RIGHT;
		boolean bl4 = bl3 ? this.leftArmPose.method_30156() : this.rightArmPose.method_30156();
		if (bl3 != bl4) {
			this.method_30155(livingEntity);
			this.method_30154(livingEntity);
		} else {
			this.method_30154(livingEntity);
			this.method_30155(livingEntity);
		}

		this.method_29353(livingEntity, h);
		if (this.sneaking) {
			this.torso.pitch = 0.5F;
			this.rightArm.pitch += 0.4F;
			this.field_27433.pitch += 0.4F;
			this.rightLeg.pivotZ = 4.0F;
			this.leftLeg.pivotZ = 4.0F;
			this.rightLeg.pivotY = 12.2F;
			this.leftLeg.pivotY = 12.2F;
			this.head.pivotY = 4.2F;
			this.torso.pivotY = 3.2F;
			this.field_27433.pivotY = 5.2F;
			this.rightArm.pivotY = 5.2F;
		} else {
			this.torso.pitch = 0.0F;
			this.rightLeg.pivotZ = 0.1F;
			this.leftLeg.pivotZ = 0.1F;
			this.rightLeg.pivotY = 12.0F;
			this.leftLeg.pivotY = 12.0F;
			this.head.pivotY = 0.0F;
			this.torso.pivotY = 0.0F;
			this.field_27433.pivotY = 2.0F;
			this.rightArm.pivotY = 2.0F;
		}

		CrossbowPosing.method_29350(this.rightArm, this.field_27433, h);
		if (this.leaningPitch > 0.0F) {
			float l = f % 26.0F;
			Arm arm = this.getPreferredArm(livingEntity);
			float m = arm == Arm.RIGHT && this.handSwingProgress > 0.0F ? 0.0F : this.leaningPitch;
			float n = arm == Arm.LEFT && this.handSwingProgress > 0.0F ? 0.0F : this.leaningPitch;
			if (l < 14.0F) {
				this.field_27433.pitch = this.lerpAngle(n, this.field_27433.pitch, 0.0F);
				this.rightArm.pitch = MathHelper.lerp(m, this.rightArm.pitch, 0.0F);
				this.field_27433.yaw = this.lerpAngle(n, this.field_27433.yaw, (float) Math.PI);
				this.rightArm.yaw = MathHelper.lerp(m, this.rightArm.yaw, (float) Math.PI);
				this.field_27433.roll = this.lerpAngle(n, this.field_27433.roll, (float) Math.PI + 1.8707964F * this.method_2807(l) / this.method_2807(14.0F));
				this.rightArm.roll = MathHelper.lerp(m, this.rightArm.roll, (float) Math.PI - 1.8707964F * this.method_2807(l) / this.method_2807(14.0F));
			} else if (l >= 14.0F && l < 22.0F) {
				float o = (l - 14.0F) / 8.0F;
				this.field_27433.pitch = this.lerpAngle(n, this.field_27433.pitch, (float) (Math.PI / 2) * o);
				this.rightArm.pitch = MathHelper.lerp(m, this.rightArm.pitch, (float) (Math.PI / 2) * o);
				this.field_27433.yaw = this.lerpAngle(n, this.field_27433.yaw, (float) Math.PI);
				this.rightArm.yaw = MathHelper.lerp(m, this.rightArm.yaw, (float) Math.PI);
				this.field_27433.roll = this.lerpAngle(n, this.field_27433.roll, 5.012389F - 1.8707964F * o);
				this.rightArm.roll = MathHelper.lerp(m, this.rightArm.roll, 1.2707963F + 1.8707964F * o);
			} else if (l >= 22.0F && l < 26.0F) {
				float o = (l - 22.0F) / 4.0F;
				this.field_27433.pitch = this.lerpAngle(n, this.field_27433.pitch, (float) (Math.PI / 2) - (float) (Math.PI / 2) * o);
				this.rightArm.pitch = MathHelper.lerp(m, this.rightArm.pitch, (float) (Math.PI / 2) - (float) (Math.PI / 2) * o);
				this.field_27433.yaw = this.lerpAngle(n, this.field_27433.yaw, (float) Math.PI);
				this.rightArm.yaw = MathHelper.lerp(m, this.rightArm.yaw, (float) Math.PI);
				this.field_27433.roll = this.lerpAngle(n, this.field_27433.roll, (float) Math.PI);
				this.rightArm.roll = MathHelper.lerp(m, this.rightArm.roll, (float) Math.PI);
			}

			float o = 0.3F;
			float p = 0.33333334F;
			this.leftLeg.pitch = MathHelper.lerp(this.leaningPitch, this.leftLeg.pitch, 0.3F * MathHelper.cos(f * 0.33333334F + (float) Math.PI));
			this.rightLeg.pitch = MathHelper.lerp(this.leaningPitch, this.rightLeg.pitch, 0.3F * MathHelper.cos(f * 0.33333334F));
		}

		this.helmet.copyPositionAndRotation(this.head);
	}

	private void method_30154(T livingEntity) {
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
				break;
			case BOW_AND_ARROW:
				this.rightArm.yaw = -0.1F + this.head.yaw;
				this.field_27433.yaw = 0.1F + this.head.yaw + 0.4F;
				this.rightArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
				this.field_27433.pitch = (float) (-Math.PI / 2) + this.head.pitch;
				break;
			case CROSSBOW_CHARGE:
				CrossbowPosing.charge(this.rightArm, this.field_27433, livingEntity, true);
				break;
			case CROSSBOW_HOLD:
				CrossbowPosing.hold(this.rightArm, this.field_27433, this.head, true);
				break;
			case SPYGLASS:
				this.rightArm.pitch = MathHelper.clamp(this.head.pitch + CrossbowPosing.method_31978(this.rightArm), -2.4F, 3.3F);
				this.rightArm.yaw = MathHelper.clamp(this.head.yaw + (float) (-Math.PI / 4), -1.1F, 0.0F);
		}
	}

	private void method_30155(T livingEntity) {
		switch (this.leftArmPose) {
			case EMPTY:
				this.field_27433.yaw = 0.0F;
				break;
			case BLOCK:
				this.field_27433.pitch = this.field_27433.pitch * 0.5F - 0.9424779F;
				this.field_27433.yaw = (float) (Math.PI / 6);
				break;
			case ITEM:
				this.field_27433.pitch = this.field_27433.pitch * 0.5F - (float) (Math.PI / 10);
				this.field_27433.yaw = 0.0F;
				break;
			case THROW_SPEAR:
				this.field_27433.pitch = this.field_27433.pitch * 0.5F - (float) Math.PI;
				this.field_27433.yaw = 0.0F;
				break;
			case BOW_AND_ARROW:
				this.rightArm.yaw = -0.1F + this.head.yaw - 0.4F;
				this.field_27433.yaw = 0.1F + this.head.yaw;
				this.rightArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
				this.field_27433.pitch = (float) (-Math.PI / 2) + this.head.pitch;
				break;
			case CROSSBOW_CHARGE:
				CrossbowPosing.charge(this.rightArm, this.field_27433, livingEntity, false);
				break;
			case CROSSBOW_HOLD:
				CrossbowPosing.hold(this.rightArm, this.field_27433, this.head, false);
				break;
			case SPYGLASS:
				this.field_27433.pitch = CrossbowPosing.method_31978(this.field_27433);
				this.field_27433.yaw = (float) (Math.PI / 4);
		}
	}

	protected void method_29353(T livingEntity, float f) {
		if (!(this.handSwingProgress <= 0.0F)) {
			Arm arm = this.getPreferredArm(livingEntity);
			ModelPart modelPart = this.getArm(arm);
			float g = this.handSwingProgress;
			this.torso.yaw = MathHelper.sin(MathHelper.sqrt(g) * (float) (Math.PI * 2)) * 0.2F;
			if (arm == Arm.LEFT) {
				this.torso.yaw *= -1.0F;
			}

			this.rightArm.pivotZ = MathHelper.sin(this.torso.yaw) * 5.0F;
			this.rightArm.pivotX = -MathHelper.cos(this.torso.yaw) * 5.0F;
			this.field_27433.pivotZ = -MathHelper.sin(this.torso.yaw) * 5.0F;
			this.field_27433.pivotX = MathHelper.cos(this.torso.yaw) * 5.0F;
			this.rightArm.yaw = this.rightArm.yaw + this.torso.yaw;
			this.field_27433.yaw = this.field_27433.yaw + this.torso.yaw;
			this.field_27433.pitch = this.field_27433.pitch + this.torso.yaw;
			g = 1.0F - this.handSwingProgress;
			g *= g;
			g *= g;
			g = 1.0F - g;
			float h = MathHelper.sin(g * (float) Math.PI);
			float i = MathHelper.sin(this.handSwingProgress * (float) Math.PI) * -(this.head.pitch - 0.7F) * 0.75F;
			modelPart.pitch = (float)((double)modelPart.pitch - ((double)h * 1.2 + (double)i));
			modelPart.yaw = modelPart.yaw + this.torso.yaw * 2.0F;
			modelPart.roll = modelPart.roll + MathHelper.sin(this.handSwingProgress * (float) Math.PI) * -0.4F;
		}
	}

	protected float lerpAngle(float f, float g, float h) {
		float i = (h - g) % (float) (Math.PI * 2);
		if (i < (float) -Math.PI) {
			i += (float) (Math.PI * 2);
		}

		if (i >= (float) Math.PI) {
			i -= (float) (Math.PI * 2);
		}

		return g + f * i;
	}

	private float method_2807(float f) {
		return -65.0F * f + f * f;
	}

	public void setAttributes(BipedEntityModel<T> bipedEntityModel) {
		super.copyStateTo(bipedEntityModel);
		bipedEntityModel.leftArmPose = this.leftArmPose;
		bipedEntityModel.rightArmPose = this.rightArmPose;
		bipedEntityModel.sneaking = this.sneaking;
		bipedEntityModel.head.copyPositionAndRotation(this.head);
		bipedEntityModel.helmet.copyPositionAndRotation(this.helmet);
		bipedEntityModel.torso.copyPositionAndRotation(this.torso);
		bipedEntityModel.rightArm.copyPositionAndRotation(this.rightArm);
		bipedEntityModel.field_27433.copyPositionAndRotation(this.field_27433);
		bipedEntityModel.rightLeg.copyPositionAndRotation(this.rightLeg);
		bipedEntityModel.leftLeg.copyPositionAndRotation(this.leftLeg);
	}

	public void setVisible(boolean visible) {
		this.head.visible = visible;
		this.helmet.visible = visible;
		this.torso.visible = visible;
		this.rightArm.visible = visible;
		this.field_27433.visible = visible;
		this.rightLeg.visible = visible;
		this.leftLeg.visible = visible;
	}

	@Override
	public void setArmAngle(Arm arm, MatrixStack matrices) {
		this.getArm(arm).rotate(matrices);
	}

	protected ModelPart getArm(Arm arm) {
		return arm == Arm.LEFT ? this.field_27433 : this.rightArm;
	}

	@Override
	public ModelPart getHead() {
		return this.head;
	}

	private Arm getPreferredArm(T entity) {
		Arm arm = entity.getMainArm();
		return entity.preferredHand == Hand.MAIN_HAND ? arm : arm.getOpposite();
	}

	@Environment(EnvType.CLIENT)
	public static enum ArmPose {
		EMPTY(false),
		ITEM(false),
		BLOCK(false),
		BOW_AND_ARROW(true),
		THROW_SPEAR(false),
		CROSSBOW_CHARGE(true),
		CROSSBOW_HOLD(true),
		SPYGLASS(false);

		private final boolean field_25722;

		private ArmPose(boolean bl) {
			this.field_25722 = bl;
		}

		public boolean method_30156() {
			return this.field_25722;
		}
	}
}
