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
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class QuadrupedEntityModel<T extends Entity>
extends EntityModel<T> {
    protected ModelPart head = new ModelPart(this, 0, 0);
    protected ModelPart torso;
    protected ModelPart backRightLeg;
    protected ModelPart backLeftLeg;
    protected ModelPart frontRightLeg;
    protected ModelPart frontLeftLeg;
    protected float field_3540 = 8.0f;
    protected float field_3537 = 4.0f;

    public QuadrupedEntityModel(int i, float f) {
        this.head.addCuboid(-4.0f, -4.0f, -8.0f, 8, 8, 8, f);
        this.head.setPivot(0.0f, 18 - i, -6.0f);
        this.torso = new ModelPart(this, 28, 8);
        this.torso.addCuboid(-5.0f, -10.0f, -7.0f, 10, 16, 8, f);
        this.torso.setPivot(0.0f, 17 - i, 2.0f);
        this.backRightLeg = new ModelPart(this, 0, 16);
        this.backRightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4, i, 4, f);
        this.backRightLeg.setPivot(-3.0f, 24 - i, 7.0f);
        this.backLeftLeg = new ModelPart(this, 0, 16);
        this.backLeftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4, i, 4, f);
        this.backLeftLeg.setPivot(3.0f, 24 - i, 7.0f);
        this.frontRightLeg = new ModelPart(this, 0, 16);
        this.frontRightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4, i, 4, f);
        this.frontRightLeg.setPivot(-3.0f, 24 - i, -5.0f);
        this.frontLeftLeg = new ModelPart(this, 0, 16);
        this.frontLeftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4, i, 4, f);
        this.frontLeftLeg.setPivot(3.0f, 24 - i, -5.0f);
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(entity, f, g, h, i, j, k);
        if (this.child) {
            float l = 2.0f;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, this.field_3540 * k, this.field_3537 * k);
            this.head.render(k);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 24.0f * k, 0.0f);
            this.torso.render(k);
            this.backRightLeg.render(k);
            this.backLeftLeg.render(k);
            this.frontRightLeg.render(k);
            this.frontLeftLeg.render(k);
            GlStateManager.popMatrix();
        } else {
            this.head.render(k);
            this.torso.render(k);
            this.backRightLeg.render(k);
            this.backLeftLeg.render(k);
            this.frontRightLeg.render(k);
            this.frontLeftLeg.render(k);
        }
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        this.head.pitch = j * ((float)Math.PI / 180);
        this.head.yaw = i * ((float)Math.PI / 180);
        this.torso.pitch = 1.5707964f;
        this.backRightLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.backLeftLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.frontRightLeg.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.frontLeftLeg.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
    }
}

