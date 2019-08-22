/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.LayerRandomnessSource;
import net.minecraft.world.biome.layer.LayerSampleContext;
import net.minecraft.world.biome.layer.LayerSampler;
import net.minecraft.world.biome.layer.NorthWestCoordinateTransformer;
import net.minecraft.world.biome.layer.ParentedLayer;

public interface DiagonalCrossSamplingLayer
extends ParentedLayer,
NorthWestCoordinateTransformer {
    public int sample(LayerRandomnessSource var1, int var2, int var3, int var4, int var5, int var6);

    @Override
    default public int sample(LayerSampleContext<?> layerSampleContext, LayerSampler layerSampler, int i, int j) {
        return this.sample(layerSampleContext, layerSampler.sample(this.transformX(i + 0), this.transformZ(j + 2)), layerSampler.sample(this.transformX(i + 2), this.transformZ(j + 2)), layerSampler.sample(this.transformX(i + 2), this.transformZ(j + 0)), layerSampler.sample(this.transformX(i + 0), this.transformZ(j + 0)), layerSampler.sample(this.transformX(i + 1), this.transformZ(j + 1)));
    }
}

