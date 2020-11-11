package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.Dilation;
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
	public final ModelPart leftArm;
	public final ModelPart rightLeg;
	public final ModelPart leftLeg;
	public BipedEntityModel.ArmPose leftArmPose = BipedEntityModel.ArmPose.EMPTY;
	public BipedEntityModel.ArmPose rightArmPose = BipedEntityModel.ArmPose.EMPTY;
	public boolean sneaking;
	public float leaningPitch;

	public BipedEntityModel(ModelPart part) {
		this(part, RenderLayer::getEntityCutoutNoCull);
	}

	public BipedEntityModel(ModelPart modelPart, Function<Identifier, RenderLayer> function) {
		super(function, true, 16.0F, 0.0F, 2.0F, 2.0F, 24.0F);
		this.head = modelPart.getChild("head");
		this.helmet = modelPart.getChild("hat");
		this.torso = modelPart.getChild("body");
		this.rightArm = modelPart.getChild("right_arm");
		this.leftArm = modelPart.getChild("left_arm");
		this.rightLeg = modelPart.getChild("right_leg");
		this.leftLeg = modelPart.getChild("left_leg");
	}

	public static ModelData getModelData(Dilation dilation, float pivotOffsetY) {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			"head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, dilation), ModelTransform.pivot(0.0F, 0.0F + pivotOffsetY, 0.0F)
		);
		modelPartData.addChild(
			"hat",
			ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, dilation.add(0.5F)),
			ModelTransform.pivot(0.0F, 0.0F + pivotOffsetY, 0.0F)
		);
		modelPartData.addChild(
			"body", ModelPartBuilder.create().uv(16, 16).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, dilation), ModelTransform.pivot(0.0F, 0.0F + pivotOffsetY, 0.0F)
		);
		modelPartData.addChild(
			"right_arm",
			ModelPartBuilder.create().uv(40, 16).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation),
			ModelTransform.pivot(-5.0F, 2.0F + pivotOffsetY, 0.0F)
		);
		modelPartData.addChild(
			"left_arm",
			ModelPartBuilder.create().uv(40, 16).mirrored().cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation),
			ModelTransform.pivot(5.0F, 2.0F + pivotOffsetY, 0.0F)
		);
		modelPartData.addChild(
			"right_leg",
			ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation),
			ModelTransform.pivot(-1.9F, 12.0F + pivotOffsetY, 0.0F)
		);
		modelPartData.addChild(
			"left_leg",
			ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation),
			ModelTransform.pivot(1.9F, 12.0F + pivotOffsetY, 0.0F)
		);
		return modelData;
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
		this.leftArm.yaw = 0.0F;
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

		CrossbowPosing.method_29350(this.rightArm, this.leftArm, h);
		if (this.leaningPitch > 0.0F) {
			float l = f % 26.0F;
			Arm arm = this.getPreferredArm(livingEntity);
			float m = arm == Arm.RIGHT && this.handSwingProgress > 0.0F ? 0.0F : this.leaningPitch;
			float n = arm == Arm.LEFT && this.handSwingProgress > 0.0F ? 0.0F : this.leaningPitch;
			if (l < 14.0F) {
				this.leftArm.pitch = this.lerpAngle(n, this.leftArm.pitch, 0.0F);
				this.rightArm.pitch = MathHelper.lerp(m, this.rightArm.pitch, 0.0F);
				this.leftArm.yaw = this.lerpAngle(n, this.leftArm.yaw, (float) Math.PI);
				this.rightArm.yaw = MathHelper.lerp(m, this.rightArm.yaw, (float) Math.PI);
				this.leftArm.roll = this.lerpAngle(n, this.leftArm.roll, (float) Math.PI + 1.8707964F * this.method_2807(l) / this.method_2807(14.0F));
				this.rightArm.roll = MathHelper.lerp(m, this.rightArm.roll, (float) Math.PI - 1.8707964F * this.method_2807(l) / this.method_2807(14.0F));
			} else if (l >= 14.0F && l < 22.0F) {
				float o = (l - 14.0F) / 8.0F;
				this.leftArm.pitch = this.lerpAngle(n, this.leftArm.pitch, (float) (Math.PI / 2) * o);
				this.rightArm.pitch = MathHelper.lerp(m, this.rightArm.pitch, (float) (Math.PI / 2) * o);
				this.leftArm.yaw = this.lerpAngle(n, this.leftArm.yaw, (float) Math.PI);
				this.rightArm.yaw = MathHelper.lerp(m, this.rightArm.yaw, (float) Math.PI);
				this.leftArm.roll = this.lerpAngle(n, this.leftArm.roll, 5.012389F - 1.8707964F * o);
				this.rightArm.roll = MathHelper.lerp(m, this.rightArm.roll, 1.2707963F + 1.8707964F * o);
			} else if (l >= 22.0F && l < 26.0F) {
				float o = (l - 22.0F) / 4.0F;
				this.leftArm.pitch = this.lerpAngle(n, this.leftArm.pitch, (float) (Math.PI / 2) - (float) (Math.PI / 2) * o);
				this.rightArm.pitch = MathHelper.lerp(m, this.rightArm.pitch, (float) (Math.PI / 2) - (float) (Math.PI / 2) * o);
				this.leftArm.yaw = this.lerpAngle(n, this.leftArm.yaw, (float) Math.PI);
				this.rightArm.yaw = MathHelper.lerp(m, this.rightArm.yaw, (float) Math.PI);
				this.leftArm.roll = this.lerpAngle(n, this.leftArm.roll, (float) Math.PI);
				this.rightArm.roll = MathHelper.lerp(m, this.rightArm.roll, (float) Math.PI);
			}

			float o = 0.3F;
			float p = 0.33333334F;
			this.leftLeg.pitch = MathHelper.lerp(this.leaningPitch, this.leftLeg.pitch, 0.3F * MathHelper.cos(f * 0.33333334F + (float) Math.PI));
			this.rightLeg.pitch = MathHelper.lerp(this.leaningPitch, this.rightLeg.pitch, 0.3F * MathHelper.cos(f * 0.33333334F));
		}

		this.helmet.copyTransform(this.head);
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
				this.leftArm.yaw = 0.1F + this.head.yaw + 0.4F;
				this.rightArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
				this.leftArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
				break;
			case CROSSBOW_CHARGE:
				CrossbowPosing.charge(this.rightArm, this.leftArm, livingEntity, true);
				break;
			case CROSSBOW_HOLD:
				CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, true);
				break;
			case SPYGLASS:
				this.rightArm.pitch = MathHelper.clamp(this.head.pitch + CrossbowPosing.method_31978(this.rightArm), -2.4F, 3.3F);
				this.rightArm.yaw = MathHelper.clamp(this.head.yaw + (float) (-Math.PI / 4), -1.1F, 0.0F);
		}
	}

	private void method_30155(T livingEntity) {
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
				break;
			case THROW_SPEAR:
				this.leftArm.pitch = this.leftArm.pitch * 0.5F - (float) Math.PI;
				this.leftArm.yaw = 0.0F;
				break;
			case BOW_AND_ARROW:
				this.rightArm.yaw = -0.1F + this.head.yaw - 0.4F;
				this.leftArm.yaw = 0.1F + this.head.yaw;
				this.rightArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
				this.leftArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
				break;
			case CROSSBOW_CHARGE:
				CrossbowPosing.charge(this.rightArm, this.leftArm, livingEntity, false);
				break;
			case CROSSBOW_HOLD:
				CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, false);
				break;
			case SPYGLASS:
				this.leftArm.pitch = CrossbowPosing.method_31978(this.leftArm);
				this.leftArm.yaw = (float) (Math.PI / 4);
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
			this.leftArm.pivotZ = -MathHelper.sin(this.torso.yaw) * 5.0F;
			this.leftArm.pivotX = MathHelper.cos(this.torso.yaw) * 5.0F;
			this.rightArm.yaw = this.rightArm.yaw + this.torso.yaw;
			this.leftArm.yaw = this.leftArm.yaw + this.torso.yaw;
			this.leftArm.pitch = this.leftArm.pitch + this.torso.yaw;
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
		bipedEntityModel.head.copyTransform(this.head);
		bipedEntityModel.helmet.copyTransform(this.helmet);
		bipedEntityModel.torso.copyTransform(this.torso);
		bipedEntityModel.rightArm.copyTransform(this.rightArm);
		bipedEntityModel.leftArm.copyTransform(this.leftArm);
		bipedEntityModel.rightLeg.copyTransform(this.rightLeg);
		bipedEntityModel.leftLeg.copyTransform(this.leftLeg);
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
	public void setArmAngle(Arm arm, MatrixStack matrices) {
		this.getArm(arm).rotate(matrices);
	}

	protected ModelPart getArm(Arm arm) {
		return arm == Arm.LEFT ? this.leftArm : this.rightArm;
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
