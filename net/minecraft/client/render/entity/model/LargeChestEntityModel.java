/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.ChestEntityModel;

@Environment(value=EnvType.CLIENT)
public class LargeChestEntityModel
extends ChestEntityModel {
    public LargeChestEntityModel() {
        this.lid = new ModelPart(this, 0, 0).setTextureSize(128, 64);
        this.lid.addCuboid(0.0f, -5.0f, -14.0f, 30, 5, 14, 0.0f);
        this.lid.pivotX = 1.0f;
        this.lid.pivotY = 7.0f;
        this.lid.pivotZ = 15.0f;
        this.hatch = new ModelPart(this, 0, 0).setTextureSize(128, 64);
        this.hatch.addCuboid(-1.0f, -2.0f, -15.0f, 2, 4, 1, 0.0f);
        this.hatch.pivotX = 16.0f;
        this.hatch.pivotY = 7.0f;
        this.hatch.pivotZ = 15.0f;
        this.base = new ModelPart(this, 0, 19).setTextureSize(128, 64);
        this.base.addCuboid(0.0f, 0.0f, 0.0f, 30, 10, 14, 0.0f);
        this.base.pivotX = 1.0f;
        this.base.pivotY = 6.0f;
        this.base.pivotZ = 1.0f;
    }
}

