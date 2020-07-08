/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public final class IceSpikesBiome
extends Biome {
    public IceSpikesBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(ConfiguredSurfaceBuilders.ICE_SPIKES).precipitation(Biome.Precipitation.SNOW).category(Biome.Category.ICY).depth(0.425f).scale(0.45000002f).temperature(0.0f).downfall(0.5f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent("snowy_tundra"));
        DefaultBiomeFeatures.addDefaultUndergroundStructures(this);
        this.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
        DefaultBiomeFeatures.addLandCarvers(this);
        DefaultBiomeFeatures.addDefaultLakes(this);
        DefaultBiomeFeatures.addDungeons(this);
        this.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.ICE_SPIKE);
        this.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, ConfiguredFeatures.ICE_PATCH);
        DefaultBiomeFeatures.addMineables(this);
        DefaultBiomeFeatures.addDefaultOres(this);
        DefaultBiomeFeatures.addDefaultDisks(this);
        DefaultBiomeFeatures.addSnowySpruceTrees(this);
        DefaultBiomeFeatures.addDefaultFlowers(this);
        DefaultBiomeFeatures.addDefaultGrass(this);
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        DefaultBiomeFeatures.addDefaultVegetation(this);
        DefaultBiomeFeatures.addSprings(this);
        DefaultBiomeFeatures.addFrozenTopLayer(this);
        DefaultBiomeFeatures.addSnowyMobs(this);
    }

    @Override
    public float getMaxSpawnChance() {
        return 0.07f;
    }
}

