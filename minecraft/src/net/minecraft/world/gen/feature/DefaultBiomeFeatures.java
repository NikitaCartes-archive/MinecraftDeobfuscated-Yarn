package net.minecraft.world.gen.feature;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarvers;

public class DefaultBiomeFeatures {
	public static void addBadlandsUndergroundStructures(GenerationSettings.Builder builder) {
		builder.structureFeature(ConfiguredStructureFeatures.field_26294);
		builder.structureFeature(ConfiguredStructureFeatures.field_26302);
	}

	public static void addDefaultUndergroundStructures(GenerationSettings.Builder builder) {
		builder.structureFeature(ConfiguredStructureFeatures.field_26293);
		builder.structureFeature(ConfiguredStructureFeatures.field_26302);
	}

	public static void addOceanStructures(GenerationSettings.Builder builder) {
		builder.structureFeature(ConfiguredStructureFeatures.field_26293);
		builder.structureFeature(ConfiguredStructureFeatures.field_26299);
	}

	public static void addLandCarvers(GenerationSettings.Builder builder) {
		builder.carver(GenerationStep.Carver.field_13169, ConfiguredCarvers.field_25942);
		builder.carver(GenerationStep.Carver.field_13169, ConfiguredCarvers.field_25943);
	}

	public static void addOceanCarvers(GenerationSettings.Builder builder) {
		builder.carver(GenerationStep.Carver.field_13169, ConfiguredCarvers.field_25944);
		builder.carver(GenerationStep.Carver.field_13169, ConfiguredCarvers.field_25943);
		builder.carver(GenerationStep.Carver.field_13166, ConfiguredCarvers.field_25945);
		builder.carver(GenerationStep.Carver.field_13166, ConfiguredCarvers.field_25946);
	}

	public static void addDefaultLakes(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_25186, ConfiguredFeatures.field_25964);
		builder.feature(GenerationStep.Feature.field_25186, ConfiguredFeatures.field_25965);
	}

	public static void addDesertLakes(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_25186, ConfiguredFeatures.field_25965);
	}

	public static void addDungeons(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13172, ConfiguredFeatures.field_25972);
	}

	public static void addMineables(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13176, ConfiguredFeatures.field_26067);
		builder.feature(GenerationStep.Feature.field_13176, ConfiguredFeatures.field_26068);
		builder.feature(GenerationStep.Feature.field_13176, ConfiguredFeatures.field_26069);
		builder.feature(GenerationStep.Feature.field_13176, ConfiguredFeatures.field_26070);
		builder.feature(GenerationStep.Feature.field_13176, ConfiguredFeatures.field_26071);
	}

	public static void addDefaultOres(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13176, ConfiguredFeatures.field_26072);
		builder.feature(GenerationStep.Feature.field_13176, ConfiguredFeatures.field_26073);
		builder.feature(GenerationStep.Feature.field_13176, ConfiguredFeatures.field_26075);
		builder.feature(GenerationStep.Feature.field_13176, ConfiguredFeatures.field_26076);
		builder.feature(GenerationStep.Feature.field_13176, ConfiguredFeatures.field_26077);
		builder.feature(GenerationStep.Feature.field_13176, ConfiguredFeatures.field_26078);
	}

	public static void addExtraGoldOre(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13176, ConfiguredFeatures.field_26074);
	}

	public static void addEmeraldOre(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13176, ConfiguredFeatures.field_26080);
	}

	public static void addInfestedStone(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26079);
	}

	public static void addDefaultDisks(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13176, ConfiguredFeatures.field_25968);
		builder.feature(GenerationStep.Feature.field_13176, ConfiguredFeatures.field_25966);
		builder.feature(GenerationStep.Feature.field_13176, ConfiguredFeatures.field_25967);
	}

	public static void addClay(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13176, ConfiguredFeatures.field_25966);
	}

	public static void addMossyRocks(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13171, ConfiguredFeatures.field_25954);
	}

	public static void addLargeFerns(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25988);
	}

	public static void addSweetBerryBushesSnowy(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25983);
	}

	public static void addSweetBerryBushes(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25982);
	}

	public static void addBamboo(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25961);
	}

	public static void addBambooJungleTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25962);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26088);
	}

	public static void addTaigaTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26112);
	}

	public static void addWaterBiomeOakTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26081);
	}

	public static void addBirchTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26376);
	}

	public static void addForestTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26082);
	}

	public static void addTallBirchTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26115);
	}

	public static void addSavannaTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26114);
	}

	public static void addExtraSavannaTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26113);
	}

	public static void addMountainTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26117);
	}

	public static void addExtraMountainTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26116);
	}

	public static void addJungleTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26087);
	}

	public static void addJungleEdgeTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26084);
	}

	public static void addBadlandsPlateauTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26098);
	}

	public static void addSnowySpruceTrees(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26099);
	}

	public static void addJungleGrass(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25977);
	}

	public static void addSavannaTallGrass(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25986);
	}

	public static void addShatteredSavannaGrass(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26027);
	}

	public static void addSavannaGrass(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26026);
	}

	public static void addBadlandsGrass(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26025);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25980);
	}

	public static void addForestFlowers(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26107);
	}

	public static void addForestGrass(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26024);
	}

	public static void addSwampFeatures(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26050);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26103);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26027);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25979);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25984);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26057);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26058);
	}

	public static void addMushroomFieldsFeatures(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26089);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26000);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26001);
	}

	public static void addPlainsFeatures(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26083);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26105);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26023);
	}

	public static void addDesertDeadBushes(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25978);
	}

	public static void addGiantTaigaGrass(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25976);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25979);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26055);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26056);
	}

	public static void addDefaultFlowers(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26101);
	}

	public static void addExtraDefaultFlowers(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26100);
	}

	public static void addDefaultGrass(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26025);
	}

	public static void addTaigaGrass(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25975);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26000);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26001);
	}

	public static void addPlainsTallGrass(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25985);
	}

	public static void addDefaultMushrooms(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25998);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25999);
	}

	public static void addDefaultVegetation(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25995);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26020);
	}

	public static void addBadlandsVegetation(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25994);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26020);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25991);
	}

	public static void addJungleVegetation(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25981);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25963);
	}

	public static void addDesertVegetation(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25993);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26020);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25990);
	}

	public static void addSwampVegetation(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25992);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26020);
	}

	public static void addDesertFeatures(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13173, ConfiguredFeatures.field_25973);
	}

	public static void addFossils(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13172, ConfiguredFeatures.field_25974);
	}

	public static void addKelp(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25958);
	}

	public static void addSeagrassOnStone(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25955);
	}

	public static void addLessKelp(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25959);
	}

	public static void addSprings(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26008);
		builder.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26003);
	}

	public static void addIcebergs(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13171, ConfiguredFeatures.field_25956);
		builder.feature(GenerationStep.Feature.field_13171, ConfiguredFeatures.field_25957);
	}

	public static void addBlueIce(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13173, ConfiguredFeatures.field_25960);
	}

	public static void addFrozenTopLayer(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13179, ConfiguredFeatures.field_25969);
	}

	public static void addNetherMineables(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26065);
		builder.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26066);
		builder.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26063);
		builder.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26064);
		addAncientDebris(builder);
	}

	public static void addAncientDebris(GenerationSettings.Builder builder) {
		builder.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26028);
		builder.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26029);
	}

	public static void addFarmAnimals(SpawnSettings.Builder builder) {
		builder.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6115, 12, 4, 4));
		builder.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6093, 10, 4, 4));
		builder.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6132, 10, 4, 4));
		builder.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6085, 8, 4, 4));
	}

	public static void addBats(SpawnSettings.Builder builder) {
		builder.spawn(SpawnGroup.field_6303, new SpawnSettings.SpawnEntry(EntityType.field_6108, 10, 8, 8));
	}

	public static void addBatsAndMonsters(SpawnSettings.Builder builder) {
		addBats(builder);
		addMonsters(builder, 95, 5, 100);
	}

	public static void addOceanMobs(SpawnSettings.Builder builder, int squidWeight, int squidMaxGroupSize, int codWeight) {
		builder.spawn(SpawnGroup.field_6300, new SpawnSettings.SpawnEntry(EntityType.field_6114, squidWeight, 1, squidMaxGroupSize));
		builder.spawn(SpawnGroup.field_24460, new SpawnSettings.SpawnEntry(EntityType.field_6070, codWeight, 3, 6));
		addBatsAndMonsters(builder);
		builder.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6123, 5, 1, 1));
	}

	public static void addWarmOceanMobs(SpawnSettings.Builder builder, int squidWeight, int squidMinGroupSize) {
		builder.spawn(SpawnGroup.field_6300, new SpawnSettings.SpawnEntry(EntityType.field_6114, squidWeight, squidMinGroupSize, 4));
		builder.spawn(SpawnGroup.field_24460, new SpawnSettings.SpawnEntry(EntityType.field_6111, 25, 8, 8));
		builder.spawn(SpawnGroup.field_6300, new SpawnSettings.SpawnEntry(EntityType.field_6087, 2, 1, 2));
		addBatsAndMonsters(builder);
	}

	public static void addPlainsMobs(SpawnSettings.Builder builder) {
		addFarmAnimals(builder);
		builder.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6139, 5, 2, 6));
		builder.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6067, 1, 1, 3));
		addBatsAndMonsters(builder);
	}

	public static void addSnowyMobs(SpawnSettings.Builder builder) {
		builder.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6140, 10, 2, 3));
		builder.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6042, 1, 1, 2));
		addBats(builder);
		addMonsters(builder, 95, 5, 20);
		builder.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6098, 80, 4, 4));
	}

	public static void addDesertMobs(SpawnSettings.Builder builder) {
		builder.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6140, 4, 2, 3));
		addBats(builder);
		addMonsters(builder, 19, 1, 100);
		builder.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6071, 80, 4, 4));
	}

	public static void addMonsters(SpawnSettings.Builder builder, int zombieWeight, int zombieVillagerWeight, int skeletonWeight) {
		builder.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6079, 100, 4, 4));
		builder.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6051, zombieWeight, 4, 4));
		builder.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6054, zombieVillagerWeight, 1, 1));
		builder.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6137, skeletonWeight, 4, 4));
		builder.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6046, 100, 4, 4));
		builder.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6069, 100, 4, 4));
		builder.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6091, 10, 1, 4));
		builder.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6145, 5, 1, 1));
	}

	public static void addMushroomMobs(SpawnSettings.Builder builder) {
		builder.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6143, 8, 4, 8));
		addBats(builder);
	}

	public static void addJungleMobs(SpawnSettings.Builder builder) {
		addFarmAnimals(builder);
		builder.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6132, 10, 4, 4));
		addBatsAndMonsters(builder);
	}

	public static void addEndMobs(SpawnSettings.Builder builder) {
		builder.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6091, 10, 4, 4));
	}
}
