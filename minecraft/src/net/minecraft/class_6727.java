package net.minecraft;

import net.minecraft.client.sound.MusicType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.BiomeAdditionsSound;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.BiomeParticleConfig;
import net.minecraft.world.biome.DefaultBiomeCreator;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;

public class class_6727 {
	public static Biome method_39146() {
		SpawnSettings spawnSettings = new SpawnSettings.Builder()
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.GHAST, 50, 4, 4))
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.ZOMBIFIED_PIGLIN, 100, 4, 4))
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.MAGMA_CUBE, 2, 4, 4))
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.ENDERMAN, 1, 4, 4))
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.PIGLIN, 15, 4, 4))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.STRIDER, 60, 1, 2))
			.build();
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
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
			.temperature(2.0F)
			.downfall(0.0F)
			.effects(
				new BiomeEffects.Builder()
					.waterColor(4159204)
					.waterFogColor(329011)
					.fogColor(3344392)
					.skyColor(DefaultBiomeCreator.getSkyColor(2.0F))
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

	public static Biome method_39147() {
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
			.carver(GenerationStep.Carver.AIR, ConfiguredCarvers.NETHER_CAVE)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.SPRING_LAVA)
			.feature(GenerationStep.Feature.LOCAL_MODIFICATIONS, ConfiguredFeatures.BASALT_PILLAR)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_OPEN)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_FIRE)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_SOUL_FIRE)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE_EXTRA)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.GLOWSTONE)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.PATCH_CRIMSON_ROOTS)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_MAGMA)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.SPRING_CLOSED)
			.feature(GenerationStep.Feature.UNDERGROUND_DECORATION, ConfiguredFeatures.ORE_SOUL_SAND);
		DefaultBiomeFeatures.addNetherMineables(builder);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.NONE)
			.category(Biome.Category.NETHER)
			.temperature(2.0F)
			.downfall(0.0F)
			.effects(
				new BiomeEffects.Builder()
					.waterColor(4159204)
					.waterFogColor(329011)
					.fogColor(1787717)
					.skyColor(DefaultBiomeCreator.getSkyColor(2.0F))
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

	public static Biome method_39148() {
		SpawnSettings spawnSettings = new SpawnSettings.Builder()
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.GHAST, 40, 1, 1))
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.MAGMA_CUBE, 100, 2, 5))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.STRIDER, 60, 1, 2))
			.build();
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
			.carver(GenerationStep.Carver.AIR, ConfiguredCarvers.NETHER_CAVE)
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
			.temperature(2.0F)
			.downfall(0.0F)
			.effects(
				new BiomeEffects.Builder()
					.waterColor(4159204)
					.waterFogColor(329011)
					.fogColor(6840176)
					.skyColor(DefaultBiomeCreator.getSkyColor(2.0F))
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

	public static Biome method_39149() {
		SpawnSettings spawnSettings = new SpawnSettings.Builder()
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.ZOMBIFIED_PIGLIN, 1, 2, 4))
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.HOGLIN, 9, 3, 4))
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.PIGLIN, 5, 3, 4))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.STRIDER, 60, 1, 2))
			.build();
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
			.carver(GenerationStep.Carver.AIR, ConfiguredCarvers.NETHER_CAVE)
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
			.temperature(2.0F)
			.downfall(0.0F)
			.effects(
				new BiomeEffects.Builder()
					.waterColor(4159204)
					.waterFogColor(329011)
					.fogColor(3343107)
					.skyColor(DefaultBiomeCreator.getSkyColor(2.0F))
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

	public static Biome method_39150() {
		SpawnSettings spawnSettings = new SpawnSettings.Builder()
			.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.ENDERMAN, 1, 4, 4))
			.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.STRIDER, 60, 1, 2))
			.spawnCost(EntityType.ENDERMAN, 1.0, 0.12)
			.build();
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
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
			.temperature(2.0F)
			.downfall(0.0F)
			.effects(
				new BiomeEffects.Builder()
					.waterColor(4159204)
					.waterFogColor(329011)
					.fogColor(1705242)
					.skyColor(DefaultBiomeCreator.getSkyColor(2.0F))
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
