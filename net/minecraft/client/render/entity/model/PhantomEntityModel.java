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
public class PhantomEntityModel<T extends Entity>
extends EntityModel<T> {
    private final Cuboid field_3475;
    private final Cuboid field_3477;
    private final Cuboid field_3476;
    private final Cuboid field_3474;
    private final Cuboid field_3472;
    private final Cuboid field_3478;
    private final Cuboid field_3471;
    private final Cuboid field_3473;

    public PhantomEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.field_3475 = new Cuboid(this, 0, 8);
        this.field_3475.addBox(-3.0f, -2.0f, -8.0f, 5, 3, 9);
        this.field_3471 = new Cuboid(this, 3, 20);
        this.field_3471.addBox(-2.0f, 0.0f, 0.0f, 3, 2, 6);
        this.field_3471.setRotationPoint(0.0f, -2.0f, 1.0f);
        this.field_3475.addChild(this.field_3471);
        this.field_3473 = new Cuboid(this, 4, 29);
        this.field_3473.addBox(-1.0f, 0.0f, 0.0f, 1, 1, 6);
        this.field_3473.setRotationPoint(0.0f, 0.5f, 6.0f);
        this.field_3471.addChild(this.field_3473);
        this.field_3477 = new Cuboid(this, 23, 12);
        this.field_3477.addBox(0.0f, 0.0f, 0.0f, 6, 2, 9);
        this.field_3477.setRotationPoint(2.0f, -2.0f, -8.0f);
        this.field_3476 = new Cuboid(this, 16, 24);
        this.field_3476.addBox(0.0f, 0.0f, 0.0f, 13, 1, 9);
        this.field_3476.setRotationPoint(6.0f, 0.0f, 0.0f);
        this.field_3477.addChild(this.field_3476);
        this.field_3474 = new Cuboid(this, 23, 12);
        this.field_3474.mirror = true;
        this.field_3474.addBox(-6.0f, 0.0f, 0.0f, 6, 2, 9);
        this.field_3474.setRotationPoint(-3.0f, -2.0f, -8.0f);
        this.field_3472 = new Cuboid(this, 16, 24);
        this.field_3472.mirror = true;
        this.field_3472.addBox(-13.0f, 0.0f, 0.0f, 13, 1, 9);
        this.field_3472.setRotationPoint(-6.0f, 0.0f, 0.0f);
        this.field_3474.addChild(this.field_3472);
        this.field_3477.roll = 0.1f;
        this.field_3476.roll = 0.1f;
        this.field_3474.roll = -0.1f;
        this.field_3472.roll = -0.1f;
        this.field_3475.pitch = -0.1f;
        this.field_3478 = new Cuboid(this, 0, 0);
        this.field_3478.addBox(-4.0f, -2.0f, -5.0f, 7, 3, 5);
        this.field_3478.setRotationPoint(0.0f, 1.0f, -7.0f);
        this.field_3478.pitch = 0.2f;
        this.field_3475.addChild(this.field_3478);
        this.field_3475.addChild(this.field_3477);
        this.field_3475.addChild(this.field_3474);
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        this.field_3475.render(k);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        float l = ((float)(((Entity)entity).getEntityId() * 3) + h) * 0.13f;
        float m = 16.0f;
        this.field_3477.roll = MathHelper.cos(l) * 16.0f * ((float)Math.PI / 180);
        this.field_3476.roll = MathHelper.cos(l) * 16.0f * ((float)Math.PI / 180);
        this.field_3474.roll = -this.field_3477.roll;
        this.field_3472.roll = -this.field_3476.roll;
        this.field_3471.pitch = -(5.0f + MathHelper.cos(l * 2.0f) * 5.0f) * ((float)Math.PI / 180);
        this.field_3473.pitch = -(5.0f + MathHelper.cos(l * 2.0f) * 5.0f) * ((float)Math.PI / 180);
    }
}

