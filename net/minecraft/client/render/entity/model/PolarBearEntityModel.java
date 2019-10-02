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
        this.body = new ModelPart(this);
        this.body.setTextureOffset(0, 19).addCuboid(-5.0f, -13.0f, -7.0f, 14.0f, 14.0f, 11.0f, 0.0f);
        this.body.setTextureOffset(39, 0).addCuboid(-4.0f, -25.0f, -7.0f, 12.0f, 12.0f, 10.0f, 0.0f);
        this.body.setPivot(-2.0f, 9.0f, 12.0f);
        int i = 10;
        this.leg1 = new ModelPart(this, 50, 22);
        this.leg1.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 8.0f, 0.0f);
        this.leg1.setPivot(-3.5f, 14.0f, 6.0f);
        this.leg2 = new ModelPart(this, 50, 22);
        this.leg2.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 8.0f, 0.0f);
        this.leg2.setPivot(3.5f, 14.0f, 6.0f);
        this.leg3 = new ModelPart(this, 50, 40);
        this.leg3.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 6.0f, 0.0f);
        this.leg3.setPivot(-2.5f, 14.0f, -7.0f);
        this.leg4 = new ModelPart(this, 50, 40);
        this.leg4.addCuboid(-2.0f, 0.0f, -2.0f, 4.0f, 10.0f, 6.0f, 0.0f);
        this.leg4.setPivot(2.5f, 14.0f, -7.0f);
        this.leg1.pivotX -= 1.0f;
        this.leg2.pivotX += 1.0f;
        this.leg1.pivotZ += 0.0f;
        this.leg2.pivotZ += 0.0f;
        this.leg3.pivotX -= 1.0f;
        this.leg4.pivotX += 1.0f;
        this.leg3.pivotZ -= 1.0f;
        this.leg4.pivotZ -= 1.0f;
    }

    public void method_17114(T polarBearEntity, float f, float g, float h, float i, float j, float k) {
        super.setAngles(polarBearEntity, f, g, h, i, j, k);
        float l = h - (float)((PolarBearEntity)polarBearEntity).age;
        float m = ((PolarBearEntity)polarBearEntity).getWarningAnimationProgress(l);
        m *= m;
        float n = 1.0f - m;
        this.body.pitch = 1.5707964f - m * (float)Math.PI * 0.35f;
        this.body.pivotY = 9.0f * n + 11.0f * m;
        this.leg3.pivotY = 14.0f * n - 6.0f * m;
        this.leg3.pivotZ = -8.0f * n - 4.0f * m;
        this.leg3.pitch -= m * (float)Math.PI * 0.45f;
        this.leg4.pivotY = this.leg3.pivotY;
        this.leg4.pivotZ = this.leg3.pivotZ;
        this.leg4.pitch -= m * (float)Math.PI * 0.45f;
        if (this.isChild) {
            this.head.pivotY = 10.0f * n - 9.0f * m;
            this.head.pivotZ = -16.0f * n - 7.0f * m;
        } else {
            this.head.pivotY = 10.0f * n - 14.0f * m;
            this.head.pivotZ = -16.0f * n - 3.0f * m;
        }
        this.head.pitch += m * (float)Math.PI * 0.15f;
    }
}

