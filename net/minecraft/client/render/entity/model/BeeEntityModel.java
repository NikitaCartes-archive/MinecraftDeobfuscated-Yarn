/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.class_5605;
import net.minecraft.class_5606;
import net.minecraft.class_5607;
import net.minecraft.class_5609;
import net.minecraft.class_5610;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelUtil;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class BeeEntityModel<T extends BeeEntity>
extends AnimalModel<T> {
    private final ModelPart body;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart frontLegs;
    private final ModelPart middleLegs;
    private final ModelPart backLegs;
    private final ModelPart stinger;
    private final ModelPart leftAntenna;
    private final ModelPart rightAntenna;
    private float bodyPitch;

    public BeeEntityModel(ModelPart modelPart) {
        super(false, 24.0f, 0.0f);
        this.body = modelPart.method_32086("bone");
        ModelPart modelPart2 = this.body.method_32086("body");
        this.stinger = modelPart2.method_32086("stinger");
        this.leftAntenna = modelPart2.method_32086("left_antenna");
        this.rightAntenna = modelPart2.method_32086("right_antenna");
        this.rightWing = this.body.method_32086("right_wing");
        this.leftWing = this.body.method_32086("left_wing");
        this.frontLegs = this.body.method_32086("front_legs");
        this.middleLegs = this.body.method_32086("middle_legs");
        this.backLegs = this.body.method_32086("back_legs");
    }

    public static class_5607 method_31981() {
        float f = 19.0f;
        class_5609 lv = new class_5609();
        class_5610 lv2 = lv.method_32111();
        class_5610 lv3 = lv2.method_32117("bone", class_5606.method_32108(), class_5603.method_32090(0.0f, 19.0f, 0.0f));
        class_5610 lv4 = lv3.method_32117("body", class_5606.method_32108().method_32101(0, 0).method_32097(-3.5f, -4.0f, -5.0f, 7.0f, 7.0f, 10.0f), class_5603.field_27701);
        lv4.method_32117("stinger", class_5606.method_32108().method_32101(26, 7).method_32097(0.0f, -1.0f, 5.0f, 0.0f, 1.0f, 2.0f), class_5603.field_27701);
        lv4.method_32117("left_antenna", class_5606.method_32108().method_32101(2, 0).method_32097(1.5f, -2.0f, -3.0f, 1.0f, 2.0f, 3.0f), class_5603.method_32090(0.0f, -2.0f, -5.0f));
        lv4.method_32117("right_antenna", class_5606.method_32108().method_32101(2, 3).method_32097(-2.5f, -2.0f, -3.0f, 1.0f, 2.0f, 3.0f), class_5603.method_32090(0.0f, -2.0f, -5.0f));
        class_5605 lv5 = new class_5605(0.001f);
        lv3.method_32117("right_wing", class_5606.method_32108().method_32101(0, 18).method_32098(-9.0f, 0.0f, 0.0f, 9.0f, 0.0f, 6.0f, lv5), class_5603.method_32091(-1.5f, -4.0f, -3.0f, 0.0f, -0.2618f, 0.0f));
        lv3.method_32117("left_wing", class_5606.method_32108().method_32101(0, 18).method_32096().method_32098(0.0f, 0.0f, 0.0f, 9.0f, 0.0f, 6.0f, lv5), class_5603.method_32091(1.5f, -4.0f, -3.0f, 0.0f, 0.2618f, 0.0f));
        lv3.method_32117("front_legs", class_5606.method_32108().method_32104("front_legs", -5.0f, 0.0f, 0.0f, 7, 2, 0, 26, 1), class_5603.method_32090(1.5f, 3.0f, -2.0f));
        lv3.method_32117("middle_legs", class_5606.method_32108().method_32104("middle_legs", -5.0f, 0.0f, 0.0f, 7, 2, 0, 26, 3), class_5603.method_32090(1.5f, 3.0f, 0.0f));
        lv3.method_32117("back_legs", class_5606.method_32108().method_32104("back_legs", -5.0f, 0.0f, 0.0f, 7, 2, 0, 26, 5), class_5603.method_32090(1.5f, 3.0f, 2.0f));
        return class_5607.method_32110(lv, 64, 64);
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
        this.body.pitch = 0.0f;
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
            k = h * 2.1f;
            this.rightWing.yaw = 0.0f;
            this.rightWing.roll = MathHelper.cos(k) * (float)Math.PI * 0.15f;
            this.leftWing.pitch = this.rightWing.pitch;
            this.leftWing.yaw = this.rightWing.yaw;
            this.leftWing.roll = -this.rightWing.roll;
            this.frontLegs.pitch = 0.7853982f;
            this.middleLegs.pitch = 0.7853982f;
            this.backLegs.pitch = 0.7853982f;
            this.body.pitch = 0.0f;
            this.body.yaw = 0.0f;
            this.body.roll = 0.0f;
        }
        if (!beeEntity.hasAngerTime()) {
            this.body.pitch = 0.0f;
            this.body.yaw = 0.0f;
            this.body.roll = 0.0f;
            if (!bl) {
                k = MathHelper.cos(h * 0.18f);
                this.body.pitch = 0.1f + k * (float)Math.PI * 0.025f;
                this.leftAntenna.pitch = k * (float)Math.PI * 0.03f;
                this.rightAntenna.pitch = k * (float)Math.PI * 0.03f;
                this.frontLegs.pitch = -k * (float)Math.PI * 0.1f + 0.3926991f;
                this.backLegs.pitch = -k * (float)Math.PI * 0.05f + 0.7853982f;
                this.body.pivotY = 19.0f - MathHelper.cos(h * 0.18f) * 0.9f;
            }
        }
        if (this.bodyPitch > 0.0f) {
            this.body.pitch = ModelUtil.interpolateAngle(this.body.pitch, 3.0915928f, this.bodyPitch);
        }
    }

    @Override
    protected Iterable<ModelPart> getHeadParts() {
        return ImmutableList.of();
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return ImmutableList.of(this.body);
    }
}

