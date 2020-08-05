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
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26719, 16);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26719, 26);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26722, 2);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26722, 17);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26722, 130);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26712, 131);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26712, 162);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26712, 20);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26712, 3);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26712, 34);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26720, 27);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26720, 28);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26720, 29);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26720, 157);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26720, 132);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26720, 4);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26720, 155);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26720, 156);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26720, 18);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26718, 140);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26718, 13);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26718, 12);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26713, 168);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26713, 169);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26713, 21);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26713, 23);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26713, 22);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26713, 149);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26713, 151);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26714, 37);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26714, 165);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26714, 167);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26714, 166);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26715, 39);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26715, 38);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26725, 14);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26725, 15);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26710, 25);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26721, 46);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26721, 49);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26721, 50);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26721, 48);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26721, 24);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26721, 47);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26721, 10);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26721, 45);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26721, 0);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26721, 44);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26716, 1);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26716, 129);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26723, 11);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26723, 7);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26717, 35);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26717, 36);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26717, 163);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26717, 164);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26724, 6);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26724, 134);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26711, 160);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26711, 161);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26711, 32);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26711, 33);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26711, 30);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26711, 31);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26711, 158);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26711, 5);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26711, 19);
		method_31117(int2IntOpenHashMap, BiomeLayers.class_5503.field_26711, 133);
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
		layerFactory3 = SmoothenShorelineLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), layerFactory3);
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

		layerFactory4 = SmoothenShorelineLayer.INSTANCE.create((LayerSampleContext)contextProvider.apply(1000L), layerFactory4);
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

	private static void method_31117(Int2IntOpenHashMap int2IntOpenHashMap, BiomeLayers.class_5503 arg, int i) {
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
