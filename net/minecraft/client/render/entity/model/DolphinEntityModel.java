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
public class DolphinEntityModel<T extends Entity>
extends EntityModel<T> {
    private final Cuboid field_4656;
    private final Cuboid field_4658;
    private final Cuboid field_4657;
    private final Cuboid field_4655;

    public DolphinEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        float f = 18.0f;
        float g = -8.0f;
        this.field_4658 = new Cuboid(this, 22, 0);
        this.field_4658.addBox(-4.0f, -7.0f, 0.0f, 8, 7, 13);
        this.field_4658.setRotationPoint(0.0f, 22.0f, -5.0f);
        Cuboid cuboid = new Cuboid(this, 51, 0);
        cuboid.addBox(-0.5f, 0.0f, 8.0f, 1, 4, 5);
        cuboid.pitch = 1.0471976f;
        this.field_4658.addChild(cuboid);
        Cuboid cuboid2 = new Cuboid(this, 48, 20);
        cuboid2.mirror = true;
        cuboid2.addBox(-0.5f, -4.0f, 0.0f, 1, 4, 7);
        cuboid2.setRotationPoint(2.0f, -2.0f, 4.0f);
        cuboid2.pitch = 1.0471976f;
        cuboid2.roll = 2.0943952f;
        this.field_4658.addChild(cuboid2);
        Cuboid cuboid3 = new Cuboid(this, 48, 20);
        cuboid3.addBox(-0.5f, -4.0f, 0.0f, 1, 4, 7);
        cuboid3.setRotationPoint(-2.0f, -2.0f, 4.0f);
        cuboid3.pitch = 1.0471976f;
        cuboid3.roll = -2.0943952f;
        this.field_4658.addChild(cuboid3);
        this.field_4657 = new Cuboid(this, 0, 19);
        this.field_4657.addBox(-2.0f, -2.5f, 0.0f, 4, 5, 11);
        this.field_4657.setRotationPoint(0.0f, -2.5f, 11.0f);
        this.field_4657.pitch = -0.10471976f;
        this.field_4658.addChild(this.field_4657);
        this.field_4655 = new Cuboid(this, 19, 20);
        this.field_4655.addBox(-5.0f, -0.5f, 0.0f, 10, 1, 6);
        this.field_4655.setRotationPoint(0.0f, 0.0f, 9.0f);
        this.field_4655.pitch = 0.0f;
        this.field_4657.addChild(this.field_4655);
        this.field_4656 = new Cuboid(this, 0, 0);
        this.field_4656.addBox(-4.0f, -3.0f, -3.0f, 8, 7, 6);
        this.field_4656.setRotationPoint(0.0f, -4.0f, -3.0f);
        Cuboid cuboid4 = new Cuboid(this, 0, 13);
        cuboid4.addBox(-1.0f, 2.0f, -7.0f, 2, 2, 4);
        this.field_4656.addChild(cuboid4);
        this.field_4658.addChild(this.field_4656);
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        this.field_4658.render(k);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        this.field_4658.pitch = j * ((float)Math.PI / 180);
        this.field_4658.yaw = i * ((float)Math.PI / 180);
        if (Entity.squaredHorizontalLength(((Entity)entity).getVelocity()) > 1.0E-7) {
            this.field_4658.pitch += -0.05f + -0.05f * MathHelper.cos(h * 0.3f);
            this.field_4657.pitch = -0.1f * MathHelper.cos(h * 0.3f);
            this.field_4655.pitch = -0.2f * MathHelper.cos(h * 0.3f);
        }
    }
}

