/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer.util;

import net.minecraft.world.biome.layer.util.LayerSampler;

public interface LayerFactory<A extends LayerSampler> {
    public A make();
}

