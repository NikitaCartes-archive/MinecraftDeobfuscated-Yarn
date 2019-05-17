/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class FoxEntityModel<T extends FoxEntity>
extends EntityModel<T> {
    public final Cuboid head;
    private final Cuboid leftEar;
    private final Cuboid rightEar;
    private final Cuboid nose;
    private final Cuboid body;
    private final Cuboid frontLeftLeg;
    private final Cuboid frontRightLeg;
    private final Cuboid rearLeftLeg;
    private final Cuboid rearRightLeg;
    private final Cuboid tail;
    private float field_18025;

    public FoxEntityModel() {
        this.textureWidth = 48;
        this.textureHeight = 32;
        this.head = new Cuboid(this, 1, 5);
        this.head.addBox(-3.0f, -2.0f, -5.0f, 8, 6, 6);
        this.head.setRotationPoint(-1.0f, 16.5f, -3.0f);
        this.leftEar = new Cuboid(this, 8, 1);
        this.leftEar.addBox(-3.0f, -4.0f, -4.0f, 2, 2, 1);
        this.rightEar = new Cuboid(this, 15, 1);
        this.rightEar.addBox(3.0f, -4.0f, -4.0f, 2, 2, 1);
        this.nose = new Cuboid(this, 6, 18);
        this.nose.addBox(-1.0f, 2.01f, -8.0f, 4, 2, 3);
        this.head.addChild(this.leftEar);
        this.head.addChild(this.rightEar);
        this.head.addChild(this.nose);
        this.body = new Cuboid(this, 24, 15);
        this.body.addBox(-3.0f, 3.999f, -3.5f, 6, 11, 6);
        this.body.setRotationPoint(0.0f, 16.0f, -6.0f);
        float f = 0.001f;
        this.frontLeftLeg = new Cuboid(this, 13, 24);
        this.frontLeftLeg.addBox(2.0f, 0.5f, -1.0f, 2, 6, 2, 0.001f);
        this.frontLeftLeg.setRotationPoint(-5.0f, 17.5f, 7.0f);
        this.frontRightLeg = new Cuboid(this, 4, 24);
        this.frontRightLeg.addBox(2.0f, 0.5f, -1.0f, 2, 6, 2, 0.001f);
        this.frontRightLeg.setRotationPoint(-1.0f, 17.5f, 7.0f);
        this.rearLeftLeg = new Cuboid(this, 13, 24);
        this.rearLeftLeg.addBox(2.0f, 0.5f, -1.0f, 2, 6, 2, 0.001f);
        this.rearLeftLeg.setRotationPoint(-5.0f, 17.5f, 0.0f);
        this.rearRightLeg = new Cuboid(this, 4, 24);
        this.rearRightLeg.addBox(2.0f, 0.5f, -1.0f, 2, 6, 2, 0.001f);
        this.rearRightLeg.setRotationPoint(-1.0f, 17.5f, 0.0f);
        this.tail = new Cuboid(this, 30, 0);
        this.tail.addBox(2.0f, 0.0f, -1.0f, 4, 9, 5);
        this.tail.setRotationPoint(-4.0f, 15.0f, -1.0f);
        this.body.addChild(this.tail);
    }

    public void method_18330(T foxEntity, float f, float g, float h) {
        this.body.pitch = 1.5707964f;
        this.tail.pitch = -0.05235988f;
        this.frontLeftLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.frontRightLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.rearLeftLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.rearRightLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.head.setRotationPoint(-1.0f, 16.5f, -3.0f);
        this.head.yaw = 0.0f;
        this.head.roll = ((FoxEntity)foxEntity).getHeadRoll(h);
        this.frontLeftLeg.visible = true;
        this.frontRightLeg.visible = true;
        this.rearLeftLeg.visible = true;
        this.rearRightLeg.visible = true;
        this.body.setRotationPoint(0.0f, 16.0f, -6.0f);
        this.body.roll = 0.0f;
        this.frontLeftLeg.setRotationPoint(-5.0f, 17.5f, 7.0f);
        this.frontRightLeg.setRotationPoint(-1.0f, 17.5f, 7.0f);
        if (((FoxEntity)foxEntity).isCrouching()) {
            this.body.pitch = 1.6755161f;
            float i = ((FoxEntity)foxEntity).getBodyRotationHeightOffset(h);
            this.body.setRotationPoint(0.0f, 16.0f + ((FoxEntity)foxEntity).getBodyRotationHeightOffset(h), -6.0f);
            this.head.setRotationPoint(-1.0f, 16.5f + i, -3.0f);
            this.head.yaw = 0.0f;
        } else if (((FoxEntity)foxEntity).isSleeping()) {
            this.body.roll = -1.5707964f;
            this.body.setRotationPoint(0.0f, 21.0f, -6.0f);
            this.tail.pitch = -2.6179938f;
            if (this.isChild) {
                this.tail.pitch = -2.1816616f;
                this.body.setRotationPoint(0.0f, 21.0f, -2.0f);
            }
            this.head.setRotationPoint(1.0f, 19.49f, -3.0f);
            this.head.pitch = 0.0f;
            this.head.yaw = -2.0943952f;
            this.head.roll = 0.0f;
            this.frontLeftLeg.visible = false;
            this.frontRightLeg.visible = false;
            this.rearLeftLeg.visible = false;
            this.rearRightLeg.visible = false;
        } else if (((FoxEntity)foxEntity).isSitting()) {
            this.body.pitch = 0.5235988f;
            this.body.setRotationPoint(0.0f, 9.0f, -3.0f);
            this.tail.pitch = 0.7853982f;
            this.tail.setRotationPoint(-4.0f, 15.0f, -2.0f);
            this.head.setRotationPoint(-1.0f, 10.0f, -0.25f);
            this.head.pitch = 0.0f;
            this.head.yaw = 0.0f;
            if (this.isChild) {
                this.head.setRotationPoint(-1.0f, 13.0f, -3.75f);
            }
            this.frontLeftLeg.pitch = -1.3089969f;
            this.frontLeftLeg.setRotationPoint(-5.0f, 21.5f, 6.75f);
            this.frontRightLeg.pitch = -1.3089969f;
            this.frontRightLeg.setRotationPoint(-1.0f, 21.5f, 6.75f);
            this.rearLeftLeg.pitch = -0.2617994f;
            this.rearRightLeg.pitch = -0.2617994f;
        }
    }

    public void method_18331(T foxEntity, float f, float g, float h, float i, float j, float k) {
        super.render(foxEntity, f, g, h, i, j, k);
        this.method_18332(foxEntity, f, g, h, i, j, k);
        if (this.isChild) {
            GlStateManager.pushMatrix();
            float l = 0.75f;
            GlStateManager.scalef(0.75f, 0.75f, 0.75f);
            GlStateManager.translatef(0.0f, 8.0f * k, 3.35f * k);
            this.head.render(k);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            float m = 0.5f;
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 24.0f * k, 0.0f);
            this.body.render(k);
            this.frontLeftLeg.render(k);
            this.frontRightLeg.render(k);
            this.rearLeftLeg.render(k);
            this.rearRightLeg.render(k);
            GlStateManager.popMatrix();
        } else {
            GlStateManager.pushMatrix();
            this.head.render(k);
            this.body.render(k);
            this.frontLeftLeg.render(k);
            this.frontRightLeg.render(k);
            this.rearLeftLeg.render(k);
            this.rearRightLeg.render(k);
            GlStateManager.popMatrix();
        }
    }

    public void method_18332(T foxEntity, float f, float g, float h, float i, float j, float k) {
        float l;
        super.setAngles(foxEntity, f, g, h, i, j, k);
        if (!(((FoxEntity)foxEntity).isSleeping() || ((FoxEntity)foxEntity).isWalking() || ((FoxEntity)foxEntity).isCrouching())) {
            this.head.pitch = j * ((float)Math.PI / 180);
            this.head.yaw = i * ((float)Math.PI / 180);
        }
        if (((FoxEntity)foxEntity).isSleeping()) {
            this.head.pitch = 0.0f;
            this.head.yaw = -2.0943952f;
            this.head.roll = MathHelper.cos(h * 0.027f) / 22.0f;
        }
        if (((FoxEntity)foxEntity).isCrouching()) {
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

    @Override
    public /* synthetic */ void setAngles(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.method_18332((FoxEntity)entity, f, g, h, i, j, k);
    }

    @Override
    public /* synthetic */ void render(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.method_18331((FoxEntity)entity, f, g, h, i, j, k);
    }
}

