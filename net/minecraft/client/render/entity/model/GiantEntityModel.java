/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.AbstractZombieModel;
import net.minecraft.entity.mob.GiantEntity;

@Environment(value=EnvType.CLIENT)
public class GiantEntityModel
extends AbstractZombieModel<GiantEntity> {
    public GiantEntityModel() {
        this(0.0f, false);
    }

    public GiantEntityModel(float f, boolean bl) {
        super(RenderLayer::getEntitySolid, f, 0.0f, 64, bl ? 32 : 64);
    }

    public boolean method_17792(GiantEntity giantEntity) {
        return false;
    }
}

