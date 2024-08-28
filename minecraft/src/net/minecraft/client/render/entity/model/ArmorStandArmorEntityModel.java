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
import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;

/**
 * Represents the armor model of an {@linkplain ArmorStandEntity}.
 */
@Environment(EnvType.CLIENT)
public class ArmorStandArmorEntityModel extends BipedEntityModel<ArmorStandEntityRenderState> {
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

	public void setAngles(ArmorStandEntityRenderState armorStandEntityRenderState) {
		super.setAngles(armorStandEntityRenderState);
		this.head.pitch = (float) (Math.PI / 180.0) * armorStandEntityRenderState.headRotation.getPitch();
		this.head.yaw = (float) (Math.PI / 180.0) * armorStandEntityRenderState.headRotation.getYaw();
		this.head.roll = (float) (Math.PI / 180.0) * armorStandEntityRenderState.headRotation.getRoll();
		this.body.pitch = (float) (Math.PI / 180.0) * armorStandEntityRenderState.bodyRotation.getPitch();
		this.body.yaw = (float) (Math.PI / 180.0) * armorStandEntityRenderState.bodyRotation.getYaw();
		this.body.roll = (float) (Math.PI / 180.0) * armorStandEntityRenderState.bodyRotation.getRoll();
		this.leftArm.pitch = (float) (Math.PI / 180.0) * armorStandEntityRenderState.leftArmRotation.getPitch();
		this.leftArm.yaw = (float) (Math.PI / 180.0) * armorStandEntityRenderState.leftArmRotation.getYaw();
		this.leftArm.roll = (float) (Math.PI / 180.0) * armorStandEntityRenderState.leftArmRotation.getRoll();
		this.rightArm.pitch = (float) (Math.PI / 180.0) * armorStandEntityRenderState.rightArmRotation.getPitch();
		this.rightArm.yaw = (float) (Math.PI / 180.0) * armorStandEntityRenderState.rightArmRotation.getYaw();
		this.rightArm.roll = (float) (Math.PI / 180.0) * armorStandEntityRenderState.rightArmRotation.getRoll();
		this.leftLeg.pitch = (float) (Math.PI / 180.0) * armorStandEntityRenderState.leftLegRotation.getPitch();
		this.leftLeg.yaw = (float) (Math.PI / 180.0) * armorStandEntityRenderState.leftLegRotation.getYaw();
		this.leftLeg.roll = (float) (Math.PI / 180.0) * armorStandEntityRenderState.leftLegRotation.getRoll();
		this.rightLeg.pitch = (float) (Math.PI / 180.0) * armorStandEntityRenderState.rightLegRotation.getPitch();
		this.rightLeg.yaw = (float) (Math.PI / 180.0) * armorStandEntityRenderState.rightLegRotation.getYaw();
		this.rightLeg.roll = (float) (Math.PI / 180.0) * armorStandEntityRenderState.rightLegRotation.getRoll();
	}
}
