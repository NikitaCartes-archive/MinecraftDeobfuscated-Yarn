/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.noise;

import java.util.List;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.random.RandomDeriver;

public record NoiseRouter(DensityFunction barrierNoise, DensityFunction fluidLevelFloodednessNoise, DensityFunction fluidLevelSpreadNoise, DensityFunction lavaNoise, RandomDeriver aquiferPositionalRandomFactory, RandomDeriver oreVeinsPositionalRandomFactory, DensityFunction temperature, DensityFunction humidity, DensityFunction continents, DensityFunction erosion, DensityFunction depth, DensityFunction ridges, DensityFunction initialDensityWithoutJaggedness, DensityFunction finalDensity, DensityFunction veinToggle, DensityFunction veinRidged, DensityFunction veinGap, List<MultiNoiseUtil.NoiseHypercube> spawnTarget) {
}

