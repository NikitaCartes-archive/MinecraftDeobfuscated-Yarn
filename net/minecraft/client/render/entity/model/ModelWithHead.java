/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Cuboid;

@Environment(value=EnvType.CLIENT)
public interface ModelWithHead {
    public Cuboid getHead();

    default public void setHeadAngle(float f) {
        this.getHead().applyTransform(f);
    }
}

