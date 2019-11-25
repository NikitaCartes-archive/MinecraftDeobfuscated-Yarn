/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum NoiseToRiverLayer implements CrossSamplingLayer
{
    INSTANCE;

    public static final int RIVER_ID;

    @Override
    public int sample(LayerRandomnessSource layerRandomnessSource, int i, int j, int k, int l, int m) {
        int n = NoiseToRiverLayer.isValidForRiver(m);
        if (n == NoiseToRiverLayer.isValidForRiver(l) && n == NoiseToRiverLayer.isValidForRiver(i) && n == NoiseToRiverLayer.isValidForRiver(j) && n == NoiseToRiverLayer.isValidForRiver(k)) {
            return -1;
        }
        return RIVER_ID;
    }

    private static int isValidForRiver(int i) {
        if (i >= 2) {
            return 2 + (i & 1);
        }
        return i;
    }

    static {
        RIVER_ID = Registry.BIOME.getRawId(Biomes.RIVER);
    }
}

