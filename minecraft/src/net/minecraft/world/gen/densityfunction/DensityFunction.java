package net.minecraft.world.gen.densityfunction;

import com.mojang.serialization.Codec;
import javax.annotation.Nullable;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.gen.chunk.Blender;

/**
 * Represents a function that maps from a block position to a density value.
 * 
 * <p>It can be defined in code or in data packs by using pre-defined function types
 * like constant values or {@code add}, which in turn use other density functions
 * to define their operands.
 */
public interface DensityFunction {
	Codec<DensityFunction> CODEC = DensityFunctionTypes.CODEC;
	Codec<RegistryEntry<DensityFunction>> REGISTRY_ENTRY_CODEC = RegistryElementCodec.of(RegistryKeys.DENSITY_FUNCTION, CODEC);
	Codec<DensityFunction> FUNCTION_CODEC = REGISTRY_ENTRY_CODEC.xmap(
		DensityFunctionTypes.RegistryEntryHolder::new,
		function -> (RegistryEntry)(function instanceof DensityFunctionTypes.RegistryEntryHolder registryEntryHolder
				? registryEntryHolder.function()
				: new RegistryEntry.Direct<>(function))
	);

	/**
	 * {@return the density value for the given block position}
	 * 
	 * @param pos the block position
	 */
	double sample(DensityFunction.NoisePos pos);

	/**
	 * Fills an array of densities using {@code this} density function and
	 * the {@link EachApplier}.
	 * 
	 * @param densities the array of densities to fill, like a buffer or a cache
	 * @param applier the {@code EachApplier} to use. It has a method for filling the array, as well as to get a block position for an index
	 */
	void fill(double[] densities, DensityFunction.EachApplier applier);

	/**
	 * Applies the visitor to every child density function and {@code this}.
	 * 
	 * @return the resulting density function
	 * 
	 * @param visitor the visitor that should be applied to this density function
	 */
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

	/**
	 * Represents a density function that has no other density functions as an argument,
	 * and provides default implementations of {@link #fill} and
	 * {@link #apply(DensityFunctionVisitor)} for this case.
	 */
	public interface Base extends DensityFunction {
		@Override
		default void fill(double[] densities, DensityFunction.EachApplier applier) {
			applier.fill(densities, this);
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

	/**
	 * {@code EachApplier} is used to fill an array of densities, like a density buffer
	 * or cache, with values from a density function.
	 * 
	 * <p>This exists because {@link net.minecraft.world.gen.chunk.ChunkNoiseSampler ChunkNoiseSampler}
	 * uses itself as the block position passed to density functions and needs to set the
	 * position fields correctly before calling {@code sample}, as well as setting fields
	 * for the implementation of caches.
	 */
	public interface EachApplier {
		/**
		 * {@return the block position at a specific index of the density array}
		 * 
		 * <p>If you want to manually iterate the array and call {@link DensityFunction#sample}
		 * to set the individual elements in an implementation of {@link DensityFunction#fill},
		 * this method can be used to get the block position required for that.
		 * 
		 * @implNote This can have side effects.
		 */
		DensityFunction.NoisePos at(int index);

		/**
		 * Fills the density array using a density function.
		 * 
		 * @implNote This can have side effects.
		 */
		void fill(double[] densities, DensityFunction densityFunction);
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

	/**
	 * {@code NoisePos} represents the absolute block position that is passed
	 * to density functions.
	 * 
	 * <p>It also has a way to get the currently active {@link Blender}.
	 */
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
