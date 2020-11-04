package net.minecraft.world.biome.layer;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.function.LongFunction;
import net.minecraft.util.Util;
import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.CachingLayerContext;
import net.minecraft.world.biome.layer.util.CachingLayerSampler;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.source.BiomeLayerSampler;

public class BiomeLayers {
	private static final Int2IntMap field_26709 = Util.make(new Int2IntOpenHashMap(), int2IntOpenHashMap -> {
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.BEACH, 16);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.BEACH, 26);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.DESERT, 2);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.DESERT, 17);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.DESERT, 130);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.EXTREME_HILLS, 131);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.EXTREME_HILLS, 162);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.EXTREME_HILLS, 20);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.EXTREME_HILLS, 3);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.EXTREME_HILLS, 34);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.FOREST, 27);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.FOREST, 28);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.FOREST, 29);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.FOREST, 157);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.FOREST, 132);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.FOREST, 4);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.FOREST, 155);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.FOREST, 156);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.FOREST, 18);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.ICY, 140);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.ICY, 13);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.ICY, 12);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.JUNGLE, 168);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.JUNGLE, 169);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.JUNGLE, 21);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.JUNGLE, 23);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.JUNGLE, 22);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.JUNGLE, 149);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.JUNGLE, 151);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.MESA, 37);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.MESA, 165);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.MESA, 167);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.MESA, 166);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.BADLANDS_PLATEAU, 39);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.BADLANDS_PLATEAU, 38);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.MUSHROOM, 14);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.MUSHROOM, 15);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.NONE, 25);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.OCEAN, 46);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.OCEAN, 49);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.OCEAN, 50);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.OCEAN, 48);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.OCEAN, 24);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.OCEAN, 47);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.OCEAN, 10);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.OCEAN, 45);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.OCEAN, 0);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.OCEAN, 44);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.PLAINS, 1);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.PLAINS, 129);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.RIVER, 11);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.RIVER, 7);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.SAVANNA, 35);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.SAVANNA, 36);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.SAVANNA, 163);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.SAVANNA, 164);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.SWAMP, 6);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.SWAMP, 134);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.TAIGA, 160);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.TAIGA, 161);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.TAIGA, 32);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.TAIGA, 33);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.TAIGA, 30);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.TAIGA, 31);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.TAIGA, 158);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.TAIGA, 5);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.TAIGA, 19);
		method_31117(int2IntOpenHashMap, BiomeLayers.Category.TAIGA, 133);
	});

	private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> stack(
		long seed, ParentedLayer layer, LayerFactory<T> parent, int count, LongFunction<C> contextProvider
	) {
		LayerFactory<T> layerFactory = parent;

		for (int i = 0; i < count; i++) {
			layerFactory = layer.create((LayerSampleContext<T>)contextProvider.apply(seed + (long)i), layerFactory);
		}

		return layerFactory;
	}

	private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> build(
		boolean old, int biomeSize, int riverSize, LongFunction<C> contextProvider
	) {
		LayerFactory<T> layerFactory = ContinentLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(1L));
		layerFactory = ScaleLayer.FUZZY.create((LayerSampleContext<T>)contextProvider.apply(2000L), layerFactory);
		layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(1L), layerFactory);
		layerFactory = ScaleLayer.NORMAL.create((LayerSampleContext<T>)contextProvider.apply(2001L), layerFactory);
		layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(2L), layerFactory);
		layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(50L), layerFactory);
		layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(70L), layerFactory);
		layerFactory = AddIslandLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(2L), layerFactory);
		LayerFactory<T> layerFactory2 = OceanTemperatureLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(2L));
		layerFactory2 = stack(2001L, ScaleLayer.NORMAL, layerFactory2, 6, contextProvider);
		layerFactory = AddColdClimatesLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(2L), layerFactory);
		layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(3L), layerFactory);
		layerFactory = AddClimateLayers.AddTemperateBiomesLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(2L), layerFactory);
		layerFactory = AddClimateLayers.AddCoolBiomesLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(2L), layerFactory);
		layerFactory = AddClimateLayers.AddSpecialBiomesLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(3L), layerFactory);
		layerFactory = ScaleLayer.NORMAL.create((LayerSampleContext<T>)contextProvider.apply(2002L), layerFactory);
		layerFactory = ScaleLayer.NORMAL.create((LayerSampleContext<T>)contextProvider.apply(2003L), layerFactory);
		layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(4L), layerFactory);
		layerFactory = AddMushroomIslandLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(5L), layerFactory);
		layerFactory = AddDeepOceanLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(4L), layerFactory);
		layerFactory = stack(1000L, ScaleLayer.NORMAL, layerFactory, 0, contextProvider);
		LayerFactory<T> layerFactory3 = stack(1000L, ScaleLayer.NORMAL, layerFactory, 0, contextProvider);
		layerFactory3 = SimpleLandNoiseLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(100L), layerFactory3);
		LayerFactory<T> layerFactory4 = new SetBaseBiomesLayer(old).create((LayerSampleContext<T>)contextProvider.apply(200L), layerFactory);
		layerFactory4 = AddBambooJungleLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1001L), layerFactory4);
		layerFactory4 = stack(1000L, ScaleLayer.NORMAL, layerFactory4, 2, contextProvider);
		layerFactory4 = EaseBiomeEdgeLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), layerFactory4);
		LayerFactory<T> layerFactory5 = stack(1000L, ScaleLayer.NORMAL, layerFactory3, 2, contextProvider);
		layerFactory4 = AddHillsLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), layerFactory4, layerFactory5);
		layerFactory3 = stack(1000L, ScaleLayer.NORMAL, layerFactory3, 2, contextProvider);
		layerFactory3 = stack(1000L, ScaleLayer.NORMAL, layerFactory3, riverSize, contextProvider);
		layerFactory3 = NoiseToRiverLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1L), layerFactory3);
		layerFactory3 = SmoothLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), layerFactory3);
		layerFactory4 = AddSunflowerPlainsLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1001L), layerFactory4);

		for (int i = 0; i < biomeSize; i++) {
			layerFactory4 = ScaleLayer.NORMAL.create((LayerSampleContext)contextProvider.apply((long)(1000 + i)), layerFactory4);
			if (i == 0) {
				layerFactory4 = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(3L), layerFactory4);
			}

			if (i == 1 || biomeSize == 1) {
				layerFactory4 = AddEdgeBiomesLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), layerFactory4);
			}
		}

		layerFactory4 = SmoothLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), layerFactory4);
		layerFactory4 = AddRiversLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(100L), layerFactory4, layerFactory3);
		return ApplyOceanTemperatureLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(100L), layerFactory4, layerFactory2);
	}

	public static BiomeLayerSampler build(long seed, boolean old, int biomeSize, int riverSize) {
		int i = 25;
		LayerFactory<CachingLayerSampler> layerFactory = build(old, biomeSize, riverSize, salt -> new CachingLayerContext(25, seed, salt));
		return new BiomeLayerSampler(layerFactory);
	}

	public static boolean areSimilar(int id1, int id2) {
		return id1 == id2 ? true : field_26709.get(id1) == field_26709.get(id2);
	}

	private static void method_31117(Int2IntOpenHashMap int2IntOpenHashMap, BiomeLayers.Category category, int i) {
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
