/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.LayerFactory;
import net.minecraft.world.biome.layer.LayerRandomnessSource;
import net.minecraft.world.biome.layer.LayerSampleContext;
import net.minecraft.world.biome.layer.LayerSampler;

public interface InitLayer {
    default public <R extends LayerSampler> LayerFactory<R> create(LayerSampleContext<R> layerSampleContext) {
        return () -> layerSampleContext.createSampler((i, j) -> {
            layerSampleContext.initSeed(i, j);
            return this.sample(layerSampleContext, i, j);
        });
    }

    public int sample(LayerRandomnessSource var1, int var2, int var3);
}

