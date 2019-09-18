/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Arm;

@Environment(value=EnvType.CLIENT)
public class ArmorStandEntityModel
extends ArmorStandArmorEntityModel {
    private final ModelPart bodyStickRight;
    private final ModelPart bodyStickLeft;
    private final ModelPart hip;
    private final ModelPart plate;

    public ArmorStandEntityModel() {
        this(0.0f);
    }

    public ArmorStandEntityModel(float f) {
        super(f, 64, 64);
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-1.0f, -7.0f, -1.0f, 2.0f, 7.0f, 2.0f, f);
        this.head.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.body = new ModelPart(this, 0, 26);
        this.body.addCuboid(-6.0f, 0.0f, -1.5f, 12.0f, 3.0f, 3.0f, f);
        this.body.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.rightArm = new ModelPart(this, 24, 0);
        this.rightArm.addCuboid(-2.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f, f);
        this.rightArm.setRotationPoint(-5.0f, 2.0f, 0.0f);
        this.leftArm = new ModelPart(this, 32, 16);
        this.leftArm.mirror = true;
        this.leftArm.addCuboid(0.0f, -2.0f, -1.0f, 2.0f, 12.0f, 2.0f, f);
        this.leftArm.setRotationPoint(5.0f, 2.0f, 0.0f);
        this.rightLeg = new ModelPart(this, 8, 0);
        this.rightLeg.addCuboid(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f, f);
        this.rightLeg.setRotationPoint(-1.9f, 12.0f, 0.0f);
        this.leftLeg = new ModelPart(this, 40, 16);
        this.leftLeg.mirror = true;
        this.leftLeg.addCuboid(-1.0f, 0.0f, -1.0f, 2.0f, 11.0f, 2.0f, f);
        this.leftLeg.setRotationPoint(1.9f, 12.0f, 0.0f);
        this.bodyStickRight = new ModelPart(this, 16, 0);
        this.bodyStickRight.addCuboid(-3.0f, 3.0f, -1.0f, 2.0f, 7.0f, 2.0f, f);
        this.bodyStickRight.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.bodyStickRight.visible = true;
        this.bodyStickLeft = new ModelPart(this, 48, 16);
        this.bodyStickLeft.addCuboid(1.0f, 3.0f, -1.0f, 2.0f, 7.0f, 2.0f, f);
        this.bodyStickLeft.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.hip = new ModelPart(this, 0, 48);
        this.hip.addCuboid(-4.0f, 10.0f, -1.0f, 8.0f, 2.0f, 2.0f, f);
        this.hip.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.plate = new ModelPart(this, 0, 32);
        this.plate.addCuboid(-6.0f, 11.0f, -6.0f, 12.0f, 1.0f, 12.0f, f);
        this.plate.setRotationPoint(0.0f, 12.0f, 0.0f);
        this.headwear.visible = false;
    }

    @Override
    public void method_17066(ArmorStandEntity armorStandEntity, float f, float g, float h, float i, float j, float k) {
        super.method_17066(armorStandEntity, f, g, h, i, j, k);
        this.leftArm.visible = armorStandEntity.shouldShowArms();
        this.rightArm.visible = armorStandEntity.shouldShowArms();
        this.plate.visible = !armorStandEntity.shouldHideBasePlate();
        this.leftLeg.setRotationPoint(1.9f, 12.0f, 0.0f);
        this.rightLeg.setRotationPoint(-1.9f, 12.0f, 0.0f);
        this.bodyStickRight.pitch = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getPitch();
        this.bodyStickRight.yaw = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getYaw();
        this.bodyStickRight.roll = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getRoll();
        this.bodyStickLeft.pitch = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getPitch();
        this.bodyStickLeft.yaw = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getYaw();
        this.bodyStickLeft.roll = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getRoll();
        this.hip.pitch = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getPitch();
        this.hip.yaw = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getYaw();
        this.hip.roll = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getRoll();
        this.plate.pitch = 0.0f;
        this.plate.yaw = (float)Math.PI / 180 * -armorStandEntity.yaw;
        this.plate.roll = 0.0f;
    }

    public void method_17067(ArmorStandEntity armorStandEntity, float f, float g, float h, float i, float j, float k) {
        super.method_17088(armorStandEntity, f, g, h, i, j, k);
        RenderSystem.pushMatrix();
        if (this.isChild) {
            float l = 2.0f;
            RenderSystem.scalef(0.5f, 0.5f, 0.5f);
            RenderSystem.translatef(0.0f, 24.0f * k, 0.0f);
            this.bodyStickRight.render(k);
            this.bodyStickLeft.render(k);
            this.hip.render(k);
            this.plate.render(k);
        } else {
            if (armorStandEntity.isInSneakingPose()) {
                RenderSystem.translatef(0.0f, 0.2f, 0.0f);
            }
            this.bodyStickRight.render(k);
            this.bodyStickLeft.render(k);
            this.hip.render(k);
            this.plate.render(k);
        }
        RenderSystem.popMatrix();
    }

    @Override
    public void setArmAngle(float f, Arm arm) {
        ModelPart modelPart = this.getArm(arm);
        boolean bl = modelPart.visible;
        modelPart.visible = true;
        super.setArmAngle(f, arm);
        modelPart.visible = bl;
    }
}

