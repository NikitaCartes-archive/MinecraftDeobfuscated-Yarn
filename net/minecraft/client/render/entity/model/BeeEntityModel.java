/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.ModelUtil;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BeeEntityModel<T extends BeeEntity>
extends AnimalModel<T> {
    private static final float BONE_BASE_Y_PIVOT = 19.0f;
    private static final String BONE = "bone";
    private static final String STINGER = "stinger";
    private static final String LEFT_ANTENNA = "left_antenna";
    private static final String RIGHT_ANTENNA = "right_antenna";
    private static final String FRONT_LEGS = "front_legs";
    private static final String MIDDLE_LEGS = "middle_legs";
    private static final String BACK_LEGS = "back_legs";
    private final ModelPart bone;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart frontLegs;
    private final ModelPart middleLegs;
    private final ModelPart backLegs;
    private final ModelPart stinger;
    private final ModelPart leftAntenna;
    private final ModelPart rightAntenna;
    private float bodyPitch;

    public BeeEntityModel(ModelPart root) {
        super(false, 24.0f, 0.0f);
        this.bone = root.getChild(BONE);
        ModelPart modelPart = this.bone.getChild(EntityModelPartNames.BODY);
        this.stinger = modelPart.getChild(STINGER);
        this.leftAntenna = modelPart.getChild(LEFT_ANTENNA);
        this.rightAntenna = modelPart.getChild(RIGHT_ANTENNA);
        this.rightWing = this.bone.getChild(EntityModelPartNames.RIGHT_WING);
        this.leftWing = this.bone.getChild(EntityModelPartNames.LEFT_WING);
        this.frontLegs = this.bone.getChild(FRONT_LEGS);
        this.middleLegs = this.bone.getChild(MIDDLE_LEGS);
        this.backLegs = this.bone.getChild(BACK_LEGS);
    }

    public static TexturedModelData getTexturedModelData() {
        float f = 19.0f;
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild(BONE, ModelPartBuilder.create(), ModelTransform.pivot(0.0f, 19.0f, 0.0f));
        ModelPartData modelPartData3 = modelPartData2.addChild(EntityModelPartNames.BODY, ModelPartBuilder.create().uv(0, 0).cuboid(-3.5f, -4.0f, -5.0f, 7.0f, 7.0f, 10.0f), ModelTransform.NONE);
        modelPartData3.addChild(STINGER, ModelPartBuilder.create().uv(26, 7).cuboid(0.0f, -1.0f, 5.0f, 0.0f, 1.0f, 2.0f), ModelTransform.NONE);
        modelPartData3.addChild(LEFT_ANTENNA, ModelPartBuilder.create().uv(2, 0).cuboid(1.5f, -2.0f, -3.0f, 1.0f, 2.0f, 3.0f), ModelTransform.pivot(0.0f, -2.0f, -5.0f));
        modelPartData3.addChild(RIGHT_ANTENNA, ModelPartBuilder.create().uv(2, 3).cuboid(-2.5f, -2.0f, -3.0f, 1.0f, 2.0f, 3.0f), ModelTransform.pivot(0.0f, -2.0f, -5.0f));
        Dilation dilation = new Dilation(0.001f);
        modelPartData2.addChild(EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().uv(0, 18).cuboid(-9.0f, 0.0f, 0.0f, 9.0f, 0.0f, 6.0f, dilation), ModelTransform.of(-1.5f, -4.0f, -3.0f, 0.0f, -0.2618f, 0.0f));
        modelPartData2.addChild(EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().uv(0, 18).mirrored().cuboid(0.0f, 0.0f, 0.0f, 9.0f, 0.0f, 6.0f, dilation), ModelTransform.of(1.5f, -4.0f, -3.0f, 0.0f, 0.2618f, 0.0f));
        modelPartData2.addChild(FRONT_LEGS, ModelPartBuilder.create().cuboid(FRONT_LEGS, -5.0f, 0.0f, 0.0f, 7, 2, 0, 26, 1), ModelTransform.pivot(1.5f, 3.0f, -2.0f));
        modelPartData2.addChild(MIDDLE_LEGS, ModelPartBuilder.create().cuboid(MIDDLE_LEGS, -5.0f, 0.0f, 0.0f, 7, 2, 0, 26, 3), ModelTransform.pivot(1.5f, 3.0f, 0.0f));
        modelPartData2.addChild(BACK_LEGS, ModelPartBuilder.create().cuboid(BACK_LEGS, -5.0f, 0.0f, 0.0f, 7, 2, 0, 26, 5), ModelTransform.pivot(1.5f, 3.0f, 2.0f));
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void animateModel(T beeEntity, float f, float g, float h) {
        super.animateModel(beeEntity, f, g, h);
        this.bodyPitch = ((BeeEntity)beeEntity).getBodyPitch(h);
        this.stinger.visible = !((BeeEntity)beeEntity).hasStung();
    }

    @Override
    public void setAngles(T beeEntity, float f, float g, float h, float i, float j) {
        float k;
        boolean bl;
        this.rightWing.pitch = 0.0f;
        this.leftAntenna.pitch = 0.0f;
        this.rightAntenna.pitch = 0.0f;
        this.bone.pitch = 0.0f;
        boolean bl2 = bl = ((Entity)beeEntity).isOnGround() && ((Entity)beeEntity).getVelocity().lengthSquared() < 1.0E-7;
        if (bl) {
            this.rightWing.yaw = -0.2618f;
            this.rightWing.roll = 0.0f;
            this.leftWing.pitch = 0.0f;
            this.leftWing.yaw = 0.2618f;
            this.leftWing.roll = 0.0f;
            this.frontLegs.pitch = 0.0f;
            this.middleLegs.pitch = 0.0f;
            this.backLegs.pitch = 0.0f;
        } else {
            k = h * 120.32113f * ((float)Math.PI / 180);
            this.rightWing.yaw = 0.0f;
            this.rightWing.roll = MathHelper.cos(k) * (float)Math.PI * 0.15f;
            this.leftWing.pitch = this.rightWing.pitch;
            this.leftWing.yaw = this.rightWing.yaw;
            this.leftWing.roll = -this.rightWing.roll;
            this.frontLegs.pitch = 0.7853982f;
            this.middleLegs.pitch = 0.7853982f;
            this.backLegs.pitch = 0.7853982f;
            this.bone.pitch = 0.0f;
            this.bone.yaw = 0.0f;
            this.bone.roll = 0.0f;
        }
        if (!beeEntity.hasAngerTime()) {
            this.bone.pitch = 0.0f;
            this.bone.yaw = 0.0f;
            this.bone.roll = 0.0f;
            if (!bl) {
                k = MathHelper.cos(h * 0.18f);
                this.bone.pitch = 0.1f + k * (float)Math.PI * 0.025f;
                this.leftAntenna.pitch = k * (float)Math.PI * 0.03f;
                this.rightAntenna.pitch = k * (float)Math.PI * 0.03f;
                this.frontLegs.pitch = -k * (float)Math.PI * 0.1f + 0.3926991f;
                this.backLegs.pitch = -k * (float)Math.PI * 0.05f + 0.7853982f;
                this.bone.pivotY = 19.0f - MathHelper.cos(h * 0.18f) * 0.9f;
            }
        }
        if (this.bodyPitch > 0.0f) {
            this.bone.pitch = ModelUtil.interpolateAngle(this.bone.pitch, 3.0915928f, this.bodyPitch);
        }
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.bone);
    }
}

