/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.feature.ConfiguredStructureFeatures;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public final class FrozenOceanBiome
extends Biome {
    protected static final OctaveSimplexNoiseSampler field_9487 = new OctaveSimplexNoiseSampler(new ChunkRandom(3456L), ImmutableList.of(Integer.valueOf(-2), Integer.valueOf(-1), Integer.valueOf(0)));

    public FrozenOceanBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(ConfiguredSurfaceBuilders.FROZEN_OCEAN).precipitation(Biome.Precipitation.SNOW).category(Biome.Category.OCEAN).depth(-1.0f).scale(0.1f).temperature(0.0f).downfall(0.5f).effects(new BiomeEffects.Builder().waterColor(3750089).waterFogColor(329011).fogColor(12638463).moodSound(BiomeMoodSound.CAVE).build()).parent(null));
        this.addStructureFeature(ConfiguredStructureFeatures.OCEAN_RUIN_COLD);
        DefaultBiomeFeatures.addOceanStructures(this);
        this.addStructureFeature(ConfiguredStructureFeatures.RUINED_PORTAL_OCEAN);
        DefaultBiomeFeatures.addOceanCarvers(this);
        DefaultBiomeFeatures.addDefaultLakes(this);
        DefaultBiomeFeatures.addIcebergs(this);
        DefaultBiomeFeatures.addDungeons(this);
        DefaultBiomeFeatures.addBlueIce(this);
        DefaultBiomeFeatures.addMineables(this);
        DefaultBiomeFeatures.addDefaultOres(this);
        DefaultBiomeFeatures.addDefaultDisks(this);
        DefaultBiomeFeatures.addWaterBiomeOakTrees(this);
        DefaultBiomeFeatures.addDefaultFlowers(this);
        DefaultBiomeFeatures.addDefaultGrass(this);
        DefaultBiomeFeatures.addDefaultMushrooms(this);
        DefaultBiomeFeatures.addDefaultVegetation(this);
        DefaultBiomeFeatures.addSprings(this);
        DefaultBiomeFeatures.addFrozenTopLayer(this);
        this.addSpawn(SpawnGroup.WATER_CREATURE, new Biome.SpawnEntry(EntityType.SQUID, 1, 1, 4));
        this.addSpawn(SpawnGroup.WATER_AMBIENT, new Biome.SpawnEntry(EntityType.SALMON, 15, 1, 5));
        this.addSpawn(SpawnGroup.CREATURE, new Biome.SpawnEntry(EntityType.POLAR_BEAR, 1, 1, 2));
        DefaultBiomeFeatures.addBatsAndMonsters(this);
        this.addSpawn(SpawnGroup.MONSTER, new Biome.SpawnEntry(EntityType.DROWNED, 5, 1, 1));
    }

    @Override
    protected float computeTemperature(BlockPos blockPos) {
        double h;
        double e;
        float f = this.getTemperature();
        double d = field_9487.sample((double)blockPos.getX() * 0.05, (double)blockPos.getZ() * 0.05, false) * 7.0;
        double g = d + (e = FOLIAGE_NOISE.sample((double)blockPos.getX() * 0.2, (double)blockPos.getZ() * 0.2, false));
        if (g < 0.3 && (h = FOLIAGE_NOISE.sample((double)blockPos.getX() * 0.09, (double)blockPos.getZ() * 0.09, false)) < 0.8) {
            f = 0.2f;
        }
        if (blockPos.getY() > 64) {
            float i = (float)(TEMPERATURE_NOISE.sample((float)blockPos.getX() / 8.0f, (float)blockPos.getZ() / 8.0f, false) * 4.0);
            return f - (i + (float)blockPos.getY() - 64.0f) * 0.05f / 30.0f;
        }
        return f;
    }
}

