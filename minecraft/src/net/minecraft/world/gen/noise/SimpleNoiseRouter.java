package net.minecraft.world.gen.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.world.gen.densityfunction.DensityFunction;

public record SimpleNoiseRouter(
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
	public static final Codec<SimpleNoiseRouter> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					method_41125("barrier", SimpleNoiseRouter::barrierNoise),
					method_41125("fluid_level_floodedness", SimpleNoiseRouter::fluidLevelFloodednessNoise),
					method_41125("fluid_level_spread", SimpleNoiseRouter::fluidLevelSpreadNoise),
					method_41125("lava", SimpleNoiseRouter::lavaNoise),
					method_41125("temperature", SimpleNoiseRouter::temperature),
					method_41125("vegetation", SimpleNoiseRouter::vegetation),
					method_41125("continents", SimpleNoiseRouter::continents),
					method_41125("erosion", SimpleNoiseRouter::erosion),
					method_41125("depth", SimpleNoiseRouter::depth),
					method_41125("ridges", SimpleNoiseRouter::ridges),
					method_41125("initial_density_without_jaggedness", SimpleNoiseRouter::initialDensityWithoutJaggedness),
					method_41125("final_density", SimpleNoiseRouter::finalDensity),
					method_41125("vein_toggle", SimpleNoiseRouter::veinToggle),
					method_41125("vein_ridged", SimpleNoiseRouter::veinRidged),
					method_41125("vein_gap", SimpleNoiseRouter::veinGap)
				)
				.apply(instance, SimpleNoiseRouter::new)
	);

	private static RecordCodecBuilder<SimpleNoiseRouter, DensityFunction> method_41125(String string, Function<SimpleNoiseRouter, DensityFunction> function) {
		return DensityFunction.field_37059.fieldOf(string).forGetter(function);
	}

	public SimpleNoiseRouter apply(DensityFunction.DensityFunctionVisitor visitor) {
		return new SimpleNoiseRouter(
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
