package net.minecraft.world.gen.feature;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarvers;

public class DefaultBiomeFeatures {
	public static void addLandCarvers(GenerationSettings.LookupBackedBuilder builder) {
		builder.carver(GenerationStep.Carver.AIR, ConfiguredCarvers.CAVE);
		builder.carver(GenerationStep.Carver.AIR, ConfiguredCarvers.CAVE_EXTRA_UNDERGROUND);
		builder.carver(GenerationStep.Carver.AIR, ConfiguredCarvers.CANYON);
		builder.feature(GenerationStep.Feature.LAKES, MiscPlacedFeatures.LAKE_LAVA_UNDERGROUND);
		builder.feature(GenerationStep.Feature.LAKES, MiscPlacedFeatures.LAKE_LAVA_SURFACE);
	}

	public static void addDungeons(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.UNDERGROUND_STRUCTURES, UndergroundPlacedFeatures.MONSTER_ROOM);
		builder.feature(GenerationStep.Feature.UNDERGROUND_STRUCTURES, UndergroundPlacedFeatures.MONSTER_ROOM_DEEP);
	}

	public static void addMineables(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_DIRT);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_GRAVEL);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_GRANITE_UPPER);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_GRANITE_LOWER);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_DIORITE_UPPER);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_DIORITE_LOWER);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_ANDESITE_UPPER);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_ANDESITE_LOWER);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_TUFF);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, UndergroundPlacedFeatures.GLOW_LICHEN);
	}

	public static void addDripstone(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, UndergroundPlacedFeatures.LARGE_DRIPSTONE);
		builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, UndergroundPlacedFeatures.DRIPSTONE_CLUSTER);
		builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, UndergroundPlacedFeatures.POINTED_DRIPSTONE);
	}

	public static void addSculk(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, UndergroundPlacedFeatures.SCULK_VEIN);
		builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, UndergroundPlacedFeatures.SCULK_PATCH_DEEP_DARK);
	}

	public static void addDefaultOres(GenerationSettings.LookupBackedBuilder builder) {
		addDefaultOres(builder, false);
	}

	public static void addDefaultOres(GenerationSettings.LookupBackedBuilder builder, boolean largeCopperOreBlob) {
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_COAL_UPPER);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_COAL_LOWER);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_IRON_UPPER);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_IRON_MIDDLE);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_IRON_SMALL);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_GOLD);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_GOLD_LOWER);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_REDSTONE);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_REDSTONE_LOWER);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_DIAMOND);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_DIAMOND_LARGE);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_DIAMOND_BURIED);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_LAPIS);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_LAPIS_BURIED);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, largeCopperOreBlob ? OrePlacedFeatures.ORE_COPPER_LARGE : OrePlacedFeatures.ORE_COPPER);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, UndergroundPlacedFeatures.UNDERWATER_MAGMA);
	}

	public static void addExtraGoldOre(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_GOLD_EXTRA);
	}

	public static void addEmeraldOre(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_EMERALD);
	}

	public static void addInfestedStone(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, OrePlacedFeatures.ORE_INFESTED);
	}

	public static void addDefaultDisks(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, MiscPlacedFeatures.DISK_SAND);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, MiscPlacedFeatures.DISK_CLAY);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, MiscPlacedFeatures.DISK_GRAVEL);
	}

	public static void addClayDisk(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, MiscPlacedFeatures.DISK_CLAY);
	}

	public static void addGrassAndClayDisks(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, MiscPlacedFeatures.DISK_GRASS);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, MiscPlacedFeatures.DISK_CLAY);
	}

	public static void addMossyRocks(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, MiscPlacedFeatures.FOREST_ROCK);
	}

	public static void addLargeFerns(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_LARGE_FERN);
	}

	public static void addSweetBerryBushesSnowy(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_BERRY_RARE);
	}

	public static void addSweetBerryBushes(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_BERRY_COMMON);
	}

	public static void addBamboo(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.BAMBOO_LIGHT);
	}

	public static void addBambooJungleTrees(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.BAMBOO);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.BAMBOO_VEGETATION);
	}

	public static void addTaigaTrees(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.TREES_TAIGA);
	}

	public static void addGroveTrees(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.TREES_GROVE);
	}

	public static void addWaterBiomeOakTrees(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.TREES_WATER);
	}

	public static void addBirchTrees(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.TREES_BIRCH);
	}

	public static void addForestTrees(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.TREES_BIRCH_AND_OAK);
	}

	public static void addTallBirchTrees(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.BIRCH_TALL);
	}

	public static void addSavannaTrees(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.TREES_SAVANNA);
	}

	public static void addExtraSavannaTrees(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.TREES_WINDSWEPT_SAVANNA);
	}

	public static void addLushCavesDecoration(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, UndergroundPlacedFeatures.LUSH_CAVES_CEILING_VEGETATION);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, UndergroundPlacedFeatures.CAVE_VINES);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, UndergroundPlacedFeatures.LUSH_CAVES_CLAY);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, UndergroundPlacedFeatures.LUSH_CAVES_VEGETATION);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, UndergroundPlacedFeatures.ROOTED_AZALEA_TREE);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, UndergroundPlacedFeatures.SPORE_BLOSSOM);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, UndergroundPlacedFeatures.CLASSIC_VINES_CAVE_FEATURE);
	}

	public static void addClayOre(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_CLAY);
	}

	public static void addWindsweptHillsTrees(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.TREES_WINDSWEPT_HILLS);
	}

	public static void addWindsweptForestTrees(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.TREES_WINDSWEPT_FOREST);
	}

	public static void addJungleTrees(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.TREES_JUNGLE);
	}

	public static void addSparseJungleTrees(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.TREES_SPARSE_JUNGLE);
	}

	public static void addBadlandsPlateauTrees(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.TREES_BADLANDS);
	}

	public static void addSnowySpruceTrees(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.TREES_SNOWY);
	}

	public static void addJungleGrass(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_GRASS_JUNGLE);
	}

	public static void addSavannaTallGrass(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_TALL_GRASS);
	}

	public static void addWindsweptSavannaGrass(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_GRASS_NORMAL);
	}

	public static void addSavannaGrass(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_GRASS_SAVANNA);
	}

	public static void addBadlandsGrass(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_GRASS_BADLANDS);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_DEAD_BUSH_BADLANDS);
	}

	public static void addForestFlowers(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.FOREST_FLOWERS);
	}

	public static void addForestGrass(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_GRASS_FOREST);
	}

	public static void addSwampFeatures(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.TREES_SWAMP);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.FLOWER_SWAMP);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_GRASS_NORMAL);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_DEAD_BUSH);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_WATERLILY);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.BROWN_MUSHROOM_SWAMP);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.RED_MUSHROOM_SWAMP);
	}

	public static void addMangroveSwampFeatures(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.TREES_MANGROVE);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_GRASS_NORMAL);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_DEAD_BUSH);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_WATERLILY);
	}

	public static void addMushroomFieldsFeatures(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.MUSHROOM_ISLAND_VEGETATION);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.BROWN_MUSHROOM_TAIGA);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.RED_MUSHROOM_TAIGA);
	}

	public static void addPlainsFeatures(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.TREES_PLAINS);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.FLOWER_PLAIN);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_GRASS_PLAIN);
	}

	public static void addDesertDeadBushes(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_DEAD_BUSH_2);
	}

	public static void addGiantTaigaGrass(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_GRASS_TAIGA);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_DEAD_BUSH);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.BROWN_MUSHROOM_OLD_GROWTH);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.RED_MUSHROOM_OLD_GROWTH);
	}

	public static void addDefaultFlowers(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.FLOWER_DEFAULT);
	}

	public static void addCherryGroveFeatures(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_GRASS_PLAIN);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.FLOWER_CHERRY);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.TREES_CHERRY);
	}

	public static void addMeadowFlowers(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_GRASS_PLAIN);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.FLOWER_MEADOW);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.TREES_MEADOW);
	}

	public static void addExtraDefaultFlowers(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.FLOWER_WARM);
	}

	public static void addDefaultGrass(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_GRASS_BADLANDS);
	}

	public static void addTaigaGrass(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_GRASS_TAIGA_2);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.BROWN_MUSHROOM_TAIGA);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.RED_MUSHROOM_TAIGA);
	}

	public static void addPlainsTallGrass(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_TALL_GRASS_2);
	}

	public static void addDefaultMushrooms(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.BROWN_MUSHROOM_NORMAL);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.RED_MUSHROOM_NORMAL);
	}

	public static void addDefaultVegetation(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_SUGAR_CANE);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_PUMPKIN);
	}

	public static void addBadlandsVegetation(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_SUGAR_CANE_BADLANDS);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_PUMPKIN);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_CACTUS_DECORATED);
	}

	public static void addMelons(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_MELON);
	}

	public static void addSparseMelons(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_MELON_SPARSE);
	}

	public static void addVines(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.VINES);
	}

	public static void addDesertVegetation(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_SUGAR_CANE_DESERT);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_PUMPKIN);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_CACTUS_DESERT);
	}

	public static void addSwampVegetation(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_SUGAR_CANE_SWAMP);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_PUMPKIN);
	}

	public static void addDesertFeatures(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.SURFACE_STRUCTURES, MiscPlacedFeatures.DESERT_WELL);
	}

	public static void addFossils(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.UNDERGROUND_STRUCTURES, UndergroundPlacedFeatures.FOSSIL_UPPER);
		builder.feature(GenerationStep.Feature.UNDERGROUND_STRUCTURES, UndergroundPlacedFeatures.FOSSIL_LOWER);
	}

	public static void addKelp(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, OceanPlacedFeatures.KELP_COLD);
	}

	public static void addSeagrassOnStone(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, OceanPlacedFeatures.SEAGRASS_SIMPLE);
	}

	public static void addLessKelp(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, OceanPlacedFeatures.KELP_WARM);
	}

	public static void addSprings(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.FLUID_SPRINGS, MiscPlacedFeatures.SPRING_WATER);
		builder.feature(GenerationStep.Feature.FLUID_SPRINGS, MiscPlacedFeatures.SPRING_LAVA);
	}

	public static void addFrozenLavaSpring(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.FLUID_SPRINGS, MiscPlacedFeatures.SPRING_LAVA_FROZEN);
	}

	public static void addIcebergs(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, MiscPlacedFeatures.ICEBERG_PACKED);
		builder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, MiscPlacedFeatures.ICEBERG_BLUE);
	}

	public static void addBlueIce(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.SURFACE_STRUCTURES, MiscPlacedFeatures.BLUE_ICE);
	}

	public static void addFrozenTopLayer(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, MiscPlacedFeatures.FREEZE_TOP_LAYER);
	}

	public static void addNetherMineables(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, OrePlacedFeatures.ORE_GRAVEL_NETHER);
		builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, OrePlacedFeatures.ORE_BLACKSTONE);
		builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, OrePlacedFeatures.ORE_GOLD_NETHER);
		builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, OrePlacedFeatures.ORE_QUARTZ_NETHER);
		addAncientDebris(builder);
	}

	public static void addAncientDebris(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, OrePlacedFeatures.ORE_ANCIENT_DEBRIS_LARGE);
		builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, OrePlacedFeatures.ORE_DEBRIS_SMALL);
	}

	public static void addAmethystGeodes(GenerationSettings.LookupBackedBuilder builder) {
		builder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, UndergroundPlacedFeatures.AMETHYST_GEODE);
	}

	public static void addFarmAnimals(SpawnSettings.Builder builder) {
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.SHEEP, 12, 4, 4));
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.PIG, 10, 4, 4));
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.CHICKEN, 10, 4, 4));
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.COW, 8, 4, 4));
	}

	public static void addCaveMobs(SpawnSettings.Builder builder) {
		builder.spawn(SpawnGroup.AMBIENT, new SpawnSettings.SpawnEntry(EntityType.BAT, 10, 8, 8));
		builder.spawn(SpawnGroup.UNDERGROUND_WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.GLOW_SQUID, 10, 4, 6));
	}

	public static void addBatsAndMonsters(SpawnSettings.Builder builder) {
		addCaveMobs(builder);
		addMonsters(builder, 95, 5, 100, false);
	}

	public static void addOceanMobs(SpawnSettings.Builder builder, int squidWeight, int squidMaxGroupSize, int codWeight) {
		builder.spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.SQUID, squidWeight, 1, squidMaxGroupSize));
		builder.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.COD, codWeight, 3, 6));
		addBatsAndMonsters(builder);
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.DROWNED, 5, 1, 1));
	}

	public static void addWarmOceanMobs(SpawnSettings.Builder builder, int squidWeight, int squidMinGroupSize) {
		builder.spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.SQUID, squidWeight, squidMinGroupSize, 4));
		builder.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.TROPICAL_FISH, 25, 8, 8));
		builder.spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.DOLPHIN, 2, 1, 2));
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.DROWNED, 5, 1, 1));
		addBatsAndMonsters(builder);
	}

	public static void addPlainsMobs(SpawnSettings.Builder builder) {
		addFarmAnimals(builder);
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.HORSE, 5, 2, 6));
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.DONKEY, 1, 1, 3));
		addBatsAndMonsters(builder);
	}

	public static void addSnowyMobs(SpawnSettings.Builder builder) {
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 10, 2, 3));
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.POLAR_BEAR, 1, 1, 2));
		addCaveMobs(builder);
		addMonsters(builder, 95, 5, 20, false);
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.STRAY, 80, 4, 4));
	}

	public static void addDesertMobs(SpawnSettings.Builder builder) {
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
		addCaveMobs(builder);
		addMonsters(builder, 19, 1, 100, false);
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.HUSK, 80, 4, 4));
	}

	public static void addDripstoneCaveMobs(SpawnSettings.Builder builder) {
		addCaveMobs(builder);
		int i = 95;
		addMonsters(builder, 95, 5, 100, false);
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.DROWNED, 95, 4, 4));
	}

	public static void addMonsters(SpawnSettings.Builder builder, int zombieWeight, int zombieVillagerWeight, int skeletonWeight, boolean drowned) {
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SPIDER, 100, 4, 4));
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(drowned ? EntityType.DROWNED : EntityType.ZOMBIE, zombieWeight, 4, 4));
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.ZOMBIE_VILLAGER, zombieVillagerWeight, 1, 1));
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SKELETON, skeletonWeight, 4, 4));
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.CREEPER, 100, 4, 4));
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SLIME, 100, 4, 4));
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.ENDERMAN, 10, 1, 4));
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.WITCH, 5, 1, 1));
	}

	public static void addMushroomMobs(SpawnSettings.Builder builder) {
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.MOOSHROOM, 8, 4, 8));
		addCaveMobs(builder);
	}

	public static void addJungleMobs(SpawnSettings.Builder builder) {
		addFarmAnimals(builder);
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.CHICKEN, 10, 4, 4));
		addBatsAndMonsters(builder);
	}

	public static void addEndMobs(SpawnSettings.Builder builder) {
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.ENDERMAN, 10, 4, 4));
	}
}
