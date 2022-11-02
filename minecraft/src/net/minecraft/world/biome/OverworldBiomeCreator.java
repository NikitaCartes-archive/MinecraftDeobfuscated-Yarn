package net.minecraft.world.biome;

import javax.annotation.Nullable;
import net.minecraft.client.sound.MusicType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryEntryLookup;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.MiscPlacedFeatures;
import net.minecraft.world.gen.feature.OceanPlacedFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.VegetationPlacedFeatures;

public class OverworldBiomeCreator {
	protected static final int DEFAULT_WATER_COLOR = 4159204;
	protected static final int DEFAULT_WATER_FOG_COLOR = 329011;
	private static final int DEFAULT_FOG_COLOR = 12638463;
	@Nullable
	private static final MusicSound DEFAULT_MUSIC = null;

	protected static int getSkyColor(float temperature) {
		float f = temperature / 3.0F;
		f = MathHelper.clamp(f, -1.0F, 1.0F);
		return MathHelper.hsvToRgb(0.62222224F - f * 0.05F, 0.5F + f * 0.1F, 1.0F);
	}

	private static Biome createBiome(
		Biome.Precipitation precipitation,
		float temperature,
		float downfall,
		SpawnSettings.Builder spawnSettings,
		GenerationSettings.LookupBackedBuilder generationSettings,
		@Nullable MusicSound music
	) {
		return createBiome(precipitation, temperature, downfall, 4159204, 329011, spawnSettings, generationSettings, music);
	}

	private static Biome createBiome(
		Biome.Precipitation precipitation,
		float temperature,
		float downfall,
		int waterColor,
		int waterFogColor,
		SpawnSettings.Builder spawnSettings,
		GenerationSettings.LookupBackedBuilder generationSettings,
		@Nullable MusicSound music
	) {
		return new Biome.Builder()
			.precipitation(precipitation)
			.temperature(temperature)
			.downfall(downfall)
			.effects(
				new BiomeEffects.Builder()
					.waterColor(waterColor)
					.waterFogColor(waterFogColor)
					.fogColor(12638463)
					.skyColor(getSkyColor(temperature))
					.moodSound(BiomeMoodSound.CAVE)
					.music(music)
					.build()
			)
			.spawnSettings(spawnSettings.build())
			.generationSettings(generationSettings.build())
			.build();
	}

	private static void addBasicFeatures(GenerationSettings.LookupBackedBuilder generationSettings) {
		DefaultBiomeFeatures.addLandCarvers(generationSettings);
		DefaultBiomeFeatures.addAmethystGeodes(generationSettings);
		DefaultBiomeFeatures.addDungeons(generationSettings);
		DefaultBiomeFeatures.addMineables(generationSettings);
		DefaultBiomeFeatures.addSprings(generationSettings);
		DefaultBiomeFeatures.addFrozenTopLayer(generationSettings);
	}

	public static Biome createOldGrowthTaiga(
		RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup, boolean spruce
	) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.WOLF, 8, 4, 4));
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.FOX, 8, 2, 4));
		if (spruce) {
			DefaultBiomeFeatures.addBatsAndMonsters(builder);
		} else {
			DefaultBiomeFeatures.addCaveMobs(builder);
			DefaultBiomeFeatures.addMonsters(builder, 100, 25, 100, false);
		}

		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		addBasicFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addMossyRocks(lookupBackedBuilder);
		DefaultBiomeFeatures.addLargeFerns(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultDisks(lookupBackedBuilder);
		lookupBackedBuilder.feature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			spruce ? VegetationPlacedFeatures.TREES_OLD_GROWTH_SPRUCE_TAIGA : VegetationPlacedFeatures.TREES_OLD_GROWTH_PINE_TAIGA
		);
		DefaultBiomeFeatures.addDefaultFlowers(lookupBackedBuilder);
		DefaultBiomeFeatures.addGiantTaigaGrass(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultMushrooms(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultVegetation(lookupBackedBuilder);
		DefaultBiomeFeatures.addSweetBerryBushes(lookupBackedBuilder);
		MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_OLD_GROWTH_TAIGA);
		return createBiome(Biome.Precipitation.RAIN, spruce ? 0.25F : 0.3F, 0.8F, builder, lookupBackedBuilder, musicSound);
	}

	public static Biome createSparseJungle(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addJungleMobs(builder);
		return createJungleFeatures(featureLookup, carverLookup, 0.8F, false, true, false, builder);
	}

	public static Biome createJungle(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addJungleMobs(builder);
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.PARROT, 40, 1, 2))
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.OCELOT, 2, 1, 3))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.PANDA, 1, 1, 2));
		return createJungleFeatures(featureLookup, carverLookup, 0.9F, false, false, true, builder);
	}

	public static Biome createNormalBambooJungle(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addJungleMobs(builder);
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.PARROT, 40, 1, 2))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.PANDA, 80, 1, 2))
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.OCELOT, 2, 1, 1));
		return createJungleFeatures(featureLookup, carverLookup, 0.9F, true, false, true, builder);
	}

	private static Biome createJungleFeatures(
		RegistryEntryLookup<PlacedFeature> featureLookup,
		RegistryEntryLookup<ConfiguredCarver<?>> carverLookup,
		float depth,
		boolean bamboo,
		boolean sparse,
		boolean unmodified,
		SpawnSettings.Builder spawnSettings
	) {
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		addBasicFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultDisks(lookupBackedBuilder);
		if (bamboo) {
			DefaultBiomeFeatures.addBambooJungleTrees(lookupBackedBuilder);
		} else {
			if (unmodified) {
				DefaultBiomeFeatures.addBamboo(lookupBackedBuilder);
			}

			if (sparse) {
				DefaultBiomeFeatures.addSparseJungleTrees(lookupBackedBuilder);
			} else {
				DefaultBiomeFeatures.addJungleTrees(lookupBackedBuilder);
			}
		}

		DefaultBiomeFeatures.addExtraDefaultFlowers(lookupBackedBuilder);
		DefaultBiomeFeatures.addJungleGrass(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultMushrooms(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultVegetation(lookupBackedBuilder);
		DefaultBiomeFeatures.addVines(lookupBackedBuilder);
		if (sparse) {
			DefaultBiomeFeatures.addSparseMelons(lookupBackedBuilder);
		} else {
			DefaultBiomeFeatures.addMelons(lookupBackedBuilder);
		}

		MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_JUNGLE_AND_FOREST);
		return createBiome(Biome.Precipitation.RAIN, 0.95F, depth, spawnSettings, lookupBackedBuilder, musicSound);
	}

	public static Biome createWindsweptHills(
		RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup, boolean forest
	) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.LLAMA, 5, 4, 6));
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		addBasicFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultDisks(lookupBackedBuilder);
		if (forest) {
			DefaultBiomeFeatures.addWindsweptForestTrees(lookupBackedBuilder);
		} else {
			DefaultBiomeFeatures.addWindsweptHillsTrees(lookupBackedBuilder);
		}

		DefaultBiomeFeatures.addDefaultFlowers(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultGrass(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultMushrooms(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultVegetation(lookupBackedBuilder);
		DefaultBiomeFeatures.addEmeraldOre(lookupBackedBuilder);
		DefaultBiomeFeatures.addInfestedStone(lookupBackedBuilder);
		return createBiome(Biome.Precipitation.RAIN, 0.2F, 0.3F, builder, lookupBackedBuilder, DEFAULT_MUSIC);
	}

	public static Biome createDesert(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addDesertMobs(builder);
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		DefaultBiomeFeatures.addFossils(lookupBackedBuilder);
		addBasicFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultDisks(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultFlowers(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultGrass(lookupBackedBuilder);
		DefaultBiomeFeatures.addDesertDeadBushes(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultMushrooms(lookupBackedBuilder);
		DefaultBiomeFeatures.addDesertVegetation(lookupBackedBuilder);
		DefaultBiomeFeatures.addDesertFeatures(lookupBackedBuilder);
		return createBiome(Biome.Precipitation.NONE, 2.0F, 0.0F, builder, lookupBackedBuilder, DEFAULT_MUSIC);
	}

	public static Biome createPlains(
		RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup, boolean sunflower, boolean snowy, boolean iceSpikes
	) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		addBasicFeatures(lookupBackedBuilder);
		if (snowy) {
			builder.creatureSpawnProbability(0.07F);
			DefaultBiomeFeatures.addSnowyMobs(builder);
			if (iceSpikes) {
				lookupBackedBuilder.feature(GenerationStep.Feature.SURFACE_STRUCTURES, MiscPlacedFeatures.ICE_SPIKE);
				lookupBackedBuilder.feature(GenerationStep.Feature.SURFACE_STRUCTURES, MiscPlacedFeatures.ICE_PATCH);
			}
		} else {
			DefaultBiomeFeatures.addPlainsMobs(builder);
			DefaultBiomeFeatures.addPlainsTallGrass(lookupBackedBuilder);
			if (sunflower) {
				lookupBackedBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_SUNFLOWER);
			}
		}

		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultDisks(lookupBackedBuilder);
		if (snowy) {
			DefaultBiomeFeatures.addSnowySpruceTrees(lookupBackedBuilder);
			DefaultBiomeFeatures.addDefaultFlowers(lookupBackedBuilder);
			DefaultBiomeFeatures.addDefaultGrass(lookupBackedBuilder);
		} else {
			DefaultBiomeFeatures.addPlainsFeatures(lookupBackedBuilder);
		}

		DefaultBiomeFeatures.addDefaultMushrooms(lookupBackedBuilder);
		if (sunflower) {
			lookupBackedBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_SUGAR_CANE);
			lookupBackedBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_PUMPKIN);
		} else {
			DefaultBiomeFeatures.addDefaultVegetation(lookupBackedBuilder);
		}

		float f = snowy ? 0.0F : 0.8F;
		return createBiome(snowy ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN, f, snowy ? 0.5F : 0.4F, builder, lookupBackedBuilder, DEFAULT_MUSIC);
	}

	public static Biome createMushroomFields(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addMushroomMobs(builder);
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		addBasicFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultDisks(lookupBackedBuilder);
		DefaultBiomeFeatures.addMushroomFieldsFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultVegetation(lookupBackedBuilder);
		return createBiome(Biome.Precipitation.RAIN, 0.9F, 1.0F, builder, lookupBackedBuilder, DEFAULT_MUSIC);
	}

	public static Biome createSavanna(
		RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup, boolean windswept, boolean plateau
	) {
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		addBasicFeatures(lookupBackedBuilder);
		if (!windswept) {
			DefaultBiomeFeatures.addSavannaTallGrass(lookupBackedBuilder);
		}

		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultDisks(lookupBackedBuilder);
		if (windswept) {
			DefaultBiomeFeatures.addExtraSavannaTrees(lookupBackedBuilder);
			DefaultBiomeFeatures.addDefaultFlowers(lookupBackedBuilder);
			DefaultBiomeFeatures.addWindsweptSavannaGrass(lookupBackedBuilder);
		} else {
			DefaultBiomeFeatures.addSavannaTrees(lookupBackedBuilder);
			DefaultBiomeFeatures.addExtraDefaultFlowers(lookupBackedBuilder);
			DefaultBiomeFeatures.addSavannaGrass(lookupBackedBuilder);
		}

		DefaultBiomeFeatures.addDefaultMushrooms(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultVegetation(lookupBackedBuilder);
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.HORSE, 1, 2, 6))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.DONKEY, 1, 1, 1));
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		if (plateau) {
			builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.LLAMA, 8, 4, 4));
		}

		return createBiome(Biome.Precipitation.NONE, 2.0F, 0.0F, builder, lookupBackedBuilder, DEFAULT_MUSIC);
	}

	public static Biome createBadlands(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup, boolean plateau) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		addBasicFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder);
		DefaultBiomeFeatures.addExtraGoldOre(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultDisks(lookupBackedBuilder);
		if (plateau) {
			DefaultBiomeFeatures.addBadlandsPlateauTrees(lookupBackedBuilder);
		}

		DefaultBiomeFeatures.addBadlandsGrass(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultMushrooms(lookupBackedBuilder);
		DefaultBiomeFeatures.addBadlandsVegetation(lookupBackedBuilder);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.NONE)
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
			.generationSettings(lookupBackedBuilder.build())
			.build();
	}

	private static Biome createOcean(
		SpawnSettings.Builder spawnSettings, int waterColor, int waterFogColor, GenerationSettings.LookupBackedBuilder generationSettings
	) {
		return createBiome(Biome.Precipitation.RAIN, 0.5F, 0.5F, waterColor, waterFogColor, spawnSettings, generationSettings, DEFAULT_MUSIC);
	}

	private static GenerationSettings.LookupBackedBuilder createOceanGenerationSettings(
		RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup
	) {
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		addBasicFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultDisks(lookupBackedBuilder);
		DefaultBiomeFeatures.addWaterBiomeOakTrees(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultFlowers(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultGrass(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultMushrooms(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultVegetation(lookupBackedBuilder);
		return lookupBackedBuilder;
	}

	public static Biome createColdOcean(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup, boolean deep) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addOceanMobs(builder, 3, 4, 15);
		builder.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.SALMON, 15, 1, 5));
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = createOceanGenerationSettings(featureLookup, carverLookup);
		lookupBackedBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, deep ? OceanPlacedFeatures.SEAGRASS_DEEP_COLD : OceanPlacedFeatures.SEAGRASS_COLD);
		DefaultBiomeFeatures.addSeagrassOnStone(lookupBackedBuilder);
		DefaultBiomeFeatures.addKelp(lookupBackedBuilder);
		return createOcean(builder, 4020182, 329011, lookupBackedBuilder);
	}

	public static Biome createNormalOcean(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup, boolean deep) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addOceanMobs(builder, 1, 4, 10);
		builder.spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.DOLPHIN, 1, 1, 2));
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = createOceanGenerationSettings(featureLookup, carverLookup);
		lookupBackedBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, deep ? OceanPlacedFeatures.SEAGRASS_DEEP : OceanPlacedFeatures.SEAGRASS_NORMAL);
		DefaultBiomeFeatures.addSeagrassOnStone(lookupBackedBuilder);
		DefaultBiomeFeatures.addKelp(lookupBackedBuilder);
		return createOcean(builder, 4159204, 329011, lookupBackedBuilder);
	}

	public static Biome createLukewarmOcean(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup, boolean deep) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		if (deep) {
			DefaultBiomeFeatures.addOceanMobs(builder, 8, 4, 8);
		} else {
			DefaultBiomeFeatures.addOceanMobs(builder, 10, 2, 15);
		}

		builder.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.PUFFERFISH, 5, 1, 3))
			.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.TROPICAL_FISH, 25, 8, 8))
			.spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.DOLPHIN, 2, 1, 2));
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = createOceanGenerationSettings(featureLookup, carverLookup);
		lookupBackedBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, deep ? OceanPlacedFeatures.SEAGRASS_DEEP_WARM : OceanPlacedFeatures.SEAGRASS_WARM);
		if (deep) {
			DefaultBiomeFeatures.addSeagrassOnStone(lookupBackedBuilder);
		}

		DefaultBiomeFeatures.addLessKelp(lookupBackedBuilder);
		return createOcean(builder, 4566514, 267827, lookupBackedBuilder);
	}

	public static Biome createWarmOcean(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder().spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.PUFFERFISH, 15, 1, 3));
		DefaultBiomeFeatures.addWarmOceanMobs(builder, 10, 4);
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = createOceanGenerationSettings(featureLookup, carverLookup)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, OceanPlacedFeatures.WARM_OCEAN_VEGETATION)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, OceanPlacedFeatures.SEAGRASS_WARM)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, OceanPlacedFeatures.SEA_PICKLE);
		return createOcean(builder, 4445678, 270131, lookupBackedBuilder);
	}

	public static Biome createFrozenOcean(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup, boolean deep) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder()
			.spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.SQUID, 1, 1, 4))
			.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.SALMON, 15, 1, 5))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.POLAR_BEAR, 1, 1, 2));
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.DROWNED, 5, 1, 1));
		float f = deep ? 0.5F : 0.0F;
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		DefaultBiomeFeatures.addIcebergs(lookupBackedBuilder);
		addBasicFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addBlueIce(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultDisks(lookupBackedBuilder);
		DefaultBiomeFeatures.addWaterBiomeOakTrees(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultFlowers(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultGrass(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultMushrooms(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultVegetation(lookupBackedBuilder);
		return new Biome.Builder()
			.precipitation(deep ? Biome.Precipitation.RAIN : Biome.Precipitation.SNOW)
			.temperature(f)
			.temperatureModifier(Biome.TemperatureModifier.FROZEN)
			.downfall(0.5F)
			.effects(
				new BiomeEffects.Builder().waterColor(3750089).waterFogColor(329011).fogColor(12638463).skyColor(getSkyColor(f)).moodSound(BiomeMoodSound.CAVE).build()
			)
			.spawnSettings(builder.build())
			.generationSettings(lookupBackedBuilder.build())
			.build();
	}

	public static Biome createNormalForest(
		RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup, boolean birch, boolean oldGrowth, boolean flower
	) {
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		addBasicFeatures(lookupBackedBuilder);
		if (flower) {
			lookupBackedBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.FLOWER_FOREST_FLOWERS);
		} else {
			DefaultBiomeFeatures.addForestFlowers(lookupBackedBuilder);
		}

		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultDisks(lookupBackedBuilder);
		if (flower) {
			lookupBackedBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.TREES_FLOWER_FOREST);
			lookupBackedBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.FLOWER_FLOWER_FOREST);
			DefaultBiomeFeatures.addDefaultGrass(lookupBackedBuilder);
		} else {
			if (birch) {
				if (oldGrowth) {
					DefaultBiomeFeatures.addTallBirchTrees(lookupBackedBuilder);
				} else {
					DefaultBiomeFeatures.addBirchTrees(lookupBackedBuilder);
				}
			} else {
				DefaultBiomeFeatures.addForestTrees(lookupBackedBuilder);
			}

			DefaultBiomeFeatures.addDefaultFlowers(lookupBackedBuilder);
			DefaultBiomeFeatures.addForestGrass(lookupBackedBuilder);
		}

		DefaultBiomeFeatures.addDefaultMushrooms(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultVegetation(lookupBackedBuilder);
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		if (flower) {
			builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
		} else if (!birch) {
			builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.WOLF, 5, 4, 4));
		}

		float f = birch ? 0.6F : 0.7F;
		MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_JUNGLE_AND_FOREST);
		return createBiome(Biome.Precipitation.RAIN, f, birch ? 0.6F : 0.8F, builder, lookupBackedBuilder, musicSound);
	}

	public static Biome createTaiga(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup, boolean snowy) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.WOLF, 8, 4, 4))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 4, 2, 3))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.FOX, 8, 2, 4));
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		float f = snowy ? -0.5F : 0.25F;
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		addBasicFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addLargeFerns(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultDisks(lookupBackedBuilder);
		DefaultBiomeFeatures.addTaigaTrees(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultFlowers(lookupBackedBuilder);
		DefaultBiomeFeatures.addTaigaGrass(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultVegetation(lookupBackedBuilder);
		if (snowy) {
			DefaultBiomeFeatures.addSweetBerryBushesSnowy(lookupBackedBuilder);
		} else {
			DefaultBiomeFeatures.addSweetBerryBushes(lookupBackedBuilder);
		}

		return createBiome(
			snowy ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN,
			f,
			snowy ? 0.4F : 0.8F,
			snowy ? 4020182 : 4159204,
			329011,
			builder,
			lookupBackedBuilder,
			DEFAULT_MUSIC
		);
	}

	public static Biome createDarkForest(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		addBasicFeatures(lookupBackedBuilder);
		lookupBackedBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.DARK_FOREST_VEGETATION);
		DefaultBiomeFeatures.addForestFlowers(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultDisks(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultFlowers(lookupBackedBuilder);
		DefaultBiomeFeatures.addForestGrass(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultMushrooms(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultVegetation(lookupBackedBuilder);
		MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_JUNGLE_AND_FOREST);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.RAIN)
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
					.music(musicSound)
					.build()
			)
			.spawnSettings(builder.build())
			.generationSettings(lookupBackedBuilder.build())
			.build();
	}

	public static Biome createSwamp(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SLIME, 1, 1, 1));
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.FROG, 10, 2, 5));
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		DefaultBiomeFeatures.addFossils(lookupBackedBuilder);
		addBasicFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder);
		DefaultBiomeFeatures.addClayDisk(lookupBackedBuilder);
		DefaultBiomeFeatures.addSwampFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultMushrooms(lookupBackedBuilder);
		DefaultBiomeFeatures.addSwampVegetation(lookupBackedBuilder);
		lookupBackedBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, OceanPlacedFeatures.SEAGRASS_SWAMP);
		MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_SWAMP);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.RAIN)
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
					.music(musicSound)
					.build()
			)
			.spawnSettings(builder.build())
			.generationSettings(lookupBackedBuilder.build())
			.build();
	}

	public static Biome createMangroveSwamp(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SLIME, 1, 1, 1));
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.FROG, 10, 2, 5));
		builder.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.TROPICAL_FISH, 25, 8, 8));
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		DefaultBiomeFeatures.addFossils(lookupBackedBuilder);
		addBasicFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder);
		DefaultBiomeFeatures.addGrassAndClayDisks(lookupBackedBuilder);
		DefaultBiomeFeatures.addMangroveSwampFeatures(lookupBackedBuilder);
		lookupBackedBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, OceanPlacedFeatures.SEAGRASS_SWAMP);
		MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_SWAMP);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.RAIN)
			.temperature(0.8F)
			.downfall(0.9F)
			.effects(
				new BiomeEffects.Builder()
					.waterColor(3832426)
					.waterFogColor(5077600)
					.fogColor(12638463)
					.skyColor(getSkyColor(0.8F))
					.foliageColor(9285927)
					.grassColorModifier(BiomeEffects.GrassColorModifier.SWAMP)
					.moodSound(BiomeMoodSound.CAVE)
					.music(musicSound)
					.build()
			)
			.spawnSettings(builder.build())
			.generationSettings(lookupBackedBuilder.build())
			.build();
	}

	public static Biome createRiver(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup, boolean frozen) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder()
			.spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.SQUID, 2, 1, 4))
			.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.SALMON, 5, 1, 5));
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.DROWNED, frozen ? 1 : 100, 1, 1));
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		addBasicFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultDisks(lookupBackedBuilder);
		DefaultBiomeFeatures.addWaterBiomeOakTrees(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultFlowers(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultGrass(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultMushrooms(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultVegetation(lookupBackedBuilder);
		if (!frozen) {
			lookupBackedBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, OceanPlacedFeatures.SEAGRASS_RIVER);
		}

		float f = frozen ? 0.0F : 0.5F;
		return createBiome(
			frozen ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN, f, 0.5F, frozen ? 3750089 : 4159204, 329011, builder, lookupBackedBuilder, DEFAULT_MUSIC
		);
	}

	public static Biome createBeach(
		RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup, boolean snowy, boolean stony
	) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		boolean bl = !stony && !snowy;
		if (bl) {
			builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.TURTLE, 5, 2, 5));
		}

		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		addBasicFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultDisks(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultFlowers(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultGrass(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultMushrooms(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultVegetation(lookupBackedBuilder);
		float f;
		if (snowy) {
			f = 0.05F;
		} else if (stony) {
			f = 0.2F;
		} else {
			f = 0.8F;
		}

		return createBiome(
			snowy ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN,
			f,
			bl ? 0.4F : 0.3F,
			snowy ? 4020182 : 4159204,
			329011,
			builder,
			lookupBackedBuilder,
			DEFAULT_MUSIC
		);
	}

	public static Biome createTheVoid(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		lookupBackedBuilder.feature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, MiscPlacedFeatures.VOID_START_PLATFORM);
		return createBiome(Biome.Precipitation.NONE, 0.5F, 0.5F, new SpawnSettings.Builder(), lookupBackedBuilder, DEFAULT_MUSIC);
	}

	public static Biome createMeadow(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.DONKEY, 1, 1, 2))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 2, 2, 6))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.SHEEP, 2, 2, 4));
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		addBasicFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addPlainsTallGrass(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultDisks(lookupBackedBuilder);
		DefaultBiomeFeatures.addMeadowFlowers(lookupBackedBuilder);
		DefaultBiomeFeatures.addEmeraldOre(lookupBackedBuilder);
		DefaultBiomeFeatures.addInfestedStone(lookupBackedBuilder);
		MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_MEADOW);
		return createBiome(Biome.Precipitation.RAIN, 0.5F, 0.8F, 937679, 329011, builder, lookupBackedBuilder, musicSound);
	}

	public static Biome createFrozenPeaks(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.GOAT, 5, 1, 3));
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		addBasicFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addFrozenLavaSpring(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultDisks(lookupBackedBuilder);
		DefaultBiomeFeatures.addEmeraldOre(lookupBackedBuilder);
		DefaultBiomeFeatures.addInfestedStone(lookupBackedBuilder);
		MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_FROZEN_PEAKS);
		return createBiome(Biome.Precipitation.SNOW, -0.7F, 0.9F, builder, lookupBackedBuilder, musicSound);
	}

	public static Biome createJaggedPeaks(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.GOAT, 5, 1, 3));
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		addBasicFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addFrozenLavaSpring(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultDisks(lookupBackedBuilder);
		DefaultBiomeFeatures.addEmeraldOre(lookupBackedBuilder);
		DefaultBiomeFeatures.addInfestedStone(lookupBackedBuilder);
		MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_JAGGED_PEAKS);
		return createBiome(Biome.Precipitation.SNOW, -0.7F, 0.9F, builder, lookupBackedBuilder, musicSound);
	}

	public static Biome createStonyPeaks(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		addBasicFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultDisks(lookupBackedBuilder);
		DefaultBiomeFeatures.addEmeraldOre(lookupBackedBuilder);
		DefaultBiomeFeatures.addInfestedStone(lookupBackedBuilder);
		MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_STONY_PEAKS);
		return createBiome(Biome.Precipitation.RAIN, 1.0F, 0.3F, builder, lookupBackedBuilder, musicSound);
	}

	public static Biome createSnowySlopes(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 4, 2, 3))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.GOAT, 5, 1, 3));
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		addBasicFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addFrozenLavaSpring(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultDisks(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultVegetation(lookupBackedBuilder);
		DefaultBiomeFeatures.addEmeraldOre(lookupBackedBuilder);
		DefaultBiomeFeatures.addInfestedStone(lookupBackedBuilder);
		MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_SNOWY_SLOPES);
		return createBiome(Biome.Precipitation.SNOW, -0.3F, 0.9F, builder, lookupBackedBuilder, musicSound);
	}

	public static Biome createGrove(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.WOLF, 8, 4, 4))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 4, 2, 3))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.FOX, 8, 2, 4));
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		addBasicFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addFrozenLavaSpring(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultDisks(lookupBackedBuilder);
		DefaultBiomeFeatures.addGroveTrees(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultVegetation(lookupBackedBuilder);
		DefaultBiomeFeatures.addEmeraldOre(lookupBackedBuilder);
		DefaultBiomeFeatures.addInfestedStone(lookupBackedBuilder);
		MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_GROVE);
		return createBiome(Biome.Precipitation.SNOW, -0.2F, 0.8F, builder, lookupBackedBuilder, musicSound);
	}

	public static Biome createLushCaves(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		builder.spawn(SpawnGroup.AXOLOTLS, new SpawnSettings.SpawnEntry(EntityType.AXOLOTL, 10, 4, 6));
		builder.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.TROPICAL_FISH, 25, 8, 8));
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		addBasicFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addPlainsTallGrass(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder);
		DefaultBiomeFeatures.addClayOre(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultDisks(lookupBackedBuilder);
		DefaultBiomeFeatures.addLushCavesDecoration(lookupBackedBuilder);
		MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_LUSH_CAVES);
		return createBiome(Biome.Precipitation.RAIN, 0.5F, 0.5F, builder, lookupBackedBuilder, musicSound);
	}

	public static Biome createDripstoneCaves(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addDripstoneCaveMobs(builder);
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		addBasicFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addPlainsTallGrass(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder, true);
		DefaultBiomeFeatures.addDefaultDisks(lookupBackedBuilder);
		DefaultBiomeFeatures.addPlainsFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultMushrooms(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultVegetation(lookupBackedBuilder);
		DefaultBiomeFeatures.addDripstone(lookupBackedBuilder);
		MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_DRIPSTONE_CAVES);
		return createBiome(Biome.Precipitation.RAIN, 0.8F, 0.4F, builder, lookupBackedBuilder, musicSound);
	}

	public static Biome createDeepDark(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		lookupBackedBuilder.carver(GenerationStep.Carver.AIR, ConfiguredCarvers.CAVE);
		lookupBackedBuilder.carver(GenerationStep.Carver.AIR, ConfiguredCarvers.CAVE_EXTRA_UNDERGROUND);
		lookupBackedBuilder.carver(GenerationStep.Carver.AIR, ConfiguredCarvers.CANYON);
		DefaultBiomeFeatures.addAmethystGeodes(lookupBackedBuilder);
		DefaultBiomeFeatures.addDungeons(lookupBackedBuilder);
		DefaultBiomeFeatures.addMineables(lookupBackedBuilder);
		DefaultBiomeFeatures.addFrozenTopLayer(lookupBackedBuilder);
		DefaultBiomeFeatures.addPlainsTallGrass(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultOres(lookupBackedBuilder, true);
		DefaultBiomeFeatures.addDefaultDisks(lookupBackedBuilder);
		DefaultBiomeFeatures.addPlainsFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultMushrooms(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultVegetation(lookupBackedBuilder);
		DefaultBiomeFeatures.addSculk(lookupBackedBuilder);
		MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_DEEP_DARK);
		return createBiome(Biome.Precipitation.RAIN, 0.8F, 0.4F, builder, lookupBackedBuilder, musicSound);
	}
}
