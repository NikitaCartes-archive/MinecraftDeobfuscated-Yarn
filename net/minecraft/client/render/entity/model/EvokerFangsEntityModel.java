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
public class EvokerFangsEntityModel<T extends Entity>
extends EntityModel<T> {
    private final Cuboid field_3374 = new Cuboid(this, 0, 0);
    private final Cuboid field_3376;
    private final Cuboid field_3375;

    public EvokerFangsEntityModel() {
        this.field_3374.setRotationPoint(-5.0f, 22.0f, -5.0f);
        this.field_3374.addBox(0.0f, 0.0f, 0.0f, 10, 12, 10);
        this.field_3376 = new Cuboid(this, 40, 0);
        this.field_3376.setRotationPoint(1.5f, 22.0f, -4.0f);
        this.field_3376.addBox(0.0f, 0.0f, 0.0f, 4, 14, 8);
        this.field_3375 = new Cuboid(this, 40, 0);
        this.field_3375.setRotationPoint(-1.5f, 22.0f, 4.0f);
        this.field_3375.addBox(0.0f, 0.0f, 0.0f, 4, 14, 8);
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        float l = f * 2.0f;
        if (l > 1.0f) {
            l = 1.0f;
        }
        l = 1.0f - l * l * l;
        this.field_3376.roll = (float)Math.PI - l * 0.35f * (float)Math.PI;
        this.field_3375.roll = (float)Math.PI + l * 0.35f * (float)Math.PI;
        this.field_3375.yaw = (float)Math.PI;
        float m = (f + MathHelper.sin(f * 2.7f)) * 0.6f * 12.0f;
        this.field_3375.rotationPointY = this.field_3376.rotationPointY = 24.0f - m;
        this.field_3374.rotationPointY = this.field_3376.rotationPointY;
        this.field_3374.render(k);
        this.field_3376.render(k);
        this.field_3375.render(k);
    }
}

