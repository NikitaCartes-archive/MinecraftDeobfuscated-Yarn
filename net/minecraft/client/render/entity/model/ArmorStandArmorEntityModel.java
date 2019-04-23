/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.decoration.ArmorStandEntity;

@Environment(value=EnvType.CLIENT)
public class ArmorStandArmorEntityModel
extends BipedEntityModel<ArmorStandEntity> {
    public ArmorStandArmorEntityModel() {
        this(0.0f);
    }

    public ArmorStandArmorEntityModel(float f) {
        this(f, 64, 32);
    }

    protected ArmorStandArmorEntityModel(float f, int i, int j) {
        super(f, 0.0f, i, j);
    }

    public void method_17066(ArmorStandEntity armorStandEntity, float f, float g, float h, float i, float j, float k) {
        this.head.pitch = (float)Math.PI / 180 * armorStandEntity.getHeadRotation().getX();
        this.head.yaw = (float)Math.PI / 180 * armorStandEntity.getHeadRotation().getY();
        this.head.roll = (float)Math.PI / 180 * armorStandEntity.getHeadRotation().getZ();
        this.head.setRotationPoint(0.0f, 1.0f, 0.0f);
        this.body.pitch = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getX();
        this.body.yaw = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getY();
        this.body.roll = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getZ();
        this.leftArm.pitch = (float)Math.PI / 180 * armorStandEntity.getLeftArmRotation().getX();
        this.leftArm.yaw = (float)Math.PI / 180 * armorStandEntity.getLeftArmRotation().getY();
        this.leftArm.roll = (float)Math.PI / 180 * armorStandEntity.getLeftArmRotation().getZ();
        this.rightArm.pitch = (float)Math.PI / 180 * armorStandEntity.getRightArmRotation().getX();
        this.rightArm.yaw = (float)Math.PI / 180 * armorStandEntity.getRightArmRotation().getY();
        this.rightArm.roll = (float)Math.PI / 180 * armorStandEntity.getRightArmRotation().getZ();
        this.leftLeg.pitch = (float)Math.PI / 180 * armorStandEntity.getLeftLegRotation().getX();
        this.leftLeg.yaw = (float)Math.PI / 180 * armorStandEntity.getLeftLegRotation().getY();
        this.leftLeg.roll = (float)Math.PI / 180 * armorStandEntity.getLeftLegRotation().getZ();
        this.leftLeg.setRotationPoint(1.9f, 11.0f, 0.0f);
        this.rightLeg.pitch = (float)Math.PI / 180 * armorStandEntity.getRightLegRotation().getX();
        this.rightLeg.yaw = (float)Math.PI / 180 * armorStandEntity.getRightLegRotation().getY();
        this.rightLeg.roll = (float)Math.PI / 180 * armorStandEntity.getRightLegRotation().getZ();
        this.rightLeg.setRotationPoint(-1.9f, 11.0f, 0.0f);
        this.headwear.copyRotation(this.head);
    }
}

