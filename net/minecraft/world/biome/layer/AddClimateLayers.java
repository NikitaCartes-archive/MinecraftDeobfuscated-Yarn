/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.CrossSamplingLayer;
import net.minecraft.world.biome.layer.IdentitySamplingLayer;
import net.minecraft.world.biome.layer.LayerRandomnessSource;

public class AddClimateLayers {

    public static enum AddSpecialBiomesLayer implements IdentitySamplingLayer
    {
        INSTANCE;


        @Override
        public int sample(LayerRandomnessSource layerRandomnessSource, int i) {
            if (!BiomeLayers.isShallowOcean(i) && layerRandomnessSource.nextInt(13) == 0) {
                i |= 1 + layerRandomnessSource.nextInt(15) << 8 & 0xF00;
            }
            return i;
        }
    }

    public static enum AddCoolBiomesLayer implements CrossSamplingLayer
    {
        INSTANCE;


        @Override
        public int sample(LayerRandomnessSource layerRandomnessSource, int i, int j, int k, int l, int m) {
            if (m == 4 && (i == 1 || j == 1 || l == 1 || k == 1 || i == 2 || j == 2 || l == 2 || k == 2)) {
                return 3;
            }
            return m;
        }
    }

    public static enum AddTemperateBiomesLayer implements CrossSamplingLayer
    {
        INSTANCE;


        @Override
        public int sample(LayerRandomnessSource layerRandomnessSource, int i, int j, int k, int l, int m) {
            if (m == 1 && (i == 3 || j == 3 || l == 3 || k == 3 || i == 4 || j == 4 || l == 4 || k == 4)) {
                return 2;
            }
            return m;
        }
    }
}

