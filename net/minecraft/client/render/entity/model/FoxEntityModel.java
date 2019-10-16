/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class FoxEntityModel<T extends FoxEntity>
extends AnimalModel<T> {
    public final ModelPart head;
    private final ModelPart leftEar;
    private final ModelPart rightEar;
    private final ModelPart nose;
    private final ModelPart body;
    private final ModelPart frontLeftLeg;
    private final ModelPart frontRightLeg;
    private final ModelPart rearLeftLeg;
    private final ModelPart rearRightLeg;
    private final ModelPart tail;
    private float field_18025;

    public FoxEntityModel() {
        super(true, 8.0f, 3.35f);
        this.textureWidth = 48;
        this.textureHeight = 32;
        this.head = new ModelPart(this, 1, 5);
        this.head.addCuboid(-3.0f, -2.0f, -5.0f, 8.0f, 6.0f, 6.0f);
        this.head.setPivot(-1.0f, 16.5f, -3.0f);
        this.leftEar = new ModelPart(this, 8, 1);
        this.leftEar.addCuboid(-3.0f, -4.0f, -4.0f, 2.0f, 2.0f, 1.0f);
        this.rightEar = new ModelPart(this, 15, 1);
        this.rightEar.addCuboid(3.0f, -4.0f, -4.0f, 2.0f, 2.0f, 1.0f);
        this.nose = new ModelPart(this, 6, 18);
        this.nose.addCuboid(-1.0f, 2.01f, -8.0f, 4.0f, 2.0f, 3.0f);
        this.head.addChild(this.leftEar);
        this.head.addChild(this.rightEar);
        this.head.addChild(this.nose);
        this.body = new ModelPart(this, 24, 15);
        this.body.addCuboid(-3.0f, 3.999f, -3.5f, 6.0f, 11.0f, 6.0f);
        this.body.setPivot(0.0f, 16.0f, -6.0f);
        float f = 0.001f;
        this.frontLeftLeg = new ModelPart(this, 13, 24);
        this.frontLeftLeg.addCuboid(2.0f, 0.5f, -1.0f, 2.0f, 6.0f, 2.0f, 0.001f);
        this.frontLeftLeg.setPivot(-5.0f, 17.5f, 7.0f);
        this.frontRightLeg = new ModelPart(this, 4, 24);
        this.frontRightLeg.addCuboid(2.0f, 0.5f, -1.0f, 2.0f, 6.0f, 2.0f, 0.001f);
        this.frontRightLeg.setPivot(-1.0f, 17.5f, 7.0f);
        this.rearLeftLeg = new ModelPart(this, 13, 24);
        this.rearLeftLeg.addCuboid(2.0f, 0.5f, -1.0f, 2.0f, 6.0f, 2.0f, 0.001f);
        this.rearLeftLeg.setPivot(-5.0f, 17.5f, 0.0f);
        this.rearRightLeg = new ModelPart(this, 4, 24);
        this.rearRightLeg.addCuboid(2.0f, 0.5f, -1.0f, 2.0f, 6.0f, 2.0f, 0.001f);
        this.rearRightLeg.setPivot(-1.0f, 17.5f, 0.0f);
        this.tail = new ModelPart(this, 30, 0);
        this.tail.addCuboid(2.0f, 0.0f, -1.0f, 4.0f, 9.0f, 5.0f);
        this.tail.setPivot(-4.0f, 15.0f, -1.0f);
        this.body.addChild(this.tail);
    }

    public void method_18330(T foxEntity, float f, float g, float h) {
        this.body.pitch = 1.5707964f;
        this.tail.pitch = -0.05235988f;
        this.frontLeftLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.frontRightLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.rearLeftLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.rearRightLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.head.setPivot(-1.0f, 16.5f, -3.0f);
        this.head.yaw = 0.0f;
        this.head.roll = ((FoxEntity)foxEntity).getHeadRoll(h);
        this.frontLeftLeg.visible = true;
        this.frontRightLeg.visible = true;
        this.rearLeftLeg.visible = true;
        this.rearRightLeg.visible = true;
        this.body.setPivot(0.0f, 16.0f, -6.0f);
        this.body.roll = 0.0f;
        this.frontLeftLeg.setPivot(-5.0f, 17.5f, 7.0f);
        this.frontRightLeg.setPivot(-1.0f, 17.5f, 7.0f);
        if (((FoxEntity)foxEntity).isInSneakingPose()) {
            this.body.pitch = 1.6755161f;
            float i = ((FoxEntity)foxEntity).getBodyRotationHeightOffset(h);
            this.body.setPivot(0.0f, 16.0f + ((FoxEntity)foxEntity).getBodyRotationHeightOffset(h), -6.0f);
            this.head.setPivot(-1.0f, 16.5f + i, -3.0f);
            this.head.yaw = 0.0f;
        } else if (((FoxEntity)foxEntity).isSleeping()) {
            this.body.roll = -1.5707964f;
            this.body.setPivot(0.0f, 21.0f, -6.0f);
            this.tail.pitch = -2.6179938f;
            if (this.isChild) {
                this.tail.pitch = -2.1816616f;
                this.body.setPivot(0.0f, 21.0f, -2.0f);
            }
            this.head.setPivot(1.0f, 19.49f, -3.0f);
            this.head.pitch = 0.0f;
            this.head.yaw = -2.0943952f;
            this.head.roll = 0.0f;
            this.frontLeftLeg.visible = false;
            this.frontRightLeg.visible = false;
            this.rearLeftLeg.visible = false;
            this.rearRightLeg.visible = false;
        } else if (((FoxEntity)foxEntity).isSitting()) {
            this.body.pitch = 0.5235988f;
            this.body.setPivot(0.0f, 9.0f, -3.0f);
            this.tail.pitch = 0.7853982f;
            this.tail.setPivot(-4.0f, 15.0f, -2.0f);
            this.head.setPivot(-1.0f, 10.0f, -0.25f);
            this.head.pitch = 0.0f;
            this.head.yaw = 0.0f;
            if (this.isChild) {
                this.head.setPivot(-1.0f, 13.0f, -3.75f);
            }
            this.frontLeftLeg.pitch = -1.3089969f;
            this.frontLeftLeg.setPivot(-5.0f, 21.5f, 6.75f);
            this.frontRightLeg.pitch = -1.3089969f;
            this.frontRightLeg.setPivot(-1.0f, 21.5f, 6.75f);
            this.rearLeftLeg.pitch = -0.2617994f;
            this.rearRightLeg.pitch = -0.2617994f;
        }
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of(this.head);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.body, this.frontLeftLeg, this.frontRightLeg, this.rearLeftLeg, this.rearRightLeg);
    }

    public void method_18332(T foxEntity, float f, float g, float h, float i, float j, float k) {
        float l;
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
            this.body.yaw = l = MathHelper.cos(h) * 0.01f;
            this.frontLeftLeg.roll = l;
            this.frontRightLeg.roll = l;
            this.rearLeftLeg.roll = l / 2.0f;
            this.rearRightLeg.roll = l / 2.0f;
        }
        if (((FoxEntity)foxEntity).isWalking()) {
            l = 0.1f;
            this.field_18025 += 0.67f;
            this.frontLeftLeg.pitch = MathHelper.cos(this.field_18025 * 0.4662f) * 0.1f;
            this.frontRightLeg.pitch = MathHelper.cos(this.field_18025 * 0.4662f + (float)Math.PI) * 0.1f;
            this.rearLeftLeg.pitch = MathHelper.cos(this.field_18025 * 0.4662f + (float)Math.PI) * 0.1f;
            this.rearRightLeg.pitch = MathHelper.cos(this.field_18025 * 0.4662f) * 0.1f;
        }
    }
}

