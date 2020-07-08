/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public final class SwampHillsBiome
extends Biome {
    public SwampHillsBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(ConfiguredSurfaceBuilders.SWAMP).precipitation(Biome.Precipitation.RAIN).category(Biome.Category.SWAMP).depth(-0.1f).scale(0.3f).temperature(0.8f).downfall(0.9f).effects(new BiomeEffects.Builder().waterColor(6388580).waterFogColor(2302743).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent("swamp"));
        this.addStructureFeature(ConfiguredStructureFeatures.MINESHAFT);
        this.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_SWAMP);
        DefaultBiomeFeatures.addLandCarvers(this);
        DefaultBiomeFeatures.addDefaultLakes(this);
        DefaultBiomeFeatures.addDungeons(this);
        DefaultBiomeFeatures.addMineables(this);
        DefaultBiomeFeatures.addDefaultOres(this);
        DefaultBiomeFeatures.addClay(this);
        DefaultBiomeFeatures.addSwampFeatures(this);
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        DefaultBiomeFeatures.addSwampVegetation(this);
        DefaultBiomeFeatures.addSprings(this);
        DefaultBiomeFeatures.addFossils(this);
        DefaultBiomeFeatures.addFrozenTopLayer(this);
        DefaultBiomeFeatures.addFarmAnimals(this);
        DefaultBiomeFeatures.addBatsAndMonsters(this);
        this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.SLIME, 1, 1, 1));
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getGrassColorAt(double x, double z) {
        double d = FOLIAGE_NOISE.sample(x * 0.0225, z * 0.0225, false);
        if (d < -0.1) {
            return 5011004;
        }
        return 6975545;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getFoliageColor() {
        return 6975545;
    }
}

