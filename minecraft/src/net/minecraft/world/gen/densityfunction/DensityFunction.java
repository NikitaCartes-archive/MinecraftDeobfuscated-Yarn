package net.minecraft.world.gen.densityfunction;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.chunk.Blender;

public interface DensityFunction {
	Codec<DensityFunction> CODEC = DensityFunctionTypes.CODEC;
	Codec<RegistryEntry<DensityFunction>> REGISTRY_ENTRY_CODEC = RegistryElementCodec.of(Registry.DENSITY_FUNCTION_KEY, CODEC);
	Codec<DensityFunction> FUNCTION_CODEC = REGISTRY_ENTRY_CODEC.xmap(
		DensityFunctionTypes.RegistryEntryHolder::new,
		function -> (RegistryEntry)(function instanceof DensityFunctionTypes.RegistryEntryHolder registryEntryHolder
				? registryEntryHolder.function()
				: new RegistryEntry.Direct<>(function))
	);

	double sample(DensityFunction.NoisePos pos);

	void applyEach(double[] densities, DensityFunction.EachApplier applier);

	DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor);

	double minValue();

	double maxValue();

	CodecHolder<? extends DensityFunction> getCodecHolder();

	default DensityFunction clamp(double min, double max) {
		return new DensityFunctionTypes.Clamp(this, min, max);
	}

	default DensityFunction abs() {
		return DensityFunctionTypes.unary(this, DensityFunctionTypes.UnaryOperation.Type.ABS);
	}

	default DensityFunction square() {
		return DensityFunctionTypes.unary(this, DensityFunctionTypes.UnaryOperation.Type.SQUARE);
	}

	default DensityFunction cube() {
		return DensityFunctionTypes.unary(this, DensityFunctionTypes.UnaryOperation.Type.CUBE);
	}

	default DensityFunction halfNegative() {
		return DensityFunctionTypes.unary(this, DensityFunctionTypes.UnaryOperation.Type.HALF_NEGATIVE);
	}

	default DensityFunction quarterNegative() {
		return DensityFunctionTypes.unary(this, DensityFunctionTypes.UnaryOperation.Type.QUARTER_NEGATIVE);
	}

	default DensityFunction squeeze() {
		return DensityFunctionTypes.unary(this, DensityFunctionTypes.UnaryOperation.Type.SQUEEZE);
	}

	public interface Base extends DensityFunction {
		@Override
		default void applyEach(double[] densities, DensityFunction.EachApplier applier) {
			applier.applyEach(densities, this);
		}

		@Override
		default DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
			return visitor.apply(this);
		}
	}

	public interface DensityFunctionVisitor {
		DensityFunction apply(DensityFunction densityFunction);

		default DensityFunction.Noise apply(DensityFunction.Noise noiseDensityFunction) {
			return noiseDensityFunction;
		}
	}

	public interface EachApplier {
		DensityFunction.NoisePos getPosAt(int index);

		void applyEach(double[] densities, DensityFunction densityFunction);
	}

	public static record Noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, @Nullable DoublePerlinNoiseSampler noise) {
		public static final Codec<DensityFunction.Noise> CODEC = DoublePerlinNoiseSampler.NoiseParameters.REGISTRY_ENTRY_CODEC
			.xmap(noiseData -> new DensityFunction.Noise(noiseData, null), DensityFunction.Noise::noiseData);

		public Noise(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData) {
			this(noiseData, null);
		}

		public double sample(double x, double y, double z) {
			return this.noise == null ? 0.0 : this.noise.sample(x, y, z);
		}

		public double getMaxValue() {
			return this.noise == null ? 2.0 : this.noise.getMaxValue();
		}
	}

	public interface NoisePos {
		int blockX();

		int blockY();

		int blockZ();

		default Blender getBlender() {
			return Blender.getNoBlending();
		}
	}

	public static record UnblendedNoisePos(int blockX, int blockY, int blockZ) implements DensityFunction.NoisePos {
	}
}
