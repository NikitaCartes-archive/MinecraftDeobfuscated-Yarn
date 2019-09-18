/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;

@Environment(value=EnvType.CLIENT)
public class ShieldEntityModel
extends Model {
    private final ModelPart field_3550;
    private final ModelPart field_3551;

    public ShieldEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.field_3550 = new ModelPart(this, 0, 0);
        this.field_3550.addCuboid(-6.0f, -11.0f, -2.0f, 12.0f, 22.0f, 1.0f, 0.0f);
        this.field_3551 = new ModelPart(this, 26, 0);
        this.field_3551.addCuboid(-1.0f, -3.0f, -1.0f, 2.0f, 6.0f, 6.0f, 0.0f);
    }

    public void renderItem() {
        this.field_3550.render(0.0625f);
        this.field_3551.render(0.0625f);
    }
}

