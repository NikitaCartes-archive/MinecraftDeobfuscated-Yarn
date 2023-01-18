package net.minecraft.world.biome;

import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.EndPlacedFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;

public class TheEndBiomeCreator {
	private static Biome createEndBiome(GenerationSettings.LookupBackedBuilder builder) {
		SpawnSettings.Builder builder2 = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addEndMobs(builder2);
		return new Biome.Builder()
			.precipitation(false)
			.temperature(0.5F)
			.downfall(0.5F)
			.effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(10518688).skyColor(0).moodSound(BiomeMoodSound.CAVE).build())
			.spawnSettings(builder2.build())
			.generationSettings(builder.build())
			.build();
	}

	public static Biome createEndBarrens(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		return createEndBiome(lookupBackedBuilder);
	}

	public static Biome createTheEnd(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup)
			.feature(GenerationStep.Feature.SURFACE_STRUCTURES, EndPlacedFeatures.END_SPIKE);
		return createEndBiome(lookupBackedBuilder);
	}

	public static Biome createEndMidlands(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup);
		return createEndBiome(lookupBackedBuilder);
	}

	public static Biome createEndHighlands(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup)
			.feature(GenerationStep.Feature.SURFACE_STRUCTURES, EndPlacedFeatures.END_GATEWAY_RETURN)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, EndPlacedFeatures.CHORUS_PLANT);
		return createEndBiome(lookupBackedBuilder);
	}

	public static Biome createSmallEndIslands(RegistryEntryLookup<PlacedFeature> featureLookup, RegistryEntryLookup<ConfiguredCarver<?>> carverLookup) {
		GenerationSettings.LookupBackedBuilder lookupBackedBuilder = new GenerationSettings.LookupBackedBuilder(featureLookup, carverLookup)
			.feature(GenerationStep.Feature.RAW_GENERATION, EndPlacedFeatures.END_ISLAND_DECORATED);
		return createEndBiome(lookupBackedBuilder);
	}
}
