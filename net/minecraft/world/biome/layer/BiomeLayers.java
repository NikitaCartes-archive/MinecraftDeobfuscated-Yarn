/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.function.LongFunction;
import net.minecraft.util.Util;
import net.minecraft.world.biome.BiomeIds;
import net.minecraft.world.biome.layer.AddBambooJungleLayer;
import net.minecraft.world.biome.layer.AddBaseBiomesLayer;
import net.minecraft.world.biome.layer.AddClimateLayers;
import net.minecraft.world.biome.layer.AddColdClimatesLayer;
import net.minecraft.world.biome.layer.AddDeepOceanLayer;
import net.minecraft.world.biome.layer.AddEdgeBiomesLayer;
import net.minecraft.world.biome.layer.AddHillsLayer;
import net.minecraft.world.biome.layer.AddIslandLayer;
import net.minecraft.world.biome.layer.AddMushroomIslandLayer;
import net.minecraft.world.biome.layer.AddSunflowerPlainsLayer;
import net.minecraft.world.biome.layer.ApplyOceanTemperatureLayer;
import net.minecraft.world.biome.layer.ApplyRiverLayer;
import net.minecraft.world.biome.layer.ContinentLayer;
import net.minecraft.world.biome.layer.EaseBiomeEdgeLayer;
import net.minecraft.world.biome.layer.IncreaseEdgeCurvatureLayer;
import net.minecraft.world.biome.layer.NoiseToRiverLayer;
import net.minecraft.world.biome.layer.OceanTemperatureLayer;
import net.minecraft.world.biome.layer.ScaleLayer;
import net.minecraft.world.biome.layer.SimpleLandNoiseLayer;
import net.minecraft.world.biome.layer.SmoothLayer;
import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.CachingLayerContext;
import net.minecraft.world.biome.layer.util.CachingLayerSampler;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.source.BiomeLayerSampler;

public class BiomeLayers
implements BiomeIds {
    protected static final int field_31799 = 1;
    protected static final int field_31800 = 2;
    protected static final int field_31801 = 3;
    protected static final int field_31802 = 4;
    protected static final int field_31803 = 3840;
    protected static final int field_31804 = 8;
    private static final Int2IntMap BY_CATEGORY = Util.make(new Int2IntOpenHashMap(), map -> {
        BiomeLayers.putCategory(map, Category.BEACH, BiomeIds.BEACH);
        BiomeLayers.putCategory(map, Category.BEACH, BiomeIds.SNOWY_BEACH);
        BiomeLayers.putCategory(map, Category.DESERT, BiomeIds.DESERT);
        BiomeLayers.putCategory(map, Category.DESERT, BiomeIds.DESERT_HILLS);
        BiomeLayers.putCategory(map, Category.DESERT, BiomeIds.DESERT_LAKES);
        BiomeLayers.putCategory(map, Category.EXTREME_HILLS, BiomeIds.GRAVELLY_MOUNTAINS);
        BiomeLayers.putCategory(map, Category.EXTREME_HILLS, BiomeIds.MODIFIED_GRAVELLY_MOUNTAINS);
        BiomeLayers.putCategory(map, Category.EXTREME_HILLS, BiomeIds.MOUNTAIN_EDGE);
        BiomeLayers.putCategory(map, Category.EXTREME_HILLS, BiomeIds.MOUNTAINS);
        BiomeLayers.putCategory(map, Category.EXTREME_HILLS, BiomeIds.WOODED_MOUNTAINS);
        BiomeLayers.putCategory(map, Category.FOREST, BiomeIds.BIRCH_FOREST);
        BiomeLayers.putCategory(map, Category.FOREST, BiomeIds.BIRCH_FOREST_HILLS);
        BiomeLayers.putCategory(map, Category.FOREST, BiomeIds.DARK_FOREST);
        BiomeLayers.putCategory(map, Category.FOREST, BiomeIds.DARK_FOREST_HILLS);
        BiomeLayers.putCategory(map, Category.FOREST, BiomeIds.FLOWER_FOREST);
        BiomeLayers.putCategory(map, Category.FOREST, BiomeIds.FOREST);
        BiomeLayers.putCategory(map, Category.FOREST, BiomeIds.TALL_BIRCH_FOREST);
        BiomeLayers.putCategory(map, Category.FOREST, BiomeIds.TALL_BIRCH_HILLS);
        BiomeLayers.putCategory(map, Category.FOREST, BiomeIds.WOODED_HILLS);
        BiomeLayers.putCategory(map, Category.ICY, BiomeIds.ICE_SPIKES);
        BiomeLayers.putCategory(map, Category.ICY, BiomeIds.SNOWY_MOUNTAINS);
        BiomeLayers.putCategory(map, Category.ICY, BiomeIds.SNOWY_TUNDRA);
        BiomeLayers.putCategory(map, Category.JUNGLE, BiomeIds.BAMBOO_JUNGLE);
        BiomeLayers.putCategory(map, Category.JUNGLE, BiomeIds.BAMBOO_JUNGLE_HILLS);
        BiomeLayers.putCategory(map, Category.JUNGLE, BiomeIds.JUNGLE);
        BiomeLayers.putCategory(map, Category.JUNGLE, BiomeIds.JUNGLE_EDGE);
        BiomeLayers.putCategory(map, Category.JUNGLE, BiomeIds.JUNGLE_HILLS);
        BiomeLayers.putCategory(map, Category.JUNGLE, BiomeIds.MODIFIED_JUNGLE);
        BiomeLayers.putCategory(map, Category.JUNGLE, BiomeIds.MODIFIED_JUNGLE_EDGE);
        BiomeLayers.putCategory(map, Category.MESA, BiomeIds.BADLANDS);
        BiomeLayers.putCategory(map, Category.MESA, BiomeIds.ERODED_BADLANDS);
        BiomeLayers.putCategory(map, Category.MESA, BiomeIds.MODIFIED_BADLANDS_PLATEAU);
        BiomeLayers.putCategory(map, Category.MESA, BiomeIds.MODIFIED_WOODED_BADLANDS_PLATEAU);
        BiomeLayers.putCategory(map, Category.BADLANDS_PLATEAU, BiomeIds.BADLANDS_PLATEAU);
        BiomeLayers.putCategory(map, Category.BADLANDS_PLATEAU, BiomeIds.WOODED_BADLANDS_PLATEAU);
        BiomeLayers.putCategory(map, Category.MUSHROOM, BiomeIds.MUSHROOM_FIELDS);
        BiomeLayers.putCategory(map, Category.MUSHROOM, BiomeIds.MUSHROOM_FIELD_SHORE);
        BiomeLayers.putCategory(map, Category.NONE, BiomeIds.STONE_SHORE);
        BiomeLayers.putCategory(map, Category.OCEAN, BiomeIds.COLD_OCEAN);
        BiomeLayers.putCategory(map, Category.OCEAN, BiomeIds.DEEP_COLD_OCEAN);
        BiomeLayers.putCategory(map, Category.OCEAN, BiomeIds.DEEP_FROZEN_OCEAN);
        BiomeLayers.putCategory(map, Category.OCEAN, BiomeIds.DEEP_LUKEWARM_OCEAN);
        BiomeLayers.putCategory(map, Category.OCEAN, BiomeIds.DEEP_OCEAN);
        BiomeLayers.putCategory(map, Category.OCEAN, BiomeIds.DEEP_WARM_OCEAN);
        BiomeLayers.putCategory(map, Category.OCEAN, BiomeIds.FROZEN_OCEAN);
        BiomeLayers.putCategory(map, Category.OCEAN, BiomeIds.LUKEWARM_OCEAN);
        BiomeLayers.putCategory(map, Category.OCEAN, BiomeIds.OCEAN);
        BiomeLayers.putCategory(map, Category.OCEAN, BiomeIds.WARM_OCEAN);
        BiomeLayers.putCategory(map, Category.PLAINS, BiomeIds.PLAINS);
        BiomeLayers.putCategory(map, Category.PLAINS, BiomeIds.SUNFLOWER_PLAINS);
        BiomeLayers.putCategory(map, Category.RIVER, BiomeIds.FROZEN_RIVER);
        BiomeLayers.putCategory(map, Category.RIVER, BiomeIds.RIVER);
        BiomeLayers.putCategory(map, Category.SAVANNA, BiomeIds.SAVANNA);
        BiomeLayers.putCategory(map, Category.SAVANNA, BiomeIds.SAVANNA_PLATEAU);
        BiomeLayers.putCategory(map, Category.SAVANNA, BiomeIds.SHATTERED_SAVANNA);
        BiomeLayers.putCategory(map, Category.SAVANNA, BiomeIds.SHATTERED_SAVANNA_PLATEAU);
        BiomeLayers.putCategory(map, Category.SWAMP, BiomeIds.SWAMP);
        BiomeLayers.putCategory(map, Category.SWAMP, BiomeIds.SWAMP_HILLS);
        BiomeLayers.putCategory(map, Category.TAIGA, BiomeIds.GIANT_SPRUCE_TAIGA);
        BiomeLayers.putCategory(map, Category.TAIGA, BiomeIds.GIANT_SPRUCE_TAIGA_HILLS);
        BiomeLayers.putCategory(map, Category.TAIGA, BiomeIds.GIANT_TREE_TAIGA);
        BiomeLayers.putCategory(map, Category.TAIGA, BiomeIds.GIANT_TREE_TAIGA_HILLS);
        BiomeLayers.putCategory(map, Category.TAIGA, BiomeIds.SNOWY_TAIGA);
        BiomeLayers.putCategory(map, Category.TAIGA, BiomeIds.SNOWY_TAIGA_HILLS);
        BiomeLayers.putCategory(map, Category.TAIGA, BiomeIds.SNOWY_TAIGA_MOUNTAINS);
        BiomeLayers.putCategory(map, Category.TAIGA, BiomeIds.TAIGA);
        BiomeLayers.putCategory(map, Category.TAIGA, BiomeIds.TAIGA_HILLS);
        BiomeLayers.putCategory(map, Category.TAIGA, BiomeIds.TAIGA_MOUNTAINS);
    });

    private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> stack(long seed, ParentedLayer layer, LayerFactory<T> parent, int count, LongFunction<C> contextProvider) {
        LayerFactory<T> layerFactory = parent;
        for (int i = 0; i < count; ++i) {
            layerFactory = layer.create((LayerSampleContext)contextProvider.apply(seed + (long)i), layerFactory);
        }
        return layerFactory;
    }

    private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> build(boolean old, int biomeSize, int riverSize, LongFunction<C> contextProvider) {
        LayerFactory layerFactory = ContinentLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1L));
        layerFactory = ScaleLayer.FUZZY.create((LayerSampleContext)contextProvider.apply(2000L), layerFactory);
        layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1L), layerFactory);
        layerFactory = ScaleLayer.NORMAL.create((LayerSampleContext)contextProvider.apply(2001L), layerFactory);
        layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(2L), layerFactory);
        layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(50L), layerFactory);
        layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(70L), layerFactory);
        layerFactory = AddIslandLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(2L), layerFactory);
        LayerFactory layerFactory2 = OceanTemperatureLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(2L));
        layerFactory2 = BiomeLayers.stack(2001L, ScaleLayer.NORMAL, layerFactory2, 6, contextProvider);
        layerFactory = AddColdClimatesLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(2L), layerFactory);
        layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(3L), layerFactory);
        layerFactory = AddClimateLayers.AddTemperateBiomesLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(2L), layerFactory);
        layerFactory = AddClimateLayers.AddCoolBiomesLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(2L), layerFactory);
        layerFactory = AddClimateLayers.AddSpecialBiomesLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(3L), layerFactory);
        layerFactory = ScaleLayer.NORMAL.create((LayerSampleContext)contextProvider.apply(2002L), layerFactory);
        layerFactory = ScaleLayer.NORMAL.create((LayerSampleContext)contextProvider.apply(2003L), layerFactory);
        layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(4L), layerFactory);
        layerFactory = AddMushroomIslandLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(5L), layerFactory);
        layerFactory = AddDeepOceanLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(4L), layerFactory);
        LayerFactory layerFactory3 = layerFactory = BiomeLayers.stack(1000L, ScaleLayer.NORMAL, layerFactory, 0, contextProvider);
        layerFactory3 = BiomeLayers.stack(1000L, ScaleLayer.NORMAL, layerFactory3, 0, contextProvider);
        layerFactory3 = SimpleLandNoiseLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(100L), layerFactory3);
        LayerFactory layerFactory4 = layerFactory;
        layerFactory4 = new AddBaseBiomesLayer(old).create((LayerSampleContext)contextProvider.apply(200L), layerFactory4);
        layerFactory4 = AddBambooJungleLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1001L), layerFactory4);
        layerFactory4 = BiomeLayers.stack(1000L, ScaleLayer.NORMAL, layerFactory4, 2, contextProvider);
        layerFactory4 = EaseBiomeEdgeLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), layerFactory4);
        LayerFactory layerFactory5 = layerFactory3;
        layerFactory5 = BiomeLayers.stack(1000L, ScaleLayer.NORMAL, layerFactory5, 2, contextProvider);
        layerFactory4 = AddHillsLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), layerFactory4, layerFactory5);
        layerFactory3 = BiomeLayers.stack(1000L, ScaleLayer.NORMAL, layerFactory3, 2, contextProvider);
        layerFactory3 = BiomeLayers.stack(1000L, ScaleLayer.NORMAL, layerFactory3, riverSize, contextProvider);
        layerFactory3 = NoiseToRiverLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1L), layerFactory3);
        layerFactory3 = SmoothLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), layerFactory3);
        layerFactory4 = AddSunflowerPlainsLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1001L), layerFactory4);
        for (int i = 0; i < biomeSize; ++i) {
            layerFactory4 = ScaleLayer.NORMAL.create((LayerSampleContext)contextProvider.apply(1000 + i), layerFactory4);
            if (i == 0) {
                layerFactory4 = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(3L), layerFactory4);
            }
            if (i != 1 && biomeSize != 1) continue;
            layerFactory4 = AddEdgeBiomesLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), layerFactory4);
        }
        layerFactory4 = SmoothLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), layerFactory4);
        layerFactory4 = ApplyRiverLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(100L), layerFactory4, layerFactory3);
        layerFactory4 = ApplyOceanTemperatureLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(100L), layerFactory4, layerFactory2);
        return layerFactory4;
    }

    public static BiomeLayerSampler build(long seed, boolean old, int biomeSize, int riverSize) {
        int i = 25;
        LayerFactory<CachingLayerSampler> layerFactory = BiomeLayers.build(old, biomeSize, riverSize, salt -> new CachingLayerContext(25, seed, salt));
        return new BiomeLayerSampler(layerFactory);
    }

    public static boolean areSimilar(int id1, int id2) {
        if (id1 == id2) {
            return true;
        }
        return BY_CATEGORY.get(id1) == BY_CATEGORY.get(id2);
    }

    private static void putCategory(Int2IntOpenHashMap map, Category category, int id) {
        map.put(id, category.ordinal());
    }

    protected static boolean isOcean(int id) {
        return id == BiomeIds.WARM_OCEAN || id == BiomeIds.LUKEWARM_OCEAN || id == 0 || id == BiomeIds.COLD_OCEAN || id == BiomeIds.FROZEN_OCEAN || id == BiomeIds.DEEP_WARM_OCEAN || id == BiomeIds.DEEP_LUKEWARM_OCEAN || id == BiomeIds.DEEP_OCEAN || id == BiomeIds.DEEP_COLD_OCEAN || id == BiomeIds.DEEP_FROZEN_OCEAN;
    }

    protected static boolean isShallowOcean(int id) {
        return id == BiomeIds.WARM_OCEAN || id == BiomeIds.LUKEWARM_OCEAN || id == 0 || id == BiomeIds.COLD_OCEAN || id == BiomeIds.FROZEN_OCEAN;
    }

    static enum Category {
        NONE,
        TAIGA,
        EXTREME_HILLS,
        JUNGLE,
        MESA,
        BADLANDS_PLATEAU,
        PLAINS,
        SAVANNA,
        ICY,
        BEACH,
        FOREST,
        OCEAN,
        DESERT,
        RIVER,
        SWAMP,
        MUSHROOM;

    }
}

