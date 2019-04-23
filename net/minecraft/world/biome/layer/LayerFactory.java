/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.LayerSampler;

public interface LayerFactory<A extends LayerSampler> {
    public A make();
}

