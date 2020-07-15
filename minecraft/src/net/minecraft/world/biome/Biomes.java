package net.minecraft.world.biome;

import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public abstract class Biomes {
	public static final IdList<Biome> MUTATED_BIOMES = new IdList<>();
	public static final Biome OCEAN = register(0, "ocean", DefaultBiomeCreator.createNormalOcean(false));
	public static final Biome DEFAULT = OCEAN;
	public static final Biome PLAINS = register(1, "plains", DefaultBiomeCreator.createPlains(null, false));
	public static final Biome DESERT = register(2, "desert", DefaultBiomeCreator.createDesert(null, 0.125F, 0.05F, true, true, true));
	public static final Biome MOUNTAINS = register(
		3, "mountains", DefaultBiomeCreator.createMountains(1.0F, 0.5F, ConfiguredSurfaceBuilders.MOUNTAIN, false, null)
	);
	public static final Biome FOREST = register(4, "forest", DefaultBiomeCreator.createNormalForest(0.1F, 0.2F));
	public static final Biome TAIGA = register(5, "taiga", DefaultBiomeCreator.createTaiga(null, 0.2F, 0.2F, false, false, true, false));
	public static final Biome SWAMP = register(6, "swamp", DefaultBiomeCreator.createSwamp(null, -0.2F, 0.1F, false));
	public static final Biome RIVER = register(7, "river", DefaultBiomeCreator.createRiver(-0.5F, 0.0F, 0.5F, 4159204, false));
	public static final Biome NETHER_WASTES = register(8, "nether_wastes", DefaultBiomeCreator.createNetherWastes());
	public static final Biome THE_END = register(9, "the_end", DefaultBiomeCreator.createTheEnd());
	public static final Biome FROZEN_OCEAN = register(10, "frozen_ocean", DefaultBiomeCreator.createFrozenOcean(false));
	public static final Biome FROZEN_RIVER = register(11, "frozen_river", DefaultBiomeCreator.createRiver(-0.5F, 0.0F, 0.0F, 3750089, true));
	public static final Biome SNOWY_TUNDRA = register(12, "snowy_tundra", DefaultBiomeCreator.createSnowyTundra(null, 0.125F, 0.05F, false, false));
	public static final Biome SNOWY_MOUNTAINS = register(13, "snowy_mountains", DefaultBiomeCreator.createSnowyTundra(null, 0.45F, 0.3F, false, true));
	public static final Biome MUSHROOM_FIELDS = register(14, "mushroom_fields", DefaultBiomeCreator.createMushroomFields(0.2F, 0.3F));
	public static final Biome MUSHROOM_FIELD_SHORE = register(15, "mushroom_field_shore", DefaultBiomeCreator.createMushroomFields(0.0F, 0.025F));
	public static final Biome BEACH = register(16, "beach", DefaultBiomeCreator.createBeach(0.0F, 0.025F, 0.8F, 0.4F, 4159204, false, false));
	public static final Biome DESERT_HILLS = register(17, "desert_hills", DefaultBiomeCreator.createDesert(null, 0.45F, 0.3F, false, true, false));
	public static final Biome WOODED_HILLS = register(18, "wooded_hills", DefaultBiomeCreator.createNormalForest(0.45F, 0.3F));
	public static final Biome TAIGA_HILLS = register(19, "taiga_hills", DefaultBiomeCreator.createTaiga(null, 0.45F, 0.3F, false, false, false, false));
	public static final Biome MOUNTAIN_EDGE = register(
		20, "mountain_edge", DefaultBiomeCreator.createMountains(0.8F, 0.3F, ConfiguredSurfaceBuilders.GRASS, true, null)
	);
	public static final Biome JUNGLE = register(21, "jungle", DefaultBiomeCreator.createJungle());
	public static final Biome JUNGLE_HILLS = register(22, "jungle_hills", DefaultBiomeCreator.createJungleHills());
	public static final Biome JUNGLE_EDGE = register(23, "jungle_edge", DefaultBiomeCreator.createJungleEdge());
	public static final Biome DEEP_OCEAN = register(24, "deep_ocean", DefaultBiomeCreator.createNormalOcean(true));
	public static final Biome STONE_SHORE = register(25, "stone_shore", DefaultBiomeCreator.createBeach(0.1F, 0.8F, 0.2F, 0.3F, 4159204, false, true));
	public static final Biome SNOWY_BEACH = register(26, "snowy_beach", DefaultBiomeCreator.createBeach(0.0F, 0.025F, 0.05F, 0.3F, 4020182, true, false));
	public static final Biome BIRCH_FOREST = register(27, "birch_forest", DefaultBiomeCreator.createBirchForest(0.1F, 0.2F, null, false));
	public static final Biome BIRCH_FOREST_HILLS = register(28, "birch_forest_hills", DefaultBiomeCreator.createBirchForest(0.45F, 0.3F, null, false));
	public static final Biome DARK_FOREST = register(29, "dark_forest", DefaultBiomeCreator.createDarkForest(null, 0.1F, 0.2F, false));
	public static final Biome SNOWY_TAIGA = register(30, "snowy_taiga", DefaultBiomeCreator.createTaiga(null, 0.2F, 0.2F, true, false, false, true));
	public static final Biome SNOWY_TAIGA_HILLS = register(31, "snowy_taiga_hills", DefaultBiomeCreator.createTaiga(null, 0.45F, 0.3F, true, false, false, false));
	public static final Biome GIANT_TREE_TAIGA = register(32, "giant_tree_taiga", DefaultBiomeCreator.createGiantTreeTaiga(0.2F, 0.2F, 0.3F, false, null));
	public static final Biome GIANT_TREE_TAIGA_HILLS = register(
		33, "giant_tree_taiga_hills", DefaultBiomeCreator.createGiantTreeTaiga(0.45F, 0.3F, 0.3F, false, null)
	);
	public static final Biome WOODED_MOUNTAINS = register(
		34, "wooded_mountains", DefaultBiomeCreator.createMountains(1.0F, 0.5F, ConfiguredSurfaceBuilders.GRASS, true, null)
	);
	public static final Biome SAVANNA = register(35, "savanna", DefaultBiomeCreator.createSavanna(null, 0.125F, 0.05F, 1.2F, false, false));
	public static final Biome SAVANNA_PLATEAU = register(36, "savanna_plateau", DefaultBiomeCreator.createSavannaPlateau());
	public static final Biome BADLANDS = register(37, "badlands", DefaultBiomeCreator.createNormalBadlands(null, 0.1F, 0.2F, false));
	public static final Biome WOODED_BADLANDS_PLATEAU = register(
		38, "wooded_badlands_plateau", DefaultBiomeCreator.createWoodedBadlandsPlateau(null, 1.5F, 0.025F)
	);
	public static final Biome BADLANDS_PLATEAU = register(39, "badlands_plateau", DefaultBiomeCreator.createNormalBadlands(null, 1.5F, 0.025F, true));
	public static final Biome SMALL_END_ISLANDS = register(40, "small_end_islands", DefaultBiomeCreator.createSmallEndIslands());
	public static final Biome END_MIDLANDS = register(41, "end_midlands", DefaultBiomeCreator.createEndMidlands());
	public static final Biome END_HIGHLANDS = register(42, "end_highlands", DefaultBiomeCreator.createEndHighlands());
	public static final Biome END_BARRENS = register(43, "end_barrens", DefaultBiomeCreator.createEndBarrens());
	public static final Biome WARM_OCEAN = register(44, "warm_ocean", DefaultBiomeCreator.createWarmOcean());
	public static final Biome LUKEWARM_OCEAN = register(45, "lukewarm_ocean", DefaultBiomeCreator.createLukewarmOcean(false));
	public static final Biome COLD_OCEAN = register(46, "cold_ocean", DefaultBiomeCreator.createColdOcean(false));
	public static final Biome DEEP_WARM_OCEAN = register(47, "deep_warm_ocean", DefaultBiomeCreator.createDeepWarmOcean());
	public static final Biome DEEP_LUKEWARM_OCEAN = register(48, "deep_lukewarm_ocean", DefaultBiomeCreator.createLukewarmOcean(true));
	public static final Biome DEEP_COLD_OCEAN = register(49, "deep_cold_ocean", DefaultBiomeCreator.createColdOcean(true));
	public static final Biome DEEP_FROZEN_OCEAN = register(50, "deep_frozen_ocean", DefaultBiomeCreator.createFrozenOcean(true));
	public static final Biome THE_VOID = register(127, "the_void", DefaultBiomeCreator.createTheVoid());
	public static final Biome SUNFLOWER_PLAINS = register(129, "sunflower_plains", DefaultBiomeCreator.createPlains("plains", true));
	public static final Biome DESERT_LAKES = register(130, "desert_lakes", DefaultBiomeCreator.createDesert("desert", 0.225F, 0.25F, false, false, false));
	public static final Biome GRAVELLY_MOUNTAINS = register(
		131, "gravelly_mountains", DefaultBiomeCreator.createMountains(1.0F, 0.5F, ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false, "mountains")
	);
	public static final Biome FLOWER_FOREST = register(132, "flower_forest", DefaultBiomeCreator.createFlowerForest());
	public static final Biome TAIGA_MOUNTAINS = register(133, "taiga_mountains", DefaultBiomeCreator.createTaiga("taiga", 0.3F, 0.4F, false, true, false, false));
	public static final Biome SWAMP_HILLS = register(134, "swamp_hills", DefaultBiomeCreator.createSwamp("swamp", -0.1F, 0.3F, true));
	public static final Biome ICE_SPIKES = register(140, "ice_spikes", DefaultBiomeCreator.createSnowyTundra("snowy_tundra", 0.425F, 0.45000002F, true, false));
	public static final Biome MODIFIED_JUNGLE = register(149, "modified_jungle", DefaultBiomeCreator.createModifiedJungle());
	public static final Biome MODIFIED_JUNGLE_EDGE = register(151, "modified_jungle_edge", DefaultBiomeCreator.createModifiedJungleEdge());
	public static final Biome TALL_BIRCH_FOREST = register(155, "tall_birch_forest", DefaultBiomeCreator.createBirchForest(0.2F, 0.4F, "birch_forest", true));
	public static final Biome TALL_BIRCH_HILLS = register(156, "tall_birch_hills", DefaultBiomeCreator.createBirchForest(0.55F, 0.5F, "birch_forest_hills", true));
	public static final Biome DARK_FOREST_HILLS = register(157, "dark_forest_hills", DefaultBiomeCreator.createDarkForest("dark_forest", 0.2F, 0.4F, true));
	public static final Biome SNOWY_TAIGA_MOUNTAINS = register(
		158, "snowy_taiga_mountains", DefaultBiomeCreator.createTaiga("snowy_taiga", 0.3F, 0.4F, true, true, false, false)
	);
	public static final Biome GIANT_SPRUCE_TAIGA = register(
		160, "giant_spruce_taiga", DefaultBiomeCreator.createGiantTreeTaiga(0.2F, 0.2F, 0.25F, true, "giant_tree_taiga")
	);
	public static final Biome GIANT_SPRUCE_TAIGA_HILLS = register(
		161, "giant_spruce_taiga_hills", DefaultBiomeCreator.createGiantTreeTaiga(0.2F, 0.2F, 0.25F, true, "giant_tree_taiga_hills")
	);
	public static final Biome MODIFIED_GRAVELLY_MOUNTAINS = register(
		162, "modified_gravelly_mountains", DefaultBiomeCreator.createMountains(1.0F, 0.5F, ConfiguredSurfaceBuilders.GRAVELLY_MOUNTAIN, false, "wooded_mountains")
	);
	public static final Biome SHATTERED_SAVANNA = register(
		163, "shattered_savanna", DefaultBiomeCreator.createSavanna("savanna", 0.3625F, 1.225F, 1.1F, true, true)
	);
	public static final Biome SHATTERED_SAVANNA_PLATEAU = register(
		164, "shattered_savanna_plateau", DefaultBiomeCreator.createSavanna("savanna_plateau", 1.05F, 1.2125001F, 1.0F, true, true)
	);
	public static final Biome ERODED_BADLANDS = register(165, "eroded_badlands", DefaultBiomeCreator.createErodedBadlands());
	public static final Biome MODIFIED_WOODED_BADLANDS_PLATEAU = register(
		166, "modified_wooded_badlands_plateau", DefaultBiomeCreator.createWoodedBadlandsPlateau("wooded_badlands_plateau", 0.45F, 0.3F)
	);
	public static final Biome MODIFIED_BADLANDS_PLATEAU = register(
		167, "modified_badlands_plateau", DefaultBiomeCreator.createNormalBadlands("badlands_plateau", 0.45F, 0.3F, true)
	);
	public static final Biome BAMBOO_JUNGLE = register(168, "bamboo_jungle", DefaultBiomeCreator.createNormalBambooJungle());
	public static final Biome BAMBOO_JUNGLE_HILLS = register(169, "bamboo_jungle_hills", DefaultBiomeCreator.createBambooJungleHills());
	public static final Biome SOUL_SAND_VALLEY = register(170, "soul_sand_valley", DefaultBiomeCreator.createSoulSandValley());
	public static final Biome CRIMSON_FOREST = register(171, "crimson_forest", DefaultBiomeCreator.createCrimsonForest());
	public static final Biome WARPED_FOREST = register(172, "warped_forest", DefaultBiomeCreator.createWarpedForest());
	public static final Biome BASALT_DELTAS = register(173, "basalt_deltas", DefaultBiomeCreator.createBasaltDeltas());

	private static Biome register(int rawId, String id, Biome biome) {
		BuiltinRegistries.set(BuiltinRegistries.BIOME, rawId, id, biome);
		if (biome.hasParent()) {
			MUTATED_BIOMES.set(biome, BuiltinRegistries.BIOME.getRawId(BuiltinRegistries.BIOME.get(new Identifier(biome.parent))));
		}

		return biome;
	}

	@Nullable
	public static Biome getMutated(Biome biome) {
		return MUTATED_BIOMES.get(BuiltinRegistries.BIOME.getRawId(biome));
	}

	static {
		Collections.addAll(
			Biome.BIOMES,
			new Biome[]{
				OCEAN,
				PLAINS,
				DESERT,
				MOUNTAINS,
				FOREST,
				TAIGA,
				SWAMP,
				RIVER,
				FROZEN_RIVER,
				SNOWY_TUNDRA,
				SNOWY_MOUNTAINS,
				MUSHROOM_FIELDS,
				MUSHROOM_FIELD_SHORE,
				BEACH,
				DESERT_HILLS,
				WOODED_HILLS,
				TAIGA_HILLS,
				JUNGLE,
				JUNGLE_HILLS,
				JUNGLE_EDGE,
				DEEP_OCEAN,
				STONE_SHORE,
				SNOWY_BEACH,
				BIRCH_FOREST,
				BIRCH_FOREST_HILLS,
				DARK_FOREST,
				SNOWY_TAIGA,
				SNOWY_TAIGA_HILLS,
				GIANT_TREE_TAIGA,
				GIANT_TREE_TAIGA_HILLS,
				WOODED_MOUNTAINS,
				SAVANNA,
				SAVANNA_PLATEAU,
				BADLANDS,
				WOODED_BADLANDS_PLATEAU,
				BADLANDS_PLATEAU
			}
		);
	}
}
