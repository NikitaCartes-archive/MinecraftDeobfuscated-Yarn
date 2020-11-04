/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;

@Environment(value=EnvType.CLIENT)
public class EndermanEntityModel<T extends LivingEntity>
extends BipedEntityModel<T> {
    public boolean carryingBlock;
    public boolean angry;

    public EndermanEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static class_5607 method_31995() {
        float f = -14.0f;
        class_5609 lv = BipedEntityModel.method_32011(class_5605.field_27715, -14.0f);
        class_5610 lv2 = lv.method_32111();
        class_5603 lv3 = class_5603.method_32090(0.0f, -13.0f, 0.0f);
        lv2.method_32117("hat", class_5606.method_32108().method_32101(0, 16).method_32098(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f, new class_5605(-0.5f)), lv3);
        lv2.method_32117("head", class_5606.method_32108().method_32101(0, 0).method_32097(-4.0f, -8.0f, -4.0f, 8.0f, 8.0f, 8.0f), lv3);
        lv2.method_32117("body", class_5606.method_32108().method_32101(32, 16).method_32097(-4.0f, 0.0f, -2.0f, 8.0f, 12.0f, 4.0f), class_5603.method_32090(0.0f, -14.0f, 0.0f));
        lv2.method_32117("right_arm", class_5606.method_32108().method_32101(56, 0).method_32097(-1.0f, -2.0f, -1.0f, 2.0f, 30.0f, 2.0f), class_5603.method_32090(-5.0f, -12.0f, 0.0f));
        lv2.method_32117("left_arm", class_5606.method_32108().method_32101(56, 0).method_32096().method_32097(-1.0f, -2.0f, -1.0f, 2.0f, 30.0f, 2.0f), class_5603.method_32090(5.0f, -12.0f, 0.0f));
        lv2.method_32117("right_leg", class_5606.method_32108().method_32101(56, 0).method_32097(-1.0f, 0.0f, -1.0f, 2.0f, 30.0f, 2.0f), class_5603.method_32090(-2.0f, -5.0f, 0.0f));
        lv2.method_32117("left_leg", class_5606.method_32108().method_32101(56, 0).method_32096().method_32097(-1.0f, 0.0f, -1.0f, 2.0f, 30.0f, 2.0f), class_5603.method_32090(2.0f, -5.0f, 0.0f));
        return class_5607.method_32110(lv, 64, 32);
    }

    @Override
    public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
        super.setAngles(livingEntity, f, g, h, i, j);
        this.head.visible = true;
        int k = -14;
        this.torso.pitch = 0.0f;
        this.torso.pivotY = -14.0f;
        this.torso.pivotZ = -0.0f;
        this.rightLeg.pitch -= 0.0f;
        this.leftLeg.pitch -= 0.0f;
        this.rightArm.pitch = (float)((double)this.rightArm.pitch * 0.5);
        this.field_27433.pitch = (float)((double)this.field_27433.pitch * 0.5);
        this.rightLeg.pitch = (float)((double)this.rightLeg.pitch * 0.5);
        this.leftLeg.pitch = (float)((double)this.leftLeg.pitch * 0.5);
        float l = 0.4f;
        if (this.rightArm.pitch > 0.4f) {
            this.rightArm.pitch = 0.4f;
        }
        if (this.field_27433.pitch > 0.4f) {
            this.field_27433.pitch = 0.4f;
        }
        if (this.rightArm.pitch < -0.4f) {
            this.rightArm.pitch = -0.4f;
        }
        if (this.field_27433.pitch < -0.4f) {
            this.field_27433.pitch = -0.4f;
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
            this.field_27433.pitch = -0.5f;
            this.rightArm.roll = 0.05f;
            this.field_27433.roll = -0.05f;
        }
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
            float m = 1.0f;
            this.head.pivotY -= 5.0f;
        }
        int n = -14;
        this.rightArm.setPivot(-5.0f, -12.0f, 0.0f);
        this.field_27433.setPivot(5.0f, -12.0f, 0.0f);
    }
}

