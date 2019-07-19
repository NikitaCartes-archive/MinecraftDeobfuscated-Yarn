/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;

@Environment(value=EnvType.CLIENT)
public class BannerBlockEntityModel
extends Model {
    private final ModelPart field_3309;
    private final ModelPart field_3311;
    private final ModelPart field_3310;

    public BannerBlockEntityModel() {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.field_3309 = new ModelPart(this, 0, 0);
        this.field_3309.addCuboid(-10.0f, 0.0f, -2.0f, 20, 40, 1, 0.0f);
        this.field_3311 = new ModelPart(this, 44, 0);
        this.field_3311.addCuboid(-1.0f, -30.0f, -1.0f, 2, 42, 2, 0.0f);
        this.field_3310 = new ModelPart(this, 0, 42);
        this.field_3310.addCuboid(-10.0f, -32.0f, -1.0f, 20, 2, 2, 0.0f);
    }

    public void method_2793() {
        this.field_3309.pivotY = -32.0f;
        this.field_3309.render(0.0625f);
        this.field_3311.render(0.0625f);
        this.field_3310.render(0.0625f);
    }

    public ModelPart method_2791() {
        return this.field_3311;
    }

    public ModelPart method_2792() {
        return this.field_3309;
    }
}

