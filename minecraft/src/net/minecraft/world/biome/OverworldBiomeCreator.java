package net.minecraft.world.biome;

import javax.annotation.Nullable;
import net.minecraft.client.sound.MusicType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.MiscPlacedFeatures;
import net.minecraft.world.gen.feature.OceanPlacedFeatures;
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
		GenerationSettings.Builder generationSettings,
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
		GenerationSettings.Builder generationSettings,
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

	private static void addBasicFeatures(GenerationSettings.Builder generationSettings) {
		DefaultBiomeFeatures.addLandCarvers(generationSettings);
		DefaultBiomeFeatures.addAmethystGeodes(generationSettings);
		DefaultBiomeFeatures.addDungeons(generationSettings);
		DefaultBiomeFeatures.addMineables(generationSettings);
		DefaultBiomeFeatures.addSprings(generationSettings);
		DefaultBiomeFeatures.addFrozenTopLayer(generationSettings);
	}

	public static Biome createOldGrowthTaiga(boolean spruce) {
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

		GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
		addBasicFeatures(builder2);
		DefaultBiomeFeatures.addMossyRocks(builder2);
		DefaultBiomeFeatures.addLargeFerns(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		builder2.feature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			spruce ? VegetationPlacedFeatures.TREES_OLD_GROWTH_SPRUCE_TAIGA : VegetationPlacedFeatures.TREES_OLD_GROWTH_PINE_TAIGA
		);
		DefaultBiomeFeatures.addDefaultFlowers(builder2);
		DefaultBiomeFeatures.addGiantTaigaGrass(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
		DefaultBiomeFeatures.addSweetBerryBushes(builder2);
		return createBiome(Biome.Precipitation.RAIN, spruce ? 0.25F : 0.3F, 0.8F, builder, builder2, DEFAULT_MUSIC);
	}

	public static Biome createSparseJungle() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addJungleMobs(builder);
		return createJungleFeatures(0.8F, false, true, false, builder);
	}

	public static Biome createJungle() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addJungleMobs(builder);
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.PARROT, 40, 1, 2))
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.OCELOT, 2, 1, 3))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.PANDA, 1, 1, 2));
		return createJungleFeatures(0.9F, false, false, true, builder);
	}

	public static Biome createNormalBambooJungle() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addJungleMobs(builder);
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.PARROT, 40, 1, 2))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.PANDA, 80, 1, 2))
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.OCELOT, 2, 1, 1));
		return createJungleFeatures(0.9F, true, false, true, builder);
	}

	private static Biome createJungleFeatures(float depth, boolean bamboo, boolean sparse, boolean unmodified, SpawnSettings.Builder spawnSettings) {
		GenerationSettings.Builder builder = new GenerationSettings.Builder();
		addBasicFeatures(builder);
		DefaultBiomeFeatures.addDefaultOres(builder);
		DefaultBiomeFeatures.addDefaultDisks(builder);
		if (bamboo) {
			DefaultBiomeFeatures.addBambooJungleTrees(builder);
		} else {
			if (unmodified) {
				DefaultBiomeFeatures.addBamboo(builder);
			}

			if (sparse) {
				DefaultBiomeFeatures.addSparseJungleTrees(builder);
			} else {
				DefaultBiomeFeatures.addJungleTrees(builder);
			}
		}

		DefaultBiomeFeatures.addExtraDefaultFlowers(builder);
		DefaultBiomeFeatures.addJungleGrass(builder);
		DefaultBiomeFeatures.addDefaultMushrooms(builder);
		DefaultBiomeFeatures.addDefaultVegetation(builder);
		DefaultBiomeFeatures.addVines(builder);
		if (sparse) {
			DefaultBiomeFeatures.addSparseMelons(builder);
		} else {
			DefaultBiomeFeatures.addMelons(builder);
		}

		return createBiome(Biome.Precipitation.RAIN, 0.95F, depth, spawnSettings, builder, DEFAULT_MUSIC);
	}

	public static Biome createWindsweptHills(boolean forest) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.LLAMA, 5, 4, 6));
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
		addBasicFeatures(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		if (forest) {
			DefaultBiomeFeatures.addWindsweptForestTrees(builder2);
		} else {
			DefaultBiomeFeatures.addWindsweptHillsTrees(builder2);
		}

		DefaultBiomeFeatures.addDefaultFlowers(builder2);
		DefaultBiomeFeatures.addDefaultGrass(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
		DefaultBiomeFeatures.addEmeraldOre(builder2);
		DefaultBiomeFeatures.addInfestedStone(builder2);
		return createBiome(Biome.Precipitation.RAIN, 0.2F, 0.3F, builder, builder2, DEFAULT_MUSIC);
	}

	public static Biome createDesert() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addDesertMobs(builder);
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
		DefaultBiomeFeatures.addFossils(builder2);
		addBasicFeatures(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		DefaultBiomeFeatures.addDefaultFlowers(builder2);
		DefaultBiomeFeatures.addDefaultGrass(builder2);
		DefaultBiomeFeatures.addDesertDeadBushes(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDesertVegetation(builder2);
		DefaultBiomeFeatures.addDesertFeatures(builder2);
		return createBiome(Biome.Precipitation.NONE, 2.0F, 0.0F, builder, builder2, DEFAULT_MUSIC);
	}

	public static Biome createPlains(boolean sunflower, boolean snowy, boolean iceSpikes) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
		addBasicFeatures(builder2);
		if (snowy) {
			builder.creatureSpawnProbability(0.07F);
			DefaultBiomeFeatures.addSnowyMobs(builder);
			if (iceSpikes) {
				builder2.feature(GenerationStep.Feature.SURFACE_STRUCTURES, MiscPlacedFeatures.ICE_SPIKE);
				builder2.feature(GenerationStep.Feature.SURFACE_STRUCTURES, MiscPlacedFeatures.ICE_PATCH);
			}
		} else {
			DefaultBiomeFeatures.addPlainsMobs(builder);
			DefaultBiomeFeatures.addPlainsTallGrass(builder2);
			if (sunflower) {
				builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_SUNFLOWER);
			}
		}

		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		if (snowy) {
			DefaultBiomeFeatures.addSnowySpruceTrees(builder2);
			DefaultBiomeFeatures.addDefaultFlowers(builder2);
			DefaultBiomeFeatures.addDefaultGrass(builder2);
		} else {
			DefaultBiomeFeatures.addPlainsFeatures(builder2);
		}

		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		if (sunflower) {
			builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_SUGAR_CANE);
			builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_PUMPKIN);
		} else {
			DefaultBiomeFeatures.addDefaultVegetation(builder2);
		}

		float f = snowy ? 0.0F : 0.8F;
		return createBiome(snowy ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN, f, snowy ? 0.5F : 0.4F, builder, builder2, DEFAULT_MUSIC);
	}

	public static Biome createMushroomFields() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addMushroomMobs(builder);
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
		addBasicFeatures(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		DefaultBiomeFeatures.addMushroomFieldsFeatures(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
		return createBiome(Biome.Precipitation.RAIN, 0.9F, 1.0F, builder, builder2, DEFAULT_MUSIC);
	}

	public static Biome createSavanna(boolean windswept, boolean plateau) {
		GenerationSettings.Builder builder = new GenerationSettings.Builder();
		addBasicFeatures(builder);
		if (!windswept) {
			DefaultBiomeFeatures.addSavannaTallGrass(builder);
		}

		DefaultBiomeFeatures.addDefaultOres(builder);
		DefaultBiomeFeatures.addDefaultDisks(builder);
		if (windswept) {
			DefaultBiomeFeatures.addExtraSavannaTrees(builder);
			DefaultBiomeFeatures.addDefaultFlowers(builder);
			DefaultBiomeFeatures.addWindsweptSavannaGrass(builder);
		} else {
			DefaultBiomeFeatures.addSavannaTrees(builder);
			DefaultBiomeFeatures.addExtraDefaultFlowers(builder);
			DefaultBiomeFeatures.addSavannaGrass(builder);
		}

		DefaultBiomeFeatures.addDefaultMushrooms(builder);
		DefaultBiomeFeatures.addDefaultVegetation(builder);
		SpawnSettings.Builder builder2 = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder2);
		builder2.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.HORSE, 1, 2, 6))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.DONKEY, 1, 1, 1));
		DefaultBiomeFeatures.addBatsAndMonsters(builder2);
		if (plateau) {
			builder2.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.LLAMA, 8, 4, 4));
		}

		return createBiome(Biome.Precipitation.NONE, 2.0F, 0.0F, builder2, builder, DEFAULT_MUSIC);
	}

	public static Biome createBadlands(boolean plateau) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
		addBasicFeatures(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addExtraGoldOre(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		if (plateau) {
			DefaultBiomeFeatures.addBadlandsPlateauTrees(builder2);
		}

		DefaultBiomeFeatures.addBadlandsGrass(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addBadlandsVegetation(builder2);
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
			.generationSettings(builder2.build())
			.build();
	}

	private static Biome createOcean(SpawnSettings.Builder spawnSettings, int waterColor, int waterFogColor, GenerationSettings.Builder builder) {
		return createBiome(Biome.Precipitation.RAIN, 0.5F, 0.5F, waterColor, waterFogColor, spawnSettings, builder, DEFAULT_MUSIC);
	}

	private static GenerationSettings.Builder createOceanGenerationSettings() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder();
		addBasicFeatures(builder);
		DefaultBiomeFeatures.addDefaultOres(builder);
		DefaultBiomeFeatures.addDefaultDisks(builder);
		DefaultBiomeFeatures.addWaterBiomeOakTrees(builder);
		DefaultBiomeFeatures.addDefaultFlowers(builder);
		DefaultBiomeFeatures.addDefaultGrass(builder);
		DefaultBiomeFeatures.addDefaultMushrooms(builder);
		DefaultBiomeFeatures.addDefaultVegetation(builder);
		return builder;
	}

	public static Biome createColdOcean(boolean deep) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addOceanMobs(builder, 3, 4, 15);
		builder.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.SALMON, 15, 1, 5));
		GenerationSettings.Builder builder2 = createOceanGenerationSettings();
		builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, deep ? OceanPlacedFeatures.SEAGRASS_DEEP_COLD : OceanPlacedFeatures.SEAGRASS_COLD);
		DefaultBiomeFeatures.addSeagrassOnStone(builder2);
		DefaultBiomeFeatures.addKelp(builder2);
		return createOcean(builder, 4020182, 329011, builder2);
	}

	public static Biome createNormalOcean(boolean deep) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addOceanMobs(builder, 1, 4, 10);
		builder.spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.DOLPHIN, 1, 1, 2));
		GenerationSettings.Builder builder2 = createOceanGenerationSettings();
		builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, deep ? OceanPlacedFeatures.SEAGRASS_DEEP : OceanPlacedFeatures.SEAGRASS_NORMAL);
		DefaultBiomeFeatures.addSeagrassOnStone(builder2);
		DefaultBiomeFeatures.addKelp(builder2);
		return createOcean(builder, 4159204, 329011, builder2);
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
		GenerationSettings.Builder builder2 = createOceanGenerationSettings();
		builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, deep ? OceanPlacedFeatures.SEAGRASS_DEEP_WARM : OceanPlacedFeatures.SEAGRASS_WARM);
		if (deep) {
			DefaultBiomeFeatures.addSeagrassOnStone(builder2);
		}

		DefaultBiomeFeatures.addLessKelp(builder2);
		return createOcean(builder, 4566514, 267827, builder2);
	}

	public static Biome createWarmOcean() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder().spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.PUFFERFISH, 15, 1, 3));
		DefaultBiomeFeatures.addWarmOceanMobs(builder, 10, 4);
		GenerationSettings.Builder builder2 = createOceanGenerationSettings()
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, OceanPlacedFeatures.WARM_OCEAN_VEGETATION)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, OceanPlacedFeatures.SEAGRASS_WARM)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, OceanPlacedFeatures.SEA_PICKLE);
		return createOcean(builder, 4445678, 270131, builder2);
	}

	public static Biome createFrozenOcean(boolean monument) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder()
			.spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.SQUID, 1, 1, 4))
			.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.SALMON, 15, 1, 5))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.POLAR_BEAR, 1, 1, 2));
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.DROWNED, 5, 1, 1));
		float f = monument ? 0.5F : 0.0F;
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
		DefaultBiomeFeatures.addIcebergs(builder2);
		addBasicFeatures(builder2);
		DefaultBiomeFeatures.addBlueIce(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		DefaultBiomeFeatures.addWaterBiomeOakTrees(builder2);
		DefaultBiomeFeatures.addDefaultFlowers(builder2);
		DefaultBiomeFeatures.addDefaultGrass(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
		return new Biome.Builder()
			.precipitation(monument ? Biome.Precipitation.RAIN : Biome.Precipitation.SNOW)
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

	public static Biome createNormalForest(boolean birch, boolean oldGrowth, boolean flower) {
		GenerationSettings.Builder builder = new GenerationSettings.Builder();
		addBasicFeatures(builder);
		if (flower) {
			builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.FLOWER_FOREST_FLOWERS);
		} else {
			DefaultBiomeFeatures.addForestFlowers(builder);
		}

		DefaultBiomeFeatures.addDefaultOres(builder);
		DefaultBiomeFeatures.addDefaultDisks(builder);
		if (flower) {
			builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.TREES_FLOWER_FOREST);
			builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.FLOWER_FLOWER_FOREST);
			DefaultBiomeFeatures.addDefaultGrass(builder);
		} else {
			if (birch) {
				if (oldGrowth) {
					DefaultBiomeFeatures.addTallBirchTrees(builder);
				} else {
					DefaultBiomeFeatures.addBirchTrees(builder);
				}
			} else {
				DefaultBiomeFeatures.addForestTrees(builder);
			}

			DefaultBiomeFeatures.addDefaultFlowers(builder);
			DefaultBiomeFeatures.addForestGrass(builder);
		}

		DefaultBiomeFeatures.addDefaultMushrooms(builder);
		DefaultBiomeFeatures.addDefaultVegetation(builder);
		SpawnSettings.Builder builder2 = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder2);
		DefaultBiomeFeatures.addBatsAndMonsters(builder2);
		if (flower) {
			builder2.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
		} else if (!birch) {
			builder2.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.WOLF, 5, 4, 4));
		}

		float f = birch ? 0.6F : 0.7F;
		return createBiome(Biome.Precipitation.RAIN, f, birch ? 0.6F : 0.8F, builder2, builder, DEFAULT_MUSIC);
	}

	public static Biome createTaiga(boolean cold) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.WOLF, 8, 4, 4))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 4, 2, 3))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.FOX, 8, 2, 4));
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		float f = cold ? -0.5F : 0.25F;
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
		addBasicFeatures(builder2);
		DefaultBiomeFeatures.addLargeFerns(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		DefaultBiomeFeatures.addTaigaTrees(builder2);
		DefaultBiomeFeatures.addDefaultFlowers(builder2);
		DefaultBiomeFeatures.addTaigaGrass(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
		if (cold) {
			DefaultBiomeFeatures.addSweetBerryBushesSnowy(builder2);
		} else {
			DefaultBiomeFeatures.addSweetBerryBushes(builder2);
		}

		return createBiome(
			cold ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN, f, cold ? 0.4F : 0.8F, cold ? 4020182 : 4159204, 329011, builder, builder2, DEFAULT_MUSIC
		);
	}

	public static Biome createDarkForest() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
		addBasicFeatures(builder2);
		builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.DARK_FOREST_VEGETATION);
		DefaultBiomeFeatures.addForestFlowers(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		DefaultBiomeFeatures.addDefaultFlowers(builder2);
		DefaultBiomeFeatures.addForestGrass(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
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
					.build()
			)
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	public static Biome createSwamp() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SLIME, 1, 1, 1));
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.FROG, 10, 2, 5));
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
		DefaultBiomeFeatures.addFossils(builder2);
		addBasicFeatures(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addClayDisk(builder2);
		DefaultBiomeFeatures.addSwampFeatures(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addSwampVegetation(builder2);
		builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, OceanPlacedFeatures.SEAGRASS_SWAMP);
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
					.build()
			)
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	public static Biome createMangroveSwamp() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SLIME, 1, 1, 1));
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.FROG, 10, 2, 5));
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
		DefaultBiomeFeatures.addFossils(builder2);
		addBasicFeatures(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addGrassAndClayDisks(builder2);
		DefaultBiomeFeatures.addMangroveSwampFeatures(builder2);
		builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, OceanPlacedFeatures.SEAGRASS_SWAMP);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.RAIN)
			.temperature(0.8F)
			.downfall(0.9F)
			.effects(
				new BiomeEffects.Builder()
					.waterColor(3832426)
					.waterFogColor(2302743)
					.fogColor(12638463)
					.skyColor(getSkyColor(0.8F))
					.foliageColor(9285927)
					.grassColorModifier(BiomeEffects.GrassColorModifier.SWAMP)
					.moodSound(BiomeMoodSound.CAVE)
					.build()
			)
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	public static Biome createRiver(boolean frozen) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder()
			.spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.SQUID, 2, 1, 4))
			.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.SALMON, 5, 1, 5));
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.DROWNED, frozen ? 1 : 100, 1, 1));
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
		addBasicFeatures(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		DefaultBiomeFeatures.addWaterBiomeOakTrees(builder2);
		DefaultBiomeFeatures.addDefaultFlowers(builder2);
		DefaultBiomeFeatures.addDefaultGrass(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
		if (!frozen) {
			builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, OceanPlacedFeatures.SEAGRASS_RIVER);
		}

		float f = frozen ? 0.0F : 0.5F;
		return createBiome(
			frozen ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN, f, 0.5F, frozen ? 3750089 : 4159204, 329011, builder, builder2, DEFAULT_MUSIC
		);
	}

	public static Biome createBeach(boolean snowy, boolean stony) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		boolean bl = !stony && !snowy;
		if (bl) {
			builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.TURTLE, 5, 2, 5));
		}

		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
		addBasicFeatures(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		DefaultBiomeFeatures.addDefaultFlowers(builder2);
		DefaultBiomeFeatures.addDefaultGrass(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
		float f;
		if (snowy) {
			f = 0.05F;
		} else if (stony) {
			f = 0.2F;
		} else {
			f = 0.8F;
		}

		return createBiome(
			snowy ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN, f, bl ? 0.4F : 0.3F, snowy ? 4020182 : 4159204, 329011, builder, builder2, DEFAULT_MUSIC
		);
	}

	public static Biome createTheVoid() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder();
		builder.feature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, MiscPlacedFeatures.VOID_START_PLATFORM);
		return createBiome(Biome.Precipitation.NONE, 0.5F, 0.5F, new SpawnSettings.Builder(), builder, DEFAULT_MUSIC);
	}

	public static Biome createMeadow() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder();
		SpawnSettings.Builder builder2 = new SpawnSettings.Builder();
		builder2.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.DONKEY, 1, 1, 2))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 2, 2, 6))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.SHEEP, 2, 2, 4));
		DefaultBiomeFeatures.addBatsAndMonsters(builder2);
		addBasicFeatures(builder);
		DefaultBiomeFeatures.addPlainsTallGrass(builder);
		DefaultBiomeFeatures.addDefaultOres(builder);
		DefaultBiomeFeatures.addDefaultDisks(builder);
		DefaultBiomeFeatures.addMeadowFlowers(builder);
		DefaultBiomeFeatures.addEmeraldOre(builder);
		DefaultBiomeFeatures.addInfestedStone(builder);
		MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_MEADOW);
		return createBiome(Biome.Precipitation.RAIN, 0.5F, 0.8F, 937679, 329011, builder2, builder, musicSound);
	}

	public static Biome createFrozenPeaks() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder();
		SpawnSettings.Builder builder2 = new SpawnSettings.Builder();
		builder2.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.GOAT, 5, 1, 3));
		DefaultBiomeFeatures.addBatsAndMonsters(builder2);
		addBasicFeatures(builder);
		DefaultBiomeFeatures.addFrozenLavaSpring(builder);
		DefaultBiomeFeatures.addDefaultOres(builder);
		DefaultBiomeFeatures.addDefaultDisks(builder);
		DefaultBiomeFeatures.addEmeraldOre(builder);
		DefaultBiomeFeatures.addInfestedStone(builder);
		MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_FROZEN_PEAKS);
		return createBiome(Biome.Precipitation.SNOW, -0.7F, 0.9F, builder2, builder, musicSound);
	}

	public static Biome createJaggedPeaks() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder();
		SpawnSettings.Builder builder2 = new SpawnSettings.Builder();
		builder2.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.GOAT, 5, 1, 3));
		DefaultBiomeFeatures.addBatsAndMonsters(builder2);
		addBasicFeatures(builder);
		DefaultBiomeFeatures.addFrozenLavaSpring(builder);
		DefaultBiomeFeatures.addDefaultOres(builder);
		DefaultBiomeFeatures.addDefaultDisks(builder);
		DefaultBiomeFeatures.addEmeraldOre(builder);
		DefaultBiomeFeatures.addInfestedStone(builder);
		MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_JAGGED_PEAKS);
		return createBiome(Biome.Precipitation.SNOW, -0.7F, 0.9F, builder2, builder, musicSound);
	}

	public static Biome createStonyPeaks() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder();
		SpawnSettings.Builder builder2 = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addBatsAndMonsters(builder2);
		addBasicFeatures(builder);
		DefaultBiomeFeatures.addDefaultOres(builder);
		DefaultBiomeFeatures.addDefaultDisks(builder);
		DefaultBiomeFeatures.addEmeraldOre(builder);
		DefaultBiomeFeatures.addInfestedStone(builder);
		MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_STONY_PEAKS);
		return createBiome(Biome.Precipitation.RAIN, 1.0F, 0.3F, builder2, builder, musicSound);
	}

	public static Biome createSnowySlopes() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder();
		SpawnSettings.Builder builder2 = new SpawnSettings.Builder();
		builder2.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 4, 2, 3))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.GOAT, 5, 1, 3));
		DefaultBiomeFeatures.addBatsAndMonsters(builder2);
		addBasicFeatures(builder);
		DefaultBiomeFeatures.addFrozenLavaSpring(builder);
		DefaultBiomeFeatures.addDefaultOres(builder);
		DefaultBiomeFeatures.addDefaultDisks(builder);
		DefaultBiomeFeatures.addDefaultVegetation(builder);
		DefaultBiomeFeatures.addEmeraldOre(builder);
		DefaultBiomeFeatures.addInfestedStone(builder);
		MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_SNOWY_SLOPES);
		return createBiome(Biome.Precipitation.SNOW, -0.3F, 0.9F, builder2, builder, musicSound);
	}

	public static Biome createGrove() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder();
		SpawnSettings.Builder builder2 = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder2);
		builder2.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.WOLF, 8, 4, 4))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 4, 2, 3))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.FOX, 8, 2, 4));
		DefaultBiomeFeatures.addBatsAndMonsters(builder2);
		addBasicFeatures(builder);
		DefaultBiomeFeatures.addFrozenLavaSpring(builder);
		DefaultBiomeFeatures.addDefaultOres(builder);
		DefaultBiomeFeatures.addDefaultDisks(builder);
		DefaultBiomeFeatures.addGroveTrees(builder);
		DefaultBiomeFeatures.addDefaultVegetation(builder);
		DefaultBiomeFeatures.addEmeraldOre(builder);
		DefaultBiomeFeatures.addInfestedStone(builder);
		MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_GROVE);
		return createBiome(Biome.Precipitation.SNOW, -0.2F, 0.8F, builder2, builder, musicSound);
	}

	public static Biome createLushCaves() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		builder.spawn(SpawnGroup.AXOLOTLS, new SpawnSettings.SpawnEntry(EntityType.AXOLOTL, 10, 4, 6));
		builder.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.TROPICAL_FISH, 25, 8, 8));
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
		addBasicFeatures(builder2);
		DefaultBiomeFeatures.addPlainsTallGrass(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addClayOre(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		DefaultBiomeFeatures.addLushCavesDecoration(builder2);
		MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_LUSH_CAVES);
		return createBiome(Biome.Precipitation.RAIN, 0.5F, 0.5F, builder, builder2, musicSound);
	}

	public static Biome createDripstoneCaves() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addDripstoneCaveMobs(builder);
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
		addBasicFeatures(builder2);
		DefaultBiomeFeatures.addPlainsTallGrass(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2, true);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		DefaultBiomeFeatures.addPlainsFeatures(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
		DefaultBiomeFeatures.addDripstone(builder2);
		MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_DRIPSTONE_CAVES);
		return createBiome(Biome.Precipitation.RAIN, 0.8F, 0.4F, builder, builder2, musicSound);
	}

	public static Biome createDeepDark() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
		builder2.carver(GenerationStep.Carver.AIR, ConfiguredCarvers.CAVE);
		builder2.carver(GenerationStep.Carver.AIR, ConfiguredCarvers.CAVE_EXTRA_UNDERGROUND);
		builder2.carver(GenerationStep.Carver.AIR, ConfiguredCarvers.CANYON);
		DefaultBiomeFeatures.addAmethystGeodes(builder2);
		DefaultBiomeFeatures.addDungeons(builder2);
		DefaultBiomeFeatures.addMineables(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		DefaultBiomeFeatures.addPlainsTallGrass(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2, true);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		DefaultBiomeFeatures.addPlainsFeatures(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
		DefaultBiomeFeatures.addSculk(builder2);
		MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_DEEP_DARK);
		return createBiome(Biome.Precipitation.RAIN, 0.8F, 0.4F, builder, builder2, musicSound);
	}
}
