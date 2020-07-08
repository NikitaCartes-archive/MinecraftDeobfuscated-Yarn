package net.minecraft.world.gen.feature;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarvers;

public class DefaultBiomeFeatures {
	public static void addBadlandsUndergroundStructures(Biome biome) {
		biome.addStructureFeature(ConfiguredStructureFeatures.MINESHAFT_MESA);
		biome.addStructureFeature(ConfiguredStructureFeatures.STRONGHOLD);
	}

	public static void addDefaultUndergroundStructures(Biome biome) {
		biome.addStructureFeature(ConfiguredStructureFeatures.MINESHAFT);
		biome.addStructureFeature(ConfiguredStructureFeatures.STRONGHOLD);
	}

	public static void addOceanStructures(Biome biome) {
		biome.addStructureFeature(ConfiguredStructureFeatures.MINESHAFT);
		biome.addStructureFeature(ConfiguredStructureFeatures.SHIPWRECK);
	}

	public static void addLandCarvers(Biome biome) {
		biome.addCarver(GenerationStep.Carver.AIR, ConfiguredCarvers.CAVE);
		biome.addCarver(GenerationStep.Carver.AIR, ConfiguredCarvers.CANYON);
	}

	public static void addOceanCarvers(Biome biome) {
		biome.addCarver(GenerationStep.Carver.AIR, ConfiguredCarvers.OCEAN_CAVE);
		biome.addCarver(GenerationStep.Carver.AIR, ConfiguredCarvers.CANYON);
		biome.addCarver(GenerationStep.Carver.LIQUID, ConfiguredCarvers.UNDERWATER_CANYON);
		biome.addCarver(GenerationStep.Carver.LIQUID, ConfiguredCarvers.UNDERWATER_CAVE);
	}

	public static void addDefaultLakes(Biome biome) {
		biome.addFeature(GenerationStep.Feature.LAKES, ConfiguredFeatures.LAKE_WATER);
		biome.addFeature(GenerationStep.Feature.LAKES, ConfiguredFeatures.LAKE_LAVA);
	}

	public static void addDesertLakes(Biome biome) {
		biome.addFeature(GenerationStep.Feature.LAKES, ConfiguredFeatures.LAKE_LAVA);
	}

	public static void addDungeons(Biome biome) {
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_STRUCTURES, ConfiguredFeatures.MONSTER_ROOM);
	}

	public static void addMineables(Biome biome) {
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_DIRT);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_GRAVEL);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_GRANITE);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_DIORITE);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_ANDESITE);
	}

	public static void addDefaultOres(Biome biome) {
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_COAL);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_IRON);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_GOLD);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_REDSTONE);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_DIAMOND);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_LAPIS);
	}

	public static void addExtraGoldOre(Biome biome) {
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_GOLD_EXTRA);
	}

	public static void addEmeraldOre(Biome biome) {
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_EMERALD);
	}

	public static void addInfestedStone(Biome biome) {
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_INFESTED);
	}

	public static void addDefaultDisks(Biome biome) {
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.DISK_SAND);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.DISK_CLAY);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.DISK_GRAVEL);
	}

	public static void addClay(Biome biome) {
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.DISK_CLAY);
	}

	public static void addMossyRocks(Biome biome) {
		biome.addFeature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ConfiguredFeatures.FOREST_ROCK);
	}

	public static void addLargeFerns(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_LARGE_FERN);
	}

	public static void addSweetBerryBushesSnowy(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_BERRY_DECORATED);
	}

	public static void addSweetBerryBushes(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_BERRY_SPARSE);
	}

	public static void addBamboo(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.BAMBOO_LIGHT);
	}

	public static void addBambooJungleTrees(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.BAMBOO);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.BAMBOO_VEGETATION);
	}

	public static void addTaigaTrees(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.TAIGA_VEGETATION);
	}

	public static void addWaterBiomeOakTrees(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.TREES_WATER);
	}

	public static void addBirchTrees(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.BIRCH_BEES_0002);
	}

	public static void addForestTrees(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.BIRCH_OTHER);
	}

	public static void addTallBirchTrees(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.BIRCH_TALL);
	}

	public static void addSavannaTrees(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.TREES_SAVANNA);
	}

	public static void addExtraSavannaTrees(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.TREES_SHATTERED_SAVANNA);
	}

	public static void addMountainTrees(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.TREES_MOUNTAIN);
	}

	public static void addExtraMountainTrees(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.TREES_MOUNTAIN_EDGE);
	}

	public static void addJungleTrees(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.TREES_JUNGLE);
	}

	public static void addJungleEdgeTrees(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.TREES_JUNGLE_EDGE);
	}

	public static void addBadlandsPlateauTrees(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.OAK_BADLANDS);
	}

	public static void addSnowySpruceTrees(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SPRUCE_SNOVY);
	}

	public static void addGiantSpruceTaigaTrees(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.TREES_GIANT_SPRUCE);
	}

	public static void addGiantTreeTaigaTrees(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.TREES_GIANT);
	}

	public static void addJungleGrass(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_GRASS_JUNGLE);
	}

	public static void addSavannaTallGrass(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_TALL_GRASS);
	}

	public static void addShatteredSavannaGrass(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_GRASS_NORMAL);
	}

	public static void addSavannaGrass(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_GRASS_SAVANNA);
	}

	public static void addBadlandsGrass(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_GRASS_BADLANDS);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_DEAD_BUSH_BADLANDS);
	}

	public static void addForestFlowers(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.FOREST_FLOWER_VEGETATION);
	}

	public static void addForestGrass(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_GRASS_FOREST);
	}

	public static void addSwampFeatures(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SWAMP_TREE);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.FLOWER_SWAMP);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_GRASS_NORMAL);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_DEAD_BUSH);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_WATERLILLY);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.BROWN_MUSHROOM_SWAMP);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.RED_MUSHROOM_SWAMP);
	}

	public static void addMushroomFieldsFeatures(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.MUSHROOM_FIELD_VEGETATION);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.BROWN_MUSHROOM_TAIGA);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.RED_MUSHROOM_TAIGA);
	}

	public static void addPlainsFeatures(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PLAIN_VEGETATION);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.FLOWER_PLAIN_DECORATED);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_GRASS_PLAIN);
	}

	public static void addDesertDeadBushes(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_DEAD_BUSH_2);
	}

	public static void addGiantTaigaGrass(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_GRASS_TAIGA);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_DEAD_BUSH);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.BROWN_MUSHROOM_GIANT);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.RED_MUSHROOM_GIANT);
	}

	public static void addDefaultFlowers(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.FLOWER_DEFAULT);
	}

	public static void addExtraDefaultFlowers(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.FLOWER_WARM);
	}

	public static void addDefaultGrass(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_GRASS_BADLANDS);
	}

	public static void addTaigaGrass(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_GRASS_TAIGA_2);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.BROWN_MUSHROOM_TAIGA);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.RED_MUSHROOM_TAIGA);
	}

	public static void addPlainsTallGrass(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_TALL_GRASS_2);
	}

	public static void addDefaultMushrooms(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.BROWN_MUSHROOM_NORMAL);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.RED_MUSHROOM_NORMAL);
	}

	public static void addDefaultVegetation(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_SUGAR_CANE);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_PUMPKIN);
	}

	public static void addBadlandsVegetation(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_SUGAR_CANE_BADLANDS);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_PUMPKIN);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_CACTUS_DECORATED);
	}

	public static void addJungleVegetation(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_MELON);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.VINES);
	}

	public static void addDesertVegetation(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_SUGAR_CANE_DESERT);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_PUMPKIN);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_CACTUS_DESERT);
	}

	public static void addSwampVegetation(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_SUGAR_CANE_SWAMP);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_PUMPKIN);
	}

	public static void addDesertFeatures(Biome biome) {
		biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.DESERT_WELL);
	}

	public static void addFossils(Biome biome) {
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_STRUCTURES, ConfiguredFeatures.FOSSIL);
	}

	public static void addKelp(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.KELP_COLD);
	}

	public static void addSeagrassOnStone(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SEAGRASS_SIMPLE);
	}

	public static void addLessKelp(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.KELP_WARM);
	}

	public static void addSprings(Biome biome) {
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SPRING_WATER);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SPRING_LAVA);
	}

	public static void addIcebergs(Biome biome) {
		biome.addFeature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ConfiguredFeatures.ICEBERG_PACKED);
		biome.addFeature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ConfiguredFeatures.ICEBERG_BLUE);
	}

	public static void addBlueIce(Biome biome) {
		biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.BLUE_ICE);
	}

	public static void addFrozenTopLayer(Biome biome) {
		biome.addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, ConfiguredFeatures.FREEZE_TOP_LAYER);
	}

	public static void addNetherMineables(Biome biome) {
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_GRAVEL_NETHER);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_BLACKSTONE);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_GOLD_NETHER);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_QUARTZ_NETHER);
		addAncientDebris(biome);
	}

	public static void addAncientDebris(Biome biome) {
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_DEBRIS_LARGE);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_DEBRIS_SMALL);
	}

	public static void addFarmAnimals(Biome biome) {
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.SHEEP, 12, 4, 4));
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.PIG, 10, 4, 4));
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.CHICKEN, 10, 4, 4));
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.COW, 8, 4, 4));
	}

	private static void addBats(Biome biome) {
		biome.addSpawn(SpawnGroup.AMBIENT, new Biome.SpawnEntry(EntityType.BAT, 10, 8, 8));
	}

	public static void addBatsAndMonsters(Biome biome) {
		addBats(biome);
		addMonsters(biome, 95, 5, 100);
	}

	public static void addSnowyMobs(Biome biome) {
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.RABBIT, 10, 2, 3));
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.POLAR_BEAR, 1, 1, 2));
		addBats(biome);
		addMonsters(biome, 95, 5, 20);
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.STRAY, 80, 4, 4));
	}

	public static void addDesertMobs(Biome biome) {
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
		addBats(biome);
		addMonsters(biome, 19, 1, 100);
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.HUSK, 80, 4, 4));
	}

	public static void addTaigaMobs(Biome biome) {
		addFarmAnimals(biome);
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.WOLF, 8, 4, 4));
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.FOX, 8, 2, 4));
		addBats(biome);
		addMonsters(biome, 100, 25, 100);
	}

	private static void addMonsters(Biome biome, int zombieWeight, int zombieVillagerWeight, int skeletonWeight) {
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.SPIDER, 100, 4, 4));
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE, zombieWeight, 4, 4));
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE_VILLAGER, zombieVillagerWeight, 1, 1));
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.SKELETON, skeletonWeight, 4, 4));
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.CREEPER, 100, 4, 4));
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.SLIME, 100, 4, 4));
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 10, 1, 4));
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.WITCH, 5, 1, 1));
	}

	public static void addMushroomMobs(Biome biome) {
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.MOOSHROOM, 8, 4, 8));
		addBats(biome);
	}

	public static void addJungleMobs(Biome biome) {
		addFarmAnimals(biome);
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.CHICKEN, 10, 4, 4));
		addBatsAndMonsters(biome);
	}

	public static void addEndMobs(Biome biome) {
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 10, 4, 4));
	}
}
