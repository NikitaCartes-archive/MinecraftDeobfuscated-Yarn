package net.minecraft.world.biome;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public abstract class BuiltinBiomes {
	public static final Biome PLAINS = register(BiomeKeys.PLAINS, DefaultBiomeCreator.createPlains(false));
	public static final Biome THE_VOID = register(BiomeKeys.THE_VOID, DefaultBiomeCreator.createTheVoid());

	private static Biome register(RegistryKey<Biome> key, Biome biome) {
		return BuiltinRegistries.set(BuiltinRegistries.BIOME, key, biome);
	}

	static {
		register(BiomeKeys.OCEAN, DefaultBiomeCreator.createNormalOcean(false));
		register(BiomeKeys.DESERT, DefaultBiomeCreator.createDesert(true));
		register(BiomeKeys.WINDSWEPT_HILLS, DefaultBiomeCreator.createMountains(ConfiguredSurfaceBuilders.MOUNTAIN, false));
		register(BiomeKeys.FOREST, DefaultBiomeCreator.createNormalForest());
		register(BiomeKeys.TAIGA, DefaultBiomeCreator.createTaiga(false, false));
		register(BiomeKeys.SWAMP, DefaultBiomeCreator.createSwamp(false));
		register(BiomeKeys.RIVER, DefaultBiomeCreator.createRiver(0.5F, 4159204, false));
		register(BiomeKeys.NETHER_WASTES, DefaultBiomeCreator.createNetherWastes());
		register(BiomeKeys.THE_END, DefaultBiomeCreator.createTheEnd());
		register(BiomeKeys.FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(false));
		register(BiomeKeys.FROZEN_RIVER, DefaultBiomeCreator.createRiver(0.0F, 3750089, true));
		register(BiomeKeys.SNOWY_PLAINS, DefaultBiomeCreator.createSnowyTundra(false));
		register(BiomeKeys.MUSHROOM_FIELDS, DefaultBiomeCreator.createMushroomFields());
		register(BiomeKeys.BEACH, DefaultBiomeCreator.createBeach(0.8F, 0.4F, 4159204, false, false));
		register(BiomeKeys.JUNGLE, DefaultBiomeCreator.createJungle());
		register(BiomeKeys.SPARSE_JUNGLE, DefaultBiomeCreator.createJungleEdge());
		register(BiomeKeys.DEEP_OCEAN, DefaultBiomeCreator.createNormalOcean(true));
		register(BiomeKeys.STONY_SHORE, DefaultBiomeCreator.createBeach(0.2F, 0.3F, 4159204, false, true));
		register(BiomeKeys.SNOWY_BEACH, DefaultBiomeCreator.createBeach(0.05F, 0.3F, 4020182, true, false));
		register(BiomeKeys.BIRCH_FOREST, DefaultBiomeCreator.createBirchForest(false));
		register(BiomeKeys.DARK_FOREST, DefaultBiomeCreator.createDarkForest(false));
		register(BiomeKeys.SNOWY_TAIGA, DefaultBiomeCreator.createTaiga(true, false));
		register(BiomeKeys.OLD_GROWTH_PINE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.3F, false));
		register(BiomeKeys.WINDSWEPT_FOREST, DefaultBiomeCreator.createMountains(ConfiguredSurfaceBuilders.GRASS, true));
		register(BiomeKeys.SAVANNA, DefaultBiomeCreator.createSavanna(1.2F, false));
		register(BiomeKeys.SAVANNA_PLATEAU, DefaultBiomeCreator.createSavannaPlateau());
		register(BiomeKeys.WOODED_BADLANDS, DefaultBiomeCreator.createWoodedBadlandsPlateau());
		register(BiomeKeys.BADLANDS, DefaultBiomeCreator.createNormalBadlands());
		register(BiomeKeys.SMALL_END_ISLANDS, DefaultBiomeCreator.createSmallEndIslands());
		register(BiomeKeys.END_MIDLANDS, DefaultBiomeCreator.createEndMidlands());
		register(BiomeKeys.END_HIGHLANDS, DefaultBiomeCreator.createEndHighlands());
		register(BiomeKeys.END_BARRENS, DefaultBiomeCreator.createEndBarrens());
		register(BiomeKeys.WARM_OCEAN, DefaultBiomeCreator.createWarmOcean());
		register(BiomeKeys.LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(false));
		register(BiomeKeys.COLD_OCEAN, DefaultBiomeCreator.createColdOcean(false));
		register(BiomeKeys.DEEP_WARM_OCEAN, DefaultBiomeCreator.createDeepWarmOcean());
		register(BiomeKeys.DEEP_LUKEWARM_OCEAN, DefaultBiomeCreator.createLukewarmOcean(true));
		register(BiomeKeys.DEEP_COLD_OCEAN, DefaultBiomeCreator.createColdOcean(true));
		register(BiomeKeys.DEEP_FROZEN_OCEAN, DefaultBiomeCreator.createFrozenOcean(true));
		register(BiomeKeys.SUNFLOWER_PLAINS, DefaultBiomeCreator.createPlains(true));
		register(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS, DefaultBiomeCreator.createMountains(ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false));
		register(BiomeKeys.FLOWER_FOREST, DefaultBiomeCreator.createFlowerForest());
		register(BiomeKeys.ICE_SPIKES, DefaultBiomeCreator.createSnowyTundra(true));
		register(BiomeKeys.OLD_GROWTH_BIRCH_FOREST, DefaultBiomeCreator.createBirchForest(true));
		register(BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA, DefaultBiomeCreator.createGiantTreeTaiga(0.25F, true));
		register(BiomeKeys.WINDSWEPT_SAVANNA, DefaultBiomeCreator.createSavanna(1.1F, true));
		register(BiomeKeys.ERODED_BADLANDS, DefaultBiomeCreator.createErodedBadlands());
		register(BiomeKeys.BAMBOO_JUNGLE, DefaultBiomeCreator.createNormalBambooJungle());
		register(BiomeKeys.SOUL_SAND_VALLEY, DefaultBiomeCreator.createSoulSandValley());
		register(BiomeKeys.CRIMSON_FOREST, DefaultBiomeCreator.createCrimsonForest());
		register(BiomeKeys.WARPED_FOREST, DefaultBiomeCreator.createWarpedForest());
		register(BiomeKeys.BASALT_DELTAS, DefaultBiomeCreator.createBasaltDeltas());
		register(BiomeKeys.DRIPSTONE_CAVES, DefaultBiomeCreator.createDripstoneCaves());
		register(BiomeKeys.LUSH_CAVES, DefaultBiomeCreator.createLushCaves());
		register(BiomeKeys.MEADOW, DefaultBiomeCreator.composeMeadowSettings());
		register(BiomeKeys.GROVE, DefaultBiomeCreator.composeGroveSettings());
		register(BiomeKeys.SNOWY_SLOPES, DefaultBiomeCreator.composeSnowySlopesSettings());
		register(BiomeKeys.FROZEN_PEAKS, DefaultBiomeCreator.composeSnowcappedPeaksSettings());
		register(BiomeKeys.JAGGED_PEAKS, DefaultBiomeCreator.composeLoftyPeaksSettings());
		register(BiomeKeys.STONY_PEAKS, DefaultBiomeCreator.composeStonyPeaksSettings());
	}
}
