package net.minecraft.world.gen.densityfunction;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.chunk.Blender;

public interface DensityFunction {
	Codec<DensityFunction> field_37057 = DensityFunctionTypes.field_37061;
	Codec<RegistryEntry<DensityFunction>> REGISTRY_ENTRY_CODEC = RegistryElementCodec.of(Registry.DENSITY_FUNCTION_KEY, field_37057);
	Codec<DensityFunction> field_37059 = REGISTRY_ENTRY_CODEC.xmap(
		DensityFunctionTypes.class_7051::new,
		densityFunction -> (RegistryEntry)(densityFunction instanceof DensityFunctionTypes.class_7051 lv
				? lv.function()
				: new RegistryEntry.Direct<>(densityFunction))
	);

	double sample(DensityFunction.NoisePos pos);

	void method_40470(double[] ds, DensityFunction.class_6911 arg);

	DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor);

	double minValue();

	double maxValue();

	Codec<? extends DensityFunction> getCodec();

	default DensityFunction clamp(double min, double max) {
		return new DensityFunctionTypes.Clamp(this, min, max);
	}

	default DensityFunction abs() {
		return DensityFunctionTypes.method_40490(this, DensityFunctionTypes.class_6925.class_6926.ABS);
	}

	default DensityFunction square() {
		return DensityFunctionTypes.method_40490(this, DensityFunctionTypes.class_6925.class_6926.SQUARE);
	}

	default DensityFunction cube() {
		return DensityFunctionTypes.method_40490(this, DensityFunctionTypes.class_6925.class_6926.CUBE);
	}

	default DensityFunction halfNegative() {
		return DensityFunctionTypes.method_40490(this, DensityFunctionTypes.class_6925.class_6926.HALF_NEGATIVE);
	}

	default DensityFunction quarterNegative() {
		return DensityFunctionTypes.method_40490(this, DensityFunctionTypes.class_6925.class_6926.QUARTER_NEGATIVE);
	}

	default DensityFunction squeeze() {
		return DensityFunctionTypes.method_40490(this, DensityFunctionTypes.class_6925.class_6926.SQUEEZE);
	}

	public interface DensityFunctionVisitor extends Function<DensityFunction, DensityFunction> {
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
		default DensityFunction method_40469(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
			return (DensityFunction)densityFunctionVisitor.apply(this);
		}
	}
}
