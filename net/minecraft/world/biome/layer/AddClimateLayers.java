/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.type.IdentitySamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public class AddClimateLayers {

    public static enum AddSpecialBiomesLayer implements IdentitySamplingLayer
    {
        INSTANCE;


        @Override
        public int sample(LayerRandomnessSource context, int value) {
            if (!BiomeLayers.isShallowOcean(value) && context.nextInt(13) == 0) {
                value |= 1 + context.nextInt(15) << 8 & 0xF00;
            }
            return value;
        }
    }

    public static enum AddCoolBiomesLayer implements CrossSamplingLayer
    {
        INSTANCE;


        @Override
        public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
            if (center == 4 && (n == 1 || e == 1 || w == 1 || s == 1 || n == 2 || e == 2 || w == 2 || s == 2)) {
                return 3;
            }
            return center;
        }
    }

    public static enum AddTemperateBiomesLayer implements CrossSamplingLayer
    {
        INSTANCE;


        @Override
        public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
            if (center == 1 && (n == 3 || e == 3 || w == 3 || s == 3 || n == 4 || e == 4 || w == 4 || s == 4)) {
                return 2;
            }
            return center;
        }
    }
}

