/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public final class ModifiedWoodedBadlandsPlateauBiome
extends Biome {
    public ModifiedWoodedBadlandsPlateauBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(ConfiguredSurfaceBuilders.WOODED_BADLANDS).precipitation(Biome.Precipitation.NONE).category(Biome.Category.MESA).depth(0.45f).scale(0.3f).temperature(2.0f).downfall(0.0f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent("wooded_badlands_plateau"));
        DefaultBiomeFeatures.addBadlandsUndergroundStructures(this);
        this.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_MOUNTAIN);
        DefaultBiomeFeatures.addLandCarvers(this);
        DefaultBiomeFeatures.addDefaultLakes(this);
        DefaultBiomeFeatures.addDungeons(this);
        DefaultBiomeFeatures.addMineables(this);
        DefaultBiomeFeatures.addDefaultOres(this);
        DefaultBiomeFeatures.addExtraGoldOre(this);
        DefaultBiomeFeatures.addDefaultDisks(this);
        DefaultBiomeFeatures.addBadlandsPlateauTrees(this);
        DefaultBiomeFeatures.addBadlandsGrass(this);
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        DefaultBiomeFeatures.addBadlandsVegetation(this);
        DefaultBiomeFeatures.addSprings(this);
        DefaultBiomeFeatures.addFrozenTopLayer(this);
        DefaultBiomeFeatures.addBatsAndMonsters(this);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getFoliageColor() {
        return 10387789;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getGrassColorAt(double x, double z) {
        return 9470285;
    }
}

