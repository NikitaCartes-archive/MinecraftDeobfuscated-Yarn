package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ArmorStandEntityModel extends ArmorStandArmorEntityModel {
	private final ModelPart rightTorso;
	private final ModelPart leftTorso;
	private final ModelPart hip;
	private final ModelPart plate;

	public ArmorStandEntityModel() {
		this(0.0F);
	}

	public ArmorStandEntityModel(float f) {
		super(f, 64, 64);
		this.head = new ModelPart(this, 0, 0);
		this.head.addCuboid(-1.0F, -7.0F, -1.0F, 2.0F, 7.0F, 2.0F, f);
		this.head.setPivot(0.0F, 0.0F, 0.0F);
		this.torso = new ModelPart(this, 0, 26);
		this.torso.addCuboid(-6.0F, 0.0F, -1.5F, 12.0F, 3.0F, 3.0F, f);
		this.torso.setPivot(0.0F, 0.0F, 0.0F);
		this.rightArm = new ModelPart(this, 24, 0);
		this.rightArm.addCuboid(-2.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, f);
		this.rightArm.setPivot(-5.0F, 2.0F, 0.0F);
		this.leftArm = new ModelPart(this, 32, 16);
		this.leftArm.mirror = true;
		this.leftArm.addCuboid(0.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F, f);
		this.leftArm.setPivot(5.0F, 2.0F, 0.0F);
		this.rightLeg = new ModelPart(this, 8, 0);
		this.rightLeg.addCuboid(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F, f);
		this.rightLeg.setPivot(-1.9F, 12.0F, 0.0F);
		this.leftLeg = new ModelPart(this, 40, 16);
		this.leftLeg.mirror = true;
		this.leftLeg.addCuboid(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F, f);
		this.leftLeg.setPivot(1.9F, 12.0F, 0.0F);
		this.rightTorso = new ModelPart(this, 16, 0);
		this.rightTorso.addCuboid(-3.0F, 3.0F, -1.0F, 2.0F, 7.0F, 2.0F, f);
		this.rightTorso.setPivot(0.0F, 0.0F, 0.0F);
		this.rightTorso.visible = true;
		this.leftTorso = new ModelPart(this, 48, 16);
		this.leftTorso.addCuboid(1.0F, 3.0F, -1.0F, 2.0F, 7.0F, 2.0F, f);
		this.leftTorso.setPivot(0.0F, 0.0F, 0.0F);
		this.hip = new ModelPart(this, 0, 48);
		this.hip.addCuboid(-4.0F, 10.0F, -1.0F, 8.0F, 2.0F, 2.0F, f);
		this.hip.setPivot(0.0F, 0.0F, 0.0F);
		this.plate = new ModelPart(this, 0, 32);
		this.plate.addCuboid(-6.0F, 11.0F, -6.0F, 12.0F, 1.0F, 12.0F, f);
		this.plate.setPivot(0.0F, 12.0F, 0.0F);
		this.helmet.visible = false;
	}

	public void animateModel(ArmorStandEntity armorStandEntity, float f, float g, float h) {
		this.plate.pitch = 0.0F;
		this.plate.yaw = (float) (Math.PI / 180.0) * -MathHelper.lerpAngleDegrees(h, armorStandEntity.prevYaw, armorStandEntity.yaw);
		this.plate.roll = 0.0F;
	}

	@Override
	public void setAngles(ArmorStandEntity armorStandEntity, float f, float g, float h, float i, float j) {
		super.setAngles(armorStandEntity, f, g, h, i, j);
		this.leftArm.visible = armorStandEntity.shouldShowArms();
		this.rightArm.visible = armorStandEntity.shouldShowArms();
		this.plate.visible = !armorStandEntity.shouldHideBasePlate();
		this.leftLeg.setPivot(1.9F, 12.0F, 0.0F);
		this.rightLeg.setPivot(-1.9F, 12.0F, 0.0F);
		this.rightTorso.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getPitch();
		this.rightTorso.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getYaw();
		this.rightTorso.roll = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getRoll();
		this.leftTorso.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getPitch();
		this.leftTorso.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getYaw();
		this.leftTorso.roll = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getRoll();
		this.hip.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getPitch();
		this.hip.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getYaw();
		this.hip.roll = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getRoll();
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.rightTorso, this.leftTorso, this.hip, this.plate));
	}

	@Override
	public void setArmAngle(Arm arm, MatrixStack matrixStack) {
		ModelPart modelPart = this.getArm(arm);
		boolean bl = modelPart.visible;
		modelPart.visible = true;
		super.setArmAngle(arm, matrixStack);
		modelPart.visible = bl;
	}
}
