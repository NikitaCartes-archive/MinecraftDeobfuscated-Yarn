package net.minecraft.world.gen.noise;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.class_6916;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.chunk.Blender;

public interface NoiseType {
	Codec<NoiseType> field_37057 = class_6916.field_37061;
	Codec<RegistryEntry<NoiseType>> field_37058 = RegistryElementCodec.of(Registry.DENSITY_FUNCTION_WORLDGEN, field_37057);
	Codec<NoiseType> field_37059 = field_37058.xmap(
		class_6916.class_7051::new,
		noiseType -> (RegistryEntry)(noiseType instanceof class_6916.class_7051 lv ? lv.function() : new RegistryEntry.Direct<>(noiseType))
	);

	double sample(NoiseType.NoisePos pos);

	void method_40470(double[] ds, NoiseType.class_6911 arg);

	NoiseType method_40469(NoiseType.class_6915 arg);

	double minValue();

	double maxValue();

	Codec<? extends NoiseType> method_41062();

	default NoiseType method_40468(double d, double e) {
		return new class_6916.class_6922(this, d, e);
	}

	default NoiseType method_40471() {
		return class_6916.method_40490(this, class_6916.class_6925.class_6926.ABS);
	}

	default NoiseType method_40472() {
		return class_6916.method_40490(this, class_6916.class_6925.class_6926.SQUARE);
	}

	default NoiseType method_40473() {
		return class_6916.method_40490(this, class_6916.class_6925.class_6926.CUBE);
	}

	default NoiseType method_40474() {
		return class_6916.method_40490(this, class_6916.class_6925.class_6926.HALF_NEGATIVE);
	}

	default NoiseType method_40475() {
		return class_6916.method_40490(this, class_6916.class_6925.class_6926.QUARTER_NEGATIVE);
	}

	default NoiseType method_40476() {
		return class_6916.method_40490(this, class_6916.class_6925.class_6926.SQUEEZE);
	}

	public interface NoisePos {
		int blockX();

		int blockY();

		int blockZ();

		default Blender getBlender() {
			return Blender.getNoBlending();
		}
	}

	public static record UnblendedNoisePos(int blockX, int blockY, int blockZ) implements NoiseType.NoisePos {
	}

	public interface class_6911 {
		NoiseType.NoisePos method_40477(int i);

		void method_40478(double[] ds, NoiseType noiseType);
	}

	public interface class_6913 extends NoiseType {
		@Override
		default void method_40470(double[] ds, NoiseType.class_6911 arg) {
			arg.method_40478(ds, this);
		}

		@Override
		default NoiseType method_40469(NoiseType.class_6915 arg) {
			return (NoiseType)arg.apply(this);
		}
	}

	public interface class_6915 extends Function<NoiseType, NoiseType> {
	}
}
