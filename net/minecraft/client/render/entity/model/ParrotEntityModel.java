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
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ParrotEntityModel
extends SinglePartEntityModel<ParrotEntity> {
    private static final String FEATHER = "feather";
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart leftWing;
    private final ModelPart rightWing;
    private final ModelPart head;
    private final ModelPart feather;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public ParrotEntityModel(ModelPart root) {
        this.root = root;
        this.body = root.getChild(EntityModelPartNames.BODY);
        this.tail = root.getChild(EntityModelPartNames.TAIL);
        this.leftWing = root.getChild(EntityModelPartNames.LEFT_WING);
        this.rightWing = root.getChild(EntityModelPartNames.RIGHT_WING);
        this.head = root.getChild(EntityModelPartNames.HEAD);
        this.feather = this.head.getChild(FEATHER);
        this.leftLeg = root.getChild(EntityModelPartNames.LEFT_LEG);
        this.rightLeg = root.getChild(EntityModelPartNames.RIGHT_LEG);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(2, 8).cuboid(-1.5f, 0.0f, -1.5f, 3.0f, 6.0f, 3.0f), ModelTransform.pivot(0.0f, 16.5f, -3.0f));
        modelPartData.addChild(EntityModelPartNames.TAIL, ModelPartBuilder.create().uv(22, 1).cuboid(-1.5f, -1.0f, -1.0f, 3.0f, 4.0f, 1.0f), ModelTransform.pivot(0.0f, 21.07f, 1.16f));
        modelPartData.addChild(EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().uv(19, 8).cuboid(-0.5f, 0.0f, -1.5f, 1.0f, 5.0f, 3.0f), ModelTransform.pivot(1.5f, 16.94f, -2.76f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().uv(19, 8).cuboid(-0.5f, 0.0f, -1.5f, 1.0f, 5.0f, 3.0f), ModelTransform.pivot(-1.5f, 16.94f, -2.76f));
        ModelPartData modelPartData2 = modelPartData.addChild(EntityModelPartNames.HEAD, ModelPartBuilder.create().uv(2, 2).cuboid(-1.0f, -1.5f, -1.0f, 2.0f, 3.0f, 2.0f), ModelTransform.pivot(0.0f, 15.69f, -2.76f));
        modelPartData2.addChild("head2", ModelPartBuilder.create().uv(10, 0).cuboid(-1.0f, -0.5f, -2.0f, 2.0f, 1.0f, 4.0f), ModelTransform.pivot(0.0f, -2.0f, -1.0f));
        modelPartData2.addChild("beak1", ModelPartBuilder.create().uv(11, 7).cuboid(-0.5f, -1.0f, -0.5f, 1.0f, 2.0f, 1.0f), ModelTransform.pivot(0.0f, -0.5f, -1.5f));
        modelPartData2.addChild("beak2", ModelPartBuilder.create().uv(16, 7).cuboid(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f), ModelTransform.pivot(0.0f, -1.75f, -2.45f));
        modelPartData2.addChild(FEATHER, ModelPartBuilder.create().uv(2, 18).cuboid(0.0f, -4.0f, -2.0f, 0.0f, 5.0f, 4.0f), ModelTransform.pivot(0.0f, -2.15f, 0.15f));
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(14, 18).cuboid(-0.5f, 0.0f, -0.5f, 1.0f, 2.0f, 1.0f);
        modelPartData.addChild(EntityModelPartNames.LEFT_LEG, modelPartBuilder, ModelTransform.pivot(1.0f, 22.0f, -1.05f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, modelPartBuilder, ModelTransform.pivot(-1.0f, 22.0f, -1.05f));
        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
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
        this.root.render(matrices, vertexConsumer, light, overlay);
    }

    private void setAngles(Pose pose, int danceAngle, float limbAngle, float limbDistance, float age, float headYaw, float headPitch) {
        this.head.pitch = headPitch * ((float)Math.PI / 180);
        this.head.yaw = headYaw * ((float)Math.PI / 180);
        this.head.roll = 0.0f;
        this.head.pivotX = 0.0f;
        this.body.pivotX = 0.0f;
        this.tail.pivotX = 0.0f;
        this.rightWing.pivotX = -1.5f;
        this.leftWing.pivotX = 1.5f;
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
                this.body.pivotX = f;
                this.body.pivotY = 16.5f + g;
                this.leftWing.roll = -0.0873f - age;
                this.leftWing.pivotX = 1.5f + f;
                this.leftWing.pivotY = 16.94f + g;
                this.rightWing.roll = 0.0873f + age;
                this.rightWing.pivotX = -1.5f + f;
                this.rightWing.pivotY = 16.94f + g;
                this.tail.pivotX = f;
                this.tail.pivotY = 21.07f + g;
                break;
            }
            case STANDING: {
                this.leftLeg.pitch += MathHelper.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
                this.rightLeg.pitch += MathHelper.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.4f * limbDistance;
            }
            default: {
                float h = age * 0.3f;
                this.head.pivotY = 15.69f + h;
                this.tail.pitch = 1.015f + MathHelper.cos(limbAngle * 0.6662f) * 0.3f * limbDistance;
                this.tail.pivotY = 21.07f + h;
                this.body.pivotY = 16.5f + h;
                this.leftWing.roll = -0.0873f - age;
                this.leftWing.pivotY = 16.94f + h;
                this.rightWing.roll = 0.0873f + age;
                this.rightWing.pivotY = 16.94f + h;
                this.leftLeg.pivotY = 22.0f + h;
                this.rightLeg.pivotY = 22.0f + h;
            }
        }
    }

    private void animateModel(Pose pose) {
        this.feather.pitch = -0.2214f;
        this.body.pitch = 0.4937f;
        this.leftWing.pitch = -0.6981f;
        this.leftWing.yaw = (float)(-Math.PI);
        this.rightWing.pitch = -0.6981f;
        this.rightWing.yaw = (float)(-Math.PI);
        this.leftLeg.pitch = -0.0299f;
        this.rightLeg.pitch = -0.0299f;
        this.leftLeg.pivotY = 22.0f;
        this.rightLeg.pivotY = 22.0f;
        this.leftLeg.roll = 0.0f;
        this.rightLeg.roll = 0.0f;
        switch (pose) {
            case FLYING: {
                this.leftLeg.pitch += 0.6981317f;
                this.rightLeg.pitch += 0.6981317f;
                break;
            }
            case SITTING: {
                float f = 1.9f;
                this.head.pivotY = 17.59f;
                this.tail.pitch = 1.5388988f;
                this.tail.pivotY = 22.97f;
                this.body.pivotY = 18.4f;
                this.leftWing.roll = -0.0873f;
                this.leftWing.pivotY = 18.84f;
                this.rightWing.roll = 0.0873f;
                this.rightWing.pivotY = 18.84f;
                this.leftLeg.pivotY += 1.9f;
                this.rightLeg.pivotY += 1.9f;
                this.leftLeg.pitch += 1.5707964f;
                this.rightLeg.pitch += 1.5707964f;
                break;
            }
            case PARTY: {
                this.leftLeg.roll = -0.34906584f;
                this.rightLeg.roll = 0.34906584f;
                break;
            }
        }
    }

    private static Pose getPose(ParrotEntity parrot) {
        if (parrot.isSongPlaying()) {
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

