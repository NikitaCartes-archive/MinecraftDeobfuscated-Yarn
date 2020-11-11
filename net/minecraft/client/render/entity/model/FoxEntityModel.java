/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.util.math.Dilation;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class FoxEntityModel<T extends FoxEntity>
extends AnimalModel<T> {
    public final ModelPart head;
    private final ModelPart torso;
    private final ModelPart field_27415;
    private final ModelPart field_27416;
    private final ModelPart field_27417;
    private final ModelPart field_27418;
    private final ModelPart tail;
    private float legPitchModifier;

    public FoxEntityModel(ModelPart modelPart) {
        super(true, 8.0f, 3.35f);
        this.head = modelPart.getChild("head");
        this.torso = modelPart.getChild("body");
        this.field_27415 = modelPart.getChild("right_hind_leg");
        this.field_27416 = modelPart.getChild("left_hind_leg");
        this.field_27417 = modelPart.getChild("right_front_leg");
        this.field_27418 = modelPart.getChild("left_front_leg");
        this.tail = this.torso.getChild("tail");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData modelPartData2 = modelPartData.addChild("head", ModelPartBuilder.create().uv(1, 5).cuboid(-3.0f, -2.0f, -5.0f, 8.0f, 6.0f, 6.0f), ModelTransform.pivot(-1.0f, 16.5f, -3.0f));
        modelPartData2.addChild("right_ear", ModelPartBuilder.create().uv(8, 1).cuboid(-3.0f, -4.0f, -4.0f, 2.0f, 2.0f, 1.0f), ModelTransform.NONE);
        modelPartData2.addChild("left_ear", ModelPartBuilder.create().uv(15, 1).cuboid(3.0f, -4.0f, -4.0f, 2.0f, 2.0f, 1.0f), ModelTransform.NONE);
        modelPartData2.addChild("nose", ModelPartBuilder.create().uv(6, 18).cuboid(-1.0f, 2.01f, -8.0f, 4.0f, 2.0f, 3.0f), ModelTransform.NONE);
        ModelPartData modelPartData3 = modelPartData.addChild("body", ModelPartBuilder.create().uv(24, 15).cuboid(-3.0f, 3.999f, -3.5f, 6.0f, 11.0f, 6.0f), ModelTransform.of(0.0f, 16.0f, -6.0f, 1.5707964f, 0.0f, 0.0f));
        Dilation dilation = new Dilation(0.001f);
        ModelPartBuilder modelPartBuilder = ModelPartBuilder.create().uv(4, 24).cuboid(2.0f, 0.5f, -1.0f, 2.0f, 6.0f, 2.0f, dilation);
        ModelPartBuilder modelPartBuilder2 = ModelPartBuilder.create().uv(13, 24).cuboid(2.0f, 0.5f, -1.0f, 2.0f, 6.0f, 2.0f, dilation);
        modelPartData.addChild("right_hind_leg", modelPartBuilder2, ModelTransform.pivot(-5.0f, 17.5f, 7.0f));
        modelPartData.addChild("left_hind_leg", modelPartBuilder, ModelTransform.pivot(-1.0f, 17.5f, 7.0f));
        modelPartData.addChild("right_front_leg", modelPartBuilder2, ModelTransform.pivot(-5.0f, 17.5f, 0.0f));
        modelPartData.addChild("left_front_leg", modelPartBuilder, ModelTransform.pivot(-1.0f, 17.5f, 0.0f));
        modelPartData3.addChild("tail", ModelPartBuilder.create().uv(30, 0).cuboid(2.0f, 0.0f, -1.0f, 4.0f, 9.0f, 5.0f), ModelTransform.of(-4.0f, 15.0f, -1.0f, -0.05235988f, 0.0f, 0.0f));
        return TexturedModelData.of(modelData, 48, 32);
    }

    @Override
    public void animateModel(T foxEntity, float f, float g, float h) {
        this.torso.pitch = 1.5707964f;
        this.tail.pitch = -0.05235988f;
        this.field_27415.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.field_27416.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.field_27417.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.field_27418.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.head.setPivot(-1.0f, 16.5f, -3.0f);
        this.head.yaw = 0.0f;
        this.head.roll = ((FoxEntity)foxEntity).getHeadRoll(h);
        this.field_27415.visible = true;
        this.field_27416.visible = true;
        this.field_27417.visible = true;
        this.field_27418.visible = true;
        this.torso.setPivot(0.0f, 16.0f, -6.0f);
        this.torso.roll = 0.0f;
        this.field_27415.setPivot(-5.0f, 17.5f, 7.0f);
        this.field_27416.setPivot(-1.0f, 17.5f, 7.0f);
        if (((FoxEntity)foxEntity).isInSneakingPose()) {
            this.torso.pitch = 1.6755161f;
            float i = ((FoxEntity)foxEntity).getBodyRotationHeightOffset(h);
            this.torso.setPivot(0.0f, 16.0f + ((FoxEntity)foxEntity).getBodyRotationHeightOffset(h), -6.0f);
            this.head.setPivot(-1.0f, 16.5f + i, -3.0f);
            this.head.yaw = 0.0f;
        } else if (((FoxEntity)foxEntity).isSleeping()) {
            this.torso.roll = -1.5707964f;
            this.torso.setPivot(0.0f, 21.0f, -6.0f);
            this.tail.pitch = -2.6179938f;
            if (this.child) {
                this.tail.pitch = -2.1816616f;
                this.torso.setPivot(0.0f, 21.0f, -2.0f);
            }
            this.head.setPivot(1.0f, 19.49f, -3.0f);
            this.head.pitch = 0.0f;
            this.head.yaw = -2.0943952f;
            this.head.roll = 0.0f;
            this.field_27415.visible = false;
            this.field_27416.visible = false;
            this.field_27417.visible = false;
            this.field_27418.visible = false;
        } else if (((FoxEntity)foxEntity).isSitting()) {
            this.torso.pitch = 0.5235988f;
            this.torso.setPivot(0.0f, 9.0f, -3.0f);
            this.tail.pitch = 0.7853982f;
            this.tail.setPivot(-4.0f, 15.0f, -2.0f);
            this.head.setPivot(-1.0f, 10.0f, -0.25f);
            this.head.pitch = 0.0f;
            this.head.yaw = 0.0f;
            if (this.child) {
                this.head.setPivot(-1.0f, 13.0f, -3.75f);
            }
            this.field_27415.pitch = -1.3089969f;
            this.field_27415.setPivot(-5.0f, 21.5f, 6.75f);
            this.field_27416.pitch = -1.3089969f;
            this.field_27416.setPivot(-1.0f, 21.5f, 6.75f);
            this.field_27417.pitch = -0.2617994f;
            this.field_27418.pitch = -0.2617994f;
        }
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.torso, this.field_27415, this.field_27416, this.field_27417, this.field_27418);
    }

    @Override
    public void setAngles(T foxEntity, float f, float g, float h, float i, float j) {
        float k;
        if (!(((FoxEntity)foxEntity).isSleeping() || ((FoxEntity)foxEntity).isWalking() || ((FoxEntity)foxEntity).isInSneakingPose())) {
            this.head.pitch = j * ((float)Math.PI / 180);
            this.head.yaw = i * ((float)Math.PI / 180);
        }
        if (((FoxEntity)foxEntity).isSleeping()) {
            this.head.pitch = 0.0f;
            this.head.yaw = -2.0943952f;
            this.head.roll = MathHelper.cos(h * 0.027f) / 22.0f;
        }
        if (((FoxEntity)foxEntity).isInSneakingPose()) {
            this.torso.yaw = k = MathHelper.cos(h) * 0.01f;
            this.field_27415.roll = k;
            this.field_27416.roll = k;
            this.field_27417.roll = k / 2.0f;
            this.field_27418.roll = k / 2.0f;
        }
        if (((FoxEntity)foxEntity).isWalking()) {
            k = 0.1f;
            this.legPitchModifier += 0.67f;
            this.field_27415.pitch = MathHelper.cos(this.legPitchModifier * 0.4662f) * 0.1f;
            this.field_27416.pitch = MathHelper.cos(this.legPitchModifier * 0.4662f + (float)Math.PI) * 0.1f;
            this.field_27417.pitch = MathHelper.cos(this.legPitchModifier * 0.4662f + (float)Math.PI) * 0.1f;
            this.field_27418.pitch = MathHelper.cos(this.legPitchModifier * 0.4662f) * 0.1f;
        }
    }
}

