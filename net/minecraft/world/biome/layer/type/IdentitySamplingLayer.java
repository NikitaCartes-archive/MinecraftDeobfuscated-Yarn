/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer.type;

import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.IdentityCoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;

public interface IdentitySamplingLayer
extends ParentedLayer,
IdentityCoordinateTransformer {
    public int sample(LayerRandomnessSource var1, int var2);

    @Override
    default public int sample(LayerSampleContext<?> layerSampleContext, LayerSampler layerSampler, int i, int j) {
        return this.sample(layerSampleContext, layerSampler.sample(this.transformX(i), this.transformZ(j)));
    }
}

