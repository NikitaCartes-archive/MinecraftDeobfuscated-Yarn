package net.minecraft.world.biome.layer;

import java.util.function.LongFunction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;

public class BiomeLayers {
	protected static final int WARM_OCEAN_ID = Registry.BIOME.getRawId(Biomes.WARM_OCEAN);
	protected static final int LUKEWARM_OCEAN_ID = Registry.BIOME.getRawId(Biomes.LUKEWARM_OCEAN);
	protected static final int OCEAN_ID = Registry.BIOME.getRawId(Biomes.OCEAN);
	protected static final int COLD_OCEAN_ID = Registry.BIOME.getRawId(Biomes.COLD_OCEAN);
	protected static final int FROZEN_OCEAN_ID = Registry.BIOME.getRawId(Biomes.FROZEN_OCEAN);
	protected static final int DEEP_WARM_OCEAN_ID = Registry.BIOME.getRawId(Biomes.DEEP_WARM_OCEAN);
	protected static final int DEEP_LUKEWARM_OCEAN_ID = Registry.BIOME.getRawId(Biomes.DEEP_LUKEWARM_OCEAN);
	protected static final int DEEP_OCEAN_ID = Registry.BIOME.getRawId(Biomes.DEEP_OCEAN);
	protected static final int DEEP_COLD_OCEAN_ID = Registry.BIOME.getRawId(Biomes.DEEP_COLD_OCEAN);
	protected static final int DEEP_FROZEN_OCEAN_ID = Registry.BIOME.getRawId(Biomes.DEEP_FROZEN_OCEAN);

	private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> stack(
		long l, ParentedLayer parentedLayer, LayerFactory<T> layerFactory, int i, LongFunction<C> longFunction
	) {
		LayerFactory<T> layerFactory2 = layerFactory;

		for (int j = 0; j < i; j++) {
			layerFactory2 = parentedLayer.create((LayerSampleContext<T>)longFunction.apply(l + (long)j), layerFactory2);
		}

		return layerFactory2;
	}

	public static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> build(
		LevelGeneratorType levelGeneratorType, OverworldChunkGeneratorConfig overworldChunkGeneratorConfig, LongFunction<C> longFunction
	) {
		LayerFactory<T> layerFactory = ContinentLayer.INSTANCE.create((LayerSampleContext<T>)longFunction.apply(1L));
		layerFactory = ScaleLayer.FUZZY.create((LayerSampleContext<T>)longFunction.apply(2000L), layerFactory);
		layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext<T>)longFunction.apply(1L), layerFactory);
		layerFactory = ScaleLayer.NORMAL.create((LayerSampleContext<T>)longFunction.apply(2001L), layerFactory);
		layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext<T>)longFunction.apply(2L), layerFactory);
		layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext<T>)longFunction.apply(50L), layerFactory);
		layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext<T>)longFunction.apply(70L), layerFactory);
		layerFactory = AddIslandLayer.INSTANCE.create((LayerSampleContext<T>)longFunction.apply(2L), layerFactory);
		LayerFactory<T> layerFactory2 = OceanTemperatureLayer.INSTANCE.create((LayerSampleContext<T>)longFunction.apply(2L));
		layerFactory2 = stack(2001L, ScaleLayer.NORMAL, layerFactory2, 6, longFunction);
		layerFactory = AddColdClimatesLayer.INSTANCE.create((LayerSampleContext<T>)longFunction.apply(2L), layerFactory);
		layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext<T>)longFunction.apply(3L), layerFactory);
		layerFactory = AddClimateLayers.AddTemperateBiomesLayer.INSTANCE.create((LayerSampleContext<T>)longFunction.apply(2L), layerFactory);
		layerFactory = AddClimateLayers.AddCoolBiomesLayer.INSTANCE.create((LayerSampleContext<T>)longFunction.apply(2L), layerFactory);
		layerFactory = AddClimateLayers.AddSpecialBiomesLayer.INSTANCE.create((LayerSampleContext<T>)longFunction.apply(3L), layerFactory);
		layerFactory = ScaleLayer.NORMAL.create((LayerSampleContext<T>)longFunction.apply(2002L), layerFactory);
		layerFactory = ScaleLayer.NORMAL.create((LayerSampleContext<T>)longFunction.apply(2003L), layerFactory);
		layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext<T>)longFunction.apply(4L), layerFactory);
		layerFactory = AddMushroomIslandLayer.INSTANCE.create((LayerSampleContext<T>)longFunction.apply(5L), layerFactory);
		layerFactory = AddDeepOceanLayer.INSTANCE.create((LayerSampleContext<T>)longFunction.apply(4L), layerFactory);
		layerFactory = stack(1000L, ScaleLayer.NORMAL, layerFactory, 0, longFunction);
		int i = levelGeneratorType == LevelGeneratorType.LARGE_BIOMES ? 6 : overworldChunkGeneratorConfig.getBiomeSize();
		int j = overworldChunkGeneratorConfig.getRiverSize();
		LayerFactory<T> layerFactory3 = stack(1000L, ScaleLayer.NORMAL, layerFactory, 0, longFunction);
		layerFactory3 = SimpleLandNoiseLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(100L), layerFactory3);
		LayerFactory<T> layerFactory4 = new SetBaseBiomesLayer(levelGeneratorType, overworldChunkGeneratorConfig.getForcedBiome())
			.create((LayerSampleContext<T>)longFunction.apply(200L), layerFactory);
		layerFactory4 = AddBambooJungleLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(1001L), layerFactory4);
		layerFactory4 = stack(1000L, ScaleLayer.NORMAL, layerFactory4, 2, longFunction);
		layerFactory4 = EaseBiomeEdgeLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(1000L), layerFactory4);
		LayerFactory<T> layerFactory5 = stack(1000L, ScaleLayer.NORMAL, layerFactory3, 2, longFunction);
		layerFactory4 = AddHillsLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(1000L), layerFactory4, layerFactory5);
		layerFactory3 = stack(1000L, ScaleLayer.NORMAL, layerFactory3, 2, longFunction);
		layerFactory3 = stack(1000L, ScaleLayer.NORMAL, layerFactory3, j, longFunction);
		layerFactory3 = NoiseToRiverLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(1L), layerFactory3);
		layerFactory3 = SmoothenShorelineLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(1000L), layerFactory3);
		layerFactory4 = AddSunflowerPlainsLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(1001L), layerFactory4);

		for (int k = 0; k < i; k++) {
			layerFactory4 = ScaleLayer.NORMAL.create((LayerSampleContext)longFunction.apply((long)(1000 + k)), layerFactory4);
			if (k == 0) {
				layerFactory4 = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(3L), layerFactory4);
			}

			if (k == 1 || i == 1) {
				layerFactory4 = AddEdgeBiomesLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(1000L), layerFactory4);
			}
		}

		layerFactory4 = SmoothenShorelineLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(1000L), layerFactory4);
		layerFactory4 = AddRiversLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(100L), layerFactory4, layerFactory3);
		return ApplyOceanTemperatureLayer.INSTANCE.create((LayerSampleContext<T>)longFunction.apply(100L), layerFactory4, layerFactory2);
	}

	public static BiomeLayerSampler build(long l, LevelGeneratorType levelGeneratorType, OverworldChunkGeneratorConfig overworldChunkGeneratorConfig) {
		int i = 25;
		LayerFactory<CachingLayerSampler> layerFactory = build(levelGeneratorType, overworldChunkGeneratorConfig, m -> new CachingLayerContext(25, l, m));
		return new BiomeLayerSampler(layerFactory);
	}

	public static boolean areSimilar(int i, int j) {
		if (i == j) {
			return true;
		} else {
			Biome biome = Registry.BIOME.get(i);
			Biome biome2 = Registry.BIOME.get(j);
			if (biome == null || biome2 == null) {
				return false;
			} else if (biome != Biomes.WOODED_BADLANDS_PLATEAU && biome != Biomes.BADLANDS_PLATEAU) {
				return biome.getCategory() != Biome.Category.NONE && biome2.getCategory() != Biome.Category.NONE && biome.getCategory() == biome2.getCategory()
					? true
					: biome == biome2;
			} else {
				return biome2 == Biomes.WOODED_BADLANDS_PLATEAU || biome2 == Biomes.BADLANDS_PLATEAU;
			}
		}
	}

	protected static boolean isOcean(int i) {
		return i == WARM_OCEAN_ID
			|| i == LUKEWARM_OCEAN_ID
			|| i == OCEAN_ID
			|| i == COLD_OCEAN_ID
			|| i == FROZEN_OCEAN_ID
			|| i == DEEP_WARM_OCEAN_ID
			|| i == DEEP_LUKEWARM_OCEAN_ID
			|| i == DEEP_OCEAN_ID
			|| i == DEEP_COLD_OCEAN_ID
			|| i == DEEP_FROZEN_OCEAN_ID;
	}

	protected static boolean isShallowOcean(int i) {
		return i == WARM_OCEAN_ID || i == LUKEWARM_OCEAN_ID || i == OCEAN_ID || i == COLD_OCEAN_ID || i == FROZEN_OCEAN_ID;
	}
}
