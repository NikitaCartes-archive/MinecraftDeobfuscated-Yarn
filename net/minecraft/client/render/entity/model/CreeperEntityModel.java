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
public class CreeperEntityModel<T extends Entity>
extends EntityModel<T> {
    private final Cuboid head;
    private final Cuboid field_3362;
    private final Cuboid field_3361;
    private final Cuboid field_3359;
    private final Cuboid field_3358;
    private final Cuboid field_3363;
    private final Cuboid field_3357;

    public CreeperEntityModel() {
        this(0.0f);
    }

    public CreeperEntityModel(float f) {
        int i = 6;
        this.head = new Cuboid(this, 0, 0);
        this.head.addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, f);
        this.head.setRotationPoint(0.0f, 6.0f, 0.0f);
        this.field_3362 = new Cuboid(this, 32, 0);
        this.field_3362.addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, f + 0.5f);
        this.field_3362.setRotationPoint(0.0f, 6.0f, 0.0f);
        this.field_3361 = new Cuboid(this, 16, 16);
        this.field_3361.addBox(-4.0f, 0.0f, -2.0f, 8, 12, 4, f);
        this.field_3361.setRotationPoint(0.0f, 6.0f, 0.0f);
        this.field_3359 = new Cuboid(this, 0, 16);
        this.field_3359.addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, f);
        this.field_3359.setRotationPoint(-2.0f, 18.0f, 4.0f);
        this.field_3358 = new Cuboid(this, 0, 16);
        this.field_3358.addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, f);
        this.field_3358.setRotationPoint(2.0f, 18.0f, 4.0f);
        this.field_3363 = new Cuboid(this, 0, 16);
        this.field_3363.addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, f);
        this.field_3363.setRotationPoint(-2.0f, 18.0f, -4.0f);
        this.field_3357 = new Cuboid(this, 0, 16);
        this.field_3357.addBox(-2.0f, 0.0f, -2.0f, 4, 6, 4, f);
        this.field_3357.setRotationPoint(2.0f, 18.0f, -4.0f);
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(entity, f, g, h, i, j, k);
        this.head.render(k);
        this.field_3361.render(k);
        this.field_3359.render(k);
        this.field_3358.render(k);
        this.field_3363.render(k);
        this.field_3357.render(k);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        this.head.yaw = i * ((float)Math.PI / 180);
        this.head.pitch = j * ((float)Math.PI / 180);
        this.field_3359.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
        this.field_3358.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.field_3363.pitch = MathHelper.cos(f * 0.6662f + (float)Math.PI) * 1.4f * g;
        this.field_3357.pitch = MathHelper.cos(f * 0.6662f) * 1.4f * g;
    }
}

