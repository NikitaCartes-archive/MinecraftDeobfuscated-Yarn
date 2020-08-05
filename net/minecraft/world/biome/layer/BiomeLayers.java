/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome.layer;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.function.LongFunction;
import net.minecraft.util.Util;
import net.minecraft.world.biome.layer.AddBambooJungleLayer;
import net.minecraft.world.biome.layer.AddClimateLayers;
import net.minecraft.world.biome.layer.AddColdClimatesLayer;
import net.minecraft.world.biome.layer.AddDeepOceanLayer;
import net.minecraft.world.biome.layer.AddEdgeBiomesLayer;
import net.minecraft.world.biome.layer.AddHillsLayer;
import net.minecraft.world.biome.layer.AddIslandLayer;
import net.minecraft.world.biome.layer.AddMushroomIslandLayer;
import net.minecraft.world.biome.layer.AddRiversLayer;
import net.minecraft.world.biome.layer.AddSunflowerPlainsLayer;
import net.minecraft.world.biome.layer.ApplyOceanTemperatureLayer;
import net.minecraft.world.biome.layer.ContinentLayer;
import net.minecraft.world.biome.layer.EaseBiomeEdgeLayer;
import net.minecraft.world.biome.layer.IncreaseEdgeCurvatureLayer;
import net.minecraft.world.biome.layer.NoiseToRiverLayer;
import net.minecraft.world.biome.layer.OceanTemperatureLayer;
import net.minecraft.world.biome.layer.ScaleLayer;
import net.minecraft.world.biome.layer.SetBaseBiomesLayer;
import net.minecraft.world.biome.layer.SimpleLandNoiseLayer;
import net.minecraft.world.biome.layer.SmoothenShorelineLayer;
import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.CachingLayerContext;
import net.minecraft.world.biome.layer.util.CachingLayerSampler;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.source.BiomeLayerSampler;

public class BiomeLayers {
    private static final Int2IntMap field_26709 = Util.make(new Int2IntOpenHashMap(), int2IntOpenHashMap -> {
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26719, 16);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26719, 26);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26722, 2);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26722, 17);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26722, 130);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26712, 131);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26712, 162);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26712, 20);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26712, 3);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26712, 34);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26720, 27);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26720, 28);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26720, 29);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26720, 157);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26720, 132);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26720, 4);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26720, 155);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26720, 156);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26720, 18);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26718, 140);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26718, 13);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26718, 12);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26713, 168);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26713, 169);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26713, 21);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26713, 23);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26713, 22);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26713, 149);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26713, 151);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26714, 37);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26714, 165);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26714, 167);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26714, 166);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26715, 39);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26715, 38);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26725, 14);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26725, 15);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26710, 25);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26721, 46);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26721, 49);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26721, 50);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26721, 48);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26721, 24);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26721, 47);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26721, 10);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26721, 45);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26721, 0);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26721, 44);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26716, 1);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26716, 129);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26723, 11);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26723, 7);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26717, 35);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26717, 36);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26717, 163);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26717, 164);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26724, 6);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26724, 134);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26711, 160);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26711, 161);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26711, 32);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26711, 33);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26711, 30);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26711, 31);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26711, 158);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26711, 5);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26711, 19);
        BiomeLayers.method_31117(int2IntOpenHashMap, class_5503.field_26711, 133);
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
        layerFactory4 = new SetBaseBiomesLayer(old).create((LayerSampleContext)contextProvider.apply(200L), layerFactory4);
        layerFactory4 = AddBambooJungleLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1001L), layerFactory4);
        layerFactory4 = BiomeLayers.stack(1000L, ScaleLayer.NORMAL, layerFactory4, 2, contextProvider);
        layerFactory4 = EaseBiomeEdgeLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), layerFactory4);
        LayerFactory layerFactory5 = layerFactory3;
        layerFactory5 = BiomeLayers.stack(1000L, ScaleLayer.NORMAL, layerFactory5, 2, contextProvider);
        layerFactory4 = AddHillsLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), layerFactory4, layerFactory5);
        layerFactory3 = BiomeLayers.stack(1000L, ScaleLayer.NORMAL, layerFactory3, 2, contextProvider);
        layerFactory3 = BiomeLayers.stack(1000L, ScaleLayer.NORMAL, layerFactory3, riverSize, contextProvider);
        layerFactory3 = NoiseToRiverLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1L), layerFactory3);
        layerFactory3 = SmoothenShorelineLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), layerFactory3);
        layerFactory4 = AddSunflowerPlainsLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1001L), layerFactory4);
        for (int i = 0; i < biomeSize; ++i) {
            layerFactory4 = ScaleLayer.NORMAL.create((LayerSampleContext)contextProvider.apply(1000 + i), layerFactory4);
            if (i == 0) {
                layerFactory4 = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(3L), layerFactory4);
            }
            if (i != 1 && biomeSize != 1) continue;
            layerFactory4 = AddEdgeBiomesLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), layerFactory4);
        }
        layerFactory4 = SmoothenShorelineLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), layerFactory4);
        layerFactory4 = AddRiversLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(100L), layerFactory4, layerFactory3);
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

    private static void method_31117(Int2IntOpenHashMap int2IntOpenHashMap, class_5503 arg, int i) {
        int2IntOpenHashMap.put(i, arg.ordinal());
    }

    protected static boolean isOcean(int id) {
        return id == 44 || id == 45 || id == 0 || id == 46 || id == 10 || id == 47 || id == 48 || id == 24 || id == 49 || id == 50;
    }

    protected static boolean isShallowOcean(int id) {
        return id == 44 || id == 45 || id == 0 || id == 46 || id == 10;
    }

    static enum class_5503 {
        field_26710,
        field_26711,
        field_26712,
        field_26713,
        field_26714,
        field_26715,
        field_26716,
        field_26717,
        field_26718,
        field_26719,
        field_26720,
        field_26721,
        field_26722,
        field_26723,
        field_26724,
        field_26725;

    }
}

