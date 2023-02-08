package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.entity.decoration.ArmorStandEntity;

/**
 * Represents the armor model of an {@linkplain ArmorStandEntity}.
 */
@Environment(EnvType.CLIENT)
public class ArmorStandArmorEntityModel extends BipedEntityModel<ArmorStandEntity> {
	public ArmorStandArmorEntityModel(ModelPart modelPart) {
		super(modelPart);
	}

	public static TexturedModelData getTexturedModelData(Dilation dilation) {
		ModelData modelData = BipedEntityModel.getModelData(dilation, 0.0F);
		ModelPartData modelPartData = modelData.getRoot();
		modelPartData.addChild(
			EntityModelPartNames.HEAD,
			ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, dilation),
			ModelTransform.pivot(0.0F, 1.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.HAT,
			ModelPartBuilder.create().uv(32, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, dilation.add(0.5F)),
			ModelTransform.pivot(0.0F, 1.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.RIGHT_LEG,
			ModelPartBuilder.create().uv(0, 16).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation.add(-0.1F)),
			ModelTransform.pivot(-1.9F, 11.0F, 0.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.LEFT_LEG,
			ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation.add(-0.1F)),
			ModelTransform.pivot(1.9F, 11.0F, 0.0F)
		);
		return TexturedModelData.of(modelData, 64, 32);
	}

	public void setAngles(ArmorStandEntity armorStandEntity, float f, float g, float h, float i, float j) {
		this.head.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getHeadRotation().getPitch();
		this.head.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getHeadRotation().getYaw();
		this.head.roll = (float) (Math.PI / 180.0) * armorStandEntity.getHeadRotation().getRoll();
		this.body.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getPitch();
		this.body.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getYaw();
		this.body.roll = (float) (Math.PI / 180.0) * armorStandEntity.getBodyRotation().getRoll();
		this.leftArm.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getLeftArmRotation().getPitch();
		this.leftArm.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getLeftArmRotation().getYaw();
		this.leftArm.roll = (float) (Math.PI / 180.0) * armorStandEntity.getLeftArmRotation().getRoll();
		this.rightArm.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getRightArmRotation().getPitch();
		this.rightArm.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getRightArmRotation().getYaw();
		this.rightArm.roll = (float) (Math.PI / 180.0) * armorStandEntity.getRightArmRotation().getRoll();
		this.leftLeg.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getLeftLegRotation().getPitch();
		this.leftLeg.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getLeftLegRotation().getYaw();
		this.leftLeg.roll = (float) (Math.PI / 180.0) * armorStandEntity.getLeftLegRotation().getRoll();
		this.rightLeg.pitch = (float) (Math.PI / 180.0) * armorStandEntity.getRightLegRotation().getPitch();
		this.rightLeg.yaw = (float) (Math.PI / 180.0) * armorStandEntity.getRightLegRotation().getYaw();
		this.rightLeg.roll = (float) (Math.PI / 180.0) * armorStandEntity.getRightLegRotation().getRoll();
		this.hat.copyTransform(this.head);
	}
}
