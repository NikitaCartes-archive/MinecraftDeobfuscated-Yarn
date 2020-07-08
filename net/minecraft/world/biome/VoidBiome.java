/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public final class VoidBiome
extends Biome {
    public VoidBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(ConfiguredSurfaceBuilders.NOPE).precipitation(Biome.Precipitation.NONE).category(Biome.Category.NONE).depth(0.1f).scale(0.2f).temperature(0.5f).downfall(0.5f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(null));
        this.addFeature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, ConfiguredFeatures.VOID_START_PLATFORM);
    }
}

