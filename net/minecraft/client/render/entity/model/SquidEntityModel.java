/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public class SquidEntityModel<T extends Entity>
extends EntityModel<T> {
    private final Cuboid field_3575;
    private final Cuboid[] field_3574 = new Cuboid[8];

    public SquidEntityModel() {
        int i = -16;
        this.field_3575 = new Cuboid(this, 0, 0);
        this.field_3575.addBox(-6.0f, -8.0f, -6.0f, 12, 16, 12);
        this.field_3575.rotationPointY += 8.0f;
        for (int j = 0; j < this.field_3574.length; ++j) {
            this.field_3574[j] = new Cuboid(this, 48, 0);
            double d = (double)j * Math.PI * 2.0 / (double)this.field_3574.length;
            float f = (float)Math.cos(d) * 5.0f;
            float g = (float)Math.sin(d) * 5.0f;
            this.field_3574[j].addBox(-1.0f, 0.0f, -1.0f, 2, 18, 2);
            this.field_3574[j].rotationPointX = f;
            this.field_3574[j].rotationPointZ = g;
            this.field_3574[j].rotationPointY = 15.0f;
            d = (double)j * Math.PI * -2.0 / (double)this.field_3574.length + 1.5707963267948966;
            this.field_3574[j].yaw = (float)d;
        }
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j, float k) {
        for (Cuboid cuboid : this.field_3574) {
            cuboid.pitch = h;
        }
    }

    @Override
    public void render(T entity, float f, float g, float h, float i, float j, float k) {
        this.setAngles(entity, f, g, h, i, j, k);
        this.field_3575.render(k);
        for (Cuboid cuboid : this.field_3574) {
            cuboid.render(k);
        }
    }
}

