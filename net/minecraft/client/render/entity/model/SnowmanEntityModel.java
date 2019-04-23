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
public class SnowmanEntityModel<T extends Entity>
extends EntityModel<T> {
    private final Cuboid field_3567;
    private final Cuboid field_3569;
    private final Cuboid field_3568;
    private final Cuboid field_3566;
    private final Cuboid field_3565;

    public SnowmanEntityModel() {
        float f = 4.0f;
        float g = 0.0f;
        this.field_3568 = new Cuboid(this, 0, 0).setTextureSize(64, 64);
        this.field_3568.addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, -0.5f);
        this.field_3568.setRotationPoint(0.0f, 4.0f, 0.0f);
        this.field_3566 = new Cuboid(this, 32, 0).setTextureSize(64, 64);
        this.field_3566.addBox(-1.0f, 0.0f, -1.0f, 12, 2, 2, -0.5f);
        this.field_3566.setRotationPoint(0.0f, 6.0f, 0.0f);
        this.field_3565 = new Cuboid(this, 32, 0).setTextureSize(64, 64);
        this.field_3565.addBox(-1.0f, 0.0f, -1.0f, 12, 2, 2, -0.5f);
        this.field_3565.setRotationPoint(0.0f, 6.0f, 0.0f);
        this.field_3567 = new Cuboid(this, 0, 16).setTextureSize(64, 64);
        this.field_3567.addBox(-5.0f, -10.0f, -5.0f, 10, 10, 10, -0.5f);
        this.field_3567.setRotationPoint(0.0f, 13.0f, 0.0f);
        this.field_3569 = new Cuboid(this, 0, 36).setTextureSize(64, 64);
        this.field_3569.addBox(-6.0f, -12.0f, -6.0f, 12, 12, 12, -0.5f);
        this.field_3569.setRotationPoint(0.0f, 24.0f, 0.0f);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        super.setAngles(entity, f, g, h, i, j, k);
        this.field_3568.yaw = i * ((float)Math.PI / 180);
        this.field_3568.pitch = j * ((float)Math.PI / 180);
        this.field_3567.yaw = i * ((float)Math.PI / 180) * 0.25f;
        float l = MathHelper.sin(this.field_3567.yaw);
        float m = MathHelper.cos(this.field_3567.yaw);
        this.field_3566.roll = 1.0f;
        this.field_3565.roll = -1.0f;
        this.field_3566.yaw = 0.0f + this.field_3567.yaw;
        this.field_3565.yaw = (float)Math.PI + this.field_3567.yaw;
        this.field_3566.rotationPointX = m * 5.0f;
        this.field_3566.rotationPointZ = -l * 5.0f;
        this.field_3565.rotationPointX = -m * 5.0f;
        this.field_3565.rotationPointZ = l * 5.0f;
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(entity, f, g, h, i, j, k);
        this.field_3567.render(k);
        this.field_3569.render(k);
        this.field_3568.render(k);
        this.field_3566.render(k);
        this.field_3565.render(k);
    }

    public Cuboid method_2834() {
        return this.field_3568;
    }
}

