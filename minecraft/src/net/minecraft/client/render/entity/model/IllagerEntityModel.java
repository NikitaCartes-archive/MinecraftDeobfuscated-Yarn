package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.util.math.Dilation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class IllagerEntityModel<T extends IllagerEntity> extends SinglePartEntityModel<T> implements ModelWithArms, ModelWithHead {
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart hat;
	private final ModelPart arms;
	private final ModelPart leftLeg;
	private final ModelPart rightLeg;
	private final ModelPart rightArm;
	private final ModelPart leftArm;

	public IllagerEntityModel(ModelPart root) {
		this.root = root;
		this.head = root.getChild("head");
		this.hat = this.head.getChild("hat");
		this.hat.visible = false;
		this.arms = root.getChild("arms");
		this.leftLeg = root.getChild("left_leg");
		this.rightLeg = root.getChild("right_leg");
		this.leftArm = root.getChild("left_arm");
		this.rightArm = root.getChild("right_arm");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(
			"head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F), ModelTransform.pivot(0.0F, 0.0F, 0.0F)
		);
		modelPartData2.addChild("hat", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, -10.0F, -4.0F, 8.0F, 12.0F, 8.0F, new Dilation(0.45F)), ModelTransform.NONE);
		modelPartData2.addChild("nose", ModelPartBuilder.create().uv(24, 0).cuboid(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F), ModelTransform.pivot(0.0F, -2.0F, 0.0F));
		modelPartData.addChild(
			"body",
			ModelPartBuilder.create()
				.uv(16, 20)
				.cuboid(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F)
				.uv(0, 38)
				.cuboid(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, new Dilation(0.5F)),
			ModelTransform.pivot(0.0F, 0.0F, 0.0F)
		);
		ModelPartData modelPartData3 = modelPartData.addChild(
			"arms",
			ModelPartBuilder.create().uv(44, 22).cuboid(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F).uv(40, 38).cuboid(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F),
			ModelTransform.of(0.0F, 3.0F, -1.0F, -0.75F, 0.0F, 0.0F)
		);
		modelPartData3.addChild("left_shoulder", ModelPartBuilder.create().uv(44, 22).mirrored().cuboid(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F), ModelTransform.NONE);
		modelPartData.addChild(
			"right_leg", ModelPartBuilder.create().uv(0, 22).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), ModelTransform.pivot(-2.0F, 12.0F, 0.0F)
		);
		modelPartData.addChild(
			"left_leg", ModelPartBuilder.create().uv(0, 22).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), ModelTransform.pivot(2.0F, 12.0F, 0.0F)
		);
		modelPartData.addChild(
			"right_arm", ModelPartBuilder.create().uv(40, 46).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), ModelTransform.pivot(-5.0F, 2.0F, 0.0F)
		);
		modelPartData.addChild(
			"left_arm", ModelPartBuilder.create().uv(40, 46).mirrored().cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F), ModelTransform.pivot(5.0F, 2.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 64, 64);
	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}

	public void setAngles(T illagerEntity, float f, float g, float h, float i, float j) {
		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.head.pitch = j * (float) (Math.PI / 180.0);
		if (this.riding) {
			this.rightArm.pitch = (float) (-Math.PI / 5);
			this.rightArm.yaw = 0.0F;
			this.rightArm.roll = 0.0F;
			this.leftArm.pitch = (float) (-Math.PI / 5);
			this.leftArm.yaw = 0.0F;
			this.leftArm.roll = 0.0F;
			this.rightLeg.pitch = -1.4137167F;
			this.rightLeg.yaw = (float) (Math.PI / 10);
			this.rightLeg.roll = 0.07853982F;
			this.leftLeg.pitch = -1.4137167F;
			this.leftLeg.yaw = (float) (-Math.PI / 10);
			this.leftLeg.roll = -0.07853982F;
		} else {
			this.rightArm.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 2.0F * g * 0.5F;
			this.rightArm.yaw = 0.0F;
			this.rightArm.roll = 0.0F;
			this.leftArm.pitch = MathHelper.cos(f * 0.6662F) * 2.0F * g * 0.5F;
			this.leftArm.yaw = 0.0F;
			this.leftArm.roll = 0.0F;
			this.rightLeg.pitch = MathHelper.cos(f * 0.6662F) * 1.4F * g * 0.5F;
			this.rightLeg.yaw = 0.0F;
			this.rightLeg.roll = 0.0F;
			this.leftLeg.pitch = MathHelper.cos(f * 0.6662F + (float) Math.PI) * 1.4F * g * 0.5F;
			this.leftLeg.yaw = 0.0F;
			this.leftLeg.roll = 0.0F;
		}

		IllagerEntity.State state = illagerEntity.getState();
		if (state == IllagerEntity.State.ATTACKING) {
			if (illagerEntity.getMainHandStack().isEmpty()) {
				CrossbowPosing.meleeAttack(this.leftArm, this.rightArm, true, this.handSwingProgress, h);
			} else {
				CrossbowPosing.meleeAttack(this.rightArm, this.leftArm, illagerEntity, this.handSwingProgress, h);
			}
		} else if (state == IllagerEntity.State.SPELLCASTING) {
			this.rightArm.pivotZ = 0.0F;
			this.rightArm.pivotX = -5.0F;
			this.leftArm.pivotZ = 0.0F;
			this.leftArm.pivotX = 5.0F;
			this.rightArm.pitch = MathHelper.cos(h * 0.6662F) * 0.25F;
			this.leftArm.pitch = MathHelper.cos(h * 0.6662F) * 0.25F;
			this.rightArm.roll = (float) (Math.PI * 3.0 / 4.0);
			this.leftArm.roll = (float) (-Math.PI * 3.0 / 4.0);
			this.rightArm.yaw = 0.0F;
			this.leftArm.yaw = 0.0F;
		} else if (state == IllagerEntity.State.BOW_AND_ARROW) {
			this.rightArm.yaw = -0.1F + this.head.yaw;
			this.rightArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
			this.leftArm.pitch = -0.9424779F + this.head.pitch;
			this.leftArm.yaw = this.head.yaw - 0.4F;
			this.leftArm.roll = (float) (Math.PI / 2);
		} else if (state == IllagerEntity.State.CROSSBOW_HOLD) {
			CrossbowPosing.hold(this.rightArm, this.leftArm, this.head, true);
		} else if (state == IllagerEntity.State.CROSSBOW_CHARGE) {
			CrossbowPosing.charge(this.rightArm, this.leftArm, illagerEntity, true);
		} else if (state == IllagerEntity.State.CELEBRATING) {
			this.rightArm.pivotZ = 0.0F;
			this.rightArm.pivotX = -5.0F;
			this.rightArm.pitch = MathHelper.cos(h * 0.6662F) * 0.05F;
			this.rightArm.roll = 2.670354F;
			this.rightArm.yaw = 0.0F;
			this.leftArm.pivotZ = 0.0F;
			this.leftArm.pivotX = 5.0F;
			this.leftArm.pitch = MathHelper.cos(h * 0.6662F) * 0.05F;
			this.leftArm.roll = (float) (-Math.PI * 3.0 / 4.0);
			this.leftArm.yaw = 0.0F;
		}

		boolean bl = state == IllagerEntity.State.CROSSED;
		this.arms.visible = bl;
		this.leftArm.visible = !bl;
		this.rightArm.visible = !bl;
	}

	private ModelPart getAttackingArm(Arm arm) {
		return arm == Arm.LEFT ? this.leftArm : this.rightArm;
	}

	public ModelPart getHat() {
		return this.hat;
	}

	@Override
	public ModelPart getHead() {
		return this.head;
	}

	@Override
	public void setArmAngle(Arm arm, MatrixStack matrices) {
		this.getAttackingArm(arm).rotate(matrices);
	}
}
