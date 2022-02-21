package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.world.gen.densityfunction.DensityFunction;

public record class_7056(
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
	public static final Codec<class_7056> field_37137 = RecordCodecBuilder.create(
		instance -> instance.group(
					method_41125("barrier", class_7056::barrierNoise),
					method_41125("fluid_level_floodedness", class_7056::fluidLevelFloodednessNoise),
					method_41125("fluid_level_spread", class_7056::fluidLevelSpreadNoise),
					method_41125("lava", class_7056::lavaNoise),
					method_41125("temperature", class_7056::temperature),
					method_41125("vegetation", class_7056::vegetation),
					method_41125("continents", class_7056::continents),
					method_41125("erosion", class_7056::erosion),
					method_41125("depth", class_7056::depth),
					method_41125("ridges", class_7056::ridges),
					method_41125("initial_density_without_jaggedness", class_7056::initialDensityWithoutJaggedness),
					method_41125("final_density", class_7056::finalDensity),
					method_41125("vein_toggle", class_7056::veinToggle),
					method_41125("vein_ridged", class_7056::veinRidged),
					method_41125("vein_gap", class_7056::veinGap)
				)
				.apply(instance, class_7056::new)
	);

	private static RecordCodecBuilder<class_7056, DensityFunction> method_41125(String string, Function<class_7056, DensityFunction> function) {
		return DensityFunction.field_37059.fieldOf(string).forGetter(function);
	}

	public class_7056 method_41124(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
		return new class_7056(
			this.barrierNoise.method_40469(densityFunctionVisitor),
			this.fluidLevelFloodednessNoise.method_40469(densityFunctionVisitor),
			this.fluidLevelSpreadNoise.method_40469(densityFunctionVisitor),
			this.lavaNoise.method_40469(densityFunctionVisitor),
			this.temperature.method_40469(densityFunctionVisitor),
			this.vegetation.method_40469(densityFunctionVisitor),
			this.continents.method_40469(densityFunctionVisitor),
			this.erosion.method_40469(densityFunctionVisitor),
			this.depth.method_40469(densityFunctionVisitor),
			this.ridges.method_40469(densityFunctionVisitor),
			this.initialDensityWithoutJaggedness.method_40469(densityFunctionVisitor),
			this.finalDensity.method_40469(densityFunctionVisitor),
			this.veinToggle.method_40469(densityFunctionVisitor),
			this.veinRidged.method_40469(densityFunctionVisitor),
			this.veinGap.method_40469(densityFunctionVisitor)
		);
	}
}
