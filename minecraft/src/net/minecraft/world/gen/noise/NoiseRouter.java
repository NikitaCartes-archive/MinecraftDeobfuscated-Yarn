package net.minecraft.world.gen.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.world.gen.densityfunction.DensityFunction;

public record NoiseRouter(
	DensityFunction barrierNoise,
	DensityFunction fluidLevelFloodednessNoise,
	DensityFunction fluidLevelSpreadNoise,
	DensityFunction lavaNoise,
	DensityFunction temperature,
	DensityFunction vegetation,
	DensityFunction continents,
	DensityFunction erosion,
	DensityFunction depth,
	DensityFunction ridges,
	DensityFunction initialDensityWithoutJaggedness,
	DensityFunction finalDensity,
	DensityFunction veinToggle,
	DensityFunction veinRidged,
	DensityFunction veinGap
) {
	public static final Codec<NoiseRouter> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					field("barrier", NoiseRouter::barrierNoise),
					field("fluid_level_floodedness", NoiseRouter::fluidLevelFloodednessNoise),
					field("fluid_level_spread", NoiseRouter::fluidLevelSpreadNoise),
					field("lava", NoiseRouter::lavaNoise),
					field("temperature", NoiseRouter::temperature),
					field("vegetation", NoiseRouter::vegetation),
					field("continents", NoiseRouter::continents),
					field("erosion", NoiseRouter::erosion),
					field("depth", NoiseRouter::depth),
					field("ridges", NoiseRouter::ridges),
					field("initial_density_without_jaggedness", NoiseRouter::initialDensityWithoutJaggedness),
					field("final_density", NoiseRouter::finalDensity),
					field("vein_toggle", NoiseRouter::veinToggle),
					field("vein_ridged", NoiseRouter::veinRidged),
					field("vein_gap", NoiseRouter::veinGap)
				)
				.apply(instance, NoiseRouter::new)
	);

	private static RecordCodecBuilder<NoiseRouter, DensityFunction> field(String name, Function<NoiseRouter, DensityFunction> getter) {
		return DensityFunction.FUNCTION_CODEC.fieldOf(name).forGetter(getter);
	}

	public NoiseRouter apply(DensityFunction.DensityFunctionVisitor visitor) {
		return new NoiseRouter(
			this.barrierNoise.apply(visitor),
			this.fluidLevelFloodednessNoise.apply(visitor),
			this.fluidLevelSpreadNoise.apply(visitor),
			this.lavaNoise.apply(visitor),
			this.temperature.apply(visitor),
			this.vegetation.apply(visitor),
			this.continents.apply(visitor),
			this.erosion.apply(visitor),
			this.depth.apply(visitor),
			this.ridges.apply(visitor),
			this.initialDensityWithoutJaggedness.apply(visitor),
			this.finalDensity.apply(visitor),
			this.veinToggle.apply(visitor),
			this.veinRidged.apply(visitor),
			this.veinGap.apply(visitor)
		);
	}
}
