package net.minecraft.world.gen.feature;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarvers;

public class DefaultBiomeFeatures {
	public static void addBadlandsUndergroundStructures(GenerationSettings.Builder builder) {
		builder.structureFeature(ConfiguredStructureFeatures.MINESHAFT_MESA);
		builder.structureFeature(ConfiguredStructureFeatures.STRONGHOLD);
	}

	public static void addDefaultUndergroundStructures(GenerationSettings.Builder builder) {
		builder.structureFeature(ConfiguredStructureFeatures.MINESHAFT);
		builder.structureFeature(ConfiguredStructureFeatures.STRONGHOLD);
	}

	public static void addOceanStructures(GenerationSettings.Builder builder) {
		builder.structureFeature(ConfiguredStructureFeatures.MINESHAFT);
		builder.structureFeature(ConfiguredStructureFeatures.SHIPWRECK);
	}

	public static void addLandCarvers(GenerationSettings.Builder builder) {
		builder.carver(GenerationStep.Carver.AIR, ConfiguredCarvers.CAVE);
		builder.carver(GenerationStep.Carver.AIR, ConfiguredCarvers.CANYON);
	}

	public static void addOceanCarvers(GenerationSettings.Builder builder) {
		builder.carver(GenerationStep.Carver.AIR, ConfiguredCarvers.OCEAN_CAVE);
		builder.carver(GenerationStep.Carver.AIR, ConfiguredCarvers.CANYON);
		builder.carver(GenerationStep.Carver.LIQUID, ConfiguredCarvers.UNDERWATER_CANYON);
		builder.carver(GenerationStep.Carver.LIQUID, ConfiguredCarvers.UNDERWATER_CAVE);
	}

	public static void addDefaultLakes(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.LAKES, ConfiguredFeatures.LAKE_WATER);
		builder.feature(GenerationStep.Feature.LAKES, ConfiguredFeatures.LAKE_LAVA);
	}

	public static void addDesertLakes(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.LAKES, ConfiguredFeatures.LAKE_LAVA);
	}

	public static void addDungeons(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.UNDERGROUND_STRUCTURES, ConfiguredFeatures.MONSTER_ROOM);
	}

	public static void addMineables(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_DIRT);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_GRAVEL);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_GRANITE);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_DIORITE);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_ANDESITE);
	}

	public static void addDefaultOres(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_COAL);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_IRON);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_GOLD);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_REDSTONE);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_DIAMOND);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_LAPIS);
	}

	public static void addExtraGoldOre(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_GOLD_EXTRA);
	}

	public static void addEmeraldOre(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.ORE_EMERALD);
	}

	public static void addInfestedStone(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_INFESTED);
	}

	public static void addDefaultDisks(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.DISK_SAND);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.DISK_CLAY);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.DISK_GRAVEL);
	}

	public static void addClay(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, ConfiguredFeatures.DISK_CLAY);
	}

	public static void addMossyRocks(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ConfiguredFeatures.FOREST_ROCK);
	}

	public static void addLargeFerns(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_LARGE_FERN);
	}

	public static void addSweetBerryBushesSnowy(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_BERRY_DECORATED);
	}

	public static void addSweetBerryBushes(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_BERRY_SPARSE);
	}

	public static void addBamboo(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.BAMBOO_LIGHT);
	}

	public static void addBambooJungleTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.BAMBOO);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.BAMBOO_VEGETATION);
	}

	public static void addTaigaTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.TAIGA_VEGETATION);
	}

	public static void addWaterBiomeOakTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.TREES_WATER);
	}

	public static void addBirchTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.TREES_BIRCH);
	}

	public static void addForestTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.BIRCH_OTHER);
	}

	public static void addTallBirchTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.BIRCH_TALL);
	}

	public static void addSavannaTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.TREES_SAVANNA);
	}

	public static void addExtraSavannaTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.TREES_SHATTERED_SAVANNA);
	}

	public static void addMountainTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.TREES_MOUNTAIN);
	}

	public static void addExtraMountainTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.TREES_MOUNTAIN_EDGE);
	}

	public static void addJungleTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.TREES_JUNGLE);
	}

	public static void addJungleEdgeTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.TREES_JUNGLE_EDGE);
	}

	public static void addBadlandsPlateauTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.OAK_BADLANDS);
	}

	public static void addSnowySpruceTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SPRUCE_SNOWY);
	}

	public static void addJungleGrass(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_GRASS_JUNGLE);
	}

	public static void addSavannaTallGrass(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_TALL_GRASS);
	}

	public static void addShatteredSavannaGrass(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_GRASS_NORMAL);
	}

	public static void addSavannaGrass(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_GRASS_SAVANNA);
	}

	public static void addBadlandsGrass(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_GRASS_BADLANDS);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_DEAD_BUSH_BADLANDS);
	}

	public static void addForestFlowers(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.FOREST_FLOWER_VEGETATION);
	}

	public static void addForestGrass(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_GRASS_FOREST);
	}

	public static void addSwampFeatures(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SWAMP_TREE);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.FLOWER_SWAMP);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_GRASS_NORMAL);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_DEAD_BUSH);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_WATERLILLY);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.BROWN_MUSHROOM_SWAMP);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.RED_MUSHROOM_SWAMP);
	}

	public static void addMushroomFieldsFeatures(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.MUSHROOM_FIELD_VEGETATION);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.BROWN_MUSHROOM_TAIGA);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.RED_MUSHROOM_TAIGA);
	}

	public static void addPlainsFeatures(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PLAIN_VEGETATION);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.FLOWER_PLAIN_DECORATED);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_GRASS_PLAIN);
	}

	public static void addDesertDeadBushes(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_DEAD_BUSH_2);
	}

	public static void addGiantTaigaGrass(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_GRASS_TAIGA);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_DEAD_BUSH);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.BROWN_MUSHROOM_GIANT);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.RED_MUSHROOM_GIANT);
	}

	public static void addDefaultFlowers(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.FLOWER_DEFAULT);
	}

	public static void addExtraDefaultFlowers(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.FLOWER_WARM);
	}

	public static void addDefaultGrass(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_GRASS_BADLANDS);
	}

	public static void addTaigaGrass(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_GRASS_TAIGA_2);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.BROWN_MUSHROOM_TAIGA);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.RED_MUSHROOM_TAIGA);
	}

	public static void addPlainsTallGrass(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_TALL_GRASS_2);
	}

	public static void addDefaultMushrooms(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.BROWN_MUSHROOM_NORMAL);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.RED_MUSHROOM_NORMAL);
	}

	public static void addDefaultVegetation(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_SUGAR_CANE);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_PUMPKIN);
	}

	public static void addBadlandsVegetation(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_SUGAR_CANE_BADLANDS);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_PUMPKIN);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_CACTUS_DECORATED);
	}

	public static void addJungleVegetation(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_MELON);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.VINES);
	}

	public static void addDesertVegetation(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_SUGAR_CANE_DESERT);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_PUMPKIN);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_CACTUS_DESERT);
	}

	public static void addSwampVegetation(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_SUGAR_CANE_SWAMP);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_PUMPKIN);
	}

	public static void addDesertFeatures(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.DESERT_WELL);
	}

	public static void addFossils(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.UNDERGROUND_STRUCTURES, ConfiguredFeatures.FOSSIL);
	}

	public static void addKelp(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.KELP_COLD);
	}

	public static void addSeagrassOnStone(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SEAGRASS_SIMPLE);
	}

	public static void addLessKelp(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.KELP_WARM);
	}

	public static void addSprings(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SPRING_WATER);
		builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SPRING_LAVA);
	}

	public static void addIcebergs(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ConfiguredFeatures.ICEBERG_PACKED);
		builder.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ConfiguredFeatures.ICEBERG_BLUE);
	}

	public static void addBlueIce(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.BLUE_ICE);
	}

	public static void addFrozenTopLayer(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, ConfiguredFeatures.FREEZE_TOP_LAYER);
	}

	public static void addNetherMineables(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_GRAVEL_NETHER);
		builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_BLACKSTONE);
		builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_GOLD_NETHER);
		builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_QUARTZ_NETHER);
		addAncientDebris(builder);
	}

	public static void addAncientDebris(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_DEBRIS_LARGE);
		builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_DEBRIS_SMALL);
	}

	public static void addFarmAnimals(SpawnSettings.Builder builder) {
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.SHEEP, 12, 4, 4));
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.PIG, 10, 4, 4));
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.CHICKEN, 10, 4, 4));
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.COW, 8, 4, 4));
	}

	public static void addBats(SpawnSettings.Builder builder) {
		builder.spawn(SpawnGroup.AMBIENT, new SpawnSettings.SpawnEntry(EntityType.BAT, 10, 8, 8));
	}

	public static void addBatsAndMonsters(SpawnSettings.Builder builder) {
		addBats(builder);
		addMonsters(builder, 95, 5, 100);
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
		addBats(builder);
		addMonsters(builder, 95, 5, 20);
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.STRAY, 80, 4, 4));
	}

	public static void addDesertMobs(SpawnSettings.Builder builder) {
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
		addBats(builder);
		addMonsters(builder, 19, 1, 100);
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.HUSK, 80, 4, 4));
	}

	public static void addMonsters(SpawnSettings.Builder builder, int zombieWeight, int zombieVillagerWeight, int skeletonWeight) {
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SPIDER, 100, 4, 4));
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.ZOMBIE, zombieWeight, 4, 4));
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.ZOMBIE_VILLAGER, zombieVillagerWeight, 1, 1));
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SKELETON, skeletonWeight, 4, 4));
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.CREEPER, 100, 4, 4));
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SLIME, 100, 4, 4));
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.ENDERMAN, 10, 1, 4));
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.WITCH, 5, 1, 1));
	}

	public static void addMushroomMobs(SpawnSettings.Builder builder) {
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.MOOSHROOM, 8, 4, 8));
		addBats(builder);
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
