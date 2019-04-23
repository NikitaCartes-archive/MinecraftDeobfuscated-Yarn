/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;

@Environment(value=EnvType.CLIENT)
public class BannerBlockEntityModel
extends Model {
    private final Cuboid field_3309;
    private final Cuboid field_3311;
    private final Cuboid field_3310;

    public BannerBlockEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.field_3309 = new Cuboid(this, 0, 0);
        this.field_3309.addBox(-10.0f, 0.0f, -2.0f, 20, 40, 1, 0.0f);
        this.field_3311 = new Cuboid(this, 44, 0);
        this.field_3311.addBox(-1.0f, -30.0f, -1.0f, 2, 42, 2, 0.0f);
        this.field_3310 = new Cuboid(this, 0, 42);
        this.field_3310.addBox(-10.0f, -32.0f, -1.0f, 20, 2, 2, 0.0f);
    }

    public void method_2793() {
        this.field_3309.rotationPointY = -32.0f;
        this.field_3309.render(0.0625f);
        this.field_3311.render(0.0625f);
        this.field_3310.render(0.0625f);
    }

    public Cuboid method_2791() {
        return this.field_3311;
    }

    public Cuboid method_2792() {
        return this.field_3309;
    }
}

