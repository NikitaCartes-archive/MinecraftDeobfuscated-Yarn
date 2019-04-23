/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;

@Environment(value=EnvType.CLIENT)
public class SkullEntityModel
extends Model {
    protected final Cuboid field_3564;

    public SkullEntityModel() {
        this(0, 35, 64, 64);
    }

    public SkullEntityModel(int i, int j, int k, int l) {
        this.textureWidth = k;
        this.textureHeight = l;
        this.field_3564 = new Cuboid(this, i, j);
        this.field_3564.addBox(-4.0f, -8.0f, -4.0f, 8, 8, 8, 0.0f);
        this.field_3564.setRotationPoint(0.0f, 0.0f, 0.0f);
    }

    public void setRotationAngles(float f, float g, float h, float i, float j, float k) {
        this.field_3564.yaw = i * ((float)Math.PI / 180);
        this.field_3564.pitch = j * ((float)Math.PI / 180);
        this.field_3564.render(k);
    }
}

