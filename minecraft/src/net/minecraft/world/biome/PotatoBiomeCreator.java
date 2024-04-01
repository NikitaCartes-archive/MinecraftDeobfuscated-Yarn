package net.minecraft.world.biome;

import javax.annotation.Nullable;
import net.minecraft.client.sound.MusicType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.MiscPlacedFeatures;
import net.minecraft.world.gen.feature.NetherPlacedFeatures;
import net.minecraft.world.gen.feature.OrePlacedFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.TreePlacedFeatures;
import net.minecraft.world.gen.feature.UndergroundPlacedFeatures;
import net.minecraft.world.gen.feature.VegetationPlacedFeatures;

public class PotatoBiomeCreator {
	protected static final int DEFAULT_WATER_COLOR = 4187298;
	protected static final int DEFAULT_WATER_FOG_COLOR = 740644;
	private static final int DEFAULT_FOG_COLOR = 9821879;
	private static final int DEFAULT_GRASS_COLOR = 6017107;
	private static final int DEFAULT_FOLIAGE_COLOR = 2199366;
	private static final int CORRUPTION_WATER_FOG_AND_FOLIAGE_COLOR = 1153876;
	private static final int CORRUPTION_WATER_AND_FOG_COLOR = 1356889;
	private static final int CORRUPTION_GRASS_COLOR = 1466676;
	private static final int WASTELAND_WATER_AND_GRASS_COLOR = 5105159;
	private static final int WASTELAND_FOG_COLOR = 8157780;
	private static final int WASTELAND_WATER_FOG_COLOR = 4056583;
	@Nullable
	private static final MusicSound MUSIC = null;

	protected static int getSkyColor(float f) {
		float g = f / 3.0F;
		g = MathHelper.clamp(g, -1.0F, 1.0F);
		return MathHelper.hsvToRgb(0.4722222F - g * 0.1F, 0.7F + g * 0.1F, 1.0F);
	}

	private static void addStandardFeatures(GenerationSettings.LookupBackedBuilder builder, boolean sparsePotatoes) {
		DefaultBiomeFeatures.addLandCarvers(builder);
		DefaultBiomeFeatures.addAmethystGeodes(builder);
		DefaultBiomeFeatures.addDungeons(builder);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_TATERSTONE);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_AMBER);
		builder.feature(GenerationStep.Feature.UNDERGROUND_ORES, OrePlacedFeatures.ORE_GRAVTATER);
		DefaultBiomeFeatures.addSprings(builder);
		builder.feature(
			GenerationStep.Feature.VEGETAL_DECORATION, sparsePotatoes ? VegetationPlacedFeatures.PATCH_POTATO_SPARSE : VegetationPlacedFeatures.PATCH_POTATO
		);
		builder.feature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, MiscPlacedFeatures.POTATO_CLOUD);
		DefaultBiomeFeatures.addDefaultOres(builder, false, true);
	}

	public static void addFarmAnimals(SpawnSettings.Builder spawnSettings) {
		spawnSettings.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.SHEEP, 12, 4, 4));
		spawnSettings.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.PIG, 10, 4, 4));
		spawnSettings.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.CHICKEN, 10, 4, 4));
		spawnSettings.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.COW, 8, 4, 4));
	}

	public static void addHashMobs(SpawnSettings.Builder spawnSettings) {
		spawnSettings.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.ARMADILLO, 4, 1, 2));
		addCaveMobs(spawnSettings);
		addMonsters(spawnSettings, 19, 1, 100);
		spawnSettings.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.HUSK, 80, 4, 4));
	}

	public static void addArboretumMobs(SpawnSettings.Builder spawnSettings) {
		addFarmAnimals(spawnSettings);
		spawnSettings.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.HORSE, 5, 2, 6));
		spawnSettings.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.DONKEY, 1, 1, 3));
		addStandardMobs(spawnSettings);
	}

	public static void addStandardMobs(SpawnSettings.Builder spawnSettings) {
		addCaveMobs(spawnSettings);
		addMonsters(spawnSettings, 95, 5, 100);
	}

	public static void addCaveMobs(SpawnSettings.Builder spawnSettings) {
		spawnSettings.spawn(SpawnGroup.AMBIENT, new SpawnSettings.SpawnEntry(EntityType.BAT, 10, 8, 8));
		spawnSettings.spawn(SpawnGroup.AMBIENT, new SpawnSettings.SpawnEntry(EntityType.BATATO, 13, 8, 16));
		spawnSettings.spawn(SpawnGroup.UNDERGROUND_WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.GLOW_SQUID, 10, 4, 6));
	}

	public static void addMonsters(SpawnSettings.Builder spawnSettings, int zombieWeight, int zombieVillagerWeight, int skeletonWeight) {
		spawnSettings.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SPIDER, 100, 4, 4));
		spawnSettings.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.ZOMBIE, zombieWeight, 4, 4));
		spawnSettings.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.POISONOUS_POTATO_ZOMBIE, 50, 1, 4));
		spawnSettings.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.ZOMBIE_VILLAGER, zombieVillagerWeight, 1, 1));
		spawnSettings.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SKELETON, skeletonWeight, 4, 4));
		spawnSettings.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.CREEPER, 100, 4, 4));
		spawnSettings.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SLIME, 10, 4, 4));
		spawnSettings.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.ENDERMAN, 10, 1, 4));
		spawnSettings.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.WITCH, 5, 1, 1));
	}

	public static Biome createHash(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		builder.creatureSpawnProbability(0.01F);
		addHashMobs(builder);
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		DefaultBiomeFeatures.addFossils(lookupBackedBuilder);
		addStandardFeatures(lookupBackedBuilder, true);
		DefaultBiomeFeatures.addDefaultFlowers(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultGrass(lookupBackedBuilder);
		lookupBackedBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_DEAD_BUSH_2_ALL_LEVELS);
		DefaultBiomeFeatures.addDefaultMushrooms(lookupBackedBuilder);
		lookupBackedBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.LEAF_PILE_HASH);
		lookupBackedBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.VENOMOUS_COLUMN_HASH);
		lookupBackedBuilder.feature(GenerationStep.Feature.SURFACE_STRUCTURES, MiscPlacedFeatures.HASH_WELL);
		return new Biome.Builder()
			.precipitation(false)
			.temperature(2.0F)
			.downfall(0.0F)
			.effects(
				new BiomeEffects.Builder()
					.waterColor(4187298)
					.waterFogColor(740644)
					.fogColor(9821879)
					.skyColor(getSkyColor(2.0F))
					.loopSound(SoundEvents.AMBIENT_HASH_LOOP)
					.music(MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_DESERT))
					.grassColor(6017107)
					.foliageColor(2199366)
					.build()
			)
			.spawnSettings(builder.build())
			.generationSettings(lookupBackedBuilder.build())
			.build();
	}

	public static Biome createFields(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		addFarmAnimals(builder);
		addStandardMobs(builder);
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		lookupBackedBuilder.feature(GenerationStep.Feature.STRONGHOLDS, VegetationPlacedFeatures.POTATO_FIELD);
		addStandardFeatures(lookupBackedBuilder, false);
		DefaultBiomeFeatures.addPlainsTallGrass(lookupBackedBuilder);
		DefaultBiomeFeatures.addPlainsFeatures(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultMushrooms(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultVegetation(lookupBackedBuilder);
		return new Biome.Builder()
			.precipitation(true)
			.temperature(0.8F)
			.downfall(0.4F)
			.effects(
				new BiomeEffects.Builder()
					.waterColor(4187298)
					.waterFogColor(740644)
					.fogColor(9821879)
					.skyColor(getSkyColor(0.8F))
					.loopSound(SoundEvents.AMBIENT_FIELDS_LOOP)
					.music(MUSIC)
					.grassColor(6017107)
					.foliageColor(2199366)
					.build()
			)
			.spawnSettings(builder.build())
			.generationSettings(lookupBackedBuilder.build())
			.build();
	}

	public static Biome createArboretum(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		addArboretumMobs(builder);
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.WOLF, 2, 4, 4));
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		lookupBackedBuilder.feature(GenerationStep.Feature.STRONGHOLDS, VegetationPlacedFeatures.PARK_LANE_SURFACE);
		lookupBackedBuilder.feature(GenerationStep.Feature.STRONGHOLDS, VegetationPlacedFeatures.PARK_LANE);
		addStandardFeatures(lookupBackedBuilder, false);
		lookupBackedBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.FLOWER_FOREST_FLOWERS);
		lookupBackedBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.ARBORETUM_TREES);
		DefaultBiomeFeatures.addDefaultFlowers(lookupBackedBuilder);
		DefaultBiomeFeatures.addForestGrass(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultMushrooms(lookupBackedBuilder);
		DefaultBiomeFeatures.addDefaultVegetation(lookupBackedBuilder);
		return new Biome.Builder()
			.precipitation(true)
			.temperature(0.9F)
			.downfall(0.5F)
			.effects(
				new BiomeEffects.Builder()
					.waterColor(4187298)
					.waterFogColor(740644)
					.fogColor(9821879)
					.skyColor(getSkyColor(0.7F))
					.loopSound(SoundEvents.AMBIENT_ARBORETUM_LOOP)
					.music(MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_FLOWER_FOREST))
					.grassColor(6017107)
					.build()
			)
			.spawnSettings(builder.build())
			.generationSettings(lookupBackedBuilder.build())
			.build();
	}

	public static Biome createWasteland(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SLIME, 5, 1, 2));
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.GIANT, 10, 1, 4));
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.WITHER_SKELETON, 5, 1, 1));
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.TOXIFIN, 25, 1, 4));
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		addStandardFeatures(lookupBackedBuilder, true);
		lookupBackedBuilder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, VegetationPlacedFeatures.BROWN_MUSHROOM_NETHER)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, VegetationPlacedFeatures.RED_MUSHROOM_NETHER)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, UndergroundPlacedFeatures.LARGE_POTATOSTONE)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, NetherPlacedFeatures.LARGE_POTATO_COLUMNS)
			.feature(GenerationStep.Feature.SURFACE_STRUCTURES, NetherPlacedFeatures.SMALL_DEBRIS_COLUMNS)
			.feature(GenerationStep.Feature.FLUID_SPRINGS, NetherPlacedFeatures.POISON_POOL)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, UndergroundPlacedFeatures.POTATO_LEAF)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, UndergroundPlacedFeatures.TWISTED_POTATO);
		return new Biome.Builder()
			.precipitation(true)
			.temperature(2.0F)
			.downfall(0.5F)
			.effects(
				new BiomeEffects.Builder()
					.waterColor(5105159)
					.waterFogColor(4056583)
					.fogColor(8157780)
					.grassColor(5105159)
					.foliageColor(4056583)
					.skyColor(getSkyColor(1.0F))
					.loopSound(SoundEvents.AMBIENT_WASTELAND_LOOP)
					.particleConfig(new BiomeParticleConfig(ParticleTypes.DRIPPING_HONEY, 0.001F))
					.build()
			)
			.spawnSettings(builder.build())
			.generationSettings(lookupBackedBuilder.build())
			.build();
	}

	public static Biome createCorruption(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		SpawnSettings.Builder builder = new SpawnSettings.Builder();
		addFarmAnimals(builder);
		builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.CAT, 40, 1, 1));
		addStandardMobs(builder);
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.WITCH, 100, 1, 1));
		builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.PHANTOM, 100, 1, 1));
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		addStandardFeatures(lookupBackedBuilder, true);
		DefaultBiomeFeatures.addDefaultMushrooms(lookupBackedBuilder);
		lookupBackedBuilder.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, VegetationPlacedFeatures.BROWN_MUSHROOM_NETHER)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, VegetationPlacedFeatures.RED_MUSHROOM_NETHER)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, NetherPlacedFeatures.CORRUPTED_BUDS)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, NetherPlacedFeatures.POTATO_SPROUTS)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, TreePlacedFeatures.MOTHER_POTATO_TREE)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, TreePlacedFeatures.POTATO_TREE_TALL)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, TreePlacedFeatures.POTATO_TREE);
		return new Biome.Builder()
			.precipitation(true)
			.temperature(2.0F)
			.downfall(1.0F)
			.effects(
				new BiomeEffects.Builder()
					.waterColor(1356889)
					.waterFogColor(1153876)
					.fogColor(1356889)
					.grassColor(1466676)
					.foliageColor(1153876)
					.skyColor(getSkyColor(0.0F))
					.particleConfig(new BiomeParticleConfig(ParticleTypes.WARPED_SPORE, 0.01428F))
					.loopSound(SoundEvents.AMBIENT_CORRUPTION_LOOP)
					.music(MusicType.createIngameMusic(SoundEvents.MUSIC_NETHER_WARPED_FOREST))
					.build()
			)
			.spawnSettings(builder.build())
			.generationSettings(lookupBackedBuilder.build())
			.build();
	}
}
