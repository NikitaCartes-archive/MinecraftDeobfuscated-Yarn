package net.minecraft.world.biome.layer;

import java.util.function.LongFunction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.type.ParentedLayer;
import net.minecraft.world.biome.layer.util.CachingLayerContext;
import net.minecraft.world.biome.layer.util.CachingLayerSampler;
import net.minecraft.world.biome.layer.util.LayerFactory;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.source.BiomeLayerSampler;
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
		long seed, ParentedLayer layer, LayerFactory<T> parent, int count, LongFunction<C> contextProvider
	) {
		LayerFactory<T> layerFactory = parent;

		for (int i = 0; i < count; i++) {
			layerFactory = layer.create((LayerSampleContext<T>)contextProvider.apply(seed + (long)i), layerFactory);
		}

		return layerFactory;
	}

	public static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> build(
		LevelGeneratorType generatorType, OverworldChunkGeneratorConfig settings, LongFunction<C> contextProvider
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
		int i = generatorType == LevelGeneratorType.LARGE_BIOMES ? 6 : settings.getBiomeSize();
		int j = settings.getRiverSize();
		LayerFactory<T> layerFactory3 = stack(1000L, ScaleLayer.NORMAL, layerFactory, 0, contextProvider);
		layerFactory3 = SimpleLandNoiseLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(100L), layerFactory3);
		LayerFactory<T> layerFactory4 = new SetBaseBiomesLayer(generatorType, settings.getForcedBiome())
			.create((LayerSampleContext<T>)contextProvider.apply(200L), layerFactory);
		layerFactory4 = AddBambooJungleLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1001L), layerFactory4);
		layerFactory4 = stack(1000L, ScaleLayer.NORMAL, layerFactory4, 2, contextProvider);
		layerFactory4 = EaseBiomeEdgeLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), layerFactory4);
		LayerFactory<T> layerFactory5 = stack(1000L, ScaleLayer.NORMAL, layerFactory3, 2, contextProvider);
		layerFactory4 = AddHillsLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), layerFactory4, layerFactory5);
		layerFactory3 = stack(1000L, ScaleLayer.NORMAL, layerFactory3, 2, contextProvider);
		layerFactory3 = stack(1000L, ScaleLayer.NORMAL, layerFactory3, j, contextProvider);
		layerFactory3 = NoiseToRiverLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1L), layerFactory3);
		layerFactory3 = SmoothenShorelineLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), layerFactory3);
		layerFactory4 = AddSunflowerPlainsLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1001L), layerFactory4);

		for (int k = 0; k < i; k++) {
			layerFactory4 = ScaleLayer.NORMAL.create((LayerSampleContext)contextProvider.apply((long)(1000 + k)), layerFactory4);
			if (k == 0) {
				layerFactory4 = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(3L), layerFactory4);
			}

			if (k == 1 || i == 1) {
				layerFactory4 = AddEdgeBiomesLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), layerFactory4);
			}
		}

		layerFactory4 = SmoothenShorelineLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), layerFactory4);
		layerFactory4 = AddRiversLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(100L), layerFactory4, layerFactory3);
		return ApplyOceanTemperatureLayer.INSTANCE.create((LayerSampleContext<T>)contextProvider.apply(100L), layerFactory4, layerFactory2);
	}

	public static BiomeLayerSampler build(long seed, LevelGeneratorType generatorType, OverworldChunkGeneratorConfig settings) {
		int i = 25;
		LayerFactory<CachingLayerSampler> layerFactory = build(generatorType, settings, salt -> new CachingLayerContext(25, seed, salt));
		return new BiomeLayerSampler(layerFactory);
	}

	public static boolean areSimilar(int id1, int id2) {
		if (id1 == id2) {
			return true;
		} else {
			Biome biome = Registry.BIOME.get(id1);
			Biome biome2 = Registry.BIOME.get(id2);
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

	protected static boolean isOcean(int id) {
		return id == WARM_OCEAN_ID
			|| id == LUKEWARM_OCEAN_ID
			|| id == OCEAN_ID
			|| id == COLD_OCEAN_ID
			|| id == FROZEN_OCEAN_ID
			|| id == DEEP_WARM_OCEAN_ID
			|| id == DEEP_LUKEWARM_OCEAN_ID
			|| id == DEEP_OCEAN_ID
			|| id == DEEP_COLD_OCEAN_ID
			|| id == DEEP_FROZEN_OCEAN_ID;
	}

	protected static boolean isShallowOcean(int id) {
		return id == WARM_OCEAN_ID || id == LUKEWARM_OCEAN_ID || id == OCEAN_ID || id == COLD_OCEAN_ID || id == FROZEN_OCEAN_ID;
	}
}
