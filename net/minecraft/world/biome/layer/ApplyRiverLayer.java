/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.MergingLayer;
import net.minecraft.world.biome.layer.util.IdentityCoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;

public enum ApplyRiverLayer implements MergingLayer,
IdentityCoordinateTransformer
{
    INSTANCE;


    @Override
    public int sample(LayerRandomnessSource context, LayerSampler sampler1, LayerSampler sampler2, int x, int z) {
        int i = sampler1.sample(this.transformX(x), this.transformZ(z));
        int j = sampler2.sample(this.transformX(x), this.transformZ(z));
        if (BiomeLayers.isOcean(i)) {
            return i;
        }
        if (j == 7) {
            if (i == 12) {
                return 11;
            }
            if (i == 14 || i == 15) {
                return 15;
            }
            return j & 0xFF;
        }
        return i;
    }
}

