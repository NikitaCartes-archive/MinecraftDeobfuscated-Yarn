/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
    private final ModelPart torso;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart frontLegs;
    private final ModelPart middleLegs;
    private final ModelPart backLegs;
    private final ModelPart stinger;
    private final ModelPart leftAntenna;
    private final ModelPart rightAntenna;
    private float bodyPitch;

    public BeeEntityModel() {
        super(false, 24.0f, 0.0f);
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.body = new ModelPart(this);
        this.body.setPivot(0.0f, 19.0f, 0.0f);
        this.torso = new ModelPart(this, 0, 0);
        this.torso.setPivot(0.0f, 0.0f, 0.0f);
        this.body.addChild(this.torso);
        this.torso.addCuboid(-3.5f, -4.0f, -5.0f, 7.0f, 7.0f, 10.0f, 0.0f);
        this.stinger = new ModelPart(this, 26, 7);
        this.stinger.addCuboid(0.0f, -1.0f, 5.0f, 0.0f, 1.0f, 2.0f, 0.0f);
        this.torso.addChild(this.stinger);
        this.leftAntenna = new ModelPart(this, 2, 0);
        this.leftAntenna.setPivot(0.0f, -2.0f, -5.0f);
        this.leftAntenna.addCuboid(1.5f, -2.0f, -3.0f, 1.0f, 2.0f, 3.0f, 0.0f);
        this.rightAntenna = new ModelPart(this, 2, 3);
        this.rightAntenna.setPivot(0.0f, -2.0f, -5.0f);
        this.rightAntenna.addCuboid(-2.5f, -2.0f, -3.0f, 1.0f, 2.0f, 3.0f, 0.0f);
        this.torso.addChild(this.leftAntenna);
        this.torso.addChild(this.rightAntenna);
        this.rightWing = new ModelPart(this, 0, 18);
        this.rightWing.setPivot(-1.5f, -4.0f, -3.0f);
        this.rightWing.pitch = 0.0f;
        this.rightWing.yaw = -0.2618f;
        this.rightWing.roll = 0.0f;
        this.body.addChild(this.rightWing);
        this.rightWing.addCuboid(-9.0f, 0.0f, 0.0f, 9.0f, 0.0f, 6.0f, 0.001f);
        this.leftWing = new ModelPart(this, 0, 18);
        this.leftWing.setPivot(1.5f, -4.0f, -3.0f);
        this.leftWing.pitch = 0.0f;
        this.leftWing.yaw = 0.2618f;
        this.leftWing.roll = 0.0f;
        this.leftWing.mirror = true;
        this.body.addChild(this.leftWing);
        this.leftWing.addCuboid(0.0f, 0.0f, 0.0f, 9.0f, 0.0f, 6.0f, 0.001f);
        this.frontLegs = new ModelPart(this);
        this.frontLegs.setPivot(1.5f, 3.0f, -2.0f);
        this.body.addChild(this.frontLegs);
        this.frontLegs.addCuboid("frontLegBox", -5.0f, 0.0f, 0.0f, 7, 2, 0, 0.0f, 26, 1);
        this.middleLegs = new ModelPart(this);
        this.middleLegs.setPivot(1.5f, 3.0f, 0.0f);
        this.body.addChild(this.middleLegs);
        this.middleLegs.addCuboid("midLegBox", -5.0f, 0.0f, 0.0f, 7, 2, 0, 0.0f, 26, 3);
        this.backLegs = new ModelPart(this);
        this.backLegs.setPivot(1.5f, 3.0f, 2.0f);
        this.body.addChild(this.backLegs);
        this.backLegs.addCuboid("backLegBox", -5.0f, 0.0f, 0.0f, 7, 2, 0, 0.0f, 26, 5);
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
        this.body.pivotY = 19.0f;
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

