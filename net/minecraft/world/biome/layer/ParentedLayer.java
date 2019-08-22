/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.CoordinateTransformer;
import net.minecraft.world.biome.layer.LayerFactory;
import net.minecraft.world.biome.layer.LayerSampleContext;
import net.minecraft.world.biome.layer.LayerSampler;

public interface ParentedLayer
extends CoordinateTransformer {
    default public <R extends LayerSampler> LayerFactory<R> create(LayerSampleContext<R> layerSampleContext, LayerFactory<R> layerFactory) {
        return () -> {
            Object layerSampler = layerFactory.make();
            return layerSampleContext.createSampler((i, j) -> {
                layerSampleContext.initSeed(i, j);
                return this.sample(layerSampleContext, (LayerSampler)layerSampler, i, j);
            }, layerSampler);
        };
    }

    public int sample(LayerSampleContext<?> var1, LayerSampler var2, int var3, int var4);
}

