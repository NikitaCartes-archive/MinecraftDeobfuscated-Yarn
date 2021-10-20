package net.minecraft.util.math.noise;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.random.AbstractRandom;

public class DoublePerlinNoiseSampler {
	private static final double DOMAIN_SCALE = 1.0181268882175227;
	private static final double field_31703 = 0.3333333333333333;
	private final double amplitude;
	private final OctavePerlinNoiseSampler firstSampler;
	private final OctavePerlinNoiseSampler secondSampler;

	@Deprecated
	public static DoublePerlinNoiseSampler createLegacy(AbstractRandom random, DoublePerlinNoiseSampler.NoiseParameters parameters) {
		return new DoublePerlinNoiseSampler(random, parameters.getFirstOctave(), parameters.getAmplitudes(), false);
	}

	public static DoublePerlinNoiseSampler create(AbstractRandom random, int offset, double... octaves) {
		return new DoublePerlinNoiseSampler(random, offset, new DoubleArrayList(octaves), true);
	}

	public static DoublePerlinNoiseSampler create(AbstractRandom random, DoublePerlinNoiseSampler.NoiseParameters parameters) {
		return new DoublePerlinNoiseSampler(random, parameters.getFirstOctave(), parameters.getAmplitudes(), true);
	}

	public static DoublePerlinNoiseSampler create(AbstractRandom random, int offset, DoubleList octaves) {
		return new DoublePerlinNoiseSampler(random, offset, octaves, true);
	}

	private DoublePerlinNoiseSampler(AbstractRandom random, int offset, DoubleList octaves, boolean xoroshiro) {
		if (xoroshiro) {
			this.firstSampler = OctavePerlinNoiseSampler.create(random, offset, octaves);
			this.secondSampler = OctavePerlinNoiseSampler.create(random, offset, octaves);
		} else {
			this.firstSampler = OctavePerlinNoiseSampler.createLegacy(random, offset, octaves);
			this.secondSampler = OctavePerlinNoiseSampler.createLegacy(random, offset, octaves);
		}

		int i = Integer.MAX_VALUE;
		int j = Integer.MIN_VALUE;
		DoubleListIterator doubleListIterator = octaves.iterator();

		while (doubleListIterator.hasNext()) {
			int k = doubleListIterator.nextIndex();
			double d = doubleListIterator.nextDouble();
			if (d != 0.0) {
				i = Math.min(i, k);
				j = Math.max(j, k);
			}
		}

		this.amplitude = 0.16666666666666666 / createAmplitude(j - i);
	}

	private static double createAmplitude(int octaves) {
		return 0.1 * (1.0 + 1.0 / (double)(octaves + 1));
	}

	public double sample(double x, double y, double z) {
		double d = x * 1.0181268882175227;
		double e = y * 1.0181268882175227;
		double f = z * 1.0181268882175227;
		return (this.firstSampler.sample(x, y, z) + this.secondSampler.sample(d, e, f)) * this.amplitude;
	}

	public DoublePerlinNoiseSampler.NoiseParameters copy() {
		return new DoublePerlinNoiseSampler.NoiseParameters(this.firstSampler.getFirstOctave(), this.firstSampler.getAmplitudes());
	}

	@VisibleForTesting
	public void addDebugInfo(StringBuilder info) {
		info.append("NormalNoise {");
		info.append("first: ");
		this.firstSampler.addDebugInfo(info);
		info.append(", second: ");
		this.secondSampler.addDebugInfo(info);
		info.append("}");
	}

	public static class NoiseParameters {
		private final int firstOctave;
		private final DoubleList amplitudes;
		public static final Codec<DoublePerlinNoiseSampler.NoiseParameters> field_35424 = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.INT.fieldOf("firstOctave").forGetter(DoublePerlinNoiseSampler.NoiseParameters::getFirstOctave),
						Codec.DOUBLE.listOf().fieldOf("amplitudes").forGetter(DoublePerlinNoiseSampler.NoiseParameters::getAmplitudes)
					)
					.apply(instance, DoublePerlinNoiseSampler.NoiseParameters::new)
		);
		public static final Codec<Supplier<DoublePerlinNoiseSampler.NoiseParameters>> CODEC = RegistryElementCodec.of(Registry.NOISE_WORLDGEN, field_35424);

		public NoiseParameters(int firstOctave, List<Double> amplitudes) {
			this.firstOctave = firstOctave;
			this.amplitudes = new DoubleArrayList(amplitudes);
		}

		public NoiseParameters(int firstOctave, double firstAmplitude, double... amplitudes) {
			this.firstOctave = firstOctave;
			this.amplitudes = new DoubleArrayList(amplitudes);
			this.amplitudes.add(0, firstAmplitude);
		}

		public int getFirstOctave() {
			return this.firstOctave;
		}

		public DoubleList getAmplitudes() {
			return this.amplitudes;
		}
	}
}
