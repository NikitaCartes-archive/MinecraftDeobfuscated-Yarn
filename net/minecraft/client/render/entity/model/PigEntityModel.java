/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.QuadrupedEntityModel;
import net.minecraft.entity.Entity;

@Environment(value=EnvType.CLIENT)
public class PigEntityModel<T extends Entity>
extends QuadrupedEntityModel<T> {
    public PigEntityModel() {
        this(0.0f);
    }

    public PigEntityModel(float f) {
        super(6, f);
        this.head.setTextureOffset(16, 16).addBox(-2.0f, 0.0f, -9.0f, 4, 3, 1, f);
        this.field_3540 = 4.0f;
    }
}

