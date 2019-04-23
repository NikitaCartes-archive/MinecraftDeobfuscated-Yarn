/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.LayerRandomnessSource;
import net.minecraft.world.biome.layer.LayerSampleContext;
import net.minecraft.world.biome.layer.LayerSampler;
import net.minecraft.world.biome.layer.NorthWestCoordinateTransformer;
import net.minecraft.world.biome.layer.ParentedLayer;

public interface SouthEastSamplingLayer
extends ParentedLayer,
NorthWestCoordinateTransformer {
    public int sample(LayerRandomnessSource var1, int var2);

    @Override
    default public int sample(LayerSampleContext<?> layerSampleContext, LayerSampler layerSampler, int i, int j) {
        int k = layerSampler.sample(this.transformX(i + 1), this.transformZ(j + 1));
        return this.sample(layerSampleContext, k);
    }
}

