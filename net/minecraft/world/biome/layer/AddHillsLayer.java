/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.type.MergingLayer;
import net.minecraft.world.biome.layer.util.BiomeLayers;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.layer.util.NorthWestCoordinateTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum AddHillsLayer implements MergingLayer,
NorthWestCoordinateTransformer
{
    INSTANCE;

    private static final Logger LOGGER;
    private static final int BIRCH_FOREST_ID;
    private static final int BIRCH_FOREST_HILLS_ID;
    private static final int DESERT_ID;
    private static final int DESERT_HILLS_ID;
    private static final int MOUNTAINS_ID;
    private static final int WOODED_MOUNTAINS_ID;
    private static final int FOREST_ID;
    private static final int WOODED_HILLS_ID;
    private static final int SNOWY_TUNDRA_ID;
    private static final int SNOWY_MOUNTAINS_ID;
    private static final int JUNGLE_ID;
    private static final int JUNGLE_HILLS_ID;
    private static final int BAMBOO_JUNGLE_ID;
    private static final int BAMBOO_JUNGLE_HILLS_ID;
    private static final int BADLANDS_ID;
    private static final int WOODED_BADLANDS_PLATEAU_ID;
    private static final int PLAINS_ID;
    private static final int GIANT_TREE_TAIGA_ID;
    private static final int GIANT_TREE_TAIGA_HILLS_ID;
    private static final int DARK_FOREST_ID;
    private static final int SAVANNA_ID;
    private static final int SAVANNA_PLATEAU_ID;
    private static final int TAIGA_ID;
    private static final int SNOWY_TAIGA_ID;
    private static final int SNOWY_TAIGA_HILLS_ID;
    private static final int TAIGA_HILLS_ID;

    @Override
    public int sample(LayerRandomnessSource layerRandomnessSource, LayerSampler layerSampler, LayerSampler layerSampler2, int i, int j) {
        Biome biome;
        int k = layerSampler.sample(this.transformX(i + 1), this.transformZ(j + 1));
        int l = layerSampler2.sample(this.transformX(i + 1), this.transformZ(j + 1));
        if (k > 255) {
            LOGGER.debug("old! {}", (Object)k);
        }
        int m = (l - 2) % 29;
        if (!(BiomeLayers.isShallowOcean(k) || l < 2 || m != 1 || (biome = (Biome)Registry.BIOME.get(k)) != null && biome.hasParent())) {
            Biome biome2 = Biome.getParentBiome(biome);
            return biome2 == null ? k : Registry.BIOME.getRawId(biome2);
        }
        if (layerRandomnessSource.nextInt(3) == 0 || m == 0) {
            int n = k;
            if (k == DESERT_ID) {
                n = DESERT_HILLS_ID;
            } else if (k == FOREST_ID) {
                n = WOODED_HILLS_ID;
            } else if (k == BIRCH_FOREST_ID) {
                n = BIRCH_FOREST_HILLS_ID;
            } else if (k == DARK_FOREST_ID) {
                n = PLAINS_ID;
            } else if (k == TAIGA_ID) {
                n = TAIGA_HILLS_ID;
            } else if (k == GIANT_TREE_TAIGA_ID) {
                n = GIANT_TREE_TAIGA_HILLS_ID;
            } else if (k == SNOWY_TAIGA_ID) {
                n = SNOWY_TAIGA_HILLS_ID;
            } else if (k == PLAINS_ID) {
                n = layerRandomnessSource.nextInt(3) == 0 ? WOODED_HILLS_ID : FOREST_ID;
            } else if (k == SNOWY_TUNDRA_ID) {
                n = SNOWY_MOUNTAINS_ID;
            } else if (k == JUNGLE_ID) {
                n = JUNGLE_HILLS_ID;
            } else if (k == BAMBOO_JUNGLE_ID) {
                n = BAMBOO_JUNGLE_HILLS_ID;
            } else if (k == BiomeLayers.OCEAN_ID) {
                n = BiomeLayers.DEEP_OCEAN_ID;
            } else if (k == BiomeLayers.LUKEWARM_OCEAN_ID) {
                n = BiomeLayers.DEEP_LUKEWARM_OCEAN_ID;
            } else if (k == BiomeLayers.COLD_OCEAN_ID) {
                n = BiomeLayers.DEEP_COLD_OCEAN_ID;
            } else if (k == BiomeLayers.FROZEN_OCEAN_ID) {
                n = BiomeLayers.DEEP_FROZEN_OCEAN_ID;
            } else if (k == MOUNTAINS_ID) {
                n = WOODED_MOUNTAINS_ID;
            } else if (k == SAVANNA_ID) {
                n = SAVANNA_PLATEAU_ID;
            } else if (BiomeLayers.areSimilar(k, WOODED_BADLANDS_PLATEAU_ID)) {
                n = BADLANDS_ID;
            } else if ((k == BiomeLayers.DEEP_OCEAN_ID || k == BiomeLayers.DEEP_LUKEWARM_OCEAN_ID || k == BiomeLayers.DEEP_COLD_OCEAN_ID || k == BiomeLayers.DEEP_FROZEN_OCEAN_ID) && layerRandomnessSource.nextInt(3) == 0) {
                int n2 = n = layerRandomnessSource.nextInt(2) == 0 ? PLAINS_ID : FOREST_ID;
            }
            if (m == 0 && n != k) {
                Biome biome2 = Biome.getParentBiome((Biome)Registry.BIOME.get(n));
                int n3 = n = biome2 == null ? k : Registry.BIOME.getRawId(biome2);
            }
            if (n != k) {
                int o = 0;
                if (BiomeLayers.areSimilar(layerSampler.sample(this.transformX(i + 1), this.transformZ(j + 0)), k)) {
                    ++o;
                }
                if (BiomeLayers.areSimilar(layerSampler.sample(this.transformX(i + 2), this.transformZ(j + 1)), k)) {
                    ++o;
                }
                if (BiomeLayers.areSimilar(layerSampler.sample(this.transformX(i + 0), this.transformZ(j + 1)), k)) {
                    ++o;
                }
                if (BiomeLayers.areSimilar(layerSampler.sample(this.transformX(i + 1), this.transformZ(j + 2)), k)) {
                    ++o;
                }
                if (o >= 3) {
                    return n;
                }
            }
        }
        return k;
    }

    static {
        LOGGER = LogManager.getLogger();
        BIRCH_FOREST_ID = Registry.BIOME.getRawId(Biomes.BIRCH_FOREST);
        BIRCH_FOREST_HILLS_ID = Registry.BIOME.getRawId(Biomes.BIRCH_FOREST_HILLS);
        DESERT_ID = Registry.BIOME.getRawId(Biomes.DESERT);
        DESERT_HILLS_ID = Registry.BIOME.getRawId(Biomes.DESERT_HILLS);
        MOUNTAINS_ID = Registry.BIOME.getRawId(Biomes.MOUNTAINS);
        WOODED_MOUNTAINS_ID = Registry.BIOME.getRawId(Biomes.WOODED_MOUNTAINS);
        FOREST_ID = Registry.BIOME.getRawId(Biomes.FOREST);
        WOODED_HILLS_ID = Registry.BIOME.getRawId(Biomes.WOODED_HILLS);
        SNOWY_TUNDRA_ID = Registry.BIOME.getRawId(Biomes.SNOWY_TUNDRA);
        SNOWY_MOUNTAINS_ID = Registry.BIOME.getRawId(Biomes.SNOWY_MOUNTAINS);
        JUNGLE_ID = Registry.BIOME.getRawId(Biomes.JUNGLE);
        JUNGLE_HILLS_ID = Registry.BIOME.getRawId(Biomes.JUNGLE_HILLS);
        BAMBOO_JUNGLE_ID = Registry.BIOME.getRawId(Biomes.BAMBOO_JUNGLE);
        BAMBOO_JUNGLE_HILLS_ID = Registry.BIOME.getRawId(Biomes.BAMBOO_JUNGLE_HILLS);
        BADLANDS_ID = Registry.BIOME.getRawId(Biomes.BADLANDS);
        WOODED_BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.WOODED_BADLANDS_PLATEAU);
        PLAINS_ID = Registry.BIOME.getRawId(Biomes.PLAINS);
        GIANT_TREE_TAIGA_ID = Registry.BIOME.getRawId(Biomes.GIANT_TREE_TAIGA);
        GIANT_TREE_TAIGA_HILLS_ID = Registry.BIOME.getRawId(Biomes.GIANT_TREE_TAIGA_HILLS);
        DARK_FOREST_ID = Registry.BIOME.getRawId(Biomes.DARK_FOREST);
        SAVANNA_ID = Registry.BIOME.getRawId(Biomes.SAVANNA);
        SAVANNA_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.SAVANNA_PLATEAU);
        TAIGA_ID = Registry.BIOME.getRawId(Biomes.TAIGA);
        SNOWY_TAIGA_ID = Registry.BIOME.getRawId(Biomes.SNOWY_TAIGA);
        SNOWY_TAIGA_HILLS_ID = Registry.BIOME.getRawId(Biomes.SNOWY_TAIGA_HILLS);
        TAIGA_HILLS_ID = Registry.BIOME.getRawId(Biomes.TAIGA_HILLS);
    }
}

