/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;

@Environment(value=EnvType.CLIENT)
public class EndermanEntityModel<T extends LivingEntity>
extends BipedEntityModel<T> {
    public boolean carryingBlock;
    public boolean angry;

    public EndermanEntityModel(float f) {
        super(0.0f, -14.0f, 64, 32);
        float g = -14.0f;
        this.helmet = new ModelPart(this, 0, 16);
        this.helmet.addCuboid(-4.0f, -8.0f, -4.0f, 8, 8, 8, f - 0.5f);
        this.helmet.setPivot(0.0f, -14.0f, 0.0f);
        this.torso = new ModelPart(this, 32, 16);
        this.torso.addCuboid(-4.0f, 0.0f, -2.0f, 8, 12, 4, f);
        this.torso.setPivot(0.0f, -14.0f, 0.0f);
        this.rightArm = new ModelPart(this, 56, 0);
        this.rightArm.addCuboid(-1.0f, -2.0f, -1.0f, 2, 30, 2, f);
        this.rightArm.setPivot(-3.0f, -12.0f, 0.0f);
        this.leftArm = new ModelPart(this, 56, 0);
        this.leftArm.mirror = true;
        this.leftArm.addCuboid(-1.0f, -2.0f, -1.0f, 2, 30, 2, f);
        this.leftArm.setPivot(5.0f, -12.0f, 0.0f);
        this.rightLeg = new ModelPart(this, 56, 0);
        this.rightLeg.addCuboid(-1.0f, 0.0f, -1.0f, 2, 30, 2, f);
        this.rightLeg.setPivot(-2.0f, -2.0f, 0.0f);
        this.leftLeg = new ModelPart(this, 56, 0);
        this.leftLeg.mirror = true;
        this.leftLeg.addCuboid(-1.0f, 0.0f, -1.0f, 2, 30, 2, f);
        this.leftLeg.setPivot(2.0f, -2.0f, 0.0f);
    }

    @Override
    public void setAngles(T livingEntity, float f, float g, float h, float i, float j, float k) {
        super.setAngles(livingEntity, f, g, h, i, j, k);
        this.head.visible = true;
        float l = -14.0f;
        this.torso.pitch = 0.0f;
        this.torso.pivotY = -14.0f;
        this.torso.pivotZ = -0.0f;
        this.rightLeg.pitch -= 0.0f;
        this.leftLeg.pitch -= 0.0f;
        this.rightArm.pitch = (float)((double)this.rightArm.pitch * 0.5);
        this.leftArm.pitch = (float)((double)this.leftArm.pitch * 0.5);
        this.rightLeg.pitch = (float)((double)this.rightLeg.pitch * 0.5);
        this.leftLeg.pitch = (float)((double)this.leftLeg.pitch * 0.5);
        float m = 0.4f;
        if (this.rightArm.pitch > 0.4f) {
            this.rightArm.pitch = 0.4f;
        }
        if (this.leftArm.pitch > 0.4f) {
            this.leftArm.pitch = 0.4f;
        }
        if (this.rightArm.pitch < -0.4f) {
            this.rightArm.pitch = -0.4f;
        }
        if (this.leftArm.pitch < -0.4f) {
            this.leftArm.pitch = -0.4f;
        }
        if (this.rightLeg.pitch > 0.4f) {
            this.rightLeg.pitch = 0.4f;
        }
        if (this.leftLeg.pitch > 0.4f) {
            this.leftLeg.pitch = 0.4f;
        }
        if (this.rightLeg.pitch < -0.4f) {
            this.rightLeg.pitch = -0.4f;
        }
        if (this.leftLeg.pitch < -0.4f) {
            this.leftLeg.pitch = -0.4f;
        }
        if (this.carryingBlock) {
            this.rightArm.pitch = -0.5f;
            this.leftArm.pitch = -0.5f;
            this.rightArm.roll = 0.05f;
            this.leftArm.roll = -0.05f;
        }
        this.rightArm.pivotZ = 0.0f;
        this.leftArm.pivotZ = 0.0f;
        this.rightLeg.pivotZ = 0.0f;
        this.leftLeg.pivotZ = 0.0f;
        this.rightLeg.pivotY = -5.0f;
        this.leftLeg.pivotY = -5.0f;
        this.head.pivotZ = -0.0f;
        this.head.pivotY = -13.0f;
        this.helmet.pivotX = this.head.pivotX;
        this.helmet.pivotY = this.head.pivotY;
        this.helmet.pivotZ = this.head.pivotZ;
        this.helmet.pitch = this.head.pitch;
        this.helmet.yaw = this.head.yaw;
        this.helmet.roll = this.head.roll;
        if (this.angry) {
            float n = 1.0f;
            this.head.pivotY -= 5.0f;
        }
    }
}

