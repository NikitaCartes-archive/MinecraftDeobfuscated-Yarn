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
	Codec<DensityFunction> field_37057 = DensityFunctionTypes.field_37061;
	Codec<RegistryEntry<DensityFunction>> REGISTRY_ENTRY_CODEC = RegistryElementCodec.of(Registry.DENSITY_FUNCTION_KEY, field_37057);
	Codec<DensityFunction> field_37059 = REGISTRY_ENTRY_CODEC.xmap(
		DensityFunctionTypes.RegistryEntryHolder::new,
		densityFunction -> (RegistryEntry)(densityFunction instanceof DensityFunctionTypes.RegistryEntryHolder registryEntryHolder
				? registryEntryHolder.function()
				: new RegistryEntry.Direct<>(densityFunction))
	);

	double sample(DensityFunction.NoisePos pos);

	void method_40470(double[] ds, DensityFunction.class_6911 arg);

	DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor);

	double minValue();

	double maxValue();

	CodecHolder<? extends DensityFunction> getCodec();

	default DensityFunction clamp(double min, double max) {
		return new DensityFunctionTypes.Clamp(this, min, max);
	}

	default DensityFunction abs() {
		return DensityFunctionTypes.method_40490(this, DensityFunctionTypes.class_6925.Type.ABS);
	}

	default DensityFunction square() {
		return DensityFunctionTypes.method_40490(this, DensityFunctionTypes.class_6925.Type.SQUARE);
	}

	default DensityFunction cube() {
		return DensityFunctionTypes.method_40490(this, DensityFunctionTypes.class_6925.Type.CUBE);
	}

	default DensityFunction halfNegative() {
		return DensityFunctionTypes.method_40490(this, DensityFunctionTypes.class_6925.Type.HALF_NEGATIVE);
	}

	default DensityFunction quarterNegative() {
		return DensityFunctionTypes.method_40490(this, DensityFunctionTypes.class_6925.Type.QUARTER_NEGATIVE);
	}

	default DensityFunction squeeze() {
		return DensityFunctionTypes.method_40490(this, DensityFunctionTypes.class_6925.Type.SQUEEZE);
	}

	public interface DensityFunctionVisitor {
		DensityFunction apply(DensityFunction densityFunction);

		default DensityFunction.class_7270 method_42358(DensityFunction.class_7270 arg) {
			return arg;
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

	public interface class_6911 {
		DensityFunction.NoisePos method_40477(int i);

		void method_40478(double[] ds, DensityFunction densityFunction);
	}

	public interface class_6913 extends DensityFunction {
		@Override
		default void method_40470(double[] ds, DensityFunction.class_6911 arg) {
			arg.method_40478(ds, this);
		}

		@Override
		default DensityFunction apply(DensityFunction.DensityFunctionVisitor visitor) {
			return visitor.apply(this);
		}
	}

	public static record class_7270(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseData, @Nullable DoublePerlinNoiseSampler noise) {
		public static final Codec<DensityFunction.class_7270> field_38248 = DoublePerlinNoiseSampler.NoiseParameters.CODEC
			.xmap(registryEntry -> new DensityFunction.class_7270(registryEntry, null), DensityFunction.class_7270::noiseData);

		public class_7270(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> registryEntry) {
			this(registryEntry, null);
		}

		public double method_42356(double d, double e, double f) {
			return this.noise == null ? 0.0 : this.noise.sample(d, e, f);
		}

		public double method_42355() {
			return this.noise == null ? 2.0 : this.noise.method_40554();
		}
	}
}
