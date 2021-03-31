package net.minecraft.world.biome.layer;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.util.function.LongFunction;
import net.minecraft.util.Util;
import net.minecraft.world.biome.BiomeIds;
import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.CachingLayerContext;
import net.minecraft.world.biome.layer.util.CachingLayerSampler;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.source.BiomeLayerSampler;

public class BiomeLayers implements BiomeIds {
	protected static final int field_31799 = 1;
	protected static final int field_31800 = 2;
	protected static final int field_31801 = 3;
	protected static final int field_31802 = 4;
	protected static final int field_31803 = 3840;
	protected static final int field_31804 = 8;
	private static final Int2IntMap BY_CATEGORY = Util.make(new Int2IntOpenHashMap(), map -> {
		putCategory(map, BiomeLayers.Category.BEACH, BiomeIds.BEACH);
		putCategory(map, BiomeLayers.Category.BEACH, BiomeIds.SNOWY_BEACH);
		putCategory(map, BiomeLayers.Category.DESERT, BiomeIds.DESERT);
		putCategory(map, BiomeLayers.Category.DESERT, BiomeIds.DESERT_HILLS);
		putCategory(map, BiomeLayers.Category.DESERT, BiomeIds.DESERT_LAKES);
		putCategory(map, BiomeLayers.Category.EXTREME_HILLS, BiomeIds.GRAVELLY_MOUNTAINS);
		putCategory(map, BiomeLayers.Category.EXTREME_HILLS, BiomeIds.MODIFIED_GRAVELLY_MOUNTAINS);
		putCategory(map, BiomeLayers.Category.EXTREME_HILLS, BiomeIds.MOUNTAIN_EDGE);
		putCategory(map, BiomeLayers.Category.EXTREME_HILLS, BiomeIds.MOUNTAINS);
		putCategory(map, BiomeLayers.Category.EXTREME_HILLS, BiomeIds.WOODED_MOUNTAINS);
		putCategory(map, BiomeLayers.Category.FOREST, BiomeIds.BIRCH_FOREST);
		putCategory(map, BiomeLayers.Category.FOREST, BiomeIds.BIRCH_FOREST_HILLS);
		putCategory(map, BiomeLayers.Category.FOREST, BiomeIds.DARK_FOREST);
		putCategory(map, BiomeLayers.Category.FOREST, BiomeIds.DARK_FOREST_HILLS);
		putCategory(map, BiomeLayers.Category.FOREST, BiomeIds.FLOWER_FOREST);
		putCategory(map, BiomeLayers.Category.FOREST, BiomeIds.FOREST);
		putCategory(map, BiomeLayers.Category.FOREST, BiomeIds.TALL_BIRCH_FOREST);
		putCategory(map, BiomeLayers.Category.FOREST, BiomeIds.TALL_BIRCH_HILLS);
		putCategory(map, BiomeLayers.Category.FOREST, BiomeIds.WOODED_HILLS);
		putCategory(map, BiomeLayers.Category.ICY, BiomeIds.ICE_SPIKES);
		putCategory(map, BiomeLayers.Category.ICY, BiomeIds.SNOWY_MOUNTAINS);
		putCategory(map, BiomeLayers.Category.ICY, BiomeIds.SNOWY_TUNDRA);
		putCategory(map, BiomeLayers.Category.JUNGLE, BiomeIds.BAMBOO_JUNGLE);
		putCategory(map, BiomeLayers.Category.JUNGLE, BiomeIds.BAMBOO_JUNGLE_HILLS);
		putCategory(map, BiomeLayers.Category.JUNGLE, BiomeIds.JUNGLE);
		putCategory(map, BiomeLayers.Category.JUNGLE, BiomeIds.JUNGLE_EDGE);
		putCategory(map, BiomeLayers.Category.JUNGLE, BiomeIds.JUNGLE_HILLS);
		putCategory(map, BiomeLayers.Category.JUNGLE, BiomeIds.MODIFIED_JUNGLE);
		putCategory(map, BiomeLayers.Category.JUNGLE, BiomeIds.MODIFIED_JUNGLE_EDGE);
		putCategory(map, BiomeLayers.Category.MESA, BiomeIds.BADLANDS);
		putCategory(map, BiomeLayers.Category.MESA, BiomeIds.ERODED_BADLANDS);
		putCategory(map, BiomeLayers.Category.MESA, BiomeIds.MODIFIED_BADLANDS_PLATEAU);
		putCategory(map, BiomeLayers.Category.MESA, BiomeIds.MODIFIED_WOODED_BADLANDS_PLATEAU);
		putCategory(map, BiomeLayers.Category.BADLANDS_PLATEAU, BiomeIds.BADLANDS_PLATEAU);
		putCategory(map, BiomeLayers.Category.BADLANDS_PLATEAU, BiomeIds.WOODED_BADLANDS_PLATEAU);
		putCategory(map, BiomeLayers.Category.MUSHROOM, BiomeIds.MUSHROOM_FIELDS);
		putCategory(map, BiomeLayers.Category.MUSHROOM, BiomeIds.MUSHROOM_FIELD_SHORE);
		putCategory(map, BiomeLayers.Category.NONE, BiomeIds.STONE_SHORE);
		putCategory(map, BiomeLayers.Category.OCEAN, BiomeIds.COLD_OCEAN);
		putCategory(map, BiomeLayers.Category.OCEAN, BiomeIds.DEEP_COLD_OCEAN);
		putCategory(map, BiomeLayers.Category.OCEAN, BiomeIds.DEEP_FROZEN_OCEAN);
		putCategory(map, BiomeLayers.Category.OCEAN, BiomeIds.DEEP_LUKEWARM_OCEAN);
		putCategory(map, BiomeLayers.Category.OCEAN, BiomeIds.DEEP_OCEAN);
		putCategory(map, BiomeLayers.Category.OCEAN, BiomeIds.DEEP_WARM_OCEAN);
		putCategory(map, BiomeLayers.Category.OCEAN, BiomeIds.FROZEN_OCEAN);
		putCategory(map, BiomeLayers.Category.OCEAN, BiomeIds.LUKEWARM_OCEAN);
		putCategory(map, BiomeLayers.Category.OCEAN, BiomeIds.OCEAN);
		putCategory(map, BiomeLayers.Category.OCEAN, BiomeIds.WARM_OCEAN);
		putCategory(map, BiomeLayers.Category.PLAINS, BiomeIds.PLAINS);
		putCategory(map, BiomeLayers.Category.PLAINS, BiomeIds.SUNFLOWER_PLAINS);
		putCategory(map, BiomeLayers.Category.RIVER, BiomeIds.FROZEN_RIVER);
		putCategory(map, BiomeLayers.Category.RIVER, BiomeIds.RIVER);
		putCategory(map, BiomeLayers.Category.SAVANNA, BiomeIds.SAVANNA);
		putCategory(map, BiomeLayers.Category.SAVANNA, BiomeIds.SAVANNA_PLATEAU);
		putCategory(map, BiomeLayers.Category.SAVANNA, BiomeIds.SHATTERED_SAVANNA);
		putCategory(map, BiomeLayers.Category.SAVANNA, BiomeIds.SHATTERED_SAVANNA_PLATEAU);
		putCategory(map, BiomeLayers.Category.SWAMP, BiomeIds.SWAMP);
		putCategory(map, BiomeLayers.Category.SWAMP, BiomeIds.SWAMP_HILLS);
		putCategory(map, BiomeLayers.Category.TAIGA, BiomeIds.GIANT_SPRUCE_TAIGA);
		putCategory(map, BiomeLayers.Category.TAIGA, BiomeIds.GIANT_SPRUCE_TAIGA_HILLS);
		putCategory(map, BiomeLayers.Category.TAIGA, BiomeIds.GIANT_TREE_TAIGA);
		putCategory(map, BiomeLayers.Category.TAIGA, BiomeIds.GIANT_TREE_TAIGA_HILLS);
		putCategory(map, BiomeLayers.Category.TAIGA, BiomeIds.SNOWY_TAIGA);
		putCategory(map, BiomeLayers.Category.TAIGA, BiomeIds.SNOWY_TAIGA_HILLS);
		putCategory(map, BiomeLayers.Category.TAIGA, BiomeIds.SNOWY_TAIGA_MOUNTAINS);
		putCategory(map, BiomeLayers.Category.TAIGA, BiomeIds.TAIGA);
		putCategory(map, BiomeLayers.Category.TAIGA, BiomeIds.TAIGA_HILLS);
		putCategory(map, BiomeLayers.Category.TAIGA, BiomeIds.TAIGA_MOUNTAINS);
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
		LayerFactory<T> layerFactory4 = new AddBaseBiomesLayer(old).create((LayerSampleContext<T>)contextProvider.apply(200L), layerFactory);
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
		layerFactory4 = ApplyRiverLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(100L), layerFactory4, layerFactory3);
		return ApplyOceanTemperatureLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(100L), layerFactory4, layerFactory2);
	}

	public static BiomeLayerSampler build(long seed, boolean old, int biomeSize, int riverSize) {
		int i = 25;
		LayerFactory<CachingLayerSampler> layerFactory = build(old, biomeSize, riverSize, salt -> new CachingLayerContext(25, seed, salt));
		return new BiomeLayerSampler(layerFactory);
	}

	public static boolean areSimilar(int id1, int id2) {
		return id1 == id2 ? true : BY_CATEGORY.get(id1) == BY_CATEGORY.get(id2);
	}

	private static void putCategory(Int2IntOpenHashMap map, BiomeLayers.Category category, int id) {
		map.put(id, category.ordinal());
	}

	protected static boolean isOcean(int id) {
		return id == BiomeIds.WARM_OCEAN
			|| id == BiomeIds.LUKEWARM_OCEAN
			|| id == 0
			|| id == BiomeIds.COLD_OCEAN
			|| id == BiomeIds.FROZEN_OCEAN
			|| id == BiomeIds.DEEP_WARM_OCEAN
			|| id == BiomeIds.DEEP_LUKEWARM_OCEAN
			|| id == BiomeIds.DEEP_OCEAN
			|| id == BiomeIds.DEEP_COLD_OCEAN
			|| id == BiomeIds.DEEP_FROZEN_OCEAN;
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
