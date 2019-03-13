package net.minecraft.world.biome.layer;

import com.google.common.collect.ImmutableList;
import java.util.function.LongFunction;
import net.minecraft.class_3639;
import net.minecraft.class_3651;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeGroupToBiomeLayer;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;

public class BiomeLayers {
	protected static final int WARM_OCEAN_ID = Registry.BIOME.getRawId(Biomes.field_9408);
	protected static final int LUKEWARM_OCEAN_ID = Registry.BIOME.getRawId(Biomes.field_9441);
	protected static final int OCEAN_ID = Registry.BIOME.getRawId(Biomes.field_9423);
	protected static final int COLD_OCEAN_ID = Registry.BIOME.getRawId(Biomes.field_9467);
	protected static final int FROZEN_OCEAN_ID = Registry.BIOME.getRawId(Biomes.field_9435);
	protected static final int DEEP_WARM_OCEAN_ID = Registry.BIOME.getRawId(Biomes.field_9448);
	protected static final int DEEP_LUKEWARM_OCEAN_ID = Registry.BIOME.getRawId(Biomes.field_9439);
	protected static final int DEEP_OCEAN_ID = Registry.BIOME.getRawId(Biomes.field_9446);
	protected static final int DEEP_COLD_OCEAN_ID = Registry.BIOME.getRawId(Biomes.field_9470);
	protected static final int DEEP_FROZEN_OCEAN_ID = Registry.BIOME.getRawId(Biomes.field_9418);

	private static <T extends LayerSampler, C extends LayerSampleContext<T>> LayerFactory<T> method_15848(
		long l, ParentedLayer parentedLayer, LayerFactory<T> layerFactory, int i, LongFunction<C> longFunction
	) {
		LayerFactory<T> layerFactory2 = layerFactory;

		for (int j = 0; j < i; j++) {
			layerFactory2 = parentedLayer.create((LayerSampleContext<T>)longFunction.apply(l + (long)j), layerFactory2);
		}

		return layerFactory2;
	}

	public static <T extends LayerSampler, C extends LayerSampleContext<T>> ImmutableList<LayerFactory<T>> build(
		LevelGeneratorType levelGeneratorType, OverworldChunkGeneratorConfig overworldChunkGeneratorConfig, LongFunction<C> longFunction
	) {
		LayerFactory<T> layerFactory = ContinentLayer.field_16103.create((LayerSampleContext<T>)longFunction.apply(1L));
		layerFactory = ScaleLayer.field_16198.create((LayerSampleContext<T>)longFunction.apply(2000L), layerFactory);
		layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext<T>)longFunction.apply(1L), layerFactory);
		layerFactory = ScaleLayer.field_16196.create((LayerSampleContext<T>)longFunction.apply(2001L), layerFactory);
		layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext<T>)longFunction.apply(2L), layerFactory);
		layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext<T>)longFunction.apply(50L), layerFactory);
		layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext<T>)longFunction.apply(70L), layerFactory);
		layerFactory = class_3651.field_16158.create((LayerSampleContext<T>)longFunction.apply(2L), layerFactory);
		LayerFactory<T> layerFactory2 = OceanTemperatureLayer.field_16105.create((LayerSampleContext<T>)longFunction.apply(2L));
		layerFactory2 = method_15848(2001L, ScaleLayer.field_16196, layerFactory2, 6, longFunction);
		layerFactory = class_3639.field_16059.create((LayerSampleContext<T>)longFunction.apply(2L), layerFactory);
		layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext<T>)longFunction.apply(3L), layerFactory);
		layerFactory = BiomeGroupLayer.AddGroup2Layer.field_17399.create((LayerSampleContext<T>)longFunction.apply(2L), layerFactory);
		layerFactory = BiomeGroupLayer.AddGroup3Layer.field_17401.create((LayerSampleContext<T>)longFunction.apply(2L), layerFactory);
		layerFactory = BiomeGroupLayer.AddSpecialBiomesLayer.field_16051.create((LayerSampleContext<T>)longFunction.apply(3L), layerFactory);
		layerFactory = ScaleLayer.field_16196.create((LayerSampleContext<T>)longFunction.apply(2002L), layerFactory);
		layerFactory = ScaleLayer.field_16196.create((LayerSampleContext<T>)longFunction.apply(2003L), layerFactory);
		layerFactory = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext<T>)longFunction.apply(4L), layerFactory);
		layerFactory = AddMushroomIslandLayer.INSTANCE.create((LayerSampleContext<T>)longFunction.apply(5L), layerFactory);
		layerFactory = AddDeepOceanLayer.field_16052.create((LayerSampleContext<T>)longFunction.apply(4L), layerFactory);
		layerFactory = method_15848(1000L, ScaleLayer.field_16196, layerFactory, 0, longFunction);
		int i = 4;
		int j = i;
		if (overworldChunkGeneratorConfig != null) {
			i = overworldChunkGeneratorConfig.getBiomeSize();
			j = overworldChunkGeneratorConfig.getRiverSize();
		}

		if (levelGeneratorType == LevelGeneratorType.LARGE_BIOMES) {
			i = 6;
		}

		LayerFactory<T> layerFactory3 = method_15848(1000L, ScaleLayer.field_16196, layerFactory, 0, longFunction);
		layerFactory3 = SimpleLandNoiseLayer.field_16157.create((LayerSampleContext)longFunction.apply(100L), layerFactory3);
		LayerFactory<T> layerFactory4 = new BiomeGroupToBiomeLayer(levelGeneratorType, overworldChunkGeneratorConfig)
			.create((LayerSampleContext<T>)longFunction.apply(200L), layerFactory);
		layerFactory4 = AddBambooJungleLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(1001L), layerFactory4);
		layerFactory4 = method_15848(1000L, ScaleLayer.field_16196, layerFactory4, 2, longFunction);
		layerFactory4 = EaseBiomeEdgeLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(1000L), layerFactory4);
		LayerFactory<T> layerFactory5 = method_15848(1000L, ScaleLayer.field_16196, layerFactory3, 2, longFunction);
		layerFactory4 = AddHillsLayer.field_16134.create((LayerSampleContext)longFunction.apply(1000L), layerFactory4, layerFactory5);
		layerFactory3 = method_15848(1000L, ScaleLayer.field_16196, layerFactory3, 2, longFunction);
		layerFactory3 = method_15848(1000L, ScaleLayer.field_16196, layerFactory3, j, longFunction);
		layerFactory3 = NoiseToRiverLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(1L), layerFactory3);
		layerFactory3 = SmoothenShorelineLayer.field_16171.create((LayerSampleContext)longFunction.apply(1000L), layerFactory3);
		layerFactory4 = AddSunflowerPlainsLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(1001L), layerFactory4);

		for (int k = 0; k < i; k++) {
			layerFactory4 = ScaleLayer.field_16196.create((LayerSampleContext)longFunction.apply((long)(1000 + k)), layerFactory4);
			if (k == 0) {
				layerFactory4 = IncreaseEdgeCurvatureLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(3L), layerFactory4);
			}

			if (k == 1 || i == 1) {
				layerFactory4 = AddEdgeBiomesLayer.INSTANCE.create((LayerSampleContext)longFunction.apply(1000L), layerFactory4);
			}
		}

		layerFactory4 = SmoothenShorelineLayer.field_16171.create((LayerSampleContext)longFunction.apply(1000L), layerFactory4);
		layerFactory4 = AddRiversLayer.field_16161.create((LayerSampleContext)longFunction.apply(100L), layerFactory4, layerFactory3);
		layerFactory4 = ApplyOceanTemperatureLayer.field_16121.create((LayerSampleContext<T>)longFunction.apply(100L), layerFactory4, layerFactory2);
		LayerFactory<T> layerFactory7 = CellScaleLayer.field_16200.create((LayerSampleContext<T>)longFunction.apply(10L), layerFactory4);
		return ImmutableList.of(layerFactory4, layerFactory7, layerFactory4);
	}

	public static BiomeLayerSampler[] build(long l, LevelGeneratorType levelGeneratorType, OverworldChunkGeneratorConfig overworldChunkGeneratorConfig) {
		int i = 25;
		ImmutableList<LayerFactory<CachingLayerSampler>> immutableList = build(
			levelGeneratorType, overworldChunkGeneratorConfig, m -> new CachingLayerContext(25, l, m)
		);
		BiomeLayerSampler biomeLayerSampler = new BiomeLayerSampler((LayerFactory<CachingLayerSampler>)immutableList.get(0));
		BiomeLayerSampler biomeLayerSampler2 = new BiomeLayerSampler((LayerFactory<CachingLayerSampler>)immutableList.get(1));
		BiomeLayerSampler biomeLayerSampler3 = new BiomeLayerSampler((LayerFactory<CachingLayerSampler>)immutableList.get(2));
		return new BiomeLayerSampler[]{biomeLayerSampler, biomeLayerSampler2, biomeLayerSampler3};
	}

	public static boolean areSimilar(int i, int j) {
		if (i == j) {
			return true;
		} else {
			Biome biome = Registry.BIOME.get(i);
			Biome biome2 = Registry.BIOME.get(j);
			if (biome == null || biome2 == null) {
				return false;
			} else if (biome != Biomes.field_9410 && biome != Biomes.field_9433) {
				return biome.getCategory() != Biome.Category.NONE && biome2.getCategory() != Biome.Category.NONE && biome.getCategory() == biome2.getCategory()
					? true
					: biome == biome2;
			} else {
				return biome2 == Biomes.field_9410 || biome2 == Biomes.field_9433;
			}
		}
	}

	public static boolean isOcean(int i) {
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

	public static boolean isShallowOcean(int i) {
		return i == WARM_OCEAN_ID || i == LUKEWARM_OCEAN_ID || i == OCEAN_ID || i == COLD_OCEAN_ID || i == FROZEN_OCEAN_ID;
	}
}
