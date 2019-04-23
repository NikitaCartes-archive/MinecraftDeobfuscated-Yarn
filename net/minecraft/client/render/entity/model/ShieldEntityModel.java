/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.model.Model;

@Environment(value=EnvType.CLIENT)
public class ShieldEntityModel
extends Model {
    private final Cuboid field_3550;
    private final Cuboid field_3551;

    public ShieldEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.field_3550 = new Cuboid(this, 0, 0);
        this.field_3550.addBox(-6.0f, -11.0f, -2.0f, 12, 22, 1, 0.0f);
        this.field_3551 = new Cuboid(this, 26, 0);
        this.field_3551.addBox(-1.0f, -3.0f, -1.0f, 2, 6, 6, 0.0f);
    }

    public void renderItem() {
        this.field_3550.render(0.0625f);
        this.field_3551.render(0.0625f);
    }
}

