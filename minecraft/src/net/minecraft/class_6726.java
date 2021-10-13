package net.minecraft;

import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;

public class class_6726 {
	private static Biome method_39141(GenerationSettings.Builder builder) {
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

	public static Biome method_39140() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder();
		return method_39141(builder);
	}

	public static Biome method_39142() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder().feature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.END_SPIKE);
		return method_39141(builder);
	}

	public static Biome method_39143() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder();
		return method_39141(builder);
	}

	public static Biome method_39144() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder()
			.feature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.END_GATEWAY)
			.feature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.CHORUS_PLANT);
		return method_39141(builder);
	}

	public static Biome method_39145() {
		GenerationSettings.Builder builder = new GenerationSettings.Builder().feature(GenerationStep.Feature.RAW_GENERATION, ConfiguredFeatures.END_ISLAND_DECORATED);
		return method_39141(builder);
	}
}
