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
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class QuadrupedEntityModel<T extends Entity>
extends EntityModel<T> {
    protected Cuboid head = new Cuboid(this, 0, 0);
    protected Cuboid body;
    protected Cuboid leg1;
    protected Cuboid leg2;
    protected Cuboid leg3;
    protected Cuboid leg4;
    protected float field_3540 = 8.0f;
    protected float field_3537 = 4.0f;

    public QuadrupedEntityModel(int i, float f) {
        this.head.addBox(-4.0f, -4.0f, -8.0f, 8, 8, 8, f);
        this.head.setRotationPoint(0.0f, 18 - i, -6.0f);
        this.body = new Cuboid(this, 28, 8);
        this.body.addBox(-5.0f, -10.0f, -7.0f, 10, 16, 8, f);
        this.body.setRotationPoint(0.0f, 17 - i, 2.0f);
        this.leg1 = new Cuboid(this, 0, 16);
        this.leg1.addBox(-2.0f, 0.0f, -2.0f, 4, i, 4, f);
        this.leg1.setRotationPoint(-3.0f, 24 - i, 7.0f);
        this.leg2 = new Cuboid(this, 0, 16);
        this.leg2.addBox(-2.0f, 0.0f, -2.0f, 4, i, 4, f);
        this.leg2.setRotationPoint(3.0f, 24 - i, 7.0f);
        this.leg3 = new Cuboid(this, 0, 16);
        this.leg3.addBox(-2.0f, 0.0f, -2.0f, 4, i, 4, f);
        this.leg3.setRotationPoint(-3.0f, 24 - i, -5.0f);
        this.leg4 = new Cuboid(this, 0, 16);
        this.leg4.addBox(-2.0f, 0.0f, -2.0f, 4, i, 4, f);
        this.leg4.setRotationPoint(3.0f, 24 - i, -5.0f);
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(entity, f, g, h, i, j, k);
        if (this.isChild) {
            float l = 2.0f;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, this.field_3540 * k, this.field_3537 * k);
            this.head.render(k);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 24.0f * k, 0.0f);
            this.body.render(k);
            this.leg1.render(k);
            this.leg2.render(k);
            this.leg3.render(k);
            this.leg4.render(k);
            GlStateManager.popMatrix();
        } else {
            this.head.render(k);
            this.body.render(k);
            this.leg1.render(k);
            this.leg2.render(k);
            this.leg3.render(k);
            this.leg4.render(k);
        }
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        this.head.pitch = j * ((float)Math.PI / 180);
        this.head.yaw = i * ((float)Math.PI / 180);
        this.body.pitch = 1.5707964f;
        this.leg1.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.leg2.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.leg3.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.leg4.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
    }
}

