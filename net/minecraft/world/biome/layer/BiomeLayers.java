/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.function.LongFunction;
import net.minecraft.util.Util;
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

public class BiomeLayers {
    private static final Int2IntMap field_26709 = Util.make(new Int2IntOpenHashMap(), int2IntOpenHashMap -> {
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.BEACH, 16);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.BEACH, 26);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.DESERT, 2);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.DESERT, 17);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.DESERT, 130);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.EXTREME_HILLS, 131);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.EXTREME_HILLS, 162);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.EXTREME_HILLS, 20);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.EXTREME_HILLS, 3);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.EXTREME_HILLS, 34);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.FOREST, 27);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.FOREST, 28);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.FOREST, 29);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.FOREST, 157);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.FOREST, 132);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.FOREST, 4);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.FOREST, 155);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.FOREST, 156);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.FOREST, 18);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.ICY, 140);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.ICY, 13);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.ICY, 12);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.JUNGLE, 168);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.JUNGLE, 169);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.JUNGLE, 21);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.JUNGLE, 23);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.JUNGLE, 22);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.JUNGLE, 149);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.JUNGLE, 151);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.MESA, 37);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.MESA, 165);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.MESA, 167);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.MESA, 166);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.BADLANDS_PLATEAU, 39);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.BADLANDS_PLATEAU, 38);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.MUSHROOM, 14);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.MUSHROOM, 15);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.NONE, 25);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.OCEAN, 46);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.OCEAN, 49);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.OCEAN, 50);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.OCEAN, 48);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.OCEAN, 24);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.OCEAN, 47);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.OCEAN, 10);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.OCEAN, 45);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.OCEAN, 0);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.OCEAN, 44);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.PLAINS, 1);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.PLAINS, 129);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.RIVER, 11);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.RIVER, 7);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.SAVANNA, 35);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.SAVANNA, 36);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.SAVANNA, 163);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.SAVANNA, 164);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.SWAMP, 6);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.SWAMP, 134);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.TAIGA, 160);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.TAIGA, 161);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.TAIGA, 32);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.TAIGA, 33);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.TAIGA, 30);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.TAIGA, 31);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.TAIGA, 158);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.TAIGA, 5);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.TAIGA, 19);
        BiomeLayers.method_31117(int2IntOpenHashMap, Category.TAIGA, 133);
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
        return field_26709.get(id1) == field_26709.get(id2);
    }

    private static void method_31117(Int2IntOpenHashMap int2IntOpenHashMap, Category category, int i) {
        int2IntOpenHashMap.put(i, category.ordinal());
    }

    protected static boolean isOcean(int id) {
        return id == 44 || id == 45 || id == 0 || id == 46 || id == 10 || id == 47 || id == 48 || id == 24 || id == 49 || id == 50;
    }

    protected static boolean isShallowOcean(int id) {
        return id == 44 || id == 45 || id == 0 || id == 46 || id == 10;
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

