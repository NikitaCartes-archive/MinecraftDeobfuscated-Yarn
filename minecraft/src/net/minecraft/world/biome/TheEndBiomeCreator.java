package net.minecraft.world.biome;

import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;

public class TheEndBiomeCreator {
	private static Biome createEndBiome(GenerationSettings.Builder builder) {
		SpawnSettings.Builder builder2 = new SpawnSettings.Builder();
		DefaultBiomeFeatures.addEndMobs(builder2);
		return new Biome.Builder()
			.precipitation(Biome.Precipitation.NONE)
			.category(Biome.Category.THEEND)
			.temperature(0.5F)
			.downfall(0.5F)
			.effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(10518688).skyColor(0).moodSound(BiomeMoodSound.CAVE).build())
			.spawnSettings(builder2.build())
			.generationSettings(builder.build())
			.build();
	}

	public static Biome createEndBarrens() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder();
		return createEndBiome(builder);
	}

	public static Biome createTheEnd() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder().feature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.END_SPIKE);
		return createEndBiome(builder);
	}

	public static Biome createEndMidlands() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder();
		return createEndBiome(builder);
	}

	public static Biome createEndHighlands() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
			.feature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.END_GATEWAY)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.CHORUS_PLANT);
		return createEndBiome(builder);
	}

	public static Biome createSmallEndIslands() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder().feature(GenerationStep.Feature.RAW_GENERATION, ConfiguredFeatures.END_ISLAND_DECORATED);
		return createEndBiome(builder);
	}
}
