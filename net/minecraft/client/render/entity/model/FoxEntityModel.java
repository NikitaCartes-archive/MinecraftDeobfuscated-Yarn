/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class FoxEntityModel<T extends FoxEntity>
extends EntityModel<T> {
    public final ModelPart head;
    private final ModelPart rightEar;
    private final ModelPart leftEar;
    private final ModelPart nose;
    private final ModelPart torso;
    private final ModelPart rightBackLeg;
    private final ModelPart leftBackLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart tail;
    private float field_18025;

    public FoxEntityModel() {
        this.textureWidth = 48;
        this.textureHeight = 32;
        this.head = new ModelPart(this, 1, 5);
        this.head.addCuboid(-3.0f, -2.0f, -5.0f, 8, 6, 6);
        this.head.setPivot(-1.0f, 16.5f, -3.0f);
        this.rightEar = new ModelPart(this, 8, 1);
        this.rightEar.addCuboid(-3.0f, -4.0f, -4.0f, 2, 2, 1);
        this.leftEar = new ModelPart(this, 15, 1);
        this.leftEar.addCuboid(3.0f, -4.0f, -4.0f, 2, 2, 1);
        this.nose = new ModelPart(this, 6, 18);
        this.nose.addCuboid(-1.0f, 2.01f, -8.0f, 4, 2, 3);
        this.head.addChild(this.rightEar);
        this.head.addChild(this.leftEar);
        this.head.addChild(this.nose);
        this.torso = new ModelPart(this, 24, 15);
        this.torso.addCuboid(-3.0f, 3.999f, -3.5f, 6, 11, 6);
        this.torso.setPivot(0.0f, 16.0f, -6.0f);
        float f = 0.001f;
        this.rightBackLeg = new ModelPart(this, 13, 24);
        this.rightBackLeg.addCuboid(2.0f, 0.5f, -1.0f, 2, 6, 2, 0.001f);
        this.rightBackLeg.setPivot(-5.0f, 17.5f, 7.0f);
        this.leftBackLeg = new ModelPart(this, 4, 24);
        this.leftBackLeg.addCuboid(2.0f, 0.5f, -1.0f, 2, 6, 2, 0.001f);
        this.leftBackLeg.setPivot(-1.0f, 17.5f, 7.0f);
        this.rightFrontLeg = new ModelPart(this, 13, 24);
        this.rightFrontLeg.addCuboid(2.0f, 0.5f, -1.0f, 2, 6, 2, 0.001f);
        this.rightFrontLeg.setPivot(-5.0f, 17.5f, 0.0f);
        this.leftFrontLeg = new ModelPart(this, 4, 24);
        this.leftFrontLeg.addCuboid(2.0f, 0.5f, -1.0f, 2, 6, 2, 0.001f);
        this.leftFrontLeg.setPivot(-1.0f, 17.5f, 0.0f);
        this.tail = new ModelPart(this, 30, 0);
        this.tail.addCuboid(2.0f, 0.0f, -1.0f, 4, 9, 5);
        this.tail.setPivot(-4.0f, 15.0f, -1.0f);
        this.torso.addChild(this.tail);
    }

    @Override
    public void animateModel(T foxEntity, float f, float g, float h) {
        this.torso.pitch = 1.5707964f;
        this.tail.pitch = -0.05235988f;
        this.rightBackLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.leftBackLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.rightFrontLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.leftFrontLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.head.setPivot(-1.0f, 16.5f, -3.0f);
        this.head.yaw = 0.0f;
        this.head.roll = ((FoxEntity)foxEntity).getHeadRoll(h);
        this.rightBackLeg.visible = true;
        this.leftBackLeg.visible = true;
        this.rightFrontLeg.visible = true;
        this.leftFrontLeg.visible = true;
        this.torso.setPivot(0.0f, 16.0f, -6.0f);
        this.torso.roll = 0.0f;
        this.rightBackLeg.setPivot(-5.0f, 17.5f, 7.0f);
        this.leftBackLeg.setPivot(-1.0f, 17.5f, 7.0f);
        if (((FoxEntity)foxEntity).isCrouching()) {
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
            this.rightBackLeg.visible = false;
            this.leftBackLeg.visible = false;
            this.rightFrontLeg.visible = false;
            this.leftFrontLeg.visible = false;
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
            this.rightBackLeg.pitch = -1.3089969f;
            this.rightBackLeg.setPivot(-5.0f, 21.5f, 6.75f);
            this.leftBackLeg.pitch = -1.3089969f;
            this.leftBackLeg.setPivot(-1.0f, 21.5f, 6.75f);
            this.rightFrontLeg.pitch = -0.2617994f;
            this.leftFrontLeg.pitch = -0.2617994f;
        }
    }

    @Override
    public void render(T foxEntity, float f, float g, float h, float i, float j, float k) {
        super.render(foxEntity, f, g, h, i, j, k);
        this.setAngles(foxEntity, f, g, h, i, j, k);
        if (this.child) {
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
            this.torso.render(k);
            this.rightBackLeg.render(k);
            this.leftBackLeg.render(k);
            this.rightFrontLeg.render(k);
            this.leftFrontLeg.render(k);
            GlStateManager.popMatrix();
        } else {
            GlStateManager.pushMatrix();
            this.head.render(k);
            this.torso.render(k);
            this.rightBackLeg.render(k);
            this.leftBackLeg.render(k);
            this.rightFrontLeg.render(k);
            this.leftFrontLeg.render(k);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void setAngles(T foxEntity, float f, float g, float h, float i, float j, float k) {
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
            this.torso.yaw = l = MathHelper.cos(h) * 0.01f;
            this.rightBackLeg.roll = l;
            this.leftBackLeg.roll = l;
            this.rightFrontLeg.roll = l / 2.0f;
            this.leftFrontLeg.roll = l / 2.0f;
        }
        if (((FoxEntity)foxEntity).isWalking()) {
            l = 0.1f;
            this.field_18025 += 0.67f;
            this.rightBackLeg.pitch = MathHelper.cos(this.field_18025 * 0.4662f) * 0.1f;
            this.leftBackLeg.pitch = MathHelper.cos(this.field_18025 * 0.4662f + (float)Math.PI) * 0.1f;
            this.rightFrontLeg.pitch = MathHelper.cos(this.field_18025 * 0.4662f + (float)Math.PI) * 0.1f;
            this.leftFrontLeg.pitch = MathHelper.cos(this.field_18025 * 0.4662f) * 0.1f;
        }
    }

    @Override
    public /* synthetic */ void setAngles(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles((T)((FoxEntity)entity), f, g, h, i, j, k);
    }

    @Override
    public /* synthetic */ void render(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.render((T)((FoxEntity)entity), f, g, h, i, j, k);
    }
}

