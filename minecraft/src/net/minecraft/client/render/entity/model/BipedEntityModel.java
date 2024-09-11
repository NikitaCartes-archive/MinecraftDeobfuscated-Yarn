package net.minecraft.client.render.entity.model;

import java.util.Set;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

/**
 * Represents the model of a biped living entity.
 * 
 * <div class="fabric">
 * <table border=1>
 * <caption>Model parts of this model</caption>
 * <tr>
 *   <th>Part Name</th><th>Parent</th><th>Corresponding Field</th>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#HAT}</td><td>Root part</td><td>{@link #hat}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#HEAD}</td><td>Root part</td><td>{@link #head}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#BODY}</td><td>Root part</td><td>{@link #body}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_ARM}</td><td>Root part</td><td>{@link #rightArm}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_ARM}</td><td>Root part</td><td>{@link #leftArm}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#RIGHT_LEG}</td><td>Root part</td><td>{@link #rightLeg}</td>
 * </tr>
 * <tr>
 *   <td>{@value EntityModelPartNames#LEFT_LEG}</td><td>Root part</td><td>{@link #leftLeg}</td>
 * </tr>
 * </table>
 * </div>
 */
@Environment(EnvType.CLIENT)
public class BipedEntityModel<T extends BipedEntityRenderState> extends EntityModel<T> implements ModelWithArms, ModelWithHead {
	public static final ModelTransformer BABY_TRANSFORMER = new BabyModelTransformer(true, 16.0F, 0.0F, 2.0F, 2.0F, 24.0F, Set.of("head"));
	public static final float field_32505 = 0.25F;
	public static final float field_32506 = 0.5F;
	public static final float field_42513 = -0.1F;
	private static final float field_42512 = 0.005F;
	private static final float SPYGLASS_ARM_YAW_OFFSET = (float) (Math.PI / 12);
	private static final float SPYGLASS_ARM_PITCH_OFFSET = 1.9198622F;
	private static final float SPYGLASS_SNEAKING_ARM_PITCH_OFFSET = (float) (Math.PI / 12);
	private static final float field_46576 = (float) (-Math.PI * 4.0 / 9.0);
	private static final float field_46577 = 0.43633232F;
	private static final float field_46724 = (float) (Math.PI / 6);
	public static final float field_39069 = 1.4835298F;
	public static final float field_39070 = (float) (Math.PI / 6);
	public final ModelPart head;
	public final ModelPart hat;
	public final ModelPart body;
	public final ModelPart rightArm;
	public final ModelPart leftArm;
	public final ModelPart rightLeg;
	public final ModelPart leftLeg;

	public BipedEntityModel(ModelPart modelPart) {
		this(modelPart, RenderLayer::getEntityCutoutNoCull);
	}

	public BipedEntityModel(ModelPart modelPart, Function<Identifier, RenderLayer> function) {
		super(modelPart, function);
		this.head = modelPart.getChild(EntityModelPartNames.HEAD);
		this.hat = this.head.getChild(EntityModelPartNames.HAT);
		this.body = modelPart.getChild(EntityModelPartNames.BODY);
		this.rightArm = modelPart.getChild(EntityModelPartNames.RIGHT_ARM);
		this.leftArm = modelPart.getChild(EntityModelPartNames.LEFT_ARM);
		this.rightLeg = modelPart.getChild(EntityModelPartNames.RIGHT_LEG);
		this.leftLeg = modelPart.getChild(EntityModelPartNames.LEFT_LEG);
	}

	public static ModelData getModelData(Dilation dilation, float pivotOffsetY) {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(
			EntityModelPartNames.HEAD,
			ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, dilation),
			ModelTransform.pivot(0.0F, 0.0F + pivotOffsetY, 0.0F)
		);
		modelPartData2.addChild(
			EntityModelPartNames.HAT, ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, dilation.add(0.5F)), ModelTransform.NONE
		);
		modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create().uv(16, 16).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, dilation),
			ModelTransform.pivot(0.0F, 0.0F + pivotOffsetY, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_ARM,
			ModelPartBuilder.create().uv(40, 16).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation),
			ModelTransform.pivot(-5.0F, 2.0F + pivotOffsetY, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_ARM,
			ModelPartBuilder.create().uv(40, 16).mirrored().cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation),
			ModelTransform.pivot(5.0F, 2.0F + pivotOffsetY, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_LEG,
			ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation),
			ModelTransform.pivot(-1.9F, 12.0F + pivotOffsetY, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_LEG,
			ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation),
			ModelTransform.pivot(1.9F, 12.0F + pivotOffsetY, 0.0F)
		);
		return modelData;
	}

	protected BipedEntityModel.ArmPose getArmPose(T state, Arm arm) {
		return BipedEntityModel.ArmPose.EMPTY;
	}

	public void setAngles(T bipedEntityRenderState) {
		super.setAngles(bipedEntityRenderState);
		BipedEntityModel.ArmPose armPose = this.getArmPose(bipedEntityRenderState, Arm.LEFT);
		BipedEntityModel.ArmPose armPose2 = this.getArmPose(bipedEntityRenderState, Arm.RIGHT);
		float f = bipedEntityRenderState.leaningPitch;
		boolean bl = bipedEntityRenderState.isGliding;
		this.head.pitch = bipedEntityRenderState.pitch * (float) (Math.PI / 180.0);
		this.head.yaw = bipedEntityRenderState.yawDegrees * (float) (Math.PI / 180.0);
		if (bl) {
			this.head.pitch = (float) (-Math.PI / 4);
		} else if (f > 0.0F) {
			this.head.pitch = MathHelper.lerpAngleRadians(f, this.head.pitch, (float) (-Math.PI / 4));
		}

		float g = bipedEntityRenderState.limbFrequency;
		float h = bipedEntityRenderState.limbAmplitudeMultiplier;
		this.rightArm.pitch = MathHelper.cos(g * 0.6662F + (float) Math.PI) * 2.0F * h * 0.5F / bipedEntityRenderState.limbAmplitudeInverse;
		this.leftArm.pitch = MathHelper.cos(g * 0.6662F) * 2.0F * h * 0.5F / bipedEntityRenderState.limbAmplitudeInverse;
		this.rightLeg.pitch = MathHelper.cos(g * 0.6662F) * 1.4F * h / bipedEntityRenderState.limbAmplitudeInverse;
		this.leftLeg.pitch = MathHelper.cos(g * 0.6662F + (float) Math.PI) * 1.4F * h / bipedEntityRenderState.limbAmplitudeInverse;
		this.rightLeg.yaw = 0.005F;
		this.leftLeg.yaw = -0.005F;
		this.rightLeg.roll = 0.005F;
		this.leftLeg.roll = -0.005F;
		if (bipedEntityRenderState.hasVehicle) {
			this.rightArm.pitch += (float) (-Math.PI / 5);
			this.leftArm.pitch += (float) (-Math.PI / 5);
			this.rightLeg.pitch = -1.4137167F;
			this.rightLeg.yaw = (float) (Math.PI / 10);
			this.rightLeg.roll = 0.07853982F;
			this.leftLeg.pitch = -1.4137167F;
			this.leftLeg.yaw = (float) (-Math.PI / 10);
			this.leftLeg.roll = -0.07853982F;
		}

		boolean bl2 = bipedEntityRenderState.mainArm == Arm.RIGHT;
		if (bipedEntityRenderState.isUsingItem) {
			boolean bl3 = bipedEntityRenderState.activeHand == Hand.MAIN_HAND;
			if (bl3 == bl2) {
				this.positionRightArm(bipedEntityRenderState, armPose2);
			} else {
				this.positionLeftArm(bipedEntityRenderState, armPose);
			}
		} else {
			boolean bl3 = bl2 ? armPose.isTwoHanded() : armPose2.isTwoHanded();
			if (bl2 != bl3) {
				this.positionLeftArm(bipedEntityRenderState, armPose);
				this.positionRightArm(bipedEntityRenderState, armPose2);
			} else {
				this.positionRightArm(bipedEntityRenderState, armPose2);
				this.positionLeftArm(bipedEntityRenderState, armPose);
			}
		}

		this.animateArms(bipedEntityRenderState, bipedEntityRenderState.age);
		if (bipedEntityRenderState.isInSneakingPose) {
			this.body.pitch = 0.5F;
			this.rightArm.pitch += 0.4F;
			this.leftArm.pitch += 0.4F;
			this.rightLeg.pivotZ += 4.0F;
			this.leftLeg.pivotZ += 4.0F;
			this.head.pivotY += 4.2F;
			this.body.pivotY += 3.2F;
			this.leftArm.pivotY += 3.2F;
			this.rightArm.pivotY += 3.2F;
		}

		if (armPose2 != BipedEntityModel.ArmPose.SPYGLASS) {
			CrossbowPosing.swingArm(this.rightArm, bipedEntityRenderState.age, 1.0F);
		}

		if (armPose != BipedEntityModel.ArmPose.SPYGLASS) {
			CrossbowPosing.swingArm(this.leftArm, bipedEntityRenderState.age, -1.0F);
		}

		if (f > 0.0F) {
			float i = g % 26.0F;
			Arm arm = bipedEntityRenderState.preferredArm;
			float j = arm == Arm.RIGHT && bipedEntityRenderState.handSwingProgress > 0.0F ? 0.0F : f;
			float k = arm == Arm.LEFT && bipedEntityRenderState.handSwingProgress > 0.0F ? 0.0F : f;
			if (!bipedEntityRenderState.isUsingItem) {
				if (i < 14.0F) {
					this.leftArm.pitch = MathHelper.lerpAngleRadians(k, this.leftArm.pitch, 0.0F);
					this.rightArm.pitch = MathHelper.lerp(j, this.rightArm.pitch, 0.0F);
					this.leftArm.yaw = MathHelper.lerpAngleRadians(k, this.leftArm.yaw, (float) Math.PI);
					this.rightArm.yaw = MathHelper.lerp(j, this.rightArm.yaw, (float) Math.PI);
					this.leftArm.roll = MathHelper.lerpAngleRadians(k, this.leftArm.roll, (float) Math.PI + 1.8707964F * this.method_2807(i) / this.method_2807(14.0F));
					this.rightArm.roll = MathHelper.lerp(j, this.rightArm.roll, (float) Math.PI - 1.8707964F * this.method_2807(i) / this.method_2807(14.0F));
				} else if (i >= 14.0F && i < 22.0F) {
					float l = (i - 14.0F) / 8.0F;
					this.leftArm.pitch = MathHelper.lerpAngleRadians(k, this.leftArm.pitch, (float) (Math.PI / 2) * l);
					this.rightArm.pitch = MathHelper.lerp(j, this.rightArm.pitch, (float) (Math.PI / 2) * l);
					this.leftArm.yaw = MathHelper.lerpAngleRadians(k, this.leftArm.yaw, (float) Math.PI);
					this.rightArm.yaw = MathHelper.lerp(j, this.rightArm.yaw, (float) Math.PI);
					this.leftArm.roll = MathHelper.lerpAngleRadians(k, this.leftArm.roll, 5.012389F - 1.8707964F * l);
					this.rightArm.roll = MathHelper.lerp(j, this.rightArm.roll, 1.2707963F + 1.8707964F * l);
				} else if (i >= 22.0F && i < 26.0F) {
					float l = (i - 22.0F) / 4.0F;
					this.leftArm.pitch = MathHelper.lerpAngleRadians(k, this.leftArm.pitch, (float) (Math.PI / 2) - (float) (Math.PI / 2) * l);
					this.rightArm.pitch = MathHelper.lerp(j, this.rightArm.pitch, (float) (Math.PI / 2) - (float) (Math.PI / 2) * l);
					this.leftArm.yaw = MathHelper.lerpAngleRadians(k, this.leftArm.yaw, (float) Math.PI);
					this.rightArm.yaw = MathHelper.lerp(j, this.rightArm.yaw, (float) Math.PI);
					this.leftArm.roll = MathHelper.lerpAngleRadians(k, this.leftArm.roll, (float) Math.PI);
					this.rightArm.roll = MathHelper.lerp(j, this.rightArm.roll, (float) Math.PI);
				}
			}

			float l = 0.3F;
			float m = 0.33333334F;
			this.leftLeg.pitch = MathHelper.lerp(f, this.leftLeg.pitch, 0.3F * MathHelper.cos(g * 0.33333334F + (float) Math.PI));
			this.rightLeg.pitch = MathHelper.lerp(f, this.rightLeg.pitch, 0.3F * MathHelper.cos(g * 0.33333334F));
		}
	}

	private void positionRightArm(T state, BipedEntityModel.ArmPose armPose) {
		switch (armPose) {
			case EMPTY:
				this.rightArm.yaw = 0.0F;
				break;
			case ITEM:
				this.rightArm.pitch = this.rightArm.pitch * 0.5F - (float) (Math.PI / 10);
				this.rightArm.yaw = 0.0F;
				break;
			case BLOCK:
				this.positionBlockingArm(this.rightArm, true);
				break;
			case BOW_AND_ARROW:
				this.rightArm.yaw = -0.1F + this.head.yaw;
				this.leftArm.yaw = 0.1F + this.head.yaw + 0.4F;
				this.rightArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
				this.leftArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
				break;
			case THROW_SPEAR:
				this.rightArm.pitch = this.rightArm.pitch * 0.5F - (float) Math.PI;
				this.rightArm.yaw = 0.0F;
				break;
			case CROSSBOW_CHARGE:
				CrossbowPosing.charge(this.rightArm, this.leftArm, state.crossbowPullTime, state.itemUseTime, true);
				break;
			case CROSSBOW_HOLD:
				CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, true);
				break;
			case SPYGLASS:
				this.rightArm.pitch = MathHelper.clamp(this.head.pitch - 1.9198622F - (state.isInSneakingPose ? (float) (Math.PI / 12) : 0.0F), -2.4F, 3.3F);
				this.rightArm.yaw = this.head.yaw - (float) (Math.PI / 12);
				break;
			case TOOT_HORN:
				this.rightArm.pitch = MathHelper.clamp(this.head.pitch, -1.2F, 1.2F) - 1.4835298F;
				this.rightArm.yaw = this.head.yaw - (float) (Math.PI / 6);
				break;
			case BRUSH:
				this.rightArm.pitch = this.rightArm.pitch * 0.5F - (float) (Math.PI / 5);
				this.rightArm.yaw = 0.0F;
		}
	}

	private void positionLeftArm(T state, BipedEntityModel.ArmPose armPose) {
		switch (armPose) {
			case EMPTY:
				this.leftArm.yaw = 0.0F;
				break;
			case ITEM:
				this.leftArm.pitch = this.leftArm.pitch * 0.5F - (float) (Math.PI / 10);
				this.leftArm.yaw = 0.0F;
				break;
			case BLOCK:
				this.positionBlockingArm(this.leftArm, false);
				break;
			case BOW_AND_ARROW:
				this.rightArm.yaw = -0.1F + this.head.yaw - 0.4F;
				this.leftArm.yaw = 0.1F + this.head.yaw;
				this.rightArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
				this.leftArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
				break;
			case THROW_SPEAR:
				this.leftArm.pitch = this.leftArm.pitch * 0.5F - (float) Math.PI;
				this.leftArm.yaw = 0.0F;
				break;
			case CROSSBOW_CHARGE:
				CrossbowPosing.charge(this.rightArm, this.leftArm, state.crossbowPullTime, state.itemUseTime, false);
				break;
			case CROSSBOW_HOLD:
				CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, false);
				break;
			case SPYGLASS:
				this.leftArm.pitch = MathHelper.clamp(this.head.pitch - 1.9198622F - (state.isInSneakingPose ? (float) (Math.PI / 12) : 0.0F), -2.4F, 3.3F);
				this.leftArm.yaw = this.head.yaw + (float) (Math.PI / 12);
				break;
			case TOOT_HORN:
				this.leftArm.pitch = MathHelper.clamp(this.head.pitch, -1.2F, 1.2F) - 1.4835298F;
				this.leftArm.yaw = this.head.yaw + (float) (Math.PI / 6);
				break;
			case BRUSH:
				this.leftArm.pitch = this.leftArm.pitch * 0.5F - (float) (Math.PI / 5);
				this.leftArm.yaw = 0.0F;
		}
	}

	private void positionBlockingArm(ModelPart arm, boolean rightArm) {
		arm.pitch = arm.pitch * 0.5F - 0.9424779F + MathHelper.clamp(this.head.pitch, (float) (-Math.PI * 4.0 / 9.0), 0.43633232F);
		arm.yaw = (rightArm ? -30.0F : 30.0F) * (float) (Math.PI / 180.0) + MathHelper.clamp(this.head.yaw, (float) (-Math.PI / 6), (float) (Math.PI / 6));
	}

	protected void animateArms(T state, float animationProgress) {
		float f = state.handSwingProgress;
		if (!(f <= 0.0F)) {
			Arm arm = state.preferredArm;
			ModelPart modelPart = this.getArm(arm);
			this.body.yaw = MathHelper.sin(MathHelper.sqrt(f) * (float) (Math.PI * 2)) * 0.2F;
			if (arm == Arm.LEFT) {
				this.body.yaw *= -1.0F;
			}

			float h = state.ageScale;
			this.rightArm.pivotZ = MathHelper.sin(this.body.yaw) * 5.0F * h;
			this.rightArm.pivotX = -MathHelper.cos(this.body.yaw) * 5.0F * h;
			this.leftArm.pivotZ = -MathHelper.sin(this.body.yaw) * 5.0F * h;
			this.leftArm.pivotX = MathHelper.cos(this.body.yaw) * 5.0F * h;
			this.rightArm.yaw = this.rightArm.yaw + this.body.yaw;
			this.leftArm.yaw = this.leftArm.yaw + this.body.yaw;
			this.leftArm.pitch = this.leftArm.pitch + this.body.yaw;
			float g = 1.0F - f;
			g *= g;
			g *= g;
			g = 1.0F - g;
			float i = MathHelper.sin(g * (float) Math.PI);
			float j = MathHelper.sin(f * (float) Math.PI) * -(this.head.pitch - 0.7F) * 0.75F;
			modelPart.pitch -= i * 1.2F + j;
			modelPart.yaw = modelPart.yaw + this.body.yaw * 2.0F;
			modelPart.roll = modelPart.roll + MathHelper.sin(f * (float) Math.PI) * -0.4F;
		}
	}

	private float method_2807(float f) {
		return -65.0F * f + f * f;
	}

	public void copyTransforms(BipedEntityModel<T> model) {
		model.head.copyTransform(this.head);
		model.body.copyTransform(this.body);
		model.rightArm.copyTransform(this.rightArm);
		model.leftArm.copyTransform(this.leftArm);
		model.rightLeg.copyTransform(this.rightLeg);
		model.leftLeg.copyTransform(this.leftLeg);
	}

	public void setVisible(boolean visible) {
		this.head.visible = visible;
		this.hat.visible = visible;
		this.body.visible = visible;
		this.rightArm.visible = visible;
		this.leftArm.visible = visible;
		this.rightLeg.visible = visible;
		this.leftLeg.visible = visible;
	}

	@Override
	public void setArmAngle(Arm arm, MatrixStack matrices) {
		this.root.rotate(matrices);
		this.getArm(arm).rotate(matrices);
	}

	protected ModelPart getArm(Arm arm) {
		return arm == Arm.LEFT ? this.leftArm : this.rightArm;
	}

	@Override
	public ModelPart getHead() {
		return this.head;
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
		SPYGLASS(false),
		TOOT_HORN(false),
		BRUSH(false);

		private final boolean twoHanded;

		private ArmPose(final boolean twoHanded) {
			this.twoHanded = twoHanded;
		}

		public boolean isTwoHanded() {
			return this.twoHanded;
		}
	}
}
