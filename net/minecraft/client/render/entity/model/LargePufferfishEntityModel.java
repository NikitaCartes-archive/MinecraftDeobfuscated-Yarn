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
public class LargePufferfishEntityModel<T extends Entity>
extends EntityModel<T> {
    private final Cuboid field_3493;
    private final Cuboid field_3499;
    private final Cuboid field_3494;
    private final Cuboid field_3490;
    private final Cuboid field_3496;
    private final Cuboid field_3495;
    private final Cuboid field_3489;
    private final Cuboid field_3497;
    private final Cuboid field_3491;
    private final Cuboid field_3487;
    private final Cuboid field_3492;
    private final Cuboid field_3498;
    private final Cuboid field_3488;

    public LargePufferfishEntityModel() {
        this.textureWidth = 32;
        this.textureHeight = 32;
        int i = 22;
        this.field_3493 = new Cuboid(this, 0, 0);
        this.field_3493.addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8);
        this.field_3493.setRotationPoint(0.0f, 22.0f, 0.0f);
        this.field_3499 = new Cuboid(this, 24, 0);
        this.field_3499.addBox(-2.0f, 0.0f, -1.0f, 2, 1, 2);
        this.field_3499.setRotationPoint(-4.0f, 15.0f, -2.0f);
        this.field_3494 = new Cuboid(this, 24, 3);
        this.field_3494.addBox(0.0f, 0.0f, -1.0f, 2, 1, 2);
        this.field_3494.setRotationPoint(4.0f, 15.0f, -2.0f);
        this.field_3490 = new Cuboid(this, 15, 17);
        this.field_3490.addBox(-4.0f, -1.0f, 0.0f, 8, 1, 0);
        this.field_3490.setRotationPoint(0.0f, 14.0f, -4.0f);
        this.field_3490.pitch = 0.7853982f;
        this.field_3496 = new Cuboid(this, 14, 16);
        this.field_3496.addBox(-4.0f, -1.0f, 0.0f, 8, 1, 1);
        this.field_3496.setRotationPoint(0.0f, 14.0f, 0.0f);
        this.field_3495 = new Cuboid(this, 23, 18);
        this.field_3495.addBox(-4.0f, -1.0f, 0.0f, 8, 1, 0);
        this.field_3495.setRotationPoint(0.0f, 14.0f, 4.0f);
        this.field_3495.pitch = -0.7853982f;
        this.field_3489 = new Cuboid(this, 5, 17);
        this.field_3489.addBox(-1.0f, -8.0f, 0.0f, 1, 8, 0);
        this.field_3489.setRotationPoint(-4.0f, 22.0f, -4.0f);
        this.field_3489.yaw = -0.7853982f;
        this.field_3497 = new Cuboid(this, 1, 17);
        this.field_3497.addBox(0.0f, -8.0f, 0.0f, 1, 8, 0);
        this.field_3497.setRotationPoint(4.0f, 22.0f, -4.0f);
        this.field_3497.yaw = 0.7853982f;
        this.field_3491 = new Cuboid(this, 15, 20);
        this.field_3491.addBox(-4.0f, 0.0f, 0.0f, 8, 1, 0);
        this.field_3491.setRotationPoint(0.0f, 22.0f, -4.0f);
        this.field_3491.pitch = -0.7853982f;
        this.field_3492 = new Cuboid(this, 15, 20);
        this.field_3492.addBox(-4.0f, 0.0f, 0.0f, 8, 1, 0);
        this.field_3492.setRotationPoint(0.0f, 22.0f, 0.0f);
        this.field_3487 = new Cuboid(this, 15, 20);
        this.field_3487.addBox(-4.0f, 0.0f, 0.0f, 8, 1, 0);
        this.field_3487.setRotationPoint(0.0f, 22.0f, 4.0f);
        this.field_3487.pitch = 0.7853982f;
        this.field_3498 = new Cuboid(this, 9, 17);
        this.field_3498.addBox(-1.0f, -8.0f, 0.0f, 1, 8, 0);
        this.field_3498.setRotationPoint(-4.0f, 22.0f, 4.0f);
        this.field_3498.yaw = 0.7853982f;
        this.field_3488 = new Cuboid(this, 9, 17);
        this.field_3488.addBox(0.0f, -8.0f, 0.0f, 1, 8, 0);
        this.field_3488.setRotationPoint(4.0f, 22.0f, 4.0f);
        this.field_3488.yaw = -0.7853982f;
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(entity, f, g, h, i, j, k);
        this.field_3493.render(k);
        this.field_3499.render(k);
        this.field_3494.render(k);
        this.field_3490.render(k);
        this.field_3496.render(k);
        this.field_3495.render(k);
        this.field_3489.render(k);
        this.field_3497.render(k);
        this.field_3491.render(k);
        this.field_3492.render(k);
        this.field_3487.render(k);
        this.field_3498.render(k);
        this.field_3488.render(k);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        this.field_3499.roll = -0.2f + 0.4f * MathHelper.sin(h * 0.2f);
        this.field_3494.roll = 0.2f - 0.4f * MathHelper.sin(h * 0.2f);
    }
}

