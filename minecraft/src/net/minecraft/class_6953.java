package net.minecraft;

import java.util.List;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;
import net.minecraft.world.gen.random.RandomDeriver;

public record class_6953(
	class_6910 barrierNoise,
	class_6910 fluidLevelFloodednessNoise,
	class_6910 fluidLevelSpreadNoise,
	class_6910 lavaNoise,
	RandomDeriver aquiferPositionalRandomFactory,
	RandomDeriver oreVeinsPositionalRandomFactory,
	class_6910 temperature,
	class_6910 humidity,
	class_6910 continentalness,
	class_6910 erosion,
	class_6910 depth,
	class_6910 weirdness,
	class_6910 initialDensityNoJaggedness,
	class_6910 fullNoise,
	class_6910 veinToggle,
	class_6910 veinRidged,
	class_6910 veinGap,
	List<MultiNoiseUtil.NoiseHypercube> spawnTarget
) {
}
