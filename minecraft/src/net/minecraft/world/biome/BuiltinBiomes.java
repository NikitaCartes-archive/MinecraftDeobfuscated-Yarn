package net.minecraft.world.biome;

import net.minecraft.class_6726;
import net.minecraft.class_6727;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;

public abstract class BuiltinBiomes {
	@Deprecated
	public static final Biome THE_VOID = register(BiomeKeys.THE_VOID, DefaultBiomeCreator.createTheVoid());
	@Deprecated
	public static final Biome PLAINS = register(BiomeKeys.PLAINS, DefaultBiomeCreator.createPlains(false, false, false));

	private static Biome register(RegistryKey<Biome> key, Biome biome) {
		return BuiltinRegistries.set(BuiltinRegistries.BIOME, key, biome);
	}

	static {
		register(BiomeKeys.SUNFLOWER_PLAINS, DefaultBiomeCreator.createPlains(true, false, false));
		register(BiomeKeys.SNOWY_PLAINS, DefaultBiomeCreator.createPlains(false, true, false));
		register(BiomeKeys.ICE_SPIKES, DefaultBiomeCreator.createPlains(false, true, true));
		register(BiomeKeys.DESERT, DefaultBiomeCreator.createDesert());
		register(BiomeKeys.SWAMP, DefaultBiomeCreator.createSwamp());
		register(BiomeKeys.FOREST, DefaultBiomeCreator.createNormalForest(false, false, false));
		register(BiomeKeys.FLOWER_FOREST, DefaultBiomeCreator.createNormalForest(false, false, true));
		register(BiomeKeys.BIRCH_FOREST, DefaultBiomeCreator.createNormalForest(true, false, false));
		register(BiomeKeys.DARK_FOREST, DefaultBiomeCreator.createDarkForest());
		register(BiomeKeys.OLD_GROWTH_BIRCH_FOREST, DefaultBiomeCreator.createNormalForest(true, true, false));
		register(BiomeKeys.OLD_GROWTH_PINE_TAIGA, DefaultBiomeCreator.createOldGrowthPineTaiga(false));
		register(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA, DefaultBiomeCreator.createOldGrowthPineTaiga(true));
		register(BiomeKeys.TAIGA, DefaultBiomeCreator.createTaiga(false));
		register(BiomeKeys.SNOWY_TAIGA, DefaultBiomeCreator.createTaiga(true));
		register(BiomeKeys.SAVANNA, DefaultBiomeCreator.createSavanna(false, false));
		register(BiomeKeys.SAVANNA_PLATEAU, DefaultBiomeCreator.createSavanna(false, true));
		register(BiomeKeys.WINDSWEPT_HILLS, DefaultBiomeCreator.createWindsweptHills(false));
		register(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS, DefaultBiomeCreator.createWindsweptHills(false));
		register(BiomeKeys.WINDSWEPT_FOREST, DefaultBiomeCreator.createWindsweptHills(true));
		register(BiomeKeys.WINDSWEPT_SAVANNA, DefaultBiomeCreator.createSavanna(true, false));
		register(BiomeKeys.JUNGLE, DefaultBiomeCreator.createJungle());
		register(BiomeKeys.SPARSE_JUNGLE, DefaultBiomeCreator.createSparseJungle());
		register(BiomeKeys.BAMBOO_JUNGLE, DefaultBiomeCreator.createNormalBambooJungle());
		register(BiomeKeys.BADLANDS, DefaultBiomeCreator.createNormalBadlands(false));
		register(BiomeKeys.ERODED_BADLANDS, DefaultBiomeCreator.createNormalBadlands(false));
		register(BiomeKeys.WOODED_BADLANDS, DefaultBiomeCreator.createNormalBadlands(true));
		register(BiomeKeys.MEADOW, DefaultBiomeCreator.createMeadow());
		register(BiomeKeys.GROVE, DefaultBiomeCreator.createGrove());
		register(BiomeKeys.SNOWY_SLOPES, DefaultBiomeCreator.createSnowySlopes());
		register(BiomeKeys.FROZEN_PEAKS, DefaultBiomeCreator.createFrozenPeaks());
		register(BiomeKeys.JAGGED_PEAKS, DefaultBiomeCreator.createJaggedPeaks());
		register(BiomeKeys.STONY_PEAKS, DefaultBiomeCreator.createStonyPeaks());
		register(BiomeKeys.RIVER, DefaultBiomeCreator.createRiver(false));
		register(BiomeKeys.FROZEN_RIVER, DefaultBiomeCreator.createRiver(true));
		register(BiomeKeys.BEACH, DefaultBiomeCreator.createBeach(false, false));
		register(BiomeKeys.SNOWY_BEACH, DefaultBiomeCreator.createBeach(true, false));
		register(BiomeKeys.STONY_SHORE, DefaultBiomeCreator.createBeach(false, true));
		register(BiomeKeys.WARM_OCEAN, DefaultBiomeCreator.createWarmOcean());
		register(BiomeKeys.DEEP_WARM_OCEAN, DefaultBiomeCreator.createDeepWarmOcean());
		register(BiomeKeys.LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(false));
		register(BiomeKeys.DEEP_LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(true));
		register(BiomeKeys.OCEAN, DefaultBiomeCreator.createNormalOcean(false));
		register(BiomeKeys.DEEP_OCEAN, DefaultBiomeCreator.createNormalOcean(true));
		register(BiomeKeys.COLD_OCEAN, DefaultBiomeCreator.createColdOcean(false));
		register(BiomeKeys.DEEP_COLD_OCEAN, DefaultBiomeCreator.createColdOcean(true));
		register(BiomeKeys.FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(false));
		register(BiomeKeys.DEEP_FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(true));
		register(BiomeKeys.MUSHROOM_FIELDS, DefaultBiomeCreator.createMushroomFields());
		register(BiomeKeys.DRIPSTONE_CAVES, DefaultBiomeCreator.createDripstoneCaves());
		register(BiomeKeys.LUSH_CAVES, DefaultBiomeCreator.createLushCaves());
		register(BiomeKeys.NETHER_WASTES, class_6727.method_39146());
		register(BiomeKeys.WARPED_FOREST, class_6727.method_39150());
		register(BiomeKeys.CRIMSON_FOREST, class_6727.method_39149());
		register(BiomeKeys.SOUL_SAND_VALLEY, class_6727.method_39147());
		register(BiomeKeys.BASALT_DELTAS, class_6727.method_39148());
		register(BiomeKeys.THE_END, class_6726.method_39142());
		register(BiomeKeys.END_HIGHLANDS, class_6726.method_39144());
		register(BiomeKeys.END_MIDLANDS, class_6726.method_39143());
		register(BiomeKeys.SMALL_END_ISLANDS, class_6726.method_39145());
		register(BiomeKeys.END_BARRENS, class_6726.method_39140());
	}
}
