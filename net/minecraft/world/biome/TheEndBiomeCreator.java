/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.EndPlacedFeatures;

public class TheEndBiomeCreator {
    private static Biome createEndBiome(GenerationSettings.Builder builder) {
        SpawnSettings.Builder builder2 = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addEndMobs(builder2);
        return new Biome.Builder().precipitation(Biome.Precipitation.NONE).temperature(0.5f).downfall(0.5f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(0xA080A0).skyColor(0).moodSound(BiomeMoodSound.CAVE).build()).spawnSettings(builder2.build()).generationSettings(builder.build()).build();
    }

    public static Biome createEndBarrens() {
        GenerationSettings.Builder builder = new GenerationSettings.Builder();
        return TheEndBiomeCreator.createEndBiome(builder);
    }

    public static Biome createTheEnd() {
        GenerationSettings.Builder builder = new GenerationSettings.Builder().feature(GenerationStep.Feature.SURFACE_STRUCTURES, EndPlacedFeatures.END_SPIKE);
        return TheEndBiomeCreator.createEndBiome(builder);
    }

    public static Biome createEndMidlands() {
        GenerationSettings.Builder builder = new GenerationSettings.Builder();
        return TheEndBiomeCreator.createEndBiome(builder);
    }

    public static Biome createEndHighlands() {
        GenerationSettings.Builder builder = new GenerationSettings.Builder().feature(GenerationStep.Feature.SURFACE_STRUCTURES, EndPlacedFeatures.END_GATEWAY_RETURN).feature(GenerationStep.Feature.VEGETAL_DECORATION, EndPlacedFeatures.CHORUS_PLANT);
        return TheEndBiomeCreator.createEndBiome(builder);
    }

    public static Biome createSmallEndIslands() {
        GenerationSettings.Builder builder = new GenerationSettings.Builder().feature(GenerationStep.Feature.RAW_GENERATION, EndPlacedFeatures.END_ISLAND_DECORATED);
        return TheEndBiomeCreator.createEndBiome(builder);
    }
}

