/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5597;
import net.minecraft.class_5603;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ParrotEntityModel
extends class_5597<ParrotEntity> {
    private final ModelPart field_27458;
    private final ModelPart torso;
    private final ModelPart tail;
    private final ModelPart field_27459;
    private final ModelPart field_27460;
    private final ModelPart head;
    private final ModelPart headFeathers;
    private final ModelPart field_27461;
    private final ModelPart field_27462;

    public ParrotEntityModel(ModelPart modelPart) {
        this.field_27458 = modelPart;
        this.torso = modelPart.method_32086("body");
        this.tail = modelPart.method_32086("tail");
        this.field_27459 = modelPart.method_32086("left_wing");
        this.field_27460 = modelPart.method_32086("right_wing");
        this.head = modelPart.method_32086("head");
        this.headFeathers = this.head.method_32086("feather");
        this.field_27461 = modelPart.method_32086("left_leg");
        this.field_27462 = modelPart.method_32086("right_leg");
    }

    public static class_5607 method_32023() {
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        lv2.method_32117("body", class_5606.method_32108().method_32101(2, 8).method_32097(-1.5f, 0.0f, -1.5f, 3.0f, 6.0f, 3.0f), class_5603.method_32090(0.0f, 16.5f, -3.0f));
        lv2.method_32117("tail", class_5606.method_32108().method_32101(22, 1).method_32097(-1.5f, -1.0f, -1.0f, 3.0f, 4.0f, 1.0f), class_5603.method_32090(0.0f, 21.07f, 1.16f));
        lv2.method_32117("left_wing", class_5606.method_32108().method_32101(19, 8).method_32097(-0.5f, 0.0f, -1.5f, 1.0f, 5.0f, 3.0f), class_5603.method_32090(1.5f, 16.94f, -2.76f));
        lv2.method_32117("right_wing", class_5606.method_32108().method_32101(19, 8).method_32097(-0.5f, 0.0f, -1.5f, 1.0f, 5.0f, 3.0f), class_5603.method_32090(-1.5f, 16.94f, -2.76f));
        class_5610 lv3 = lv2.method_32117("head", class_5606.method_32108().method_32101(2, 2).method_32097(-1.0f, -1.5f, -1.0f, 2.0f, 3.0f, 2.0f), class_5603.method_32090(0.0f, 15.69f, -2.76f));
        lv3.method_32117("head2", class_5606.method_32108().method_32101(10, 0).method_32097(-1.0f, -0.5f, -2.0f, 2.0f, 1.0f, 4.0f), class_5603.method_32090(0.0f, -2.0f, -1.0f));
        lv3.method_32117("beak1", class_5606.method_32108().method_32101(11, 7).method_32097(-0.5f, -1.0f, -0.5f, 1.0f, 2.0f, 1.0f), class_5603.method_32090(0.0f, -0.5f, -1.5f));
        lv3.method_32117("beak2", class_5606.method_32108().method_32101(16, 7).method_32097(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f), class_5603.method_32090(0.0f, -1.75f, -2.45f));
        lv3.method_32117("feather", class_5606.method_32108().method_32101(2, 18).method_32097(0.0f, -4.0f, -2.0f, 0.0f, 5.0f, 4.0f), class_5603.method_32090(0.0f, -2.15f, 0.15f));
        class_5606 lv4 = class_5606.method_32108().method_32101(14, 18).method_32097(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f);
        lv2.method_32117("left_leg", lv4, class_5603.method_32090(1.0f, 22.0f, -1.05f));
        lv2.method_32117("right_leg", lv4, class_5603.method_32090(-1.0f, 22.0f, -1.05f));
        return class_5607.method_32110(lv, 32, 32);
    }

    @Override
    public ModelPart method_32008() {
        return this.field_27458;
    }

    @Override
    public void setAngles(ParrotEntity parrotEntity, float f, float g, float h, float i, float j) {
        this.setAngles(ParrotEntityModel.getPose(parrotEntity), parrotEntity.age, f, g, h, i, j);
    }

    @Override
    public void animateModel(ParrotEntity parrotEntity, float f, float g, float h) {
        this.animateModel(ParrotEntityModel.getPose(parrotEntity));
    }

    public void poseOnShoulder(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float limbAngle, float limbDistance, float headYaw, float headPitch, int danceAngle) {
        this.animateModel(Pose.ON_SHOULDER);
        this.setAngles(Pose.ON_SHOULDER, danceAngle, limbAngle, limbDistance, 0.0f, headYaw, headPitch);
        this.field_27458.render(matrices, vertexConsumer, light, overlay);
    }

    private void setAngles(Pose pose, int danceAngle, float limbAngle, float limbDistance, float age, float headYaw, float headPitch) {
        this.head.pitch = headPitch * ((float)Math.PI / 180);
        this.head.yaw = headYaw * ((float)Math.PI / 180);
        this.head.roll = 0.0f;
        this.head.pivotX = 0.0f;
        this.torso.pivotX = 0.0f;
        this.tail.pivotX = 0.0f;
        this.field_27460.pivotX = -1.5f;
        this.field_27459.pivotX = 1.5f;
        switch (pose) {
            case SITTING: {
                break;
            }
            case PARTY: {
                float f = MathHelper.cos(danceAngle);
                float g = MathHelper.sin(danceAngle);
                this.head.pivotX = f;
                this.head.pivotY = 15.69f + g;
                this.head.pitch = 0.0f;
                this.head.yaw = 0.0f;
                this.head.roll = MathHelper.sin(danceAngle) * 0.4f;
                this.torso.pivotX = f;
                this.torso.pivotY = 16.5f + g;
                this.field_27459.roll = -0.0873f - age;
                this.field_27459.pivotX = 1.5f + f;
                this.field_27459.pivotY = 16.94f + g;
                this.field_27460.roll = 0.0873f + age;
                this.field_27460.pivotX = -1.5f + f;
                this.field_27460.pivotY = 16.94f + g;
                this.tail.pivotX = f;
                this.tail.pivotY = 21.07f + g;
                break;
            }
            case STANDING: {
                this.field_27461.pitch += MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
                this.field_27462.pitch += MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.4f * limbDistance;
            }
            default: {
                float h = age * 0.3f;
                this.head.pivotY = 15.69f + h;
                this.tail.pitch = 1.015f + MathHelper.cos(limbAngle * 0.6662f) * 0.3f * limbDistance;
                this.tail.pivotY = 21.07f + h;
                this.torso.pivotY = 16.5f + h;
                this.field_27459.roll = -0.0873f - age;
                this.field_27459.pivotY = 16.94f + h;
                this.field_27460.roll = 0.0873f + age;
                this.field_27460.pivotY = 16.94f + h;
                this.field_27461.pivotY = 22.0f + h;
                this.field_27462.pivotY = 22.0f + h;
            }
        }
    }

    private void animateModel(Pose pose) {
        this.headFeathers.pitch = -0.2214f;
        this.torso.pitch = 0.4937f;
        this.field_27459.pitch = -0.6981f;
        this.field_27459.yaw = (float)(-Math.PI);
        this.field_27460.pitch = -0.6981f;
        this.field_27460.yaw = (float)(-Math.PI);
        this.field_27461.pitch = -0.0299f;
        this.field_27462.pitch = -0.0299f;
        this.field_27461.pivotY = 22.0f;
        this.field_27462.pivotY = 22.0f;
        this.field_27461.roll = 0.0f;
        this.field_27462.roll = 0.0f;
        switch (pose) {
            case FLYING: {
                this.field_27461.pitch += 0.6981317f;
                this.field_27462.pitch += 0.6981317f;
                break;
            }
            case SITTING: {
                float f = 1.9f;
                this.head.pivotY = 17.59f;
                this.tail.pitch = 1.5388988f;
                this.tail.pivotY = 22.97f;
                this.torso.pivotY = 18.4f;
                this.field_27459.roll = -0.0873f;
                this.field_27459.pivotY = 18.84f;
                this.field_27460.roll = 0.0873f;
                this.field_27460.pivotY = 18.84f;
                this.field_27461.pivotY += 1.9f;
                this.field_27462.pivotY += 1.9f;
                this.field_27461.pitch += 1.5707964f;
                this.field_27462.pitch += 1.5707964f;
                break;
            }
            case PARTY: {
                this.field_27461.roll = -0.34906584f;
                this.field_27462.roll = 0.34906584f;
                break;
            }
        }
    }

    private static Pose getPose(ParrotEntity parrot) {
        if (parrot.getSongPlaying()) {
            return Pose.PARTY;
        }
        if (parrot.isInSittingPose()) {
            return Pose.SITTING;
        }
        if (parrot.isInAir()) {
            return Pose.FLYING;
        }
        return Pose.STANDING;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Pose {
        FLYING,
        STANDING,
        SITTING,
        PARTY,
        ON_SHOULDER;

    }
}

