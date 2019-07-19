/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.PolarBearEntity;

@Environment(value=EnvType.CLIENT)
public class PolarBearEntityModel<T extends PolarBearEntity>
extends QuadrupedEntityModel<T> {
    public PolarBearEntityModel() {
        super(12, 0.0f);
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-3.5f, -3.0f, -3.0f, 7, 7, 7, 0.0f);
        this.head.setPivot(0.0f, 10.0f, -16.0f);
        this.head.setTextureOffset(0, 44).addCuboid(-2.5f, 1.0f, -6.0f, 5, 3, 3, 0.0f);
        this.head.setTextureOffset(26, 0).addCuboid(-4.5f, -4.0f, -1.0f, 2, 2, 1, 0.0f);
        ModelPart modelPart = this.head.setTextureOffset(26, 0);
        modelPart.mirror = true;
        modelPart.addCuboid(2.5f, -4.0f, -1.0f, 2, 2, 1, 0.0f);
        this.torso = new ModelPart(this);
        this.torso.setTextureOffset(0, 19).addCuboid(-5.0f, -13.0f, -7.0f, 14, 14, 11, 0.0f);
        this.torso.setTextureOffset(39, 0).addCuboid(-4.0f, -25.0f, -7.0f, 12, 12, 10, 0.0f);
        this.torso.setPivot(-2.0f, 9.0f, 12.0f);
        int i = 10;
        this.backRightLeg = new ModelPart(this, 50, 22);
        this.backRightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4, 10, 8, 0.0f);
        this.backRightLeg.setPivot(-3.5f, 14.0f, 6.0f);
        this.backLeftLeg = new ModelPart(this, 50, 22);
        this.backLeftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4, 10, 8, 0.0f);
        this.backLeftLeg.setPivot(3.5f, 14.0f, 6.0f);
        this.frontRightLeg = new ModelPart(this, 50, 40);
        this.frontRightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4, 10, 6, 0.0f);
        this.frontRightLeg.setPivot(-2.5f, 14.0f, -7.0f);
        this.frontLeftLeg = new ModelPart(this, 50, 40);
        this.frontLeftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4, 10, 6, 0.0f);
        this.frontLeftLeg.setPivot(2.5f, 14.0f, -7.0f);
        this.backRightLeg.pivotX -= 1.0f;
        this.backLeftLeg.pivotX += 1.0f;
        this.backRightLeg.pivotZ += 0.0f;
        this.backLeftLeg.pivotZ += 0.0f;
        this.frontRightLeg.pivotX -= 1.0f;
        this.frontLeftLeg.pivotX += 1.0f;
        this.frontRightLeg.pivotZ -= 1.0f;
        this.frontLeftLeg.pivotZ -= 1.0f;
        this.field_3537 += 2.0f;
    }

    @Override
    public void render(T polarBearEntity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(polarBearEntity, f, g, h, i, j, k);
        if (this.child) {
            float l = 2.0f;
            this.field_3540 = 16.0f;
            this.field_3537 = 4.0f;
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.6666667f, 0.6666667f, 0.6666667f);
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
    public void setAngles(T polarBearEntity, float f, float g, float h, float i, float j, float k) {
        super.setAngles(polarBearEntity, f, g, h, i, j, k);
        float l = h - (float)((PolarBearEntity)polarBearEntity).age;
        float m = ((PolarBearEntity)polarBearEntity).getWarningAnimationProgress(l);
        m *= m;
        float n = 1.0f - m;
        this.torso.pitch = 1.5707964f - m * (float)Math.PI * 0.35f;
        this.torso.pivotY = 9.0f * n + 11.0f * m;
        this.frontRightLeg.pivotY = 14.0f * n - 6.0f * m;
        this.frontRightLeg.pivotZ = -8.0f * n - 4.0f * m;
        this.frontRightLeg.pitch -= m * (float)Math.PI * 0.45f;
        this.frontLeftLeg.pivotY = this.frontRightLeg.pivotY;
        this.frontLeftLeg.pivotZ = this.frontRightLeg.pivotZ;
        this.frontLeftLeg.pitch -= m * (float)Math.PI * 0.45f;
        if (this.child) {
            this.head.pivotY = 10.0f * n - 9.0f * m;
            this.head.pivotZ = -16.0f * n - 7.0f * m;
        } else {
            this.head.pivotY = 10.0f * n - 14.0f * m;
            this.head.pivotZ = -16.0f * n - 3.0f * m;
        }
        this.head.pitch += m * (float)Math.PI * 0.15f;
    }

    @Override
    public /* synthetic */ void setAngles(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles((T)((PolarBearEntity)entity), f, g, h, i, j, k);
    }

    @Override
    public /* synthetic */ void render(Entity entity, float f, float g, float h, float i, float j, float k) {
        this.render((T)((PolarBearEntity)entity), f, g, h, i, j, k);
    }
}

