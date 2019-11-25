/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer.type;

import net.minecraft.world.biome.layer.util.CoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;

public interface MergingLayer
extends CoordinateTransformer {
    default public <R extends LayerSampler> LayerFactory<R> create(LayerSampleContext<R> layerSampleContext, LayerFactory<R> layerFactory, LayerFactory<R> layerFactory2) {
        return () -> {
            Object layerSampler = layerFactory.make();
            Object layerSampler2 = layerFactory2.make();
            return layerSampleContext.createSampler((i, j) -> {
                layerSampleContext.initSeed(i, j);
                return this.sample(layerSampleContext, (LayerSampler)layerSampler, (LayerSampler)layerSampler2, i, j);
            }, layerSampler, layerSampler2);
        };
    }

    public int sample(LayerRandomnessSource var1, LayerSampler var2, LayerSampler var3, int var4, int var5);
}

