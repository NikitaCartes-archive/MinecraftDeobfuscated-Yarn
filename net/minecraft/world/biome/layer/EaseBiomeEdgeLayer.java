/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.BiomeLayers;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;

public enum EaseBiomeEdgeLayer implements CrossSamplingLayer
{
    INSTANCE;

    private static final int DESERT_ID;
    private static final int MOUNTAINS_ID;
    private static final int WOODED_MOUNTAINS_ID;
    private static final int SNOWY_TUNDRA_ID;
    private static final int JUNGLE_ID;
    private static final int BAMBOO_JUNGLE_ID;
    private static final int JUNGLE_EDGE_ID;
    private static final int BADLANDS_ID;
    private static final int BADLANDS_PLATEAU_ID;
    private static final int WOODED_BADLANDS_PLATEAU_ID;
    private static final int PLAINS_ID;
    private static final int GIANT_TREE_TAIGA_ID;
    private static final int MOUNTAIN_EDGE_ID;
    private static final int SWAMP_ID;
    private static final int TAIGA_ID;
    private static final int SNOWY_TAIGA_ID;

    @Override
    public int sample(LayerRandomnessSource layerRandomnessSource, int i, int j, int k, int l, int m) {
        int[] is = new int[1];
        if (this.method_15841(is, i, j, k, l, m, MOUNTAINS_ID, MOUNTAIN_EDGE_ID) || this.method_15840(is, i, j, k, l, m, WOODED_BADLANDS_PLATEAU_ID, BADLANDS_ID) || this.method_15840(is, i, j, k, l, m, BADLANDS_PLATEAU_ID, BADLANDS_ID) || this.method_15840(is, i, j, k, l, m, GIANT_TREE_TAIGA_ID, TAIGA_ID)) {
            return is[0];
        }
        if (m == DESERT_ID && (i == SNOWY_TUNDRA_ID || j == SNOWY_TUNDRA_ID || l == SNOWY_TUNDRA_ID || k == SNOWY_TUNDRA_ID)) {
            return WOODED_MOUNTAINS_ID;
        }
        if (m == SWAMP_ID) {
            if (i == DESERT_ID || j == DESERT_ID || l == DESERT_ID || k == DESERT_ID || i == SNOWY_TAIGA_ID || j == SNOWY_TAIGA_ID || l == SNOWY_TAIGA_ID || k == SNOWY_TAIGA_ID || i == SNOWY_TUNDRA_ID || j == SNOWY_TUNDRA_ID || l == SNOWY_TUNDRA_ID || k == SNOWY_TUNDRA_ID) {
                return PLAINS_ID;
            }
            if (i == JUNGLE_ID || k == JUNGLE_ID || j == JUNGLE_ID || l == JUNGLE_ID || i == BAMBOO_JUNGLE_ID || k == BAMBOO_JUNGLE_ID || j == BAMBOO_JUNGLE_ID || l == BAMBOO_JUNGLE_ID) {
                return JUNGLE_EDGE_ID;
            }
        }
        return m;
    }

    private boolean method_15841(int[] is, int i, int j, int k, int l, int m, int n, int o) {
        if (!BiomeLayers.areSimilar(m, n)) {
            return false;
        }
        is[0] = this.method_15839(i, n) && this.method_15839(j, n) && this.method_15839(l, n) && this.method_15839(k, n) ? m : o;
        return true;
    }

    private boolean method_15840(int[] is, int i, int j, int k, int l, int m, int n, int o) {
        if (m != n) {
            return false;
        }
        is[0] = BiomeLayers.areSimilar(i, n) && BiomeLayers.areSimilar(j, n) && BiomeLayers.areSimilar(l, n) && BiomeLayers.areSimilar(k, n) ? m : o;
        return true;
    }

    private boolean method_15839(int i, int j) {
        if (BiomeLayers.areSimilar(i, j)) {
            return true;
        }
        Biome biome = (Biome)Registry.BIOME.get(i);
        Biome biome2 = (Biome)Registry.BIOME.get(j);
        if (biome != null && biome2 != null) {
            Biome.TemperatureGroup temperatureGroup2;
            Biome.TemperatureGroup temperatureGroup = biome.getTemperatureGroup();
            return temperatureGroup == (temperatureGroup2 = biome2.getTemperatureGroup()) || temperatureGroup == Biome.TemperatureGroup.MEDIUM || temperatureGroup2 == Biome.TemperatureGroup.MEDIUM;
        }
        return false;
    }

    static {
        DESERT_ID = Registry.BIOME.getRawId(Biomes.DESERT);
        MOUNTAINS_ID = Registry.BIOME.getRawId(Biomes.MOUNTAINS);
        WOODED_MOUNTAINS_ID = Registry.BIOME.getRawId(Biomes.WOODED_MOUNTAINS);
        SNOWY_TUNDRA_ID = Registry.BIOME.getRawId(Biomes.SNOWY_TUNDRA);
        JUNGLE_ID = Registry.BIOME.getRawId(Biomes.JUNGLE);
        BAMBOO_JUNGLE_ID = Registry.BIOME.getRawId(Biomes.BAMBOO_JUNGLE);
        JUNGLE_EDGE_ID = Registry.BIOME.getRawId(Biomes.JUNGLE_EDGE);
        BADLANDS_ID = Registry.BIOME.getRawId(Biomes.BADLANDS);
        BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.BADLANDS_PLATEAU);
        WOODED_BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.WOODED_BADLANDS_PLATEAU);
        PLAINS_ID = Registry.BIOME.getRawId(Biomes.PLAINS);
        GIANT_TREE_TAIGA_ID = Registry.BIOME.getRawId(Biomes.GIANT_TREE_TAIGA);
        MOUNTAIN_EDGE_ID = Registry.BIOME.getRawId(Biomes.MOUNTAIN_EDGE);
        SWAMP_ID = Registry.BIOME.getRawId(Biomes.SWAMP);
        TAIGA_ID = Registry.BIOME.getRawId(Biomes.TAIGA);
        SNOWY_TAIGA_ID = Registry.BIOME.getRawId(Biomes.SNOWY_TAIGA);
    }
}

