/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.passive.PolarBearEntity;

@Environment(value=EnvType.CLIENT)
public class PolarBearEntityModel<T extends PolarBearEntity>
extends QuadrupedEntityModel<T> {
    public PolarBearEntityModel() {
        super(12, 0.0f, true, 16.0f, 4.0f, 2.25f, 2.0f, 24);
        this.textureWidth = 128;
        this.textureHeight = 64;
        this.head = new ModelPart(this, 0, 0);
        this.head.addCuboid(-3.5f, -3.0f, -3.0f, 7.0f, 7.0f, 7.0f, 0.0f);
        this.head.setPivot(0.0f, 10.0f, -16.0f);
        this.head.setTextureOffset(0, 44).addCuboid(-2.5f, 1.0f, -6.0f, 5.0f, 3.0f, 3.0f, 0.0f);
        this.head.setTextureOffset(26, 0).addCuboid(-4.5f, -4.0f, -1.0f, 2.0f, 2.0f, 1.0f, 0.0f);
        ModelPart modelPart = this.head.setTextureOffset(26, 0);
        modelPart.mirror = true;
        modelPart.addCuboid(2.5f, -4.0f, -1.0f, 2.0f, 2.0f, 1.0f, 0.0f);
        this.torso = new ModelPart(this);
        this.torso.setTextureOffset(0, 19).addCuboid(-5.0f, -13.0f, -7.0f, 14.0f, 14.0f, 11.0f, 0.0f);
        this.torso.setTextureOffset(39, 0).addCuboid(-4.0f, -25.0f, -7.0f, 12.0f, 12.0f, 10.0f, 0.0f);
        this.torso.setPivot(-2.0f, 9.0f, 12.0f);
        int i = 10;
        this.backRightLeg = new ModelPart(this, 50, 22);
        this.backRightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 8.0f, 0.0f);
        this.backRightLeg.setPivot(-3.5f, 14.0f, 6.0f);
        this.backLeftLeg = new ModelPart(this, 50, 22);
        this.backLeftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 8.0f, 0.0f);
        this.backLeftLeg.setPivot(3.5f, 14.0f, 6.0f);
        this.frontRightLeg = new ModelPart(this, 50, 40);
        this.frontRightLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 6.0f, 0.0f);
        this.frontRightLeg.setPivot(-2.5f, 14.0f, -7.0f);
        this.frontLeftLeg = new ModelPart(this, 50, 40);
        this.frontLeftLeg.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 6.0f, 0.0f);
        this.frontLeftLeg.setPivot(2.5f, 14.0f, -7.0f);
        this.backRightLeg.pivotX -= 1.0f;
        this.backLeftLeg.pivotX += 1.0f;
        this.backRightLeg.pivotZ += 0.0f;
        this.backLeftLeg.pivotZ += 0.0f;
        this.frontRightLeg.pivotX -= 1.0f;
        this.frontLeftLeg.pivotX += 1.0f;
        this.frontRightLeg.pivotZ -= 1.0f;
        this.frontLeftLeg.pivotZ -= 1.0f;
    }

    @Override
    public void setAngles(T polarBearEntity, float f, float g, float h, float i, float j) {
        super.setAngles(polarBearEntity, f, g, h, i, j);
        float k = h - (float)((PolarBearEntity)polarBearEntity).age;
        float l = ((PolarBearEntity)polarBearEntity).getWarningAnimationProgress(k);
        l *= l;
        float m = 1.0f - l;
        this.torso.pitch = 1.5707964f - l * (float)Math.PI * 0.35f;
        this.torso.pivotY = 9.0f * m + 11.0f * l;
        this.frontRightLeg.pivotY = 14.0f * m - 6.0f * l;
        this.frontRightLeg.pivotZ = -8.0f * m - 4.0f * l;
        this.frontRightLeg.pitch -= l * (float)Math.PI * 0.45f;
        this.frontLeftLeg.pivotY = this.frontRightLeg.pivotY;
        this.frontLeftLeg.pivotZ = this.frontRightLeg.pivotZ;
        this.frontLeftLeg.pitch -= l * (float)Math.PI * 0.45f;
        if (this.isChild) {
            this.head.pivotY = 10.0f * m - 9.0f * l;
            this.head.pivotZ = -16.0f * m - 7.0f * l;
        } else {
            this.head.pivotY = 10.0f * m - 14.0f * l;
            this.head.pivotZ = -16.0f * m - 3.0f * l;
        }
        this.head.pitch += l * (float)Math.PI * 0.15f;
    }
}

