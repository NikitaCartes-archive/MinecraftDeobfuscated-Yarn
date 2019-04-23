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
public class ChickenEntityModel<T extends Entity>
extends EntityModel<T> {
    private final Cuboid head;
    private final Cuboid field_3346;
    private final Cuboid field_3345;
    private final Cuboid field_3343;
    private final Cuboid field_3341;
    private final Cuboid field_3347;
    private final Cuboid field_3340;
    private final Cuboid field_3342;

    public ChickenEntityModel() {
        int i = 16;
        this.head = new Cuboid(this, 0, 0);
        this.head.addBox(-2.0f, -6.0f, -2.0f, 4, 6, 3, 0.0f);
        this.head.setRotationPoint(0.0f, 15.0f, -4.0f);
        this.field_3340 = new Cuboid(this, 14, 0);
        this.field_3340.addBox(-2.0f, -4.0f, -4.0f, 4, 2, 2, 0.0f);
        this.field_3340.setRotationPoint(0.0f, 15.0f, -4.0f);
        this.field_3342 = new Cuboid(this, 14, 4);
        this.field_3342.addBox(-1.0f, -2.0f, -3.0f, 2, 2, 2, 0.0f);
        this.field_3342.setRotationPoint(0.0f, 15.0f, -4.0f);
        this.field_3346 = new Cuboid(this, 0, 9);
        this.field_3346.addBox(-3.0f, -4.0f, -3.0f, 6, 8, 6, 0.0f);
        this.field_3346.setRotationPoint(0.0f, 16.0f, 0.0f);
        this.field_3345 = new Cuboid(this, 26, 0);
        this.field_3345.addBox(-1.0f, 0.0f, -3.0f, 3, 5, 3);
        this.field_3345.setRotationPoint(-2.0f, 19.0f, 1.0f);
        this.field_3343 = new Cuboid(this, 26, 0);
        this.field_3343.addBox(-1.0f, 0.0f, -3.0f, 3, 5, 3);
        this.field_3343.setRotationPoint(1.0f, 19.0f, 1.0f);
        this.field_3341 = new Cuboid(this, 24, 13);
        this.field_3341.addBox(0.0f, 0.0f, -3.0f, 1, 4, 6);
        this.field_3341.setRotationPoint(-4.0f, 13.0f, 0.0f);
        this.field_3347 = new Cuboid(this, 24, 13);
        this.field_3347.addBox(-1.0f, 0.0f, -3.0f, 1, 4, 6);
        this.field_3347.setRotationPoint(4.0f, 13.0f, 0.0f);
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(entity, f, g, h, i, j, k);
        if (this.isChild) {
            float l = 2.0f;
            GlStateManager.pushMatrix();
            GlStateManager.translatef(0.0f, 5.0f * k, 2.0f * k);
            this.head.render(k);
            this.field_3340.render(k);
            this.field_3342.render(k);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scalef(0.5f, 0.5f, 0.5f);
            GlStateManager.translatef(0.0f, 24.0f * k, 0.0f);
            this.field_3346.render(k);
            this.field_3345.render(k);
            this.field_3343.render(k);
            this.field_3341.render(k);
            this.field_3347.render(k);
            GlStateManager.popMatrix();
        } else {
            this.head.render(k);
            this.field_3340.render(k);
            this.field_3342.render(k);
            this.field_3346.render(k);
            this.field_3345.render(k);
            this.field_3343.render(k);
            this.field_3341.render(k);
            this.field_3347.render(k);
        }
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        this.head.pitch = j * ((float)Math.PI / 180);
        this.head.yaw = i * ((float)Math.PI / 180);
        this.field_3340.pitch = this.head.pitch;
        this.field_3340.yaw = this.head.yaw;
        this.field_3342.pitch = this.head.pitch;
        this.field_3342.yaw = this.head.yaw;
        this.field_3346.pitch = 1.5707964f;
        this.field_3345.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.field_3343.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.field_3341.roll = h;
        this.field_3347.roll = -h;
    }
}

