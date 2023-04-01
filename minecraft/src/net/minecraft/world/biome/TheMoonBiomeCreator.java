package net.minecraft.world.biome;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.UndergroundPlacedFeatures;

public class TheMoonBiomeCreator {
	private static Biome createMoonBiome(GenerationSettings.LookupBackedBuilder builder) {
		SpawnSettings.Builder builder2 = new SpawnSettings.Builder().spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.MOON_COW, 8, 1, 5));
		builder2.creatureSpawnProbability(0.07F);
		return new Biome.Builder()
			.precipitation(false)
			.temperature(-0.5F)
			.downfall(0.5F)
			.effects(
				new BiomeEffects.Builder()
					.waterColor(0)
					.waterFogColor(16777215)
					.fogColor(10518688)
					.skyColor(0)
					.foliageColor(16777215)
					.grassColor(6908265)
					.particleConfig(new BiomeParticleConfig(ParticleTypes.WHITE_ASH, 0.001F))
					.moodSound(BiomeMoodSound.CAVE)
					.build()
			)
			.spawnSettings(builder2.build())
			.generationSettings(builder.build())
			.build();
	}

	public static Biome createTheMoon(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		lookupBackedBuilder.feature(GenerationStep.Feature.RAW_GENERATION, UndergroundPlacedFeatures.CRATER_MEGA);
		lookupBackedBuilder.feature(GenerationStep.Feature.RAW_GENERATION, UndergroundPlacedFeatures.CRATER_LARGE);
		lookupBackedBuilder.feature(GenerationStep.Feature.RAW_GENERATION, UndergroundPlacedFeatures.CRATER_SMALL);
		lookupBackedBuilder.feature(GenerationStep.Feature.VEGETAL_DECORATION, UndergroundPlacedFeatures.LUNAR_BASE);
		return createMoonBiome(lookupBackedBuilder);
	}
}
