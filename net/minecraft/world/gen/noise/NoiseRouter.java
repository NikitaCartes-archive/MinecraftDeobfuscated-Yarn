/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.noise;

import java.util.List;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.noise.NoiseType;
import net.minecraft.world.gen.random.RandomDeriver;

public record NoiseRouter(NoiseType barrierNoise, NoiseType fluidLevelFloodednessNoise, NoiseType fluidLevelSpreadNoise, NoiseType lavaNoise, RandomDeriver aquiferPositionalRandomFactory, RandomDeriver oreVeinsPositionalRandomFactory, NoiseType temperature, NoiseType humidity, NoiseType continents, NoiseType erosion, NoiseType depth, NoiseType ridges, NoiseType initialDensityWithoutJaggedness, NoiseType finalDensity, NoiseType veinToggle, NoiseType veinRidged, NoiseType veinGap, List<MultiNoiseUtil.NoiseHypercube> spawnTarget) {
}

