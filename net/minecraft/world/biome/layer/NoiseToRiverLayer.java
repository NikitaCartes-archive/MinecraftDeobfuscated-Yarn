/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum NoiseToRiverLayer implements CrossSamplingLayer
{
    INSTANCE;


    @Override
    public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
        int i = NoiseToRiverLayer.isValidForRiver(center);
        if (i == NoiseToRiverLayer.isValidForRiver(w) && i == NoiseToRiverLayer.isValidForRiver(n) && i == NoiseToRiverLayer.isValidForRiver(e) && i == NoiseToRiverLayer.isValidForRiver(s)) {
            return -1;
        }
        return 7;
    }

    private static int isValidForRiver(int value) {
        if (value >= 2) {
            return 2 + (value & 1);
        }
        return value;
    }
}

