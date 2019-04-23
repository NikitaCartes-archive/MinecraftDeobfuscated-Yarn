/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class MediumPufferfishEntityModel<T extends Entity>
extends EntityModel<T> {
    private final Cuboid field_3516;
    private final Cuboid field_3518;
    private final Cuboid field_3517;
    private final Cuboid field_3513;
    private final Cuboid field_3511;
    private final Cuboid field_3519;
    private final Cuboid field_3510;
    private final Cuboid field_3512;
    private final Cuboid field_3514;
    private final Cuboid field_3509;
    private final Cuboid field_3515;

    public MediumPufferfishEntityModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        int i = 22;
        this.field_3516 = new Cuboid(this, 12, 22);
        this.field_3516.addBox(-2.5f, -5.0f, -2.5f, 5, 5, 5);
        this.field_3516.setRotationPoint(0.0f, 22.0f, 0.0f);
        this.field_3518 = new Cuboid(this, 24, 0);
        this.field_3518.addBox(-2.0f, 0.0f, 0.0f, 2, 0, 2);
        this.field_3518.setRotationPoint(-2.5f, 17.0f, -1.5f);
        this.field_3517 = new Cuboid(this, 24, 3);
        this.field_3517.addBox(0.0f, 0.0f, 0.0f, 2, 0, 2);
        this.field_3517.setRotationPoint(2.5f, 17.0f, -1.5f);
        this.field_3513 = new Cuboid(this, 15, 16);
        this.field_3513.addBox(-2.5f, -1.0f, 0.0f, 5, 1, 1);
        this.field_3513.setRotationPoint(0.0f, 17.0f, -2.5f);
        this.field_3513.pitch = 0.7853982f;
        this.field_3511 = new Cuboid(this, 10, 16);
        this.field_3511.addBox(-2.5f, -1.0f, -1.0f, 5, 1, 1);
        this.field_3511.setRotationPoint(0.0f, 17.0f, 2.5f);
        this.field_3511.pitch = -0.7853982f;
        this.field_3519 = new Cuboid(this, 8, 16);
        this.field_3519.addBox(-1.0f, -5.0f, 0.0f, 1, 5, 1);
        this.field_3519.setRotationPoint(-2.5f, 22.0f, -2.5f);
        this.field_3519.yaw = -0.7853982f;
        this.field_3510 = new Cuboid(this, 8, 16);
        this.field_3510.addBox(-1.0f, -5.0f, 0.0f, 1, 5, 1);
        this.field_3510.setRotationPoint(-2.5f, 22.0f, 2.5f);
        this.field_3510.yaw = 0.7853982f;
        this.field_3512 = new Cuboid(this, 4, 16);
        this.field_3512.addBox(0.0f, -5.0f, 0.0f, 1, 5, 1);
        this.field_3512.setRotationPoint(2.5f, 22.0f, 2.5f);
        this.field_3512.yaw = -0.7853982f;
        this.field_3514 = new Cuboid(this, 0, 16);
        this.field_3514.addBox(0.0f, -5.0f, 0.0f, 1, 5, 1);
        this.field_3514.setRotationPoint(2.5f, 22.0f, -2.5f);
        this.field_3514.yaw = 0.7853982f;
        this.field_3509 = new Cuboid(this, 8, 22);
        this.field_3509.addBox(0.0f, 0.0f, 0.0f, 1, 1, 1);
        this.field_3509.setRotationPoint(0.5f, 22.0f, 2.5f);
        this.field_3509.pitch = 0.7853982f;
        this.field_3515 = new Cuboid(this, 17, 21);
        this.field_3515.addBox(-2.5f, 0.0f, 0.0f, 5, 1, 1);
        this.field_3515.setRotationPoint(0.0f, 22.0f, -2.5f);
        this.field_3515.pitch = -0.7853982f;
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(entity, f, g, h, i, j, k);
        this.field_3516.render(k);
        this.field_3518.render(k);
        this.field_3517.render(k);
        this.field_3513.render(k);
        this.field_3511.render(k);
        this.field_3519.render(k);
        this.field_3510.render(k);
        this.field_3512.render(k);
        this.field_3514.render(k);
        this.field_3509.render(k);
        this.field_3515.render(k);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        this.field_3518.roll = -0.2f + 0.4f * MathHelper.sin(h * 0.2f);
        this.field_3517.roll = 0.2f - 0.4f * MathHelper.sin(h * 0.2f);
    }
}

