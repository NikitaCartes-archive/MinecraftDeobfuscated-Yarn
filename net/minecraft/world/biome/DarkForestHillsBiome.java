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

public final class DarkForestHillsBiome
extends Biome {
    public DarkForestHillsBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(ConfiguredSurfaceBuilders.GRASS).precipitation(Biome.Precipitation.RAIN).category(Biome.Category.FOREST).depth(0.2f).scale(0.4f).temperature(0.7f).downfall(0.8f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent("dark_forest"));
        this.addStructureFeature(ConfiguredStructureFeatures.MANSION);
        DefaultBiomeFeatures.addDefaultUndergroundStructures(this);
        this.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL);
        DefaultBiomeFeatures.addLandCarvers(this);
        DefaultBiomeFeatures.addDefaultLakes(this);
        DefaultBiomeFeatures.addDungeons(this);
        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, ConfiguredFeatures.DARK_FOREST_VEGETATION_RED);
        DefaultBiomeFeatures.addForestFlowers(this);
        DefaultBiomeFeatures.addMineables(this);
        DefaultBiomeFeatures.addDefaultOres(this);
        DefaultBiomeFeatures.addDefaultDisks(this);
        DefaultBiomeFeatures.addDefaultFlowers(this);
        DefaultBiomeFeatures.addForestGrass(this);
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        DefaultBiomeFeatures.addDefaultVegetation(this);
        DefaultBiomeFeatures.addSprings(this);
        DefaultBiomeFeatures.addFrozenTopLayer(this);
        DefaultBiomeFeatures.addFarmAnimals(this);
        DefaultBiomeFeatures.addBatsAndMonsters(this);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getGrassColorAt(double x, double z) {
        int i = super.getGrassColorAt(x, z);
        return (i & 0xFEFEFE) + 2634762 >> 1;
    }
}

