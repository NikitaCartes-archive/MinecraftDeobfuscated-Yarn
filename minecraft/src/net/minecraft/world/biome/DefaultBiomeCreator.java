package net.minecraft.world.biome;

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

	public static Biome createGiantTreeTaiga(float depth, float scale, float temperature, boolean spruce) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.WOLF, 8, 4, 4));
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.FOX, 8, 2, 4));
		if (spruce) {
			DefaultBiomeFeatures.addBatsAndMonsters(builder);
		} else {
			DefaultBiomeFeatures.addBats(builder);
			DefaultBiomeFeatures.addMonsters(builder, 100, 25, 100);
		}

		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.GIANT_TREE_TAIGA);
		DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		builder2.structureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(builder2);
		DefaultBiomeFeatures.addDefaultLakes(builder2);
		DefaultBiomeFeatures.addDungeons(builder2);
		DefaultBiomeFeatures.addMossyRocks(builder2);
		DefaultBiomeFeatures.addLargeFerns(builder2);
		DefaultBiomeFeatures.addMineables(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, spruce ? ConfiguredFeatures.TREES_GIANT_SPRUCE : ConfiguredFeatures.TREES_GIANT);
		DefaultBiomeFeatures.addDefaultFlowers(builder2);
		DefaultBiomeFeatures.addGiantTaigaGrass(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
		DefaultBiomeFeatures.addSprings(builder2);
		DefaultBiomeFeatures.addSweetBerryBushes(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return new Biome.Builder()
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
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	public static Biome createBirchForest(float depth, float scale, boolean tallTrees) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.GRASS);
		DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		builder2.structureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(builder2);
		DefaultBiomeFeatures.addDefaultLakes(builder2);
		DefaultBiomeFeatures.addDungeons(builder2);
		DefaultBiomeFeatures.addForestFlowers(builder2);
		DefaultBiomeFeatures.addMineables(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		if (tallTrees) {
			DefaultBiomeFeatures.addTallBirchTrees(builder2);
		} else {
			DefaultBiomeFeatures.addBirchTrees(builder2);
		}

		DefaultBiomeFeatures.addDefaultFlowers(builder2);
		DefaultBiomeFeatures.addForestGrass(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
		DefaultBiomeFeatures.addSprings(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.RAIN)
			.category(Biome.Category.FOREST)
			.depth(depth)
			.scale(scale)
			.temperature(0.6F)
			.downfall(0.6F)
			.effects(
				new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(getSkyColor(0.6F)).moodSound(BiomeMoodSound.CAVE).build()
			)
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	public static Biome createJungle() {
		return createJungle(0.1F, 0.2F, 40, 2, 3);
	}

	public static Biome createJungleEdge() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addJungleMobs(builder);
		return createJungleFeatures(0.1F, 0.2F, 0.8F, false, true, false, builder);
	}

	public static Biome createModifiedJungleEdge() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addJungleMobs(builder);
		return createJungleFeatures(0.2F, 0.4F, 0.8F, false, true, true, builder);
	}

	public static Biome createModifiedJungle() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addJungleMobs(builder);
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.PARROT, 10, 1, 1))
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.OCELOT, 2, 1, 1));
		return createJungleFeatures(0.2F, 0.4F, 0.9F, false, false, true, builder);
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
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addJungleMobs(builder);
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.PARROT, parrotWeight, 1, parrotMaxGroupSize))
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.OCELOT, 2, 1, ocelotMaxGroupSize))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.PANDA, 1, 1, 2));
		builder.playerSpawnFriendly();
		return createJungleFeatures(depth, scale, 0.9F, false, false, false, builder);
	}

	private static Biome createBambooJungle(float depth, float scale, int parrotWeight, int parrotMaxGroupSize) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addJungleMobs(builder);
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.PARROT, parrotWeight, 1, parrotMaxGroupSize))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.PANDA, 80, 1, 2))
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.OCELOT, 2, 1, 1));
		return createJungleFeatures(depth, scale, 0.9F, true, false, false, builder);
	}

	private static Biome createJungleFeatures(float depth, float scale, float downfall, boolean bl, boolean bl2, boolean bl3, SpawnSettings.Builder builder) {
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.GRASS);
		if (!bl2 && !bl3) {
			builder2.structureFeature(ConfiguredStructureFeatures.JUNGLE_PYRAMID);
		}

		DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		builder2.structureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_JUNGLE);
		DefaultBiomeFeatures.addLandCarvers(builder2);
		DefaultBiomeFeatures.addDefaultLakes(builder2);
		DefaultBiomeFeatures.addDungeons(builder2);
		DefaultBiomeFeatures.addMineables(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		if (bl) {
			DefaultBiomeFeatures.addBambooJungleTrees(builder2);
		} else {
			if (!bl2 && !bl3) {
				DefaultBiomeFeatures.addBamboo(builder2);
			}

			if (bl2) {
				DefaultBiomeFeatures.addJungleEdgeTrees(builder2);
			} else {
				DefaultBiomeFeatures.addJungleTrees(builder2);
			}
		}

		DefaultBiomeFeatures.addExtraDefaultFlowers(builder2);
		DefaultBiomeFeatures.addJungleGrass(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
		DefaultBiomeFeatures.addSprings(builder2);
		DefaultBiomeFeatures.addJungleVegetation(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.RAIN)
			.category(Biome.Category.JUNGLE)
			.depth(depth)
			.scale(scale)
			.temperature(0.95F)
			.downfall(downfall)
			.effects(
				new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(getSkyColor(0.95F)).moodSound(BiomeMoodSound.CAVE).build()
			)
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	public static Biome createMountains(float depth, float scale, ConfiguredSurfaceBuilder<TernarySurfaceConfig> surfaceBuilder, boolean extraTrees) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.LLAMA, 5, 4, 6));
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(surfaceBuilder);
		DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		builder2.structureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_MOUNTAIN);
		DefaultBiomeFeatures.addLandCarvers(builder2);
		DefaultBiomeFeatures.addDefaultLakes(builder2);
		DefaultBiomeFeatures.addDungeons(builder2);
		DefaultBiomeFeatures.addMineables(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		if (extraTrees) {
			DefaultBiomeFeatures.addExtraMountainTrees(builder2);
		} else {
			DefaultBiomeFeatures.addMountainTrees(builder2);
		}

		DefaultBiomeFeatures.addDefaultFlowers(builder2);
		DefaultBiomeFeatures.addDefaultGrass(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
		DefaultBiomeFeatures.addSprings(builder2);
		DefaultBiomeFeatures.addEmeraldOre(builder2);
		DefaultBiomeFeatures.addInfestedStone(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.RAIN)
			.category(Biome.Category.EXTREME_HILLS)
			.depth(depth)
			.scale(scale)
			.temperature(0.2F)
			.downfall(0.3F)
			.effects(
				new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(getSkyColor(0.2F)).moodSound(BiomeMoodSound.CAVE).build()
			)
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	public static Biome createDesert(float depth, float scale, boolean bl, boolean bl2, boolean bl3) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addDesertMobs(builder);
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.DESERT);
		if (bl) {
			builder2.structureFeature(ConfiguredStructureFeatures.VILLAGE_DESERT);
			builder2.structureFeature(ConfiguredStructureFeatures.PILLAGER_OUTPOST);
		}

		if (bl2) {
			builder2.structureFeature(ConfiguredStructureFeatures.DESERT_PYRAMID);
		}

		if (bl3) {
			DefaultBiomeFeatures.addFossils(builder2);
		}

		DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		builder2.structureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_DESERT);
		DefaultBiomeFeatures.addLandCarvers(builder2);
		DefaultBiomeFeatures.addDesertLakes(builder2);
		DefaultBiomeFeatures.addDungeons(builder2);
		DefaultBiomeFeatures.addMineables(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		DefaultBiomeFeatures.addDefaultFlowers(builder2);
		DefaultBiomeFeatures.addDefaultGrass(builder2);
		DefaultBiomeFeatures.addDesertDeadBushes(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDesertVegetation(builder2);
		DefaultBiomeFeatures.addSprings(builder2);
		DefaultBiomeFeatures.addDesertFeatures(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.NONE)
			.category(Biome.Category.DESERT)
			.depth(depth)
			.scale(scale)
			.temperature(2.0F)
			.downfall(0.0F)
			.effects(
				new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(getSkyColor(2.0F)).moodSound(BiomeMoodSound.CAVE).build()
			)
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	public static Biome createPlains(boolean bl) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addPlainsMobs(builder);
		if (!bl) {
			builder.playerSpawnFriendly();
		}

		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.GRASS);
		if (!bl) {
			builder2.structureFeature(ConfiguredStructureFeatures.VILLAGE_PLAINS).structureFeature(ConfiguredStructureFeatures.PILLAGER_OUTPOST);
		}

		DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		builder2.structureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(builder2);
		DefaultBiomeFeatures.addDefaultLakes(builder2);
		DefaultBiomeFeatures.addDungeons(builder2);
		DefaultBiomeFeatures.addPlainsTallGrass(builder2);
		if (bl) {
			builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_SUNFLOWER);
		}

		DefaultBiomeFeatures.addMineables(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		DefaultBiomeFeatures.addPlainsFeatures(builder2);
		if (bl) {
			builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_SUGAR_CANE);
		}

		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		if (bl) {
			builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.PATCH_PUMPKIN);
		} else {
			DefaultBiomeFeatures.addDefaultVegetation(builder2);
		}

		DefaultBiomeFeatures.addSprings(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.RAIN)
			.category(Biome.Category.PLAINS)
			.depth(0.125F)
			.scale(0.05F)
			.temperature(0.8F)
			.downfall(0.4F)
			.effects(
				new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(getSkyColor(0.8F)).moodSound(BiomeMoodSound.CAVE).build()
			)
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	private static Biome composeEndSpawnSettings(GenerationSettings.Builder builder) {
		SpawnSettings.Builder builder2 = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addEndMobs(builder2);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.NONE)
			.category(Biome.Category.THEEND)
			.depth(0.1F)
			.scale(0.2F)
			.temperature(0.5F)
			.downfall(0.5F)
			.effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(10518688).skyColor(0).moodSound(BiomeMoodSound.CAVE).build())
			.spawnSettings(builder2.build())
			.generationSettings(builder.build())
			.build();
	}

	public static Biome createEndBarrens() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.END);
		return composeEndSpawnSettings(builder);
	}

	public static Biome createTheEnd() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
			.surfaceBuilder(ConfiguredSurfaceBuilders.END)
			.feature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.END_SPIKE);
		return composeEndSpawnSettings(builder);
	}

	public static Biome createEndMidlands() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
			.surfaceBuilder(ConfiguredSurfaceBuilders.END)
			.structureFeature(ConfiguredStructureFeatures.END_CITY);
		return composeEndSpawnSettings(builder);
	}

	public static Biome createEndHighlands() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
			.surfaceBuilder(ConfiguredSurfaceBuilders.END)
			.structureFeature(ConfiguredStructureFeatures.END_CITY)
			.feature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.END_GATEWAY)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.CHORUS_PLANT);
		return composeEndSpawnSettings(builder);
	}

	public static Biome createSmallEndIslands() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
			.surfaceBuilder(ConfiguredSurfaceBuilders.END)
			.feature(GenerationStep.Feature.RAW_GENERATION, ConfiguredFeatures.END_ISLAND_DECORATED);
		return composeEndSpawnSettings(builder);
	}

	public static Biome createMushroomFields(float depth, float scale) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addMushroomMobs(builder);
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.MYCELIUM);
		DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		builder2.structureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(builder2);
		DefaultBiomeFeatures.addDefaultLakes(builder2);
		DefaultBiomeFeatures.addDungeons(builder2);
		DefaultBiomeFeatures.addMineables(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		DefaultBiomeFeatures.addMushroomFieldsFeatures(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
		DefaultBiomeFeatures.addSprings(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.RAIN)
			.category(Biome.Category.MUSHROOM)
			.depth(depth)
			.scale(scale)
			.temperature(0.9F)
			.downfall(1.0F)
			.effects(
				new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(getSkyColor(0.9F)).moodSound(BiomeMoodSound.CAVE).build()
			)
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	private static Biome composeSavannaGenerationSettings(float depth, float scale, float temperature, boolean bl, boolean bl2, SpawnSettings.Builder builder) {
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder()
			.surfaceBuilder(bl2 ? ConfiguredSurfaceBuilders.SHATTERED_SAVANNA : ConfiguredSurfaceBuilders.GRASS);
		if (!bl && !bl2) {
			builder2.structureFeature(ConfiguredStructureFeatures.VILLAGE_SAVANNA).structureFeature(ConfiguredStructureFeatures.PILLAGER_OUTPOST);
		}

		DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		builder2.structureFeature(bl ? ConfiguredStructureFeatures.RUINED_PORTAL_MOUNTAIN : ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(builder2);
		DefaultBiomeFeatures.addDefaultLakes(builder2);
		DefaultBiomeFeatures.addDungeons(builder2);
		if (!bl2) {
			DefaultBiomeFeatures.addSavannaTallGrass(builder2);
		}

		DefaultBiomeFeatures.addMineables(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		if (bl2) {
			DefaultBiomeFeatures.addExtraSavannaTrees(builder2);
			DefaultBiomeFeatures.addDefaultFlowers(builder2);
			DefaultBiomeFeatures.addShatteredSavannaGrass(builder2);
		} else {
			DefaultBiomeFeatures.addSavannaTrees(builder2);
			DefaultBiomeFeatures.addExtraDefaultFlowers(builder2);
			DefaultBiomeFeatures.addSavannaGrass(builder2);
		}

		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
		DefaultBiomeFeatures.addSprings(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return new Biome.Builder()
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
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	public static Biome createSavanna(float depth, float scale, float temperature, boolean bl, boolean bl2) {
		SpawnSettings.Builder builder = createSavannaSpawnSettings();
		return composeSavannaGenerationSettings(depth, scale, temperature, bl, bl2, builder);
	}

	private static SpawnSettings.Builder createSavannaSpawnSettings() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.HORSE, 1, 2, 6))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.DONKEY, 1, 1, 1));
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		return builder;
	}

	public static Biome createSavannaPlateau() {
		SpawnSettings.Builder builder = createSavannaSpawnSettings();
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.LLAMA, 8, 4, 4));
		return composeSavannaGenerationSettings(1.5F, 0.025F, 1.0F, true, false, builder);
	}

	private static Biome createBadlands(ConfiguredSurfaceBuilder<TernarySurfaceConfig> configuredSurfaceBuilder, float depth, float scale, boolean bl, boolean bl2) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(configuredSurfaceBuilder);
		DefaultBiomeFeatures.addBadlandsUndergroundStructures(builder2);
		builder2.structureFeature(bl ? ConfiguredStructureFeatures.RUINED_PORTAL_MOUNTAIN : ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(builder2);
		DefaultBiomeFeatures.addDefaultLakes(builder2);
		DefaultBiomeFeatures.addDungeons(builder2);
		DefaultBiomeFeatures.addMineables(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addExtraGoldOre(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		if (bl2) {
			DefaultBiomeFeatures.addBadlandsPlateauTrees(builder2);
		}

		DefaultBiomeFeatures.addBadlandsGrass(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addBadlandsVegetation(builder2);
		DefaultBiomeFeatures.addSprings(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return new Biome.Builder()
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
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	public static Biome createNormalBadlands(float depth, float scale, boolean bl) {
		return createBadlands(ConfiguredSurfaceBuilders.BADLANDS, depth, scale, bl, false);
	}

	public static Biome createWoodedBadlandsPlateau(float depth, float scale) {
		return createBadlands(ConfiguredSurfaceBuilders.WOODED_BADLANDS, depth, scale, true, true);
	}

	public static Biome createErodedBadlands() {
		return createBadlands(ConfiguredSurfaceBuilders.ERODED_BADLANDS, 0.1F, 0.2F, true, false);
	}

	private static Biome createOcean(SpawnSettings.Builder builder, int waterColor, int waterFogColor, boolean deep, GenerationSettings.Builder builder2) {
		return new Biome.Builder()
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
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	private static GenerationSettings.Builder createOceanGenerationSettings(
		ConfiguredSurfaceBuilder<TernarySurfaceConfig> configuredSurfaceBuilder, boolean bl, boolean bl2, boolean bl3
	) {
		GenerationSettings.Builder builder = new GenerationSettings.Builder().surfaceBuilder(configuredSurfaceBuilder);
		ConfiguredStructureFeature<?, ?> configuredStructureFeature = bl2 ? ConfiguredStructureFeatures.OCEAN_RUIN_WARM : ConfiguredStructureFeatures.OCEAN_RUIN_COLD;
		if (bl3) {
			if (bl) {
				builder.structureFeature(ConfiguredStructureFeatures.MONUMENT);
			}

			DefaultBiomeFeatures.addOceanStructures(builder);
			builder.structureFeature(configuredStructureFeature);
		} else {
			builder.structureFeature(configuredStructureFeature);
			if (bl) {
				builder.structureFeature(ConfiguredStructureFeatures.MONUMENT);
			}

			DefaultBiomeFeatures.addOceanStructures(builder);
		}

		builder.structureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_OCEAN);
		DefaultBiomeFeatures.addOceanCarvers(builder);
		DefaultBiomeFeatures.addDefaultLakes(builder);
		DefaultBiomeFeatures.addDungeons(builder);
		DefaultBiomeFeatures.addMineables(builder);
		DefaultBiomeFeatures.addDefaultOres(builder);
		DefaultBiomeFeatures.addDefaultDisks(builder);
		DefaultBiomeFeatures.addWaterBiomeOakTrees(builder);
		DefaultBiomeFeatures.addDefaultFlowers(builder);
		DefaultBiomeFeatures.addDefaultGrass(builder);
		DefaultBiomeFeatures.addDefaultMushrooms(builder);
		DefaultBiomeFeatures.addDefaultVegetation(builder);
		DefaultBiomeFeatures.addSprings(builder);
		return builder;
	}

	public static Biome createColdOcean(boolean deep) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addOceanMobs(builder, 3, 4, 15);
		builder.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.SALMON, 15, 1, 5));
		boolean bl = !deep;
		GenerationSettings.Builder builder2 = createOceanGenerationSettings(ConfiguredSurfaceBuilders.GRASS, deep, false, bl);
		builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, deep ? ConfiguredFeatures.SEAGRASS_DEEP_COLD : ConfiguredFeatures.SEAGRASS_COLD);
		DefaultBiomeFeatures.addSeagrassOnStone(builder2);
		DefaultBiomeFeatures.addKelp(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return createOcean(builder, 4020182, 329011, deep, builder2);
	}

	public static Biome createNormalOcean(boolean deep) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addOceanMobs(builder, 1, 4, 10);
		builder.spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.DOLPHIN, 1, 1, 2));
		GenerationSettings.Builder builder2 = createOceanGenerationSettings(ConfiguredSurfaceBuilders.GRASS, deep, false, true);
		builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, deep ? ConfiguredFeatures.SEAGRASS_DEEP : ConfiguredFeatures.SEAGRASS_NORMAL);
		DefaultBiomeFeatures.addSeagrassOnStone(builder2);
		DefaultBiomeFeatures.addKelp(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return createOcean(builder, 4159204, 329011, deep, builder2);
	}

	public static Biome createLukewarmOcean(boolean deep) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		if (deep) {
			DefaultBiomeFeatures.addOceanMobs(builder, 8, 4, 8);
		} else {
			DefaultBiomeFeatures.addOceanMobs(builder, 10, 2, 15);
		}

		builder.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.PUFFERFISH, 5, 1, 3))
			.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.TROPICAL_FISH, 25, 8, 8))
			.spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.DOLPHIN, 2, 1, 2));
		GenerationSettings.Builder builder2 = createOceanGenerationSettings(ConfiguredSurfaceBuilders.OCEAN_SAND, deep, true, false);
		builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, deep ? ConfiguredFeatures.SEAGRASS_DEEP_WARM : ConfiguredFeatures.SEAGRASS_WARM);
		if (deep) {
			DefaultBiomeFeatures.addSeagrassOnStone(builder2);
		}

		DefaultBiomeFeatures.addLessKelp(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return createOcean(builder, 4566514, 267827, deep, builder2);
	}

	public static Biome createWarmOcean() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder().spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.PUFFERFISH, 15, 1, 3));
		DefaultBiomeFeatures.addWarmOceanMobs(builder, 10, 4);
		GenerationSettings.Builder builder2 = createOceanGenerationSettings(ConfiguredSurfaceBuilders.FULL_SAND, false, true, false)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.WARM_OCEAN_VEGETATION)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SEAGRASS_WARM)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SEA_PICKLE);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return createOcean(builder, 4445678, 270131, false, builder2);
	}

	public static Biome createDeepWarmOcean() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addWarmOceanMobs(builder, 5, 1);
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.DROWNED, 5, 1, 1));
		GenerationSettings.Builder builder2 = createOceanGenerationSettings(ConfiguredSurfaceBuilders.FULL_SAND, true, true, false)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SEAGRASS_DEEP_WARM);
		DefaultBiomeFeatures.addSeagrassOnStone(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return createOcean(builder, 4445678, 270131, true, builder2);
	}

	public static Biome createFrozenOcean(boolean monument) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder()
			.spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.SQUID, 1, 1, 4))
			.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.SALMON, 15, 1, 5))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.POLAR_BEAR, 1, 1, 2));
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.DROWNED, 5, 1, 1));
		float f = monument ? 0.5F : 0.0F;
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.FROZEN_OCEAN);
		builder2.structureFeature(ConfiguredStructureFeatures.OCEAN_RUIN_COLD);
		if (monument) {
			builder2.structureFeature(ConfiguredStructureFeatures.MONUMENT);
		}

		DefaultBiomeFeatures.addOceanStructures(builder2);
		builder2.structureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_OCEAN);
		DefaultBiomeFeatures.addOceanCarvers(builder2);
		DefaultBiomeFeatures.addDefaultLakes(builder2);
		DefaultBiomeFeatures.addIcebergs(builder2);
		DefaultBiomeFeatures.addDungeons(builder2);
		DefaultBiomeFeatures.addBlueIce(builder2);
		DefaultBiomeFeatures.addMineables(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		DefaultBiomeFeatures.addWaterBiomeOakTrees(builder2);
		DefaultBiomeFeatures.addDefaultFlowers(builder2);
		DefaultBiomeFeatures.addDefaultGrass(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
		DefaultBiomeFeatures.addSprings(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return new Biome.Builder()
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
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	private static Biome createForest(float depth, float scale, boolean bl, SpawnSettings.Builder builder) {
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.GRASS);
		DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		builder2.structureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(builder2);
		DefaultBiomeFeatures.addDefaultLakes(builder2);
		DefaultBiomeFeatures.addDungeons(builder2);
		if (bl) {
			builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.FOREST_FLOWER_VEGETATION_COMMON);
		} else {
			DefaultBiomeFeatures.addForestFlowers(builder2);
		}

		DefaultBiomeFeatures.addMineables(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		if (bl) {
			builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.FOREST_FLOWER_TREES);
			builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.FLOWER_FOREST);
			DefaultBiomeFeatures.addDefaultGrass(builder2);
		} else {
			DefaultBiomeFeatures.addForestTrees(builder2);
			DefaultBiomeFeatures.addDefaultFlowers(builder2);
			DefaultBiomeFeatures.addForestGrass(builder2);
		}

		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
		DefaultBiomeFeatures.addSprings(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.RAIN)
			.category(Biome.Category.FOREST)
			.depth(depth)
			.scale(scale)
			.temperature(0.7F)
			.downfall(0.8F)
			.effects(
				new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(getSkyColor(0.7F)).moodSound(BiomeMoodSound.CAVE).build()
			)
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	private static SpawnSettings.Builder createForestSpawnSettings() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		return builder;
	}

	public static Biome createNormalForest(float depth, float scale) {
		SpawnSettings.Builder builder = createForestSpawnSettings()
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.WOLF, 5, 4, 4))
			.playerSpawnFriendly();
		return createForest(depth, scale, false, builder);
	}

	public static Biome createFlowerForest() {
		SpawnSettings.Builder builder = createForestSpawnSettings().spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
		return createForest(0.1F, 0.4F, true, builder);
	}

	public static Biome createTaiga(float depth, float scale, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.WOLF, 8, 4, 4))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 4, 2, 3))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.FOX, 8, 2, 4));
		if (!bl && !bl2) {
			builder.playerSpawnFriendly();
		}

		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		float f = bl ? -0.5F : 0.25F;
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.GRASS);
		if (bl3) {
			builder2.structureFeature(ConfiguredStructureFeatures.VILLAGE_TAIGA);
			builder2.structureFeature(ConfiguredStructureFeatures.PILLAGER_OUTPOST);
		}

		if (bl4) {
			builder2.structureFeature(ConfiguredStructureFeatures.IGLOO);
		}

		DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		builder2.structureFeature(bl2 ? ConfiguredStructureFeatures.RUINED_PORTAL_MOUNTAIN : ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(builder2);
		DefaultBiomeFeatures.addDefaultLakes(builder2);
		DefaultBiomeFeatures.addDungeons(builder2);
		DefaultBiomeFeatures.addLargeFerns(builder2);
		DefaultBiomeFeatures.addMineables(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		DefaultBiomeFeatures.addTaigaTrees(builder2);
		DefaultBiomeFeatures.addDefaultFlowers(builder2);
		DefaultBiomeFeatures.addTaigaGrass(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
		DefaultBiomeFeatures.addSprings(builder2);
		if (bl) {
			DefaultBiomeFeatures.addSweetBerryBushesSnowy(builder2);
		} else {
			DefaultBiomeFeatures.addSweetBerryBushes(builder2);
		}

		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return new Biome.Builder()
			.precipitation(bl ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN)
			.category(Biome.Category.TAIGA)
			.depth(depth)
			.scale(scale)
			.temperature(f)
			.downfall(bl ? 0.4F : 0.8F)
			.effects(
				new BiomeEffects.Builder()
					.waterColor(bl ? 4020182 : 4159204)
					.waterFogColor(329011)
					.fogColor(12638463)
					.skyColor(getSkyColor(f))
					.moodSound(BiomeMoodSound.CAVE)
					.build()
			)
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	public static Biome createDarkForest(float depth, float scale, boolean bl) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.GRASS);
		builder2.structureFeature(ConfiguredStructureFeatures.MANSION);
		DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		builder2.structureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(builder2);
		DefaultBiomeFeatures.addDefaultLakes(builder2);
		DefaultBiomeFeatures.addDungeons(builder2);
		builder2.feature(
			GenerationStep.Feature.VEGETAL_DECORATION, bl ? ConfiguredFeatures.DARK_FOREST_VEGETATION_RED : ConfiguredFeatures.DARK_FOREST_VEGETATION_BROWN
		);
		DefaultBiomeFeatures.addForestFlowers(builder2);
		DefaultBiomeFeatures.addMineables(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		DefaultBiomeFeatures.addDefaultFlowers(builder2);
		DefaultBiomeFeatures.addForestGrass(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
		DefaultBiomeFeatures.addSprings(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return new Biome.Builder()
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
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	public static Biome createSwamp(float depth, float scale, boolean bl) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SLIME, 1, 1, 1));
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.SWAMP);
		if (!bl) {
			builder2.structureFeature(ConfiguredStructureFeatures.SWAMP_HUT);
		}

		builder2.structureFeature(ConfiguredStructureFeatures.MINESHAFT);
		builder2.structureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_SWAMP);
		DefaultBiomeFeatures.addLandCarvers(builder2);
		if (!bl) {
			DefaultBiomeFeatures.addFossils(builder2);
		}

		DefaultBiomeFeatures.addDefaultLakes(builder2);
		DefaultBiomeFeatures.addDungeons(builder2);
		DefaultBiomeFeatures.addMineables(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addClay(builder2);
		DefaultBiomeFeatures.addSwampFeatures(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addSwampVegetation(builder2);
		DefaultBiomeFeatures.addSprings(builder2);
		if (bl) {
			DefaultBiomeFeatures.addFossils(builder2);
		} else {
			builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SEAGRASS_SWAMP);
		}

		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return new Biome.Builder()
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
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	public static Biome createSnowyTundra(float depth, float scale, boolean bl, boolean bl2) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder().creatureSpawnProbability(0.07F);
		DefaultBiomeFeatures.addSnowyMobs(builder);
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder()
			.surfaceBuilder(bl ? ConfiguredSurfaceBuilders.ICE_SPIKES : ConfiguredSurfaceBuilders.GRASS);
		if (!bl && !bl2) {
			builder2.structureFeature(ConfiguredStructureFeatures.VILLAGE_SNOWY).structureFeature(ConfiguredStructureFeatures.IGLOO);
		}

		DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		if (!bl && !bl2) {
			builder2.structureFeature(ConfiguredStructureFeatures.PILLAGER_OUTPOST);
		}

		builder2.structureFeature(bl2 ? ConfiguredStructureFeatures.RUINED_PORTAL_MOUNTAIN : ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(builder2);
		DefaultBiomeFeatures.addDefaultLakes(builder2);
		DefaultBiomeFeatures.addDungeons(builder2);
		if (bl) {
			builder2.feature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.ICE_SPIKE);
			builder2.feature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.ICE_PATCH);
		}

		DefaultBiomeFeatures.addMineables(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		DefaultBiomeFeatures.addSnowySpruceTrees(builder2);
		DefaultBiomeFeatures.addDefaultFlowers(builder2);
		DefaultBiomeFeatures.addDefaultGrass(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
		DefaultBiomeFeatures.addSprings(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.SNOW)
			.category(Biome.Category.ICY)
			.depth(depth)
			.scale(scale)
			.temperature(0.0F)
			.downfall(0.5F)
			.effects(
				new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(getSkyColor(0.0F)).moodSound(BiomeMoodSound.CAVE).build()
			)
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	public static Biome createRiver(float depth, float scale, float temperature, int waterColor, boolean bl) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder()
			.spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.SQUID, 2, 1, 4))
			.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.SALMON, 5, 1, 5));
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.DROWNED, bl ? 1 : 100, 1, 1));
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.GRASS);
		builder2.structureFeature(ConfiguredStructureFeatures.MINESHAFT);
		builder2.structureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(builder2);
		DefaultBiomeFeatures.addDefaultLakes(builder2);
		DefaultBiomeFeatures.addDungeons(builder2);
		DefaultBiomeFeatures.addMineables(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		DefaultBiomeFeatures.addWaterBiomeOakTrees(builder2);
		DefaultBiomeFeatures.addDefaultFlowers(builder2);
		DefaultBiomeFeatures.addDefaultGrass(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
		DefaultBiomeFeatures.addSprings(builder2);
		if (!bl) {
			builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SEAGRASS_RIVER);
		}

		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return new Biome.Builder()
			.precipitation(bl ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN)
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
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	public static Biome createBeach(float depth, float scale, float temperature, float downfall, int waterColor, boolean snowy, boolean stony) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		if (!stony && !snowy) {
			builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.TURTLE, 5, 2, 5));
		}

		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder()
			.surfaceBuilder(stony ? ConfiguredSurfaceBuilders.STONE : ConfiguredSurfaceBuilders.DESERT);
		if (stony) {
			DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		} else {
			builder2.structureFeature(ConfiguredStructureFeatures.MINESHAFT);
			builder2.structureFeature(ConfiguredStructureFeatures.BURIED_TREASURE);
			builder2.structureFeature(ConfiguredStructureFeatures.SHIPWRECK_BEACHED);
		}

		builder2.structureFeature(stony ? ConfiguredStructureFeatures.RUINED_PORTAL_MOUNTAIN : ConfiguredStructureFeatures.RUINED_PORTAL);
		DefaultBiomeFeatures.addLandCarvers(builder2);
		DefaultBiomeFeatures.addDefaultLakes(builder2);
		DefaultBiomeFeatures.addDungeons(builder2);
		DefaultBiomeFeatures.addMineables(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		DefaultBiomeFeatures.addDefaultFlowers(builder2);
		DefaultBiomeFeatures.addDefaultGrass(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
		DefaultBiomeFeatures.addSprings(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return new Biome.Builder()
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
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	public static Biome createTheVoid() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.NOPE);
		builder.feature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, ConfiguredFeatures.VOID_START_PLATFORM);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.NONE)
			.category(Biome.Category.NONE)
			.depth(0.1F)
			.scale(0.2F)
			.temperature(0.5F)
			.downfall(0.5F)
			.effects(
				new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(getSkyColor(0.5F)).moodSound(BiomeMoodSound.CAVE).build()
			)
			.spawnSettings(SpawnSettings.INSTANCE)
			.generationSettings(builder.build())
			.build();
	}

	public static Biome createNetherWastes() {
		SpawnSettings spawnSettings = new SpawnSettings.Builder()
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.GHAST, 50, 4, 4))
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.ZOMBIFIED_PIGLIN, 100, 4, 4))
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.MAGMA_CUBE, 2, 4, 4))
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.ENDERMAN, 1, 4, 4))
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.PIGLIN, 15, 4, 4))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.STRIDER, 60, 1, 2))
			.build();
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
			.surfaceBuilder(ConfiguredSurfaceBuilders.NETHER)
			.structureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_NETHER)
			.structureFeature(ConfiguredStructureFeatures.FORTRESS)
			.structureFeature(ConfiguredStructureFeatures.BASTION_REMNANT)
			.carver(GenerationStep.Carver.AIR, ConfiguredCarvers.NETHER_CAVE)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SPRING_LAVA);
		DefaultBiomeFeatures.addDefaultMushrooms(builder);
		builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_OPEN)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_FIRE)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_SOUL_FIRE)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE_EXTRA)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.BROWN_MUSHROOM_NETHER)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.RED_MUSHROOM_NETHER)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_MAGMA)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_CLOSED);
		DefaultBiomeFeatures.addNetherMineables(builder);
		return new Biome.Builder()
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
			.spawnSettings(spawnSettings)
			.generationSettings(builder.build())
			.build();
	}

	public static Biome createSoulSandValley() {
		double d = 0.7;
		double e = 0.15;
		SpawnSettings spawnSettings = new SpawnSettings.Builder()
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SKELETON, 20, 5, 5))
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.GHAST, 50, 4, 4))
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.ENDERMAN, 1, 4, 4))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.STRIDER, 60, 1, 2))
			.spawnCost(EntityType.SKELETON, 0.7, 0.15)
			.spawnCost(EntityType.GHAST, 0.7, 0.15)
			.spawnCost(EntityType.ENDERMAN, 0.7, 0.15)
			.spawnCost(EntityType.STRIDER, 0.7, 0.15)
			.build();
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
			.surfaceBuilder(ConfiguredSurfaceBuilders.SOUL_SAND_VALLEY)
			.structureFeature(ConfiguredStructureFeatures.FORTRESS)
			.structureFeature(ConfiguredStructureFeatures.NETHER_FOSSIL)
			.structureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_NETHER)
			.structureFeature(ConfiguredStructureFeatures.BASTION_REMNANT)
			.carver(GenerationStep.Carver.AIR, ConfiguredCarvers.NETHER_CAVE)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SPRING_LAVA)
			.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ConfiguredFeatures.BASALT_PILLAR)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_OPEN)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE_EXTRA)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_CRIMSON_ROOTS)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_FIRE)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_SOUL_FIRE)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_MAGMA)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_CLOSED)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_SOUL_SAND);
		DefaultBiomeFeatures.addNetherMineables(builder);
		return new Biome.Builder()
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
			.spawnSettings(spawnSettings)
			.generationSettings(builder.build())
			.build();
	}

	public static Biome createBasaltDeltas() {
		SpawnSettings spawnSettings = new SpawnSettings.Builder()
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.GHAST, 40, 1, 1))
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.MAGMA_CUBE, 100, 2, 5))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.STRIDER, 60, 1, 2))
			.build();
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
			.surfaceBuilder(ConfiguredSurfaceBuilders.BASALT_DELTAS)
			.structureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_NETHER)
			.carver(GenerationStep.Carver.AIR, ConfiguredCarvers.NETHER_CAVE)
			.structureFeature(ConfiguredStructureFeatures.FORTRESS)
			.feature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.DELTA)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SPRING_LAVA_DOUBLE)
			.feature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.SMALL_BASALT_COLUMNS)
			.feature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.LARGE_BASALT_COLUMNS)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.BASALT_BLOBS)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.BLACKSTONE_BLOBS)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_DELTA)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_FIRE)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_SOUL_FIRE)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE_EXTRA)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.BROWN_MUSHROOM_NETHER)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.RED_MUSHROOM_NETHER)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_MAGMA)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_CLOSED_DOUBLE)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_GOLD_DELTAS)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_QUARTZ_DELTAS);
		DefaultBiomeFeatures.addAncientDebris(builder);
		return new Biome.Builder()
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
			.spawnSettings(spawnSettings)
			.generationSettings(builder.build())
			.build();
	}

	public static Biome createCrimsonForest() {
		SpawnSettings spawnSettings = new SpawnSettings.Builder()
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.ZOMBIFIED_PIGLIN, 1, 2, 4))
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.HOGLIN, 9, 3, 4))
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.PIGLIN, 5, 3, 4))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.STRIDER, 60, 1, 2))
			.build();
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
			.surfaceBuilder(ConfiguredSurfaceBuilders.CRIMSON_FOREST)
			.structureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_NETHER)
			.carver(GenerationStep.Carver.AIR, ConfiguredCarvers.NETHER_CAVE)
			.structureFeature(ConfiguredStructureFeatures.FORTRESS)
			.structureFeature(ConfiguredStructureFeatures.BASTION_REMNANT)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SPRING_LAVA);
		DefaultBiomeFeatures.addDefaultMushrooms(builder);
		builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_OPEN)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_FIRE)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE_EXTRA)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_MAGMA)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_CLOSED)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.WEEPING_VINES)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.CRIMSON_FUNGI)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.CRIMSON_FOREST_VEGETATION);
		DefaultBiomeFeatures.addNetherMineables(builder);
		return new Biome.Builder()
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
			.spawnSettings(spawnSettings)
			.generationSettings(builder.build())
			.build();
	}

	public static Biome createWarpedForest() {
		SpawnSettings spawnSettings = new SpawnSettings.Builder()
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.ENDERMAN, 1, 4, 4))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.STRIDER, 60, 1, 2))
			.spawnCost(EntityType.ENDERMAN, 1.0, 0.12)
			.build();
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
			.surfaceBuilder(ConfiguredSurfaceBuilders.WARPED_FOREST)
			.structureFeature(ConfiguredStructureFeatures.FORTRESS)
			.structureFeature(ConfiguredStructureFeatures.BASTION_REMNANT)
			.structureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_NETHER)
			.carver(GenerationStep.Carver.AIR, ConfiguredCarvers.NETHER_CAVE)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SPRING_LAVA);
		DefaultBiomeFeatures.addDefaultMushrooms(builder);
		builder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_OPEN)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_FIRE)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_SOUL_FIRE)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE_EXTRA)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_MAGMA)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_CLOSED)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.WARPED_FUNGI)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.WARPED_FOREST_VEGETATION)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.NETHER_SPROUTS)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.TWISTING_VINES);
		DefaultBiomeFeatures.addNetherMineables(builder);
		return new Biome.Builder()
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
			.spawnSettings(spawnSettings)
			.generationSettings(builder.build())
			.build();
	}
}
