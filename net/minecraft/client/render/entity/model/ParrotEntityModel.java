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
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ParrotEntityModel
extends SinglePartEntityModel<ParrotEntity> {
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
        this.torso = modelPart.getChild("body");
        this.tail = modelPart.getChild("tail");
        this.field_27459 = modelPart.getChild("left_wing");
        this.field_27460 = modelPart.getChild("right_wing");
        this.head = modelPart.getChild("head");
        this.headFeathers = this.head.getChild("feather");
        this.field_27461 = modelPart.getChild("left_leg");
        this.field_27462 = modelPart.getChild("right_leg");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("body", ModelPartBuilder.create().uv(2, 8).cuboid(-1.5f, 0.0f, -1.5f, 3.0f, 6.0f, 3.0f), ModelTransform.pivot(0.0f, 16.5f, -3.0f));
        modelPartData.addChild("tail", ModelPartBuilder.create().uv(22, 1).cuboid(-1.5f, -1.0f, -1.0f, 3.0f, 4.0f, 1.0f), ModelTransform.pivot(0.0f, 21.07f, 1.16f));
        modelPartData.addChild("left_wing", ModelPartBuilder.create().uv(19, 8).cuboid(-0.5f, 0.0f, -1.5f, 1.0f, 5.0f, 3.0f), ModelTransform.pivot(1.5f, 16.94f, -2.76f));
        modelPartData.addChild("right_wing", ModelPartBuilder.create().uv(19, 8).cuboid(-0.5f, 0.0f, -1.5f, 1.0f, 5.0f, 3.0f), ModelTransform.pivot(-1.5f, 16.94f, -2.76f));
        ModelPartData modelPartData2 = modelPartData.addChild("head", ModelPartBuilder.create().uv(2, 2).cuboid(-1.0f, -1.5f, -1.0f, 2.0f, 3.0f, 2.0f), ModelTransform.pivot(0.0f, 15.69f, -2.76f));
        modelPartData2.addChild("head2", ModelPartBuilder.create().uv(10, 0).cuboid(-1.0f, -0.5f, -2.0f, 2.0f, 1.0f, 4.0f), ModelTransform.pivot(0.0f, -2.0f, -1.0f));
        modelPartData2.addChild("beak1", ModelPartBuilder.create().uv(11, 7).cuboid(-0.5f, -1.0f, -0.5f, 1.0f, 2.0f, 1.0f), ModelTransform.pivot(0.0f, -0.5f, -1.5f));
        modelPartData2.addChild("beak2", ModelPartBuilder.create().uv(16, 7).cuboid(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f), ModelTransform.pivot(0.0f, -1.75f, -2.45f));
        modelPartData2.addChild("feather", ModelPartBuilder.create().uv(2, 18).cuboid(0.0f, -4.0f, -2.0f, 0.0f, 5.0f, 4.0f), ModelTransform.pivot(0.0f, -2.15f, 0.15f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(14, 18).cuboid(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f);
        modelPartData.addChild("left_leg", modelPartBuilder, ModelTransform.pivot(1.0f, 22.0f, -1.05f));
        modelPartData.addChild("right_leg", modelPartBuilder, ModelTransform.pivot(-1.0f, 22.0f, -1.05f));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public ModelPart getPart() {
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

