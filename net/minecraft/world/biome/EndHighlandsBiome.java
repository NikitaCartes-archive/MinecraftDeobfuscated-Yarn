/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public class EndHighlandsBiome
extends Biome {
    public EndHighlandsBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(ConfiguredSurfaceBuilders.END).precipitation(Biome.Precipitation.NONE).category(Biome.Category.THEEND).depth(0.1f).scale(0.2f).temperature(0.5f).downfall(0.5f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(0xA080A0).moodSound(BiomeMoodSound.CAVE).build()).parent(null));
        this.addStructureFeature(ConfiguredStructureFeatures.END_CITY);
        this.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.END_GATEWAY);
        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.CHORUS_PLANT);
        DefaultBiomeFeatures.addEndMobs(this);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getSkyColor() {
        return 0;
    }
}

