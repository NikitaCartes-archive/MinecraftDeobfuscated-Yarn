package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

/**
 * Represents the model of an {@linkplain ArmorStandEntity}.
 */
@Environment(EnvType.CLIENT)
public class ArmorStandEntityModel extends ArmorStandArmorEntityModel {
	private static final String RIGHT_BODY_STICK = "right_body_stick";
	private static final String LEFT_BODY_STICK = "left_body_stick";
	private static final String SHOULDER_STICK = "shoulder_stick";
	private static final String BASE_PLATE = "base_plate";
	private final ModelPart rightBodyStick;
	private final ModelPart leftBodyStick;
	private final ModelPart shoulderStick;
	private final ModelPart basePlate;

	public ArmorStandEntityModel(ModelPart modelPart) {
		super(modelPart);
		this.rightBodyStick = modelPart.getChild("right_body_stick");
		this.leftBodyStick = modelPart.getChild("left_body_stick");
		this.shoulderStick = modelPart.getChild("shoulder_stick");
		this.basePlate = modelPart.getChild("base_plate");
		this.hat.visible = false;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 0.0F);
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(0, 0).cuboid(-1.0F, -7.0F, -1.0F, 2.0F, 7.0F, 2.0F), ModelTransform.pivot(0.0F, 1.0F, 0.0F)
		);
		modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 26).cuboid(-6.0F, 0.0F, -1.5F, 12.0F, 3.0F, 3.0F), ModelTransform.NONE);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_ARM, ModelPartBuilder.create().uv(24, 0).cuboid(-2.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F), ModelTransform.pivot(-5.0F, 2.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_ARM,
			ModelPartBuilder.create().uv(32, 16).mirrored().cuboid(0.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F),
			ModelTransform.pivot(5.0F, 2.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(8, 0).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F), ModelTransform.pivot(-1.9F, 12.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_LEG,
			ModelPartBuilder.create().uv(40, 16).mirrored().cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 11.0F, 2.0F),
			ModelTransform.pivot(1.9F, 12.0F, 0.0F)
		);
		modelPartData.addChild("right_body_stick", ModelPartBuilder.create().uv(16, 0).cuboid(-3.0F, 3.0F, -1.0F, 2.0F, 7.0F, 2.0F), ModelTransform.NONE);
		modelPartData.addChild("left_body_stick", ModelPartBuilder.create().uv(48, 16).cuboid(1.0F, 3.0F, -1.0F, 2.0F, 7.0F, 2.0F), ModelTransform.NONE);
		modelPartData.addChild("shoulder_stick", ModelPartBuilder.create().uv(0, 48).cuboid(-4.0F, 10.0F, -1.0F, 8.0F, 2.0F, 2.0F), ModelTransform.NONE);
		modelPartData.addChild(
			"base_plate", ModelPartBuilder.create().uv(0, 32).cuboid(-6.0F, 11.0F, -6.0F, 12.0F, 1.0F, 12.0F), ModelTransform.pivot(0.0F, 12.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 64, 64);
	}

	public void animateModel(ArmorStandEntity armorStandEntity, float f, float g, float h) {
		this.basePlate.pitch = 0.0F;
		this.basePlate.yaw = (float) (Math.PI / 180.0) * -MathHelper.lerpAngleDegrees(h, armorStandEntity.prevYaw, armorStandEntity.getYaw());
		this.basePlate.roll = 0.0F;
	}

	@Override
	public void setAngles(ArmorStandEntity armorStandEntity, float f, float g, float h, float i, float j) {
		super.setAngles(armorStandEntity, f, g, h, i, j);
		this.leftArm.visible = armorStandEntity.shouldShowArms();
		this.rightArm.visible = armorStandEntity.shouldShowArms();
		this.basePlate.visible = !armorStandEntity.shouldHideBasePlate();
		this.rightBodyStick.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getPitch();
		this.rightBodyStick.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getYaw();
		this.rightBodyStick.roll = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getRoll();
		this.leftBodyStick.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getPitch();
		this.leftBodyStick.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getYaw();
		this.leftBodyStick.roll = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getRoll();
		this.shoulderStick.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getPitch();
		this.shoulderStick.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getYaw();
		this.shoulderStick.roll = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getRoll();
	}

	@Override
	protected Iterable<ModelPart> getBodyParts() {
		return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.rightBodyStick, this.leftBodyStick, this.shoulderStick, this.basePlate));
	}

	@Override
	public void setArmAngle(Arm arm, MatrixStack matrices) {
		ModelPart modelPart = this.getArm(arm);
		boolean bl = modelPart.visible;
		modelPart.visible = true;
		super.setArmAngle(arm, matrices);
		modelPart.visible = bl;
	}
}
