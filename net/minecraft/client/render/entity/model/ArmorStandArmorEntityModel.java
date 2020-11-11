/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.Dilation;
import net.minecraft.entity.decoration.ArmorStandEntity;

@Environment(value=EnvType.CLIENT)
public class ArmorStandArmorEntityModel
extends BipedEntityModel<ArmorStandEntity> {
    public ArmorStandArmorEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getTexturedModelData(Dilation dilation) {
        ModelData modelData = BipedEntityModel.getModelData(dilation, 0.0f);
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, dilation), ModelTransform.pivot(0.0f, 1.0f, 0.0f));
        modelPartData.addChild("hat", ModelPartBuilder.create().uv(32, 0).cuboid(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, dilation.add(0.5f)), ModelTransform.pivot(0.0f, 1.0f, 0.0f));
        modelPartData.addChild("right_leg", ModelPartBuilder.create().uv(0, 16).cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation), ModelTransform.pivot(-1.9f, 11.0f, 0.0f));
        modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(0, 16).mirrored().cuboid(-2.0f, 0.0f, -2.0f, 4.0f, 12.0f, 4.0f, dilation), ModelTransform.pivot(1.9f, 11.0f, 0.0f));
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(ArmorStandEntity armorStandEntity, float f, float g, float h, float i, float j) {
        this.head.pitch = (float)Math.PI / 180 * armorStandEntity.getHeadRotation().getPitch();
        this.head.yaw = (float)Math.PI / 180 * armorStandEntity.getHeadRotation().getYaw();
        this.head.roll = (float)Math.PI / 180 * armorStandEntity.getHeadRotation().getRoll();
        this.torso.pitch = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getPitch();
        this.torso.yaw = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getYaw();
        this.torso.roll = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getRoll();
        this.leftArm.pitch = (float)Math.PI / 180 * armorStandEntity.getLeftArmRotation().getPitch();
        this.leftArm.yaw = (float)Math.PI / 180 * armorStandEntity.getLeftArmRotation().getYaw();
        this.leftArm.roll = (float)Math.PI / 180 * armorStandEntity.getLeftArmRotation().getRoll();
        this.rightArm.pitch = (float)Math.PI / 180 * armorStandEntity.getRightArmRotation().getPitch();
        this.rightArm.yaw = (float)Math.PI / 180 * armorStandEntity.getRightArmRotation().getYaw();
        this.rightArm.roll = (float)Math.PI / 180 * armorStandEntity.getRightArmRotation().getRoll();
        this.leftLeg.pitch = (float)Math.PI / 180 * armorStandEntity.getLeftLegRotation().getPitch();
        this.leftLeg.yaw = (float)Math.PI / 180 * armorStandEntity.getLeftLegRotation().getYaw();
        this.leftLeg.roll = (float)Math.PI / 180 * armorStandEntity.getLeftLegRotation().getRoll();
        this.rightLeg.pitch = (float)Math.PI / 180 * armorStandEntity.getRightLegRotation().getPitch();
        this.rightLeg.yaw = (float)Math.PI / 180 * armorStandEntity.getRightLegRotation().getYaw();
        this.rightLeg.roll = (float)Math.PI / 180 * armorStandEntity.getRightLegRotation().getRoll();
        this.helmet.copyTransform(this.head);
    }
}

