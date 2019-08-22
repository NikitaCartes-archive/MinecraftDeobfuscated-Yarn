/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.IdentityCoordinateTransformer;
import net.minecraft.world.biome.layer.LayerRandomnessSource;
import net.minecraft.world.biome.layer.LayerSampleContext;
import net.minecraft.world.biome.layer.LayerSampler;
import net.minecraft.world.biome.layer.ParentedLayer;

public interface IdentitySamplingLayer
extends ParentedLayer,
IdentityCoordinateTransformer {
    public int sample(LayerRandomnessSource var1, int var2);

    @Override
    default public int sample(LayerSampleContext<?> layerSampleContext, LayerSampler layerSampler, int i, int j) {
        return this.sample(layerSampleContext, layerSampler.sample(this.transformX(i), this.transformZ(j)));
    }
}

