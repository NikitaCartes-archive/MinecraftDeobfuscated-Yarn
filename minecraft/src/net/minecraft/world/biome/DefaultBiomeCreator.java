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
		builder.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6055, 8, 4, 4));
		builder.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6140, 4, 2, 3));
		builder.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_17943, 8, 2, 4));
		if (spruce) {
			DefaultBiomeFeatures.addBatsAndMonsters(builder);
		} else {
			DefaultBiomeFeatures.addBats(builder);
			DefaultBiomeFeatures.addMonsters(builder, 100, 25, 100);
		}

		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.field_26326);
		DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		builder2.structureFeature(ConfiguredStructureFeatures.field_26316);
		DefaultBiomeFeatures.addLandCarvers(builder2);
		DefaultBiomeFeatures.addDefaultLakes(builder2);
		DefaultBiomeFeatures.addDungeons(builder2);
		DefaultBiomeFeatures.addMossyRocks(builder2);
		DefaultBiomeFeatures.addLargeFerns(builder2);
		DefaultBiomeFeatures.addMineables(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		builder2.feature(GenerationStep.Feature.field_13178, spruce ? ConfiguredFeatures.field_26085 : ConfiguredFeatures.field_26086);
		DefaultBiomeFeatures.addDefaultFlowers(builder2);
		DefaultBiomeFeatures.addGiantTaigaGrass(builder2);
		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		DefaultBiomeFeatures.addDefaultVegetation(builder2);
		DefaultBiomeFeatures.addSprings(builder2);
		DefaultBiomeFeatures.addSweetBerryBushes(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.field_9382)
			.category(Biome.Category.field_9361)
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

	public static Biome createBirchForest(float depth, float scale, boolean bl) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.field_26327);
		DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		builder2.structureFeature(ConfiguredStructureFeatures.field_26316);
		DefaultBiomeFeatures.addLandCarvers(builder2);
		DefaultBiomeFeatures.addDefaultLakes(builder2);
		DefaultBiomeFeatures.addDungeons(builder2);
		DefaultBiomeFeatures.addForestFlowers(builder2);
		DefaultBiomeFeatures.addMineables(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		if (bl) {
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
			.precipitation(Biome.Precipitation.field_9382)
			.category(Biome.Category.field_9370)
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
		builder.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6104, 10, 1, 1))
			.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6081, 2, 1, 1));
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
		builder.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6104, parrotWeight, 1, parrotMaxGroupSize))
			.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6081, 2, 1, ocelotMaxGroupSize))
			.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6146, 1, 1, 2));
		builder.method_31083();
		return createJungleFeatures(depth, scale, 0.9F, false, false, false, builder);
	}

	private static Biome createBambooJungle(float depth, float scale, int parrotWeight, int parrotMaxGroupSize) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addJungleMobs(builder);
		builder.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6104, parrotWeight, 1, parrotMaxGroupSize))
			.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6146, 80, 1, 2))
			.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6081, 2, 1, 1));
		return createJungleFeatures(depth, scale, 0.9F, true, false, false, builder);
	}

	private static Biome createJungleFeatures(float f, float g, float h, boolean bl, boolean bl2, boolean bl3, SpawnSettings.Builder builder) {
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.field_26327);
		if (!bl2 && !bl3) {
			builder2.structureFeature(ConfiguredStructureFeatures.field_26296);
		}

		DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		builder2.structureFeature(ConfiguredStructureFeatures.field_26287);
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
			.precipitation(Biome.Precipitation.field_9382)
			.category(Biome.Category.field_9358)
			.depth(f)
			.scale(g)
			.temperature(0.95F)
			.downfall(h)
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
		builder.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6074, 5, 4, 6));
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(surfaceBuilder);
		DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		builder2.structureFeature(ConfiguredStructureFeatures.field_26289);
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
			.precipitation(Biome.Precipitation.field_9382)
			.category(Biome.Category.field_9357)
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

	public static Biome createDesert(float f, float g, boolean bl, boolean bl2, boolean bl3) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addDesertMobs(builder);
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.field_26321);
		if (bl) {
			builder2.structureFeature(ConfiguredStructureFeatures.field_26312);
			builder2.structureFeature(ConfiguredStructureFeatures.field_26292);
		}

		if (bl2) {
			builder2.structureFeature(ConfiguredStructureFeatures.field_26297);
		}

		if (bl3) {
			DefaultBiomeFeatures.addFossils(builder2);
		}

		DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		builder2.structureFeature(ConfiguredStructureFeatures.field_26317);
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
			.precipitation(Biome.Precipitation.field_9384)
			.category(Biome.Category.field_9368)
			.depth(f)
			.scale(g)
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
			builder.method_31083();
		}

		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.field_26327);
		if (!bl) {
			builder2.structureFeature(ConfiguredStructureFeatures.field_26311).structureFeature(ConfiguredStructureFeatures.field_26292);
		}

		DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		builder2.structureFeature(ConfiguredStructureFeatures.field_26316);
		DefaultBiomeFeatures.addLandCarvers(builder2);
		DefaultBiomeFeatures.addDefaultLakes(builder2);
		DefaultBiomeFeatures.addDungeons(builder2);
		DefaultBiomeFeatures.addPlainsTallGrass(builder2);
		if (bl) {
			builder2.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26019);
		}

		DefaultBiomeFeatures.addMineables(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		DefaultBiomeFeatures.addPlainsFeatures(builder2);
		if (bl) {
			builder2.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25995);
		}

		DefaultBiomeFeatures.addDefaultMushrooms(builder2);
		if (bl) {
			builder2.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26020);
		} else {
			DefaultBiomeFeatures.addDefaultVegetation(builder2);
		}

		DefaultBiomeFeatures.addSprings(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.field_9382)
			.category(Biome.Category.field_9355)
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

	private static Biome method_31065(GenerationSettings.Builder builder) {
		SpawnSettings.Builder builder2 = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addEndMobs(builder2);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.field_9384)
			.category(Biome.Category.field_9360)
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
		GenerationSettings.Builder builder = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.field_26322);
		return method_31065(builder);
	}

	public static Biome createTheEnd() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
			.surfaceBuilder(ConfiguredSurfaceBuilders.field_26322)
			.feature(GenerationStep.Feature.field_13173, ConfiguredFeatures.field_26040);
		return method_31065(builder);
	}

	public static Biome createEndMidlands() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
			.surfaceBuilder(ConfiguredSurfaceBuilders.field_26322)
			.structureFeature(ConfiguredStructureFeatures.field_26308);
		return method_31065(builder);
	}

	public static Biome createEndHighlands() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
			.surfaceBuilder(ConfiguredSurfaceBuilders.field_26322)
			.structureFeature(ConfiguredStructureFeatures.field_26308)
			.feature(GenerationStep.Feature.field_13173, ConfiguredFeatures.field_26091)
			.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26119);
		return method_31065(builder);
	}

	public static Biome createSmallEndIslands() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
			.surfaceBuilder(ConfiguredSurfaceBuilders.field_26322)
			.feature(GenerationStep.Feature.field_13174, ConfiguredFeatures.field_26121);
		return method_31065(builder);
	}

	public static Biome createMushroomFields(float depth, float scale) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addMushroomMobs(builder);
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.field_26331);
		DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		builder2.structureFeature(ConfiguredStructureFeatures.field_26316);
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
			.precipitation(Biome.Precipitation.field_9382)
			.category(Biome.Category.field_9365)
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

	private static Biome method_31067(float f, float g, float h, boolean bl, boolean bl2, SpawnSettings.Builder builder) {
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder()
			.surfaceBuilder(bl2 ? ConfiguredSurfaceBuilders.field_26335 : ConfiguredSurfaceBuilders.field_26327);
		if (!bl && !bl2) {
			builder2.structureFeature(ConfiguredStructureFeatures.field_26313).structureFeature(ConfiguredStructureFeatures.field_26292);
		}

		DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		builder2.structureFeature(bl ? ConfiguredStructureFeatures.field_26289 : ConfiguredStructureFeatures.field_26316);
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
			.precipitation(Biome.Precipitation.field_9384)
			.category(Biome.Category.field_9356)
			.depth(f)
			.scale(g)
			.temperature(h)
			.downfall(0.0F)
			.effects(
				new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(getSkyColor(h)).moodSound(BiomeMoodSound.CAVE).build()
			)
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	public static Biome createSavanna(float f, float g, float h, boolean bl, boolean bl2) {
		SpawnSettings.Builder builder = method_31068();
		return method_31067(f, g, h, bl, bl2, builder);
	}

	private static SpawnSettings.Builder method_31068() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		builder.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6139, 1, 2, 6))
			.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6067, 1, 1, 1));
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		return builder;
	}

	public static Biome createSavannaPlateau() {
		SpawnSettings.Builder builder = method_31068();
		builder.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6074, 8, 4, 4));
		return method_31067(1.5F, 0.025F, 1.0F, true, false, builder);
	}

	private static Biome createBadlands(ConfiguredSurfaceBuilder<TernarySurfaceConfig> configuredSurfaceBuilder, float f, float g, boolean bl, boolean bl2) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(configuredSurfaceBuilder);
		DefaultBiomeFeatures.addBadlandsUndergroundStructures(builder2);
		builder2.structureFeature(bl ? ConfiguredStructureFeatures.field_26289 : ConfiguredStructureFeatures.field_26316);
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
			.precipitation(Biome.Precipitation.field_9384)
			.category(Biome.Category.field_9354)
			.depth(f)
			.scale(g)
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

	public static Biome createNormalBadlands(float f, float g, boolean bl) {
		return createBadlands(ConfiguredSurfaceBuilders.field_26318, f, g, bl, false);
	}

	public static Biome createWoodedBadlandsPlateau(float f, float g) {
		return createBadlands(ConfiguredSurfaceBuilders.field_26340, f, g, true, true);
	}

	public static Biome createErodedBadlands() {
		return createBadlands(ConfiguredSurfaceBuilders.field_26323, 0.1F, 0.2F, true, false);
	}

	private static Biome createOcean(SpawnSettings.Builder builder, int waterColor, int waterFogColor, boolean deep, GenerationSettings.Builder builder2) {
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.field_9382)
			.category(Biome.Category.field_9367)
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

	private static GenerationSettings.Builder method_31066(
		ConfiguredSurfaceBuilder<TernarySurfaceConfig> configuredSurfaceBuilder, boolean bl, boolean bl2, boolean bl3
	) {
		GenerationSettings.Builder builder = new GenerationSettings.Builder().surfaceBuilder(configuredSurfaceBuilder);
		ConfiguredStructureFeature<?, ?> configuredStructureFeature = bl2 ? ConfiguredStructureFeatures.field_26305 : ConfiguredStructureFeatures.field_26304;
		if (bl3) {
			if (bl) {
				builder.structureFeature(ConfiguredStructureFeatures.field_26303);
			}

			DefaultBiomeFeatures.addOceanStructures(builder);
			builder.structureFeature(configuredStructureFeature);
		} else {
			builder.structureFeature(configuredStructureFeature);
			if (bl) {
				builder.structureFeature(ConfiguredStructureFeatures.field_26303);
			}

			DefaultBiomeFeatures.addOceanStructures(builder);
		}

		builder.structureFeature(ConfiguredStructureFeatures.field_26290);
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
		builder.spawn(SpawnGroup.field_24460, new SpawnSettings.SpawnEntry(EntityType.field_6073, 15, 1, 5));
		boolean bl = !deep;
		GenerationSettings.Builder builder2 = method_31066(ConfiguredSurfaceBuilders.field_26327, deep, false, bl);
		builder2.feature(GenerationStep.Feature.field_13178, deep ? ConfiguredFeatures.field_26136 : ConfiguredFeatures.field_26135);
		DefaultBiomeFeatures.addSeagrassOnStone(builder2);
		DefaultBiomeFeatures.addKelp(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return createOcean(builder, 4020182, 329011, deep, builder2);
	}

	public static Biome createNormalOcean(boolean deep) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addOceanMobs(builder, 1, 4, 10);
		builder.spawn(SpawnGroup.field_6300, new SpawnSettings.SpawnEntry(EntityType.field_6087, 1, 1, 2));
		GenerationSettings.Builder builder2 = method_31066(ConfiguredSurfaceBuilders.field_26327, deep, false, true);
		builder2.feature(GenerationStep.Feature.field_13178, deep ? ConfiguredFeatures.field_26139 : ConfiguredFeatures.field_26137);
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

		builder.spawn(SpawnGroup.field_24460, new SpawnSettings.SpawnEntry(EntityType.field_6062, 5, 1, 3))
			.spawn(SpawnGroup.field_24460, new SpawnSettings.SpawnEntry(EntityType.field_6111, 25, 8, 8))
			.spawn(SpawnGroup.field_6300, new SpawnSettings.SpawnEntry(EntityType.field_6087, 2, 1, 2));
		GenerationSettings.Builder builder2 = method_31066(ConfiguredSurfaceBuilders.field_26334, deep, true, false);
		builder2.feature(GenerationStep.Feature.field_13178, deep ? ConfiguredFeatures.field_25950 : ConfiguredFeatures.field_25949);
		if (deep) {
			DefaultBiomeFeatures.addSeagrassOnStone(builder2);
		}

		DefaultBiomeFeatures.addLessKelp(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return createOcean(builder, 4566514, 267827, deep, builder2);
	}

	public static Biome createWarmOcean() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder().spawn(SpawnGroup.field_24460, new SpawnSettings.SpawnEntry(EntityType.field_6062, 15, 1, 3));
		DefaultBiomeFeatures.addWarmOceanMobs(builder, 10, 4);
		GenerationSettings.Builder builder2 = method_31066(ConfiguredSurfaceBuilders.field_26325, false, true, false)
			.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26110)
			.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25949)
			.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25951);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return createOcean(builder, 4445678, 270131, false, builder2);
	}

	public static Biome createDeepWarmOcean() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addWarmOceanMobs(builder, 5, 1);
		builder.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6123, 5, 1, 1));
		GenerationSettings.Builder builder2 = method_31066(ConfiguredSurfaceBuilders.field_26325, true, true, false)
			.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_25950);
		DefaultBiomeFeatures.addSeagrassOnStone(builder2);
		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return createOcean(builder, 4445678, 270131, true, builder2);
	}

	public static Biome createFrozenOcean(boolean monument) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder()
			.spawn(SpawnGroup.field_6300, new SpawnSettings.SpawnEntry(EntityType.field_6114, 1, 1, 4))
			.spawn(SpawnGroup.field_24460, new SpawnSettings.SpawnEntry(EntityType.field_6073, 15, 1, 5))
			.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6042, 1, 1, 2));
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		builder.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6123, 5, 1, 1));
		float f = monument ? 0.5F : 0.0F;
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.field_26324);
		builder2.structureFeature(ConfiguredStructureFeatures.field_26304);
		if (monument) {
			builder2.structureFeature(ConfiguredStructureFeatures.field_26303);
		}

		DefaultBiomeFeatures.addOceanStructures(builder2);
		builder2.structureFeature(ConfiguredStructureFeatures.field_26290);
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
			.precipitation(monument ? Biome.Precipitation.field_9382 : Biome.Precipitation.field_9383)
			.category(Biome.Category.field_9367)
			.depth(monument ? -1.8F : -1.0F)
			.scale(0.1F)
			.temperature(f)
			.temperatureModifier(Biome.TemperatureModifier.field_26408)
			.downfall(0.5F)
			.effects(
				new BiomeEffects.Builder().waterColor(3750089).waterFogColor(329011).fogColor(12638463).skyColor(getSkyColor(f)).moodSound(BiomeMoodSound.CAVE).build()
			)
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	private static Biome createForest(float f, float g, boolean bl, SpawnSettings.Builder builder) {
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.field_26327);
		DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		builder2.structureFeature(ConfiguredStructureFeatures.field_26316);
		DefaultBiomeFeatures.addLandCarvers(builder2);
		DefaultBiomeFeatures.addDefaultLakes(builder2);
		DefaultBiomeFeatures.addDungeons(builder2);
		if (bl) {
			builder2.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26106);
		} else {
			DefaultBiomeFeatures.addForestFlowers(builder2);
		}

		DefaultBiomeFeatures.addMineables(builder2);
		DefaultBiomeFeatures.addDefaultOres(builder2);
		DefaultBiomeFeatures.addDefaultDisks(builder2);
		if (bl) {
			builder2.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26111);
			builder2.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26102);
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
			.precipitation(Biome.Precipitation.field_9382)
			.category(Biome.Category.field_9370)
			.depth(f)
			.scale(g)
			.temperature(0.7F)
			.downfall(0.8F)
			.effects(
				new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(getSkyColor(0.7F)).moodSound(BiomeMoodSound.CAVE).build()
			)
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	private static SpawnSettings.Builder method_31069() {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		return builder;
	}

	public static Biome createNormalForest(float depth, float scale) {
		SpawnSettings.Builder builder = method_31069().spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6055, 5, 4, 4)).method_31083();
		return createForest(depth, scale, false, builder);
	}

	public static Biome createFlowerForest() {
		SpawnSettings.Builder builder = method_31069().spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6140, 4, 2, 3));
		return createForest(0.1F, 0.4F, true, builder);
	}

	public static Biome createTaiga(float f, float g, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		builder.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6055, 8, 4, 4))
			.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6140, 4, 2, 3))
			.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_17943, 8, 2, 4));
		if (!bl && !bl2) {
			builder.method_31083();
		}

		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		float h = bl ? -0.5F : 0.25F;
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.field_26327);
		if (bl3) {
			builder2.structureFeature(ConfiguredStructureFeatures.field_26315);
			builder2.structureFeature(ConfiguredStructureFeatures.field_26292);
		}

		if (bl4) {
			builder2.structureFeature(ConfiguredStructureFeatures.field_26298);
		}

		DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		builder2.structureFeature(bl2 ? ConfiguredStructureFeatures.field_26289 : ConfiguredStructureFeatures.field_26316);
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
			.precipitation(bl ? Biome.Precipitation.field_9383 : Biome.Precipitation.field_9382)
			.category(Biome.Category.field_9361)
			.depth(f)
			.scale(g)
			.temperature(h)
			.downfall(bl ? 0.4F : 0.8F)
			.effects(
				new BiomeEffects.Builder()
					.waterColor(bl ? 4020182 : 4159204)
					.waterFogColor(329011)
					.fogColor(12638463)
					.skyColor(getSkyColor(h))
					.moodSound(BiomeMoodSound.CAVE)
					.build()
			)
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	public static Biome createDarkForest(float f, float g, boolean bl) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.field_26327);
		builder2.structureFeature(ConfiguredStructureFeatures.field_26295);
		DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		builder2.structureFeature(ConfiguredStructureFeatures.field_26316);
		DefaultBiomeFeatures.addLandCarvers(builder2);
		DefaultBiomeFeatures.addDefaultLakes(builder2);
		DefaultBiomeFeatures.addDungeons(builder2);
		builder2.feature(GenerationStep.Feature.field_13178, bl ? ConfiguredFeatures.field_26109 : ConfiguredFeatures.field_26108);
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
			.precipitation(Biome.Precipitation.field_9382)
			.category(Biome.Category.field_9370)
			.depth(f)
			.scale(g)
			.temperature(0.7F)
			.downfall(0.8F)
			.effects(
				new BiomeEffects.Builder()
					.waterColor(4159204)
					.waterFogColor(329011)
					.fogColor(12638463)
					.skyColor(getSkyColor(0.7F))
					.grassColorModifier(BiomeEffects.GrassColorModifier.field_26427)
					.moodSound(BiomeMoodSound.CAVE)
					.build()
			)
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	public static Biome createSwamp(float f, float g, boolean bl) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addFarmAnimals(builder);
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		builder.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6069, 1, 1, 1));
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.field_26338);
		if (!bl) {
			builder2.structureFeature(ConfiguredStructureFeatures.field_26301);
		}

		builder2.structureFeature(ConfiguredStructureFeatures.field_26293);
		builder2.structureFeature(ConfiguredStructureFeatures.field_26288);
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
			builder2.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26140);
		}

		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.field_9382)
			.category(Biome.Category.field_9364)
			.depth(f)
			.scale(g)
			.temperature(0.8F)
			.downfall(0.9F)
			.effects(
				new BiomeEffects.Builder()
					.waterColor(6388580)
					.waterFogColor(2302743)
					.fogColor(12638463)
					.skyColor(getSkyColor(0.8F))
					.foliageColor(6975545)
					.grassColorModifier(BiomeEffects.GrassColorModifier.field_26428)
					.moodSound(BiomeMoodSound.CAVE)
					.build()
			)
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	public static Biome createSnowyTundra(float f, float g, boolean bl, boolean bl2) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder().creatureSpawnProbability(0.07F);
		DefaultBiomeFeatures.addSnowyMobs(builder);
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder()
			.surfaceBuilder(bl ? ConfiguredSurfaceBuilders.field_26329 : ConfiguredSurfaceBuilders.field_26327);
		if (!bl && !bl2) {
			builder2.structureFeature(ConfiguredStructureFeatures.field_26314).structureFeature(ConfiguredStructureFeatures.field_26298);
		}

		DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		if (!bl && !bl2) {
			builder2.structureFeature(ConfiguredStructureFeatures.field_26292);
		}

		builder2.structureFeature(bl2 ? ConfiguredStructureFeatures.field_26289 : ConfiguredStructureFeatures.field_26316);
		DefaultBiomeFeatures.addLandCarvers(builder2);
		DefaultBiomeFeatures.addDefaultLakes(builder2);
		DefaultBiomeFeatures.addDungeons(builder2);
		if (bl) {
			builder2.feature(GenerationStep.Feature.field_13173, ConfiguredFeatures.field_25952);
			builder2.feature(GenerationStep.Feature.field_13173, ConfiguredFeatures.field_25953);
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
			.precipitation(Biome.Precipitation.field_9383)
			.category(Biome.Category.field_9362)
			.depth(f)
			.scale(g)
			.temperature(0.0F)
			.downfall(0.5F)
			.effects(
				new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(getSkyColor(0.0F)).moodSound(BiomeMoodSound.CAVE).build()
			)
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	public static Biome createRiver(float depth, float scale, float temperature, int i, boolean bl) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder()
			.spawn(SpawnGroup.field_6300, new SpawnSettings.SpawnEntry(EntityType.field_6114, 2, 1, 4))
			.spawn(SpawnGroup.field_24460, new SpawnSettings.SpawnEntry(EntityType.field_6073, 5, 1, 5));
		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		builder.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6123, bl ? 1 : 100, 1, 1));
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.field_26327);
		builder2.structureFeature(ConfiguredStructureFeatures.field_26293);
		builder2.structureFeature(ConfiguredStructureFeatures.field_26316);
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
			builder2.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26138);
		}

		DefaultBiomeFeatures.addFrozenTopLayer(builder2);
		return new Biome.Builder()
			.precipitation(bl ? Biome.Precipitation.field_9383 : Biome.Precipitation.field_9382)
			.category(Biome.Category.field_9369)
			.depth(depth)
			.scale(scale)
			.temperature(temperature)
			.downfall(0.5F)
			.effects(
				new BiomeEffects.Builder().waterColor(i).waterFogColor(329011).fogColor(12638463).skyColor(getSkyColor(temperature)).moodSound(BiomeMoodSound.CAVE).build()
			)
			.spawnSettings(builder.build())
			.generationSettings(builder2.build())
			.build();
	}

	public static Biome createBeach(float depth, float scale, float temperature, float downfall, int waterColor, boolean snowy, boolean stony) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		if (!stony && !snowy) {
			builder.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_6113, 5, 2, 5));
		}

		DefaultBiomeFeatures.addBatsAndMonsters(builder);
		GenerationSettings.Builder builder2 = new GenerationSettings.Builder()
			.surfaceBuilder(stony ? ConfiguredSurfaceBuilders.field_26337 : ConfiguredSurfaceBuilders.field_26321);
		if (stony) {
			DefaultBiomeFeatures.addDefaultUndergroundStructures(builder2);
		} else {
			builder2.structureFeature(ConfiguredStructureFeatures.field_26293);
			builder2.structureFeature(ConfiguredStructureFeatures.field_26309);
			builder2.structureFeature(ConfiguredStructureFeatures.field_26300);
		}

		builder2.structureFeature(stony ? ConfiguredStructureFeatures.field_26289 : ConfiguredStructureFeatures.field_26316);
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
			.precipitation(snowy ? Biome.Precipitation.field_9383 : Biome.Precipitation.field_9382)
			.category(stony ? Biome.Category.field_9371 : Biome.Category.field_9363)
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
		GenerationSettings.Builder builder = new GenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.field_26333);
		builder.feature(GenerationStep.Feature.field_13179, ConfiguredFeatures.field_25971);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.field_9384)
			.category(Biome.Category.field_9371)
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
			.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6107, 50, 4, 4))
			.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6050, 100, 4, 4))
			.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6102, 2, 4, 4))
			.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6091, 1, 4, 4))
			.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_22281, 15, 4, 4))
			.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_23214, 60, 1, 2))
			.build();
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
			.surfaceBuilder(ConfiguredSurfaceBuilders.field_26332)
			.structureFeature(ConfiguredStructureFeatures.field_26291)
			.structureFeature(ConfiguredStructureFeatures.field_26306)
			.structureFeature(ConfiguredStructureFeatures.field_26310)
			.carver(GenerationStep.Carver.field_13169, ConfiguredCarvers.field_25947)
			.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26003);
		DefaultBiomeFeatures.addDefaultMushrooms(builder);
		builder.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26007)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26014)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26015)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26127)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26128)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_25996)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_25997)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26059)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26005);
		DefaultBiomeFeatures.addNetherMineables(builder);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.field_9384)
			.category(Biome.Category.field_9366)
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
					.loopSound(SoundEvents.field_22455)
					.moodSound(new BiomeMoodSound(SoundEvents.field_22456, 6000, 8, 2.0))
					.additionsSound(new BiomeAdditionsSound(SoundEvents.field_22454, 0.0111))
					.music(MusicType.createIngameMusic(SoundEvents.field_23794))
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
			.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6137, 20, 5, 5))
			.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6107, 50, 4, 4))
			.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6091, 1, 4, 4))
			.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_23214, 60, 1, 2))
			.spawnCost(EntityType.field_6137, 0.7, 0.15)
			.spawnCost(EntityType.field_6107, 0.7, 0.15)
			.spawnCost(EntityType.field_6091, 0.7, 0.15)
			.spawnCost(EntityType.field_23214, 0.7, 0.15)
			.build();
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
			.surfaceBuilder(ConfiguredSurfaceBuilders.field_26336)
			.structureFeature(ConfiguredStructureFeatures.field_26306)
			.structureFeature(ConfiguredStructureFeatures.field_26307)
			.structureFeature(ConfiguredStructureFeatures.field_26291)
			.structureFeature(ConfiguredStructureFeatures.field_26310)
			.carver(GenerationStep.Carver.field_13169, ConfiguredCarvers.field_25947)
			.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26003)
			.feature(GenerationStep.Feature.field_13171, ConfiguredFeatures.field_26134)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26007)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26127)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26128)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26018)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26014)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26015)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26059)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26005)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26060);
		DefaultBiomeFeatures.addNetherMineables(builder);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.field_9384)
			.category(Biome.Category.field_9366)
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
					.particleConfig(new BiomeParticleConfig(ParticleTypes.field_22247, 0.00625F))
					.loopSound(SoundEvents.field_22458)
					.moodSound(new BiomeMoodSound(SoundEvents.field_22459, 6000, 8, 2.0))
					.additionsSound(new BiomeAdditionsSound(SoundEvents.field_22457, 0.0111))
					.music(MusicType.createIngameMusic(SoundEvents.field_23795))
					.build()
			)
			.spawnSettings(spawnSettings)
			.generationSettings(builder.build())
			.build();
	}

	public static Biome createBasaltDeltas() {
		SpawnSettings spawnSettings = new SpawnSettings.Builder()
			.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6107, 40, 1, 1))
			.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6102, 100, 2, 5))
			.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_23214, 60, 1, 2))
			.build();
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
			.surfaceBuilder(ConfiguredSurfaceBuilders.field_26319)
			.structureFeature(ConfiguredStructureFeatures.field_26291)
			.carver(GenerationStep.Carver.field_13169, ConfiguredCarvers.field_25947)
			.structureFeature(ConfiguredStructureFeatures.field_26306)
			.feature(GenerationStep.Feature.field_13173, ConfiguredFeatures.field_26122)
			.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26002)
			.feature(GenerationStep.Feature.field_13173, ConfiguredFeatures.field_26123)
			.feature(GenerationStep.Feature.field_13173, ConfiguredFeatures.field_26124)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26125)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26126)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26004)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26014)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26015)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26127)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26128)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_25996)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_25997)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26059)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26006)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26061)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26062);
		DefaultBiomeFeatures.addAncientDebris(builder);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.field_9384)
			.category(Biome.Category.field_9366)
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
					.particleConfig(new BiomeParticleConfig(ParticleTypes.field_23956, 0.118093334F))
					.loopSound(SoundEvents.field_23791)
					.moodSound(new BiomeMoodSound(SoundEvents.field_23792, 6000, 8, 2.0))
					.additionsSound(new BiomeAdditionsSound(SoundEvents.field_23790, 0.0111))
					.music(MusicType.createIngameMusic(SoundEvents.field_23793))
					.build()
			)
			.spawnSettings(spawnSettings)
			.generationSettings(builder.build())
			.build();
	}

	public static Biome createCrimsonForest() {
		SpawnSettings spawnSettings = new SpawnSettings.Builder()
			.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6050, 1, 2, 4))
			.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_21973, 9, 3, 4))
			.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_22281, 5, 3, 4))
			.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_23214, 60, 1, 2))
			.build();
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
			.surfaceBuilder(ConfiguredSurfaceBuilders.field_26320)
			.structureFeature(ConfiguredStructureFeatures.field_26291)
			.carver(GenerationStep.Carver.field_13169, ConfiguredCarvers.field_25947)
			.structureFeature(ConfiguredStructureFeatures.field_26306)
			.structureFeature(ConfiguredStructureFeatures.field_26310)
			.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26003);
		DefaultBiomeFeatures.addDefaultMushrooms(builder);
		builder.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26007)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26014)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26127)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26128)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26059)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26005)
			.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26133)
			.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26030)
			.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26129);
		DefaultBiomeFeatures.addNetherMineables(builder);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.field_9384)
			.category(Biome.Category.field_9366)
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
					.particleConfig(new BiomeParticleConfig(ParticleTypes.field_22248, 0.025F))
					.loopSound(SoundEvents.field_22452)
					.moodSound(new BiomeMoodSound(SoundEvents.field_22453, 6000, 8, 2.0))
					.additionsSound(new BiomeAdditionsSound(SoundEvents.field_22451, 0.0111))
					.music(MusicType.createIngameMusic(SoundEvents.field_23796))
					.build()
			)
			.spawnSettings(spawnSettings)
			.generationSettings(builder.build())
			.build();
	}

	public static Biome createWarpedForest() {
		SpawnSettings spawnSettings = new SpawnSettings.Builder()
			.spawn(SpawnGroup.field_6302, new SpawnSettings.SpawnEntry(EntityType.field_6091, 1, 4, 4))
			.spawn(SpawnGroup.field_6294, new SpawnSettings.SpawnEntry(EntityType.field_23214, 60, 1, 2))
			.spawnCost(EntityType.field_6091, 1.0, 0.12)
			.build();
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
			.surfaceBuilder(ConfiguredSurfaceBuilders.field_26339)
			.structureFeature(ConfiguredStructureFeatures.field_26306)
			.structureFeature(ConfiguredStructureFeatures.field_26310)
			.structureFeature(ConfiguredStructureFeatures.field_26291)
			.carver(GenerationStep.Carver.field_13169, ConfiguredCarvers.field_25947)
			.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26003);
		DefaultBiomeFeatures.addDefaultMushrooms(builder);
		builder.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26007)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26014)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26015)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26127)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26128)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26059)
			.feature(GenerationStep.Feature.field_13177, ConfiguredFeatures.field_26005)
			.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26032)
			.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26130)
			.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26131)
			.feature(GenerationStep.Feature.field_13178, ConfiguredFeatures.field_26132);
		DefaultBiomeFeatures.addNetherMineables(builder);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.field_9384)
			.category(Biome.Category.field_9366)
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
					.particleConfig(new BiomeParticleConfig(ParticleTypes.field_22249, 0.01428F))
					.loopSound(SoundEvents.field_22461)
					.moodSound(new BiomeMoodSound(SoundEvents.field_22462, 6000, 8, 2.0))
					.additionsSound(new BiomeAdditionsSound(SoundEvents.field_22460, 0.0111))
					.music(MusicType.createIngameMusic(SoundEvents.field_23797))
					.build()
			)
			.spawnSettings(spawnSettings)
			.generationSettings(builder.build())
			.build();
	}
}
