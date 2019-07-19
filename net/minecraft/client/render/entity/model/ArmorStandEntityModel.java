/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.ArmorStandArmorEntityModel;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.Arm;

@Environment(value=EnvType.CLIENT)
public class ArmorStandEntityModel
extends ArmorStandArmorEntityModel {
    private final ModelPart field_3314;
    private final ModelPart field_3315;
    private final ModelPart field_3313;
    private final ModelPart field_3312;

    public ArmorStandEntityModel() {
        this(0.0f);
    }

    public ArmorStandEntityModel(float f) {
        super(f, 64, 64);
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-1.0f, -7.0f, -1.0f, 2, 7, 2, f);
        this.head.setPivot(0.0f, 0.0f, 0.0f);
        this.torso = new ModelPart(this, 0, 26);
        this.torso.addCuboid(-6.0f, 0.0f, -1.5f, 12, 3, 3, f);
        this.torso.setPivot(0.0f, 0.0f, 0.0f);
        this.rightArm = new ModelPart(this, 24, 0);
        this.rightArm.addCuboid(-2.0f, -2.0f, -1.0f, 2, 12, 2, f);
        this.rightArm.setPivot(-5.0f, 2.0f, 0.0f);
        this.leftArm = new ModelPart(this, 32, 16);
        this.leftArm.mirror = true;
        this.leftArm.addCuboid(0.0f, -2.0f, -1.0f, 2, 12, 2, f);
        this.leftArm.setPivot(5.0f, 2.0f, 0.0f);
        this.rightLeg = new ModelPart(this, 8, 0);
        this.rightLeg.addCuboid(-1.0f, 0.0f, -1.0f, 2, 11, 2, f);
        this.rightLeg.setPivot(-1.9f, 12.0f, 0.0f);
        this.leftLeg = new ModelPart(this, 40, 16);
        this.leftLeg.mirror = true;
        this.leftLeg.addCuboid(-1.0f, 0.0f, -1.0f, 2, 11, 2, f);
        this.leftLeg.setPivot(1.9f, 12.0f, 0.0f);
        this.field_3314 = new ModelPart(this, 16, 0);
        this.field_3314.addCuboid(-3.0f, 3.0f, -1.0f, 2, 7, 2, f);
        this.field_3314.setPivot(0.0f, 0.0f, 0.0f);
        this.field_3314.visible = true;
        this.field_3315 = new ModelPart(this, 48, 16);
        this.field_3315.addCuboid(1.0f, 3.0f, -1.0f, 2, 7, 2, f);
        this.field_3315.setPivot(0.0f, 0.0f, 0.0f);
        this.field_3313 = new ModelPart(this, 0, 48);
        this.field_3313.addCuboid(-4.0f, 10.0f, -1.0f, 8, 2, 2, f);
        this.field_3313.setPivot(0.0f, 0.0f, 0.0f);
        this.field_3312 = new ModelPart(this, 0, 32);
        this.field_3312.addCuboid(-6.0f, 11.0f, -6.0f, 12, 1, 12, f);
        this.field_3312.setPivot(0.0f, 12.0f, 0.0f);
        this.helmet.visible = false;
    }

    @Override
    public void setAngles(ArmorStandEntity armorStandEntity, float f, float g, float h, float i, float j, float k) {
        super.setAngles(armorStandEntity, f, g, h, i, j, k);
        this.leftArm.visible = armorStandEntity.shouldShowArms();
        this.rightArm.visible = armorStandEntity.shouldShowArms();
        this.field_3312.visible = !armorStandEntity.shouldHideBasePlate();
        this.leftLeg.setPivot(1.9f, 12.0f, 0.0f);
        this.rightLeg.setPivot(-1.9f, 12.0f, 0.0f);
        this.field_3314.pitch = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getPitch();
        this.field_3314.yaw = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getYaw();
        this.field_3314.roll = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getRoll();
        this.field_3315.pitch = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getPitch();
        this.field_3315.yaw = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getYaw();
        this.field_3315.roll = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getRoll();
        this.field_3313.pitch = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getPitch();
        this.field_3313.yaw = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getYaw();
        this.field_3313.roll = (float)Math.PI / 180 * armorStandEntity.getBodyRotation().getRoll();
        this.field_3312.pitch = 0.0f;
        this.field_3312.yaw = (float)Math.PI / 180 * -armorStandEntity.yaw;
        this.field_3312.roll = 0.0f;
    }

    @Override
    public void render(ArmorStandEntity armorStandEntity, float f, float g, float h, float i, float j, float k) {
        super.render(armorStandEntity, f, g, h, i, j, k);
        GlStateManager.pushMatrix();
        if (this.child) {
            float l = 2.0f;
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 24.0f * k, 0.0f);
            this.field_3314.render(k);
            this.field_3315.render(k);
            this.field_3313.render(k);
            this.field_3312.render(k);
        } else {
            if (armorStandEntity.isSneaking()) {
                GlStateManager.translatef(0.0f, 0.2f, 0.0f);
            }
            this.field_3314.render(k);
            this.field_3315.render(k);
            this.field_3313.render(k);
            this.field_3312.render(k);
        }
        GlStateManager.popMatrix();
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

