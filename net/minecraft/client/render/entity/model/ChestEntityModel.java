/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;

@Environment(value=EnvType.CLIENT)
public class ChestEntityModel
extends Model {
    protected Cuboid lid = new Cuboid(this, 0, 0).setTextureSize(64, 64);
    protected Cuboid base;
    protected Cuboid hatch;

    public ChestEntityModel() {
        this.lid.addBox(0.0f, -5.0f, -14.0f, 14, 5, 14, 0.0f);
        this.lid.rotationPointX = 1.0f;
        this.lid.rotationPointY = 7.0f;
        this.lid.rotationPointZ = 15.0f;
        this.hatch = new Cuboid(this, 0, 0).setTextureSize(64, 64);
        this.hatch.addBox(-1.0f, -2.0f, -15.0f, 2, 4, 1, 0.0f);
        this.hatch.rotationPointX = 8.0f;
        this.hatch.rotationPointY = 7.0f;
        this.hatch.rotationPointZ = 15.0f;
        this.base = new Cuboid(this, 0, 19).setTextureSize(64, 64);
        this.base.addBox(0.0f, 0.0f, 0.0f, 14, 10, 14, 0.0f);
        this.base.rotationPointX = 1.0f;
        this.base.rotationPointY = 6.0f;
        this.base.rotationPointZ = 1.0f;
    }

    public void method_2799() {
        this.hatch.pitch = this.lid.pitch;
        this.lid.render(0.0625f);
        this.hatch.render(0.0625f);
        this.base.render(0.0625f);
    }

    public Cuboid method_2798() {
        return this.lid;
    }
}

