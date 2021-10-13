package net.minecraft.util.math.noise;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import java.util.List;
import net.minecraft.world.gen.random.AbstractRandom;

public class DoublePerlinNoiseSampler {
	private static final double DOMAIN_SCALE = 1.0181268882175227;
	private static final double field_31703 = 0.3333333333333333;
	private final double amplitude;
	private final OctavePerlinNoiseSampler firstSampler;
	private final OctavePerlinNoiseSampler secondSampler;

	@Deprecated
	public static DoublePerlinNoiseSampler method_39122(AbstractRandom abstractRandom, int i, double... ds) {
		return new DoublePerlinNoiseSampler(abstractRandom, i, new DoubleArrayList(ds), false);
	}

	@Deprecated
	public static DoublePerlinNoiseSampler method_39123(AbstractRandom abstractRandom, DoublePerlinNoiseSampler.NoiseParameters noiseParameters) {
		return method_39121(abstractRandom, noiseParameters.getFirstOctave(), noiseParameters.getAmplitudes());
	}

	@Deprecated
	public static DoublePerlinNoiseSampler method_39121(AbstractRandom abstractRandom, int i, DoubleList doubleList) {
		return new DoublePerlinNoiseSampler(abstractRandom, i, doubleList, false);
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

	private DoublePerlinNoiseSampler(AbstractRandom random, int offset, DoubleList octaves, boolean bl) {
		if (bl) {
			this.firstSampler = OctavePerlinNoiseSampler.create(random, offset, octaves);
			this.secondSampler = OctavePerlinNoiseSampler.create(random, offset, octaves);
		} else {
			this.firstSampler = OctavePerlinNoiseSampler.method_39126(random, offset, octaves);
			this.secondSampler = OctavePerlinNoiseSampler.method_39126(random, offset, octaves);
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

	public DoublePerlinNoiseSampler.NoiseParameters method_38475() {
		return new DoublePerlinNoiseSampler.NoiseParameters(this.firstSampler.method_38477(), this.firstSampler.method_38478());
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
		public static final Codec<DoublePerlinNoiseSampler.NoiseParameters> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.INT.fieldOf("firstOctave").forGetter(DoublePerlinNoiseSampler.NoiseParameters::getFirstOctave),
						Codec.DOUBLE.listOf().fieldOf("amplitudes").forGetter(DoublePerlinNoiseSampler.NoiseParameters::getAmplitudes)
					)
					.apply(instance, DoublePerlinNoiseSampler.NoiseParameters::new)
		);

		public NoiseParameters(int firstOctave, List<Double> amplitudes) {
			this.firstOctave = firstOctave;
			this.amplitudes = new DoubleArrayList(amplitudes);
		}

		public NoiseParameters(int firstOctave, double d, double... ds) {
			this.firstOctave = firstOctave;
			this.amplitudes = new DoubleArrayList(ds);
			this.amplitudes.add(0, d);
		}

		public int getFirstOctave() {
			return this.firstOctave;
		}

		public DoubleList getAmplitudes() {
			return this.amplitudes;
		}
	}
}
