package net.minecraft.world.biome;

import javax.annotation.Nullable;
import net.minecraft.client.sound.MusicType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class DefaultBiomeCreator {
	private static int getSkyColor(float temperature) {
		float f = temperature / 3.0F;
		f = MathHelper.clamp(f, -1.0F, 1.0F);
		return MathHelper.hsvToRgb(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F);
	}

	public static Biome createGiantTreeTaiga(float depth, float scale, float temperature, boolean spruce, @Nullable String parent) {
		Biome biome = new Biome(
			new Biome.Settings()
				.surfaceBuilder(ConfiguredSurfaceBuilders.GIANT_TREE_TAIGA)
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.TAIGA)
				.depth(depth)
				.scale(scale)
				.temperature(temperature)
				.downfall(0.8F)
				.effects(
					new BiomeEffects.Builder()
						.waterColor(4159204)
						.waterFogColor(329011)
						.fogColor(12638463)
						.skyColor(getSkyColor(temperature))
						.moodSound(BiomeMoodSound.CAVE)
						.build()
				)
				.parent(parent)
		);
		DefaultBiomeFeatures.addDefaultUndergroundStructures(biome);
		biome.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(biome);
		DefaultBiomeFeatures.addDefaultLakes(biome);
		DefaultBiomeFeatures.addDungeons(biome);
		DefaultBiomeFeatures.addMossyRocks(biome);
		DefaultBiomeFeatures.addLargeFerns(biome);
		DefaultBiomeFeatures.addMineables(biome);
		DefaultBiomeFeatures.addDefaultOres(biome);
		DefaultBiomeFeatures.addDefaultDisks(biome);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, spruce ? ConfiguredFeatures.TREES_GIANT_SPRUCE : ConfiguredFeatures.TREES_GIANT);
		DefaultBiomeFeatures.addDefaultFlowers(biome);
		DefaultBiomeFeatures.addGiantTaigaGrass(biome);
		DefaultBiomeFeatures.addDefaultMushrooms(biome);
		DefaultBiomeFeatures.addDefaultVegetation(biome);
		DefaultBiomeFeatures.addSprings(biome);
		DefaultBiomeFeatures.addSweetBerryBushes(biome);
		DefaultBiomeFeatures.addFrozenTopLayer(biome);
		DefaultBiomeFeatures.addFarmAnimals(biome);
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.WOLF, 8, 4, 4));
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.FOX, 8, 2, 4));
		if (spruce) {
			DefaultBiomeFeatures.addBatsAndMonsters(biome);
		} else {
			DefaultBiomeFeatures.addBats(biome);
			DefaultBiomeFeatures.addMonsters(biome, 100, 25, 100);
		}

		return biome;
	}

	public static Biome createBirchForest(float depth, float scale, @Nullable String parent, boolean tallTrees) {
		Biome biome = new Biome(
			new Biome.Settings()
				.surfaceBuilder(ConfiguredSurfaceBuilders.GRASS)
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.FOREST)
				.depth(depth)
				.scale(scale)
				.temperature(0.6F)
				.downfall(0.6F)
				.effects(
					new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(getSkyColor(0.6F)).moodSound(BiomeMoodSound.CAVE).build()
				)
				.parent(parent)
		);
		DefaultBiomeFeatures.addDefaultUndergroundStructures(biome);
		biome.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(biome);
		DefaultBiomeFeatures.addDefaultLakes(biome);
		DefaultBiomeFeatures.addDungeons(biome);
		DefaultBiomeFeatures.addForestFlowers(biome);
		DefaultBiomeFeatures.addMineables(biome);
		DefaultBiomeFeatures.addDefaultOres(biome);
		DefaultBiomeFeatures.addDefaultDisks(biome);
		if (tallTrees) {
			DefaultBiomeFeatures.addTallBirchTrees(biome);
		} else {
			DefaultBiomeFeatures.addBirchTrees(biome);
		}

		DefaultBiomeFeatures.addDefaultFlowers(biome);
		DefaultBiomeFeatures.addForestGrass(biome);
		DefaultBiomeFeatures.addDefaultMushrooms(biome);
		DefaultBiomeFeatures.addDefaultVegetation(biome);
		DefaultBiomeFeatures.addSprings(biome);
		DefaultBiomeFeatures.addFrozenTopLayer(biome);
		DefaultBiomeFeatures.addFarmAnimals(biome);
		DefaultBiomeFeatures.addBatsAndMonsters(biome);
		return biome;
	}

	public static Biome createJungle() {
		return createJungle(0.1F, 0.2F, 40, 2, 3);
	}

	public static Biome createJungleEdge() {
		return createJungleFeatures(null, 0.1F, 0.2F, 0.8F, false, true, false);
	}

	public static Biome createModifiedJungleEdge() {
		return createJungleFeatures("jungle_edge", 0.2F, 0.4F, 0.8F, false, true, true);
	}

	public static Biome createModifiedJungle() {
		Biome biome = createJungleFeatures("jungle", 0.2F, 0.4F, 0.9F, false, false, true);
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.PARROT, 10, 1, 1));
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.OCELOT, 2, 1, 1));
		return biome;
	}

	public static Biome createJungleHills() {
		return createJungle(0.45F, 0.3F, 10, 1, 1);
	}

	public static Biome createNormalBambooJungle() {
		return createBambooJungle(0.1F, 0.2F, 40, 2);
	}

	public static Biome createBambooJungleHills() {
		return createBambooJungle(0.45F, 0.3F, 10, 1);
	}

	private static Biome createJungle(float depth, float scale, int parrotWeight, int parrotMaxGroupSize, int ocelotMaxGroupSize) {
		Biome biome = createJungleFeatures(null, depth, scale, 0.9F, false, false, false);
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.PARROT, parrotWeight, 1, parrotMaxGroupSize));
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.OCELOT, 2, 1, ocelotMaxGroupSize));
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.PANDA, 1, 1, 2));
		return biome;
	}

	private static Biome createBambooJungle(float depth, float scale, int parrotWeight, int parrotMaxGroupSize) {
		Biome biome = createJungleFeatures(null, depth, scale, 0.9F, true, false, false);
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.PARROT, parrotWeight, 1, parrotMaxGroupSize));
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.PANDA, 80, 1, 2));
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.OCELOT, 2, 1, 1));
		return biome;
	}

	private static Biome createJungleFeatures(
		@Nullable String parent, float depth, float scale, float downfall, boolean bambooTrees, boolean edge, boolean modified
	) {
		Biome biome = new Biome(
			new Biome.Settings()
				.surfaceBuilder(ConfiguredSurfaceBuilders.GRASS)
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.JUNGLE)
				.depth(depth)
				.scale(scale)
				.temperature(0.95F)
				.downfall(downfall)
				.effects(
					new BiomeEffects.Builder()
						.waterColor(4159204)
						.waterFogColor(329011)
						.fogColor(12638463)
						.skyColor(getSkyColor(0.95F))
						.moodSound(BiomeMoodSound.CAVE)
						.build()
				)
				.parent(parent)
		);
		if (!edge && !modified) {
			biome.addStructureFeature(ConfiguredStructureFeatures.JUNGLE_PYRAMID);
		}

		DefaultBiomeFeatures.addDefaultUndergroundStructures(biome);
		biome.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_JUNGLE);
		DefaultBiomeFeatures.addLandCarvers(biome);
		DefaultBiomeFeatures.addDefaultLakes(biome);
		DefaultBiomeFeatures.addDungeons(biome);
		DefaultBiomeFeatures.addMineables(biome);
		DefaultBiomeFeatures.addDefaultOres(biome);
		DefaultBiomeFeatures.addDefaultDisks(biome);
		if (bambooTrees) {
			DefaultBiomeFeatures.addBambooJungleTrees(biome);
		} else {
			if (!edge && !modified) {
				DefaultBiomeFeatures.addBamboo(biome);
			}

			if (edge) {
				DefaultBiomeFeatures.addJungleEdgeTrees(biome);
			} else {
				DefaultBiomeFeatures.addJungleTrees(biome);
			}
		}

		DefaultBiomeFeatures.addExtraDefaultFlowers(biome);
		DefaultBiomeFeatures.addJungleGrass(biome);
		DefaultBiomeFeatures.addDefaultMushrooms(biome);
		DefaultBiomeFeatures.addDefaultVegetation(biome);
		DefaultBiomeFeatures.addSprings(biome);
		DefaultBiomeFeatures.addJungleVegetation(biome);
		DefaultBiomeFeatures.addFrozenTopLayer(biome);
		DefaultBiomeFeatures.addJungleMobs(biome);
		return biome;
	}

	public static Biome createMountains(
		float depth, float scale, ConfiguredSurfaceBuilder<TernarySurfaceConfig> surfaceBuilder, boolean extraTrees, @Nullable String parent
	) {
		Biome biome = new Biome(
			new Biome.Settings()
				.surfaceBuilder(surfaceBuilder)
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.EXTREME_HILLS)
				.depth(depth)
				.scale(scale)
				.temperature(0.2F)
				.downfall(0.3F)
				.effects(
					new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(getSkyColor(0.2F)).moodSound(BiomeMoodSound.CAVE).build()
				)
				.parent(parent)
		);
		DefaultBiomeFeatures.addDefaultUndergroundStructures(biome);
		biome.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_MOUNTAIN);
		DefaultBiomeFeatures.addLandCarvers(biome);
		DefaultBiomeFeatures.addDefaultLakes(biome);
		DefaultBiomeFeatures.addDungeons(biome);
		DefaultBiomeFeatures.addMineables(biome);
		DefaultBiomeFeatures.addDefaultOres(biome);
		DefaultBiomeFeatures.addDefaultDisks(biome);
		if (extraTrees) {
			DefaultBiomeFeatures.addExtraMountainTrees(biome);
		} else {
			DefaultBiomeFeatures.addMountainTrees(biome);
		}

		DefaultBiomeFeatures.addDefaultFlowers(biome);
		DefaultBiomeFeatures.addDefaultGrass(biome);
		DefaultBiomeFeatures.addDefaultMushrooms(biome);
		DefaultBiomeFeatures.addDefaultVegetation(biome);
		DefaultBiomeFeatures.addSprings(biome);
		DefaultBiomeFeatures.addEmeraldOre(biome);
		DefaultBiomeFeatures.addInfestedStone(biome);
		DefaultBiomeFeatures.addFrozenTopLayer(biome);
		DefaultBiomeFeatures.addFarmAnimals(biome);
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.LLAMA, 5, 4, 6));
		DefaultBiomeFeatures.addBatsAndMonsters(biome);
		return biome;
	}

	public static Biome createDesert(@Nullable String parent, float depth, float scale, boolean illagerStructures, boolean pyramids, boolean fossils) {
		Biome biome = new Biome(
			new Biome.Settings()
				.surfaceBuilder(ConfiguredSurfaceBuilders.DESERT)
				.precipitation(Biome.Precipitation.NONE)
				.category(Biome.Category.DESERT)
				.depth(depth)
				.scale(scale)
				.temperature(2.0F)
				.downfall(0.0F)
				.effects(
					new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(getSkyColor(2.0F)).moodSound(BiomeMoodSound.CAVE).build()
				)
				.parent(parent)
		);
		if (illagerStructures) {
			biome.addStructureFeature(ConfiguredStructureFeatures.VILLAGE_DESERT);
			biome.addStructureFeature(ConfiguredStructureFeatures.PILLAGER_OUTPOST);
		}

		if (pyramids) {
			biome.addStructureFeature(ConfiguredStructureFeatures.DESERT_PYRAMID);
		}

		if (fossils) {
			DefaultBiomeFeatures.addFossils(biome);
		}

		DefaultBiomeFeatures.addDefaultUndergroundStructures(biome);
		biome.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_DESERT);
		DefaultBiomeFeatures.addLandCarvers(biome);
		DefaultBiomeFeatures.addDesertLakes(biome);
		DefaultBiomeFeatures.addDungeons(biome);
		DefaultBiomeFeatures.addMineables(biome);
		DefaultBiomeFeatures.addDefaultOres(biome);
		DefaultBiomeFeatures.addDefaultDisks(biome);
		DefaultBiomeFeatures.addDefaultFlowers(biome);
		DefaultBiomeFeatures.addDefaultGrass(biome);
		DefaultBiomeFeatures.addDesertDeadBushes(biome);
		DefaultBiomeFeatures.addDefaultMushrooms(biome);
		DefaultBiomeFeatures.addDesertVegetation(biome);
		DefaultBiomeFeatures.addSprings(biome);
		DefaultBiomeFeatures.addDesertFeatures(biome);
		DefaultBiomeFeatures.addFrozenTopLayer(biome);
		DefaultBiomeFeatures.addDesertMobs(biome);
		return biome;
	}

	public static Biome createPlains(@Nullable String parent, boolean sunflower) {
		Biome biome = new Biome(
			new Biome.Settings()
				.surfaceBuilder(ConfiguredSurfaceBuilders.GRASS)
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.PLAINS)
				.depth(0.125F)
				.scale(0.05F)
				.temperature(0.8F)
				.downfall(0.4F)
				.effects(
					new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(getSkyColor(0.8F)).moodSound(BiomeMoodSound.CAVE).build()
				)
				.parent(parent)
		);
		if (!sunflower) {
			biome.addStructureFeature(ConfiguredStructureFeatures.VILLAGE_PLAINS);
			biome.addStructureFeature(ConfiguredStructureFeatures.PILLAGER_OUTPOST);
		}

		DefaultBiomeFeatures.addDefaultUndergroundStructures(biome);
		biome.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(biome);
		DefaultBiomeFeatures.addDefaultLakes(biome);
		DefaultBiomeFeatures.addDungeons(biome);
		DefaultBiomeFeatures.addPlainsTallGrass(biome);
		if (sunflower) {
			biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_SUNFLOWER);
		}

		DefaultBiomeFeatures.addMineables(biome);
		DefaultBiomeFeatures.addDefaultOres(biome);
		DefaultBiomeFeatures.addDefaultDisks(biome);
		DefaultBiomeFeatures.addPlainsFeatures(biome);
		if (sunflower) {
			biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_SUGAR_CANE);
		}

		DefaultBiomeFeatures.addDefaultMushrooms(biome);
		if (sunflower) {
			biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_PUMPKIN);
		} else {
			DefaultBiomeFeatures.addDefaultVegetation(biome);
		}

		DefaultBiomeFeatures.addSprings(biome);
		DefaultBiomeFeatures.addFrozenTopLayer(biome);
		DefaultBiomeFeatures.addPlainsMobs(biome);
		return biome;
	}

	public static Biome createEndBarrens() {
		Biome biome = new Biome(
			new Biome.Settings()
				.surfaceBuilder(ConfiguredSurfaceBuilders.END)
				.precipitation(Biome.Precipitation.NONE)
				.category(Biome.Category.THEEND)
				.depth(0.1F)
				.scale(0.2F)
				.temperature(0.5F)
				.downfall(0.5F)
				.effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(10518688).skyColor(0).moodSound(BiomeMoodSound.CAVE).build())
				.parent(null)
		);
		DefaultBiomeFeatures.addEndMobs(biome);
		return biome;
	}

	public static Biome createTheEnd() {
		Biome biome = createEndBarrens();
		biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.END_SPIKE);
		return biome;
	}

	public static Biome createEndMidlands() {
		Biome biome = createEndBarrens();
		biome.addStructureFeature(ConfiguredStructureFeatures.END_CITY);
		return biome;
	}

	public static Biome createEndHighlands() {
		Biome biome = createEndMidlands();
		biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.END_GATEWAY);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.CHORUS_PLANT);
		return biome;
	}

	public static Biome createSmallEndIslands() {
		Biome biome = createEndBarrens();
		biome.addFeature(GenerationStep.Feature.RAW_GENERATION, ConfiguredFeatures.END_ISLAND_DECORATED);
		return biome;
	}

	public static Biome createMushroomFields(float depth, float scale) {
		Biome biome = new Biome(
			new Biome.Settings()
				.surfaceBuilder(ConfiguredSurfaceBuilders.MYCELIUM)
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.MUSHROOM)
				.depth(depth)
				.scale(scale)
				.temperature(0.9F)
				.downfall(1.0F)
				.effects(
					new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(getSkyColor(0.9F)).moodSound(BiomeMoodSound.CAVE).build()
				)
				.parent(null)
		);
		DefaultBiomeFeatures.addDefaultUndergroundStructures(biome);
		biome.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(biome);
		DefaultBiomeFeatures.addDefaultLakes(biome);
		DefaultBiomeFeatures.addDungeons(biome);
		DefaultBiomeFeatures.addMineables(biome);
		DefaultBiomeFeatures.addDefaultOres(biome);
		DefaultBiomeFeatures.addDefaultDisks(biome);
		DefaultBiomeFeatures.addMushroomFieldsFeatures(biome);
		DefaultBiomeFeatures.addDefaultMushrooms(biome);
		DefaultBiomeFeatures.addDefaultVegetation(biome);
		DefaultBiomeFeatures.addSprings(biome);
		DefaultBiomeFeatures.addFrozenTopLayer(biome);
		DefaultBiomeFeatures.addMushroomMobs(biome);
		return biome;
	}

	public static Biome createSavanna(@Nullable String parent, float depth, float scale, float temperature, boolean tall, boolean shattered) {
		Biome biome = new Biome(
			new Biome.Settings()
				.surfaceBuilder(shattered ? ConfiguredSurfaceBuilders.SHATTERED_SAVANNA : ConfiguredSurfaceBuilders.GRASS)
				.precipitation(Biome.Precipitation.NONE)
				.category(Biome.Category.SAVANNA)
				.depth(depth)
				.scale(scale)
				.temperature(temperature)
				.downfall(0.0F)
				.effects(
					new BiomeEffects.Builder()
						.waterColor(4159204)
						.waterFogColor(329011)
						.fogColor(12638463)
						.skyColor(getSkyColor(temperature))
						.moodSound(BiomeMoodSound.CAVE)
						.build()
				)
				.parent(parent)
		);
		if (!tall && !shattered) {
			biome.addStructureFeature(ConfiguredStructureFeatures.VILLAGE_SAVANNA);
			biome.addStructureFeature(ConfiguredStructureFeatures.PILLAGER_OUTPOST);
		}

		DefaultBiomeFeatures.addDefaultUndergroundStructures(biome);
		biome.addStructureFeature(tall ? ConfiguredStructureFeatures.RUINED_PORTAL_MOUNTAIN : ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(biome);
		DefaultBiomeFeatures.addDefaultLakes(biome);
		DefaultBiomeFeatures.addDungeons(biome);
		if (!shattered) {
			DefaultBiomeFeatures.addSavannaTallGrass(biome);
		}

		DefaultBiomeFeatures.addMineables(biome);
		DefaultBiomeFeatures.addDefaultOres(biome);
		DefaultBiomeFeatures.addDefaultDisks(biome);
		if (shattered) {
			DefaultBiomeFeatures.addExtraSavannaTrees(biome);
			DefaultBiomeFeatures.addDefaultFlowers(biome);
			DefaultBiomeFeatures.addShatteredSavannaGrass(biome);
		} else {
			DefaultBiomeFeatures.addSavannaTrees(biome);
			DefaultBiomeFeatures.addExtraDefaultFlowers(biome);
			DefaultBiomeFeatures.addSavannaGrass(biome);
		}

		DefaultBiomeFeatures.addDefaultMushrooms(biome);
		DefaultBiomeFeatures.addDefaultVegetation(biome);
		DefaultBiomeFeatures.addSprings(biome);
		DefaultBiomeFeatures.addFrozenTopLayer(biome);
		DefaultBiomeFeatures.addFarmAnimals(biome);
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.HORSE, 1, 2, 6));
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.DONKEY, 1, 1, 1));
		DefaultBiomeFeatures.addBatsAndMonsters(biome);
		return biome;
	}

	public static Biome createSavannaPlateau() {
		Biome biome = createSavanna(null, 1.5F, 0.025F, 1.0F, true, false);
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.LLAMA, 8, 4, 4));
		return biome;
	}

	private static Biome createBadlands(
		@Nullable String parent, ConfiguredSurfaceBuilder<TernarySurfaceConfig> surfaceBuilder, float depth, float scale, boolean plateau, boolean trees
	) {
		Biome biome = new Biome(
			new Biome.Settings()
				.surfaceBuilder(surfaceBuilder)
				.precipitation(Biome.Precipitation.NONE)
				.category(Biome.Category.MESA)
				.depth(depth)
				.scale(scale)
				.temperature(2.0F)
				.downfall(0.0F)
				.effects(
					new BiomeEffects.Builder()
						.waterColor(4159204)
						.waterFogColor(329011)
						.fogColor(12638463)
						.skyColor(getSkyColor(2.0F))
						.foliageColor(10387789)
						.grassColor(9470285)
						.moodSound(BiomeMoodSound.CAVE)
						.build()
				)
				.parent(parent)
		);
		DefaultBiomeFeatures.addBadlandsUndergroundStructures(biome);
		biome.addStructureFeature(plateau ? ConfiguredStructureFeatures.RUINED_PORTAL_MOUNTAIN : ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(biome);
		DefaultBiomeFeatures.addDefaultLakes(biome);
		DefaultBiomeFeatures.addDungeons(biome);
		DefaultBiomeFeatures.addMineables(biome);
		DefaultBiomeFeatures.addDefaultOres(biome);
		DefaultBiomeFeatures.addExtraGoldOre(biome);
		DefaultBiomeFeatures.addDefaultDisks(biome);
		if (trees) {
			DefaultBiomeFeatures.addBadlandsPlateauTrees(biome);
		}

		DefaultBiomeFeatures.addBadlandsGrass(biome);
		DefaultBiomeFeatures.addDefaultMushrooms(biome);
		DefaultBiomeFeatures.addBadlandsVegetation(biome);
		DefaultBiomeFeatures.addSprings(biome);
		DefaultBiomeFeatures.addFrozenTopLayer(biome);
		DefaultBiomeFeatures.addBatsAndMonsters(biome);
		return biome;
	}

	public static Biome createNormalBadlands(@Nullable String parent, float depth, float scale, boolean plateau) {
		return createBadlands(parent, ConfiguredSurfaceBuilders.BADLANDS, depth, scale, plateau, false);
	}

	public static Biome createWoodedBadlandsPlateau(@Nullable String parent, float depth, float scale) {
		return createBadlands(parent, ConfiguredSurfaceBuilders.WOODED_BADLANDS, depth, scale, true, true);
	}

	public static Biome createErodedBadlands() {
		return createBadlands("badlands", ConfiguredSurfaceBuilders.ERODED_BADLANDS, 0.1F, 0.2F, true, false);
	}

	private static Biome createOcean(
		ConfiguredSurfaceBuilder<TernarySurfaceConfig> surfaceBuilder,
		int waterColor,
		int waterFogColor,
		boolean deep,
		boolean warm,
		boolean preserveOldStructureOrder
	) {
		Biome biome = new Biome(
			new Biome.Settings()
				.surfaceBuilder(surfaceBuilder)
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.OCEAN)
				.depth(deep ? -1.8F : -1.0F)
				.scale(0.1F)
				.temperature(0.5F)
				.downfall(0.5F)
				.effects(
					new BiomeEffects.Builder()
						.waterColor(waterColor)
						.waterFogColor(waterFogColor)
						.fogColor(12638463)
						.skyColor(getSkyColor(0.5F))
						.moodSound(BiomeMoodSound.CAVE)
						.build()
				)
				.parent(null)
		);
		ConfiguredStructureFeature<?, ?> configuredStructureFeature = warm
			? ConfiguredStructureFeatures.OCEAN_RUIN_WARM
			: ConfiguredStructureFeatures.OCEAN_RUIN_COLD;
		if (preserveOldStructureOrder) {
			if (deep) {
				biome.addStructureFeature(ConfiguredStructureFeatures.MONUMENT);
			}

			DefaultBiomeFeatures.addOceanStructures(biome);
			biome.addStructureFeature(configuredStructureFeature);
		} else {
			biome.addStructureFeature(configuredStructureFeature);
			if (deep) {
				biome.addStructureFeature(ConfiguredStructureFeatures.MONUMENT);
			}

			DefaultBiomeFeatures.addOceanStructures(biome);
		}

		biome.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_OCEAN);
		DefaultBiomeFeatures.addOceanCarvers(biome);
		DefaultBiomeFeatures.addDefaultLakes(biome);
		DefaultBiomeFeatures.addDungeons(biome);
		DefaultBiomeFeatures.addMineables(biome);
		DefaultBiomeFeatures.addDefaultOres(biome);
		DefaultBiomeFeatures.addDefaultDisks(biome);
		DefaultBiomeFeatures.addWaterBiomeOakTrees(biome);
		DefaultBiomeFeatures.addDefaultFlowers(biome);
		DefaultBiomeFeatures.addDefaultGrass(biome);
		DefaultBiomeFeatures.addDefaultMushrooms(biome);
		DefaultBiomeFeatures.addDefaultVegetation(biome);
		DefaultBiomeFeatures.addSprings(biome);
		return biome;
	}

	public static Biome createColdOcean(boolean deep) {
		Biome biome = createOcean(ConfiguredSurfaceBuilders.GRASS, 4020182, 329011, deep, false, !deep);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, deep ? ConfiguredFeatures.SEAGRASS_DEEP_COLD : ConfiguredFeatures.SEAGRASS_COLD);
		DefaultBiomeFeatures.addSeagrassOnStone(biome);
		DefaultBiomeFeatures.addKelp(biome);
		DefaultBiomeFeatures.addFrozenTopLayer(biome);
		DefaultBiomeFeatures.addOceanMobs(biome, 3, 4, 15);
		biome.addSpawn(SpawnGroup.WATER_AMBIENT, new Biome.SpawnEntry(EntityType.SALMON, 15, 1, 5));
		return biome;
	}

	public static Biome createNormalOcean(boolean deep) {
		Biome biome = createOcean(ConfiguredSurfaceBuilders.GRASS, 4159204, 329011, deep, false, true);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, deep ? ConfiguredFeatures.SEAGRASS_DEEP : ConfiguredFeatures.SEAGRASS_NORMAL);
		DefaultBiomeFeatures.addSeagrassOnStone(biome);
		DefaultBiomeFeatures.addKelp(biome);
		DefaultBiomeFeatures.addFrozenTopLayer(biome);
		DefaultBiomeFeatures.addOceanMobs(biome, 1, 4, 10);
		biome.addSpawn(SpawnGroup.WATER_CREATURE, new Biome.SpawnEntry(EntityType.DOLPHIN, 1, 1, 2));
		return biome;
	}

	public static Biome createLukewarmOcean(boolean deep) {
		Biome biome = createOcean(ConfiguredSurfaceBuilders.OCEAN_SAND, 4566514, 267827, deep, true, false);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, deep ? ConfiguredFeatures.SEAGRASS_DEEP_WARM : ConfiguredFeatures.SEAGRASS_WARM);
		if (deep) {
			DefaultBiomeFeatures.addSeagrassOnStone(biome);
		}

		DefaultBiomeFeatures.addLessKelp(biome);
		DefaultBiomeFeatures.addFrozenTopLayer(biome);
		if (deep) {
			DefaultBiomeFeatures.addOceanMobs(biome, 8, 4, 8);
		} else {
			DefaultBiomeFeatures.addOceanMobs(biome, 10, 2, 15);
		}

		biome.addSpawn(SpawnGroup.WATER_AMBIENT, new Biome.SpawnEntry(EntityType.PUFFERFISH, 5, 1, 3));
		biome.addSpawn(SpawnGroup.WATER_AMBIENT, new Biome.SpawnEntry(EntityType.TROPICAL_FISH, 25, 8, 8));
		biome.addSpawn(SpawnGroup.WATER_CREATURE, new Biome.SpawnEntry(EntityType.DOLPHIN, 2, 1, 2));
		return biome;
	}

	public static Biome createWarmOcean() {
		Biome biome = createOcean(ConfiguredSurfaceBuilders.FULL_SAND, 4445678, 270131, false, true, false);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.WARM_OCEAN_VEGETATION);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SEAGRASS_WARM);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SEA_PICKLE);
		DefaultBiomeFeatures.addFrozenTopLayer(biome);
		biome.addSpawn(SpawnGroup.WATER_AMBIENT, new Biome.SpawnEntry(EntityType.PUFFERFISH, 15, 1, 3));
		DefaultBiomeFeatures.addWarmOceanMobs(biome, 10, 4);
		return biome;
	}

	public static Biome createDeepWarmOcean() {
		Biome biome = createOcean(ConfiguredSurfaceBuilders.FULL_SAND, 4445678, 270131, true, true, false);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SEAGRASS_DEEP_WARM);
		DefaultBiomeFeatures.addSeagrassOnStone(biome);
		DefaultBiomeFeatures.addFrozenTopLayer(biome);
		DefaultBiomeFeatures.addWarmOceanMobs(biome, 5, 1);
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.DROWNED, 5, 1, 1));
		return biome;
	}

	public static Biome createFrozenOcean(boolean monument) {
		float f = monument ? 0.5F : 0.0F;
		Biome biome = new Biome(
			new Biome.Settings()
				.surfaceBuilder(ConfiguredSurfaceBuilders.FROZEN_OCEAN)
				.precipitation(monument ? Biome.Precipitation.RAIN : Biome.Precipitation.SNOW)
				.category(Biome.Category.OCEAN)
				.depth(monument ? -1.8F : -1.0F)
				.scale(0.1F)
				.temperature(f)
				.temperatureModifier(Biome.TemperatureModifier.FROZEN)
				.downfall(0.5F)
				.effects(
					new BiomeEffects.Builder().waterColor(3750089).waterFogColor(329011).fogColor(12638463).skyColor(getSkyColor(f)).moodSound(BiomeMoodSound.CAVE).build()
				)
				.parent(null)
		);
		biome.addStructureFeature(ConfiguredStructureFeatures.OCEAN_RUIN_COLD);
		if (monument) {
			biome.addStructureFeature(ConfiguredStructureFeatures.MONUMENT);
		}

		DefaultBiomeFeatures.addOceanStructures(biome);
		biome.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_OCEAN);
		DefaultBiomeFeatures.addOceanCarvers(biome);
		DefaultBiomeFeatures.addDefaultLakes(biome);
		DefaultBiomeFeatures.addIcebergs(biome);
		DefaultBiomeFeatures.addDungeons(biome);
		DefaultBiomeFeatures.addBlueIce(biome);
		DefaultBiomeFeatures.addMineables(biome);
		DefaultBiomeFeatures.addDefaultOres(biome);
		DefaultBiomeFeatures.addDefaultDisks(biome);
		DefaultBiomeFeatures.addWaterBiomeOakTrees(biome);
		DefaultBiomeFeatures.addDefaultFlowers(biome);
		DefaultBiomeFeatures.addDefaultGrass(biome);
		DefaultBiomeFeatures.addDefaultMushrooms(biome);
		DefaultBiomeFeatures.addDefaultVegetation(biome);
		DefaultBiomeFeatures.addSprings(biome);
		DefaultBiomeFeatures.addFrozenTopLayer(biome);
		biome.addSpawn(SpawnGroup.WATER_CREATURE, new Biome.SpawnEntry(EntityType.SQUID, 1, 1, 4));
		biome.addSpawn(SpawnGroup.WATER_AMBIENT, new Biome.SpawnEntry(EntityType.SALMON, 15, 1, 5));
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.POLAR_BEAR, 1, 1, 2));
		DefaultBiomeFeatures.addBatsAndMonsters(biome);
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.DROWNED, 5, 1, 1));
		return biome;
	}

	private static Biome createForest(@Nullable String parent, float depth, float scale, boolean flower) {
		Biome biome = new Biome(
			new Biome.Settings()
				.surfaceBuilder(ConfiguredSurfaceBuilders.GRASS)
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.FOREST)
				.depth(depth)
				.scale(scale)
				.temperature(0.7F)
				.downfall(0.8F)
				.effects(
					new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(getSkyColor(0.7F)).moodSound(BiomeMoodSound.CAVE).build()
				)
				.parent(parent)
		);
		DefaultBiomeFeatures.addDefaultUndergroundStructures(biome);
		biome.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(biome);
		DefaultBiomeFeatures.addDefaultLakes(biome);
		DefaultBiomeFeatures.addDungeons(biome);
		if (flower) {
			biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.FOREST_FLOWER_VEGETATION_COMMON);
		} else {
			DefaultBiomeFeatures.addForestFlowers(biome);
		}

		DefaultBiomeFeatures.addMineables(biome);
		DefaultBiomeFeatures.addDefaultOres(biome);
		DefaultBiomeFeatures.addDefaultDisks(biome);
		if (flower) {
			biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.FOREST_FLOWER_TREES);
			biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.FLOWER_FOREST);
			DefaultBiomeFeatures.addDefaultGrass(biome);
		} else {
			DefaultBiomeFeatures.addForestTrees(biome);
			DefaultBiomeFeatures.addDefaultFlowers(biome);
			DefaultBiomeFeatures.addForestGrass(biome);
		}

		DefaultBiomeFeatures.addDefaultMushrooms(biome);
		DefaultBiomeFeatures.addDefaultVegetation(biome);
		DefaultBiomeFeatures.addSprings(biome);
		DefaultBiomeFeatures.addFrozenTopLayer(biome);
		DefaultBiomeFeatures.addFarmAnimals(biome);
		DefaultBiomeFeatures.addBatsAndMonsters(biome);
		return biome;
	}

	public static Biome createNormalForest(float depth, float scale) {
		Biome biome = createForest(null, depth, scale, false);
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.WOLF, 5, 4, 4));
		return biome;
	}

	public static Biome createFlowerForest() {
		Biome biome = createForest("forest", 0.1F, 0.4F, true);
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
		return biome;
	}

	public static Biome createTaiga(@Nullable String parent, float depth, float scale, boolean snowy, boolean mountains, boolean villages, boolean igloos) {
		float f = snowy ? -0.5F : 0.25F;
		Biome biome = new Biome(
			new Biome.Settings()
				.surfaceBuilder(ConfiguredSurfaceBuilders.GRASS)
				.precipitation(snowy ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN)
				.category(Biome.Category.TAIGA)
				.depth(depth)
				.scale(scale)
				.temperature(f)
				.downfall(snowy ? 0.4F : 0.8F)
				.effects(
					new BiomeEffects.Builder()
						.waterColor(snowy ? 4020182 : 4159204)
						.waterFogColor(329011)
						.fogColor(12638463)
						.skyColor(getSkyColor(f))
						.moodSound(BiomeMoodSound.CAVE)
						.build()
				)
				.parent(parent)
		);
		if (villages) {
			biome.addStructureFeature(ConfiguredStructureFeatures.VILLAGE_TAIGA);
			biome.addStructureFeature(ConfiguredStructureFeatures.PILLAGER_OUTPOST);
		}

		if (igloos) {
			biome.addStructureFeature(ConfiguredStructureFeatures.IGLOO);
		}

		DefaultBiomeFeatures.addDefaultUndergroundStructures(biome);
		biome.addStructureFeature(mountains ? ConfiguredStructureFeatures.RUINED_PORTAL_MOUNTAIN : ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(biome);
		DefaultBiomeFeatures.addDefaultLakes(biome);
		DefaultBiomeFeatures.addDungeons(biome);
		DefaultBiomeFeatures.addLargeFerns(biome);
		DefaultBiomeFeatures.addMineables(biome);
		DefaultBiomeFeatures.addDefaultOres(biome);
		DefaultBiomeFeatures.addDefaultDisks(biome);
		DefaultBiomeFeatures.addTaigaTrees(biome);
		DefaultBiomeFeatures.addDefaultFlowers(biome);
		DefaultBiomeFeatures.addTaigaGrass(biome);
		DefaultBiomeFeatures.addDefaultMushrooms(biome);
		DefaultBiomeFeatures.addDefaultVegetation(biome);
		DefaultBiomeFeatures.addSprings(biome);
		if (snowy) {
			DefaultBiomeFeatures.addSweetBerryBushesSnowy(biome);
		} else {
			DefaultBiomeFeatures.addSweetBerryBushes(biome);
		}

		DefaultBiomeFeatures.addFrozenTopLayer(biome);
		DefaultBiomeFeatures.addFarmAnimals(biome);
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.WOLF, 8, 4, 4));
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.FOX, 8, 2, 4));
		DefaultBiomeFeatures.addBatsAndMonsters(biome);
		return biome;
	}

	public static Biome createDarkForest(@Nullable String parent, float depth, float scale, boolean redMushrooms) {
		Biome biome = new Biome(
			new Biome.Settings()
				.surfaceBuilder(ConfiguredSurfaceBuilders.GRASS)
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.FOREST)
				.depth(depth)
				.scale(scale)
				.temperature(0.7F)
				.downfall(0.8F)
				.effects(
					new BiomeEffects.Builder()
						.waterColor(4159204)
						.waterFogColor(329011)
						.fogColor(12638463)
						.skyColor(getSkyColor(0.7F))
						.grassColorModifier(BiomeEffects.GrassColorModifier.DARK_FOREST)
						.moodSound(BiomeMoodSound.CAVE)
						.build()
				)
				.parent(parent)
		);
		biome.addStructureFeature(ConfiguredStructureFeatures.MANSION);
		DefaultBiomeFeatures.addDefaultUndergroundStructures(biome);
		biome.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(biome);
		DefaultBiomeFeatures.addDefaultLakes(biome);
		DefaultBiomeFeatures.addDungeons(biome);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION, redMushrooms ? ConfiguredFeatures.DARK_FOREST_VEGETATION_RED : ConfiguredFeatures.DARK_FOREST_VEGETATION_BROWN
		);
		DefaultBiomeFeatures.addForestFlowers(biome);
		DefaultBiomeFeatures.addMineables(biome);
		DefaultBiomeFeatures.addDefaultOres(biome);
		DefaultBiomeFeatures.addDefaultDisks(biome);
		DefaultBiomeFeatures.addDefaultFlowers(biome);
		DefaultBiomeFeatures.addForestGrass(biome);
		DefaultBiomeFeatures.addDefaultMushrooms(biome);
		DefaultBiomeFeatures.addDefaultVegetation(biome);
		DefaultBiomeFeatures.addSprings(biome);
		DefaultBiomeFeatures.addFrozenTopLayer(biome);
		DefaultBiomeFeatures.addFarmAnimals(biome);
		DefaultBiomeFeatures.addBatsAndMonsters(biome);
		return biome;
	}

	public static Biome createSwamp(@Nullable String parent, float depth, float scale, boolean hills) {
		Biome biome = new Biome(
			new Biome.Settings()
				.surfaceBuilder(ConfiguredSurfaceBuilders.SWAMP)
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.SWAMP)
				.depth(depth)
				.scale(scale)
				.temperature(0.8F)
				.downfall(0.9F)
				.effects(
					new BiomeEffects.Builder()
						.waterColor(6388580)
						.waterFogColor(2302743)
						.fogColor(12638463)
						.skyColor(getSkyColor(0.8F))
						.foliageColor(6975545)
						.grassColorModifier(BiomeEffects.GrassColorModifier.SWAMP)
						.moodSound(BiomeMoodSound.CAVE)
						.build()
				)
				.parent(parent)
		);
		if (!hills) {
			biome.addStructureFeature(ConfiguredStructureFeatures.SWAMP_HUT);
		}

		biome.addStructureFeature(ConfiguredStructureFeatures.MINESHAFT);
		biome.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_SWAMP);
		DefaultBiomeFeatures.addLandCarvers(biome);
		if (!hills) {
			DefaultBiomeFeatures.addFossils(biome);
		}

		DefaultBiomeFeatures.addDefaultLakes(biome);
		DefaultBiomeFeatures.addDungeons(biome);
		DefaultBiomeFeatures.addMineables(biome);
		DefaultBiomeFeatures.addDefaultOres(biome);
		DefaultBiomeFeatures.addClay(biome);
		DefaultBiomeFeatures.addSwampFeatures(biome);
		DefaultBiomeFeatures.addDefaultMushrooms(biome);
		DefaultBiomeFeatures.addSwampVegetation(biome);
		DefaultBiomeFeatures.addSprings(biome);
		if (hills) {
			DefaultBiomeFeatures.addFossils(biome);
		} else {
			biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SEAGRASS_SWAMP);
		}

		DefaultBiomeFeatures.addFrozenTopLayer(biome);
		DefaultBiomeFeatures.addFarmAnimals(biome);
		DefaultBiomeFeatures.addBatsAndMonsters(biome);
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.SLIME, 1, 1, 1));
		return biome;
	}

	public static Biome createSnowyTundra(@Nullable String parent, float depth, float scale, boolean iceSpikes, boolean snowyMountains) {
		Biome biome = new Biome(
			new Biome.Settings()
				.surfaceBuilder(iceSpikes ? ConfiguredSurfaceBuilders.ICE_SPIKES : ConfiguredSurfaceBuilders.GRASS)
				.precipitation(Biome.Precipitation.SNOW)
				.category(Biome.Category.ICY)
				.depth(depth)
				.scale(scale)
				.temperature(0.0F)
				.downfall(0.5F)
				.creatureGenerationProbability(0.07F)
				.effects(
					new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(getSkyColor(0.0F)).moodSound(BiomeMoodSound.CAVE).build()
				)
				.parent(parent)
		);
		if (!iceSpikes && !snowyMountains) {
			biome.addStructureFeature(ConfiguredStructureFeatures.VILLAGE_SNOWY);
			biome.addStructureFeature(ConfiguredStructureFeatures.IGLOO);
		}

		DefaultBiomeFeatures.addDefaultUndergroundStructures(biome);
		if (!iceSpikes && !snowyMountains) {
			biome.addStructureFeature(ConfiguredStructureFeatures.PILLAGER_OUTPOST);
		}

		biome.addStructureFeature(snowyMountains ? ConfiguredStructureFeatures.RUINED_PORTAL_MOUNTAIN : ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(biome);
		DefaultBiomeFeatures.addDefaultLakes(biome);
		DefaultBiomeFeatures.addDungeons(biome);
		if (iceSpikes) {
			biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.ICE_SPIKE);
			biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.ICE_PATCH);
		}

		DefaultBiomeFeatures.addMineables(biome);
		DefaultBiomeFeatures.addDefaultOres(biome);
		DefaultBiomeFeatures.addDefaultDisks(biome);
		DefaultBiomeFeatures.addSnowySpruceTrees(biome);
		DefaultBiomeFeatures.addDefaultFlowers(biome);
		DefaultBiomeFeatures.addDefaultGrass(biome);
		DefaultBiomeFeatures.addDefaultMushrooms(biome);
		DefaultBiomeFeatures.addDefaultVegetation(biome);
		DefaultBiomeFeatures.addSprings(biome);
		DefaultBiomeFeatures.addFrozenTopLayer(biome);
		DefaultBiomeFeatures.addSnowyMobs(biome);
		return biome;
	}

	public static Biome createRiver(float depth, float scale, float temperature, int waterColor, boolean frozen) {
		Biome biome = new Biome(
			new Biome.Settings()
				.surfaceBuilder(ConfiguredSurfaceBuilders.GRASS)
				.precipitation(frozen ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN)
				.category(Biome.Category.RIVER)
				.depth(depth)
				.scale(scale)
				.temperature(temperature)
				.downfall(0.5F)
				.effects(
					new BiomeEffects.Builder()
						.waterColor(waterColor)
						.waterFogColor(329011)
						.fogColor(12638463)
						.skyColor(getSkyColor(temperature))
						.moodSound(BiomeMoodSound.CAVE)
						.build()
				)
				.parent(null)
		);
		biome.addStructureFeature(ConfiguredStructureFeatures.MINESHAFT);
		biome.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(biome);
		DefaultBiomeFeatures.addDefaultLakes(biome);
		DefaultBiomeFeatures.addDungeons(biome);
		DefaultBiomeFeatures.addMineables(biome);
		DefaultBiomeFeatures.addDefaultOres(biome);
		DefaultBiomeFeatures.addDefaultDisks(biome);
		DefaultBiomeFeatures.addWaterBiomeOakTrees(biome);
		DefaultBiomeFeatures.addDefaultFlowers(biome);
		DefaultBiomeFeatures.addDefaultGrass(biome);
		DefaultBiomeFeatures.addDefaultMushrooms(biome);
		DefaultBiomeFeatures.addDefaultVegetation(biome);
		DefaultBiomeFeatures.addSprings(biome);
		if (!frozen) {
			biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SEAGRASS_RIVER);
		}

		DefaultBiomeFeatures.addFrozenTopLayer(biome);
		biome.addSpawn(SpawnGroup.WATER_CREATURE, new Biome.SpawnEntry(EntityType.SQUID, 2, 1, 4));
		biome.addSpawn(SpawnGroup.WATER_AMBIENT, new Biome.SpawnEntry(EntityType.SALMON, 5, 1, 5));
		DefaultBiomeFeatures.addBatsAndMonsters(biome);
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.DROWNED, frozen ? 1 : 100, 1, 1));
		return biome;
	}

	public static Biome createBeach(float depth, float scale, float temperature, float downfall, int waterColor, boolean snowy, boolean stony) {
		Biome biome = new Biome(
			new Biome.Settings()
				.surfaceBuilder(stony ? ConfiguredSurfaceBuilders.STONE : ConfiguredSurfaceBuilders.DESERT)
				.precipitation(snowy ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN)
				.category(stony ? Biome.Category.NONE : Biome.Category.BEACH)
				.depth(depth)
				.scale(scale)
				.temperature(temperature)
				.downfall(downfall)
				.effects(
					new BiomeEffects.Builder()
						.waterColor(waterColor)
						.waterFogColor(329011)
						.fogColor(12638463)
						.skyColor(getSkyColor(temperature))
						.moodSound(BiomeMoodSound.CAVE)
						.build()
				)
				.parent(null)
		);
		if (stony) {
			DefaultBiomeFeatures.addDefaultUndergroundStructures(biome);
		} else {
			biome.addStructureFeature(ConfiguredStructureFeatures.MINESHAFT);
			biome.addStructureFeature(ConfiguredStructureFeatures.BURIED_TREASURE);
			biome.addStructureFeature(ConfiguredStructureFeatures.SHIPWRECK_BEACHED);
		}

		biome.addStructureFeature(stony ? ConfiguredStructureFeatures.RUINED_PORTAL_MOUNTAIN : ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(biome);
		DefaultBiomeFeatures.addDefaultLakes(biome);
		DefaultBiomeFeatures.addDungeons(biome);
		DefaultBiomeFeatures.addMineables(biome);
		DefaultBiomeFeatures.addDefaultOres(biome);
		DefaultBiomeFeatures.addDefaultDisks(biome);
		DefaultBiomeFeatures.addDefaultFlowers(biome);
		DefaultBiomeFeatures.addDefaultGrass(biome);
		DefaultBiomeFeatures.addDefaultMushrooms(biome);
		DefaultBiomeFeatures.addDefaultVegetation(biome);
		DefaultBiomeFeatures.addSprings(biome);
		DefaultBiomeFeatures.addFrozenTopLayer(biome);
		if (!stony && !snowy) {
			biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.TURTLE, 5, 2, 5));
		}

		DefaultBiomeFeatures.addBatsAndMonsters(biome);
		return biome;
	}

	public static Biome createTheVoid() {
		Biome biome = new Biome(
			new Biome.Settings()
				.surfaceBuilder(ConfiguredSurfaceBuilders.NOPE)
				.precipitation(Biome.Precipitation.NONE)
				.category(Biome.Category.NONE)
				.depth(0.1F)
				.scale(0.2F)
				.temperature(0.5F)
				.downfall(0.5F)
				.effects(
					new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(getSkyColor(0.5F)).moodSound(BiomeMoodSound.CAVE).build()
				)
				.parent(null)
		);
		biome.addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, ConfiguredFeatures.VOID_START_PLATFORM);
		return biome;
	}

	public static Biome createNetherWastes() {
		Biome biome = new Biome(
			new Biome.Settings()
				.surfaceBuilder(ConfiguredSurfaceBuilders.NETHER)
				.precipitation(Biome.Precipitation.NONE)
				.category(Biome.Category.NETHER)
				.depth(0.1F)
				.scale(0.2F)
				.temperature(2.0F)
				.downfall(0.0F)
				.effects(
					new BiomeEffects.Builder()
						.waterColor(4159204)
						.waterFogColor(329011)
						.fogColor(3344392)
						.skyColor(getSkyColor(2.0F))
						.loopSound(SoundEvents.AMBIENT_NETHER_WASTES_LOOP)
						.moodSound(new BiomeMoodSound(SoundEvents.AMBIENT_NETHER_WASTES_MOOD, 6000, 8, 2.0))
						.additionsSound(new BiomeAdditionsSound(SoundEvents.AMBIENT_NETHER_WASTES_ADDITIONS, 0.0111))
						.music(MusicType.createIngameMusic(SoundEvents.MUSIC_NETHER_NETHER_WASTES))
						.build()
				)
				.parent(null)
		);
		biome.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_NETHER);
		biome.addStructureFeature(ConfiguredStructureFeatures.FORTRESS);
		biome.addStructureFeature(ConfiguredStructureFeatures.BASTION_REMNANT);
		biome.addCarver(GenerationStep.Carver.AIR, ConfiguredCarvers.NETHER_CAVE);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SPRING_LAVA);
		DefaultBiomeFeatures.addDefaultMushrooms(biome);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_OPEN);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_FIRE);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_SOUL_FIRE);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE_EXTRA);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.BROWN_MUSHROOM_NETHER);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.RED_MUSHROOM_NETHER);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_MAGMA);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_CLOSED);
		DefaultBiomeFeatures.addNetherMineables(biome);
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.GHAST, 50, 4, 4));
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIFIED_PIGLIN, 100, 4, 4));
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.MAGMA_CUBE, 2, 4, 4));
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 1, 4, 4));
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.PIGLIN, 15, 4, 4));
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.STRIDER, 60, 1, 2));
		return biome;
	}

	public static Biome createSoulSandValley() {
		Biome biome = new Biome(
			new Biome.Settings()
				.surfaceBuilder(ConfiguredSurfaceBuilders.SOUL_SAND_VALLEY)
				.precipitation(Biome.Precipitation.NONE)
				.category(Biome.Category.NETHER)
				.depth(0.1F)
				.scale(0.2F)
				.temperature(2.0F)
				.downfall(0.0F)
				.effects(
					new BiomeEffects.Builder()
						.waterColor(4159204)
						.waterFogColor(329011)
						.fogColor(1787717)
						.skyColor(getSkyColor(2.0F))
						.particleConfig(new BiomeParticleConfig(ParticleTypes.ASH, 0.00625F))
						.loopSound(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_LOOP)
						.moodSound(new BiomeMoodSound(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_MOOD, 6000, 8, 2.0))
						.additionsSound(new BiomeAdditionsSound(SoundEvents.AMBIENT_SOUL_SAND_VALLEY_ADDITIONS, 0.0111))
						.music(MusicType.createIngameMusic(SoundEvents.MUSIC_NETHER_SOUL_SAND_VALLEY))
						.build()
				)
				.parent(null)
		);
		biome.addStructureFeature(ConfiguredStructureFeatures.FORTRESS);
		biome.addStructureFeature(ConfiguredStructureFeatures.NETHER_FOSSIL);
		biome.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_NETHER);
		biome.addStructureFeature(ConfiguredStructureFeatures.BASTION_REMNANT);
		biome.addCarver(GenerationStep.Carver.AIR, ConfiguredCarvers.NETHER_CAVE);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SPRING_LAVA);
		biome.addFeature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ConfiguredFeatures.BASALT_PILLAR);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_OPEN);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE_EXTRA);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_CRIMSON_ROOTS);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_FIRE);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_SOUL_FIRE);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_MAGMA);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_CLOSED);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_SOUL_SAND);
		DefaultBiomeFeatures.addNetherMineables(biome);
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.SKELETON, 20, 5, 5));
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.GHAST, 50, 4, 4));
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 1, 4, 4));
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.STRIDER, 60, 1, 2));
		double d = 0.7;
		double e = 0.15;
		biome.addSpawnDensity(EntityType.SKELETON, 0.7, 0.15);
		biome.addSpawnDensity(EntityType.GHAST, 0.7, 0.15);
		biome.addSpawnDensity(EntityType.ENDERMAN, 0.7, 0.15);
		biome.addSpawnDensity(EntityType.STRIDER, 0.7, 0.15);
		return biome;
	}

	public static Biome createBasaltDeltas() {
		Biome biome = new Biome(
			new Biome.Settings()
				.surfaceBuilder(ConfiguredSurfaceBuilders.BASALT_DELTAS)
				.precipitation(Biome.Precipitation.NONE)
				.category(Biome.Category.NETHER)
				.depth(0.1F)
				.scale(0.2F)
				.temperature(2.0F)
				.downfall(0.0F)
				.effects(
					new BiomeEffects.Builder()
						.waterColor(4159204)
						.waterFogColor(4341314)
						.fogColor(6840176)
						.skyColor(getSkyColor(2.0F))
						.particleConfig(new BiomeParticleConfig(ParticleTypes.WHITE_ASH, 0.118093334F))
						.loopSound(SoundEvents.AMBIENT_BASALT_DELTAS_LOOP)
						.moodSound(new BiomeMoodSound(SoundEvents.AMBIENT_BASALT_DELTAS_MOOD, 6000, 8, 2.0))
						.additionsSound(new BiomeAdditionsSound(SoundEvents.AMBIENT_BASALT_DELTAS_ADDITIONS, 0.0111))
						.music(MusicType.createIngameMusic(SoundEvents.MUSIC_NETHER_BASALT_DELTAS))
						.build()
				)
				.parent(null)
		);
		biome.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_NETHER);
		biome.addCarver(GenerationStep.Carver.AIR, ConfiguredCarvers.NETHER_CAVE);
		biome.addStructureFeature(ConfiguredStructureFeatures.FORTRESS);
		biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.DELTA);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SPRING_LAVA_DOUBLE);
		biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.SMALL_BASALT_COLUMNS);
		biome.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.LARGE_BASALT_COLUMNS);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.BASALT_BLOBS);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.BLACKSTONE_BLOBS);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_DELTA);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_FIRE);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_SOUL_FIRE);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE_EXTRA);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.BROWN_MUSHROOM_NETHER);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.RED_MUSHROOM_NETHER);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_MAGMA);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_CLOSED_DOUBLE);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_GOLD_DELTAS);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_QUARTZ_DELTAS);
		DefaultBiomeFeatures.addAncientDebris(biome);
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.GHAST, 40, 1, 1));
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.MAGMA_CUBE, 100, 2, 5));
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.STRIDER, 60, 1, 2));
		return biome;
	}

	public static Biome createCrimsonForest() {
		Biome biome = new Biome(
			new Biome.Settings()
				.surfaceBuilder(ConfiguredSurfaceBuilders.CRIMSON_FOREST)
				.precipitation(Biome.Precipitation.NONE)
				.category(Biome.Category.NETHER)
				.depth(0.1F)
				.scale(0.2F)
				.temperature(2.0F)
				.downfall(0.0F)
				.effects(
					new BiomeEffects.Builder()
						.waterColor(4159204)
						.waterFogColor(329011)
						.fogColor(3343107)
						.skyColor(getSkyColor(2.0F))
						.particleConfig(new BiomeParticleConfig(ParticleTypes.CRIMSON_SPORE, 0.025F))
						.loopSound(SoundEvents.AMBIENT_CRIMSON_FOREST_LOOP)
						.moodSound(new BiomeMoodSound(SoundEvents.AMBIENT_CRIMSON_FOREST_MOOD, 6000, 8, 2.0))
						.additionsSound(new BiomeAdditionsSound(SoundEvents.AMBIENT_CRIMSON_FOREST_ADDITIONS, 0.0111))
						.music(MusicType.createIngameMusic(SoundEvents.MUSIC_NETHER_CRIMSON_FOREST))
						.build()
				)
				.parent(null)
		);
		biome.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_NETHER);
		biome.addCarver(GenerationStep.Carver.AIR, ConfiguredCarvers.NETHER_CAVE);
		biome.addStructureFeature(ConfiguredStructureFeatures.FORTRESS);
		biome.addStructureFeature(ConfiguredStructureFeatures.BASTION_REMNANT);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SPRING_LAVA);
		DefaultBiomeFeatures.addDefaultMushrooms(biome);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_OPEN);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_FIRE);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE_EXTRA);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_MAGMA);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_CLOSED);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.WEEPING_VINES);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.CRIMSON_FUNGI);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.CRIMSON_FOREST_VEGETATION);
		DefaultBiomeFeatures.addNetherMineables(biome);
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIFIED_PIGLIN, 1, 2, 4));
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.HOGLIN, 9, 3, 4));
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.PIGLIN, 5, 3, 4));
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.STRIDER, 60, 1, 2));
		return biome;
	}

	public static Biome createWarpedForest() {
		Biome biome = new Biome(
			new Biome.Settings()
				.surfaceBuilder(ConfiguredSurfaceBuilders.WARPED_FOREST)
				.precipitation(Biome.Precipitation.NONE)
				.category(Biome.Category.NETHER)
				.depth(0.1F)
				.scale(0.2F)
				.temperature(2.0F)
				.downfall(0.0F)
				.effects(
					new BiomeEffects.Builder()
						.waterColor(4159204)
						.waterFogColor(329011)
						.fogColor(1705242)
						.skyColor(getSkyColor(2.0F))
						.particleConfig(new BiomeParticleConfig(ParticleTypes.WARPED_SPORE, 0.01428F))
						.loopSound(SoundEvents.AMBIENT_WARPED_FOREST_LOOP)
						.moodSound(new BiomeMoodSound(SoundEvents.AMBIENT_WARPED_FOREST_MOOD, 6000, 8, 2.0))
						.additionsSound(new BiomeAdditionsSound(SoundEvents.AMBIENT_WARPED_FOREST_ADDITIONS, 0.0111))
						.music(MusicType.createIngameMusic(SoundEvents.MUSIC_NETHER_WARPED_FOREST))
						.build()
				)
				.parent(null)
		);
		biome.addStructureFeature(ConfiguredStructureFeatures.FORTRESS);
		biome.addStructureFeature(ConfiguredStructureFeatures.BASTION_REMNANT);
		biome.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_NETHER);
		biome.addCarver(GenerationStep.Carver.AIR, ConfiguredCarvers.NETHER_CAVE);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SPRING_LAVA);
		DefaultBiomeFeatures.addDefaultMushrooms(biome);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_OPEN);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_FIRE);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_SOUL_FIRE);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE_EXTRA);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_MAGMA);
		biome.addFeature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_CLOSED);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.WARPED_FUNGI);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.WARPED_FOREST_VEGETATION);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.NETHER_SPROUTS);
		biome.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.TWISTING_VINES);
		DefaultBiomeFeatures.addNetherMineables(biome);
		biome.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 1, 4, 4));
		biome.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.STRIDER, 60, 1, 2));
		biome.addSpawnDensity(EntityType.ENDERMAN, 1.0, 0.12);
		return biome;
	}
}
