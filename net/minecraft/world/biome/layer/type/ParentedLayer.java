/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer.type;

import net.minecraft.world.biome.layer.util.CoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;

public interface ParentedLayer
extends CoordinateTransformer {
    default public <R extends LayerSampler> LayerFactory<R> create(LayerSampleContext<R> context, LayerFactory<R> parent) {
        return () -> {
            Object layerSampler = parent.make();
            return context.createSampler((x, z) -> {
                context.initSeed(x, z);
                return this.sample(context, (LayerSampler)layerSampler, x, z);
            }, layerSampler);
        };
    }

    public int sample(LayerSampleContext<?> var1, LayerSampler var2, int var3, int var4);
}

