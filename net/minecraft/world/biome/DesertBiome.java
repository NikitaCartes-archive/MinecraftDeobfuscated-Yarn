/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public final class DesertBiome
extends Biome {
    public DesertBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(ConfiguredSurfaceBuilders.DESERT).precipitation(Biome.Precipitation.NONE).category(Biome.Category.DESERT).depth(0.125f).scale(0.05f).temperature(2.0f).downfall(0.0f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(null));
        this.addStructureFeature(ConfiguredStructureFeatures.VILLAGE_DESERT);
        this.addStructureFeature(ConfiguredStructureFeatures.PILLAGER_OUTPOST);
        this.addStructureFeature(ConfiguredStructureFeatures.DESERT_PYRAMID);
        DefaultBiomeFeatures.addFossils(this);
        DefaultBiomeFeatures.addDefaultUndergroundStructures(this);
        this.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_DESERT);
        DefaultBiomeFeatures.addLandCarvers(this);
        DefaultBiomeFeatures.addDesertLakes(this);
        DefaultBiomeFeatures.addDungeons(this);
        DefaultBiomeFeatures.addMineables(this);
        DefaultBiomeFeatures.addDefaultOres(this);
        DefaultBiomeFeatures.addDefaultDisks(this);
        DefaultBiomeFeatures.addDefaultFlowers(this);
        DefaultBiomeFeatures.addDefaultGrass(this);
        DefaultBiomeFeatures.addDesertDeadBushes(this);
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        DefaultBiomeFeatures.addDesertVegetation(this);
        DefaultBiomeFeatures.addSprings(this);
        DefaultBiomeFeatures.addDesertFeatures(this);
        DefaultBiomeFeatures.addFrozenTopLayer(this);
        DefaultBiomeFeatures.addDesertMobs(this);
    }
}

