package net.minecraft.util.math.noise;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import java.util.List;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

public class DoublePerlinNoiseSampler {
	private static final double DOMAIN_SCALE = 1.0181268882175227;
	private static final double field_31703 = 0.3333333333333333;
	private final double amplitude;
	private final OctavePerlinNoiseSampler firstSampler;
	private final OctavePerlinNoiseSampler secondSampler;
	private final double maxValue;
	private final DoublePerlinNoiseSampler.NoiseParameters parameters;

	@Deprecated
	public static DoublePerlinNoiseSampler createLegacy(Random random, DoublePerlinNoiseSampler.NoiseParameters parameters) {
		return new DoublePerlinNoiseSampler(random, parameters, false);
	}

	public static DoublePerlinNoiseSampler create(Random random, int offset, double... octaves) {
		return create(random, new DoublePerlinNoiseSampler.NoiseParameters(offset, new DoubleArrayList(octaves)));
	}

	public static DoublePerlinNoiseSampler create(Random random, DoublePerlinNoiseSampler.NoiseParameters parameters) {
		return new DoublePerlinNoiseSampler(random, parameters, true);
	}

	private DoublePerlinNoiseSampler(Random random, DoublePerlinNoiseSampler.NoiseParameters parameters, boolean modern) {
		int i = parameters.firstOctave;
		DoubleList doubleList = parameters.amplitudes;
		this.parameters = parameters;
		if (modern) {
			this.firstSampler = OctavePerlinNoiseSampler.create(random, i, doubleList);
			this.secondSampler = OctavePerlinNoiseSampler.create(random, i, doubleList);
		} else {
			this.firstSampler = OctavePerlinNoiseSampler.createLegacy(random, i, doubleList);
			this.secondSampler = OctavePerlinNoiseSampler.createLegacy(random, i, doubleList);
		}

		int j = Integer.MAX_VALUE;
		int k = Integer.MIN_VALUE;
		DoubleListIterator doubleListIterator = doubleList.iterator();

		while (doubleListIterator.hasNext()) {
			int l = doubleListIterator.nextIndex();
			double d = doubleListIterator.nextDouble();
			if (d != 0.0) {
				j = Math.min(j, l);
				k = Math.max(k, l);
			}
		}

		this.amplitude = 0.16666666666666666 / createAmplitude(k - j);
		this.maxValue = (this.firstSampler.getMaxValue() + this.secondSampler.getMaxValue()) * this.amplitude;
	}

	public double getMaxValue() {
		return this.maxValue;
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
		return this.parameters;
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

	public static record NoiseParameters(int firstOctave, DoubleList amplitudes) {
		public static final Codec<DoublePerlinNoiseSampler.NoiseParameters> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.INT.fieldOf("firstOctave").forGetter(DoublePerlinNoiseSampler.NoiseParameters::firstOctave),
						Codec.DOUBLE.listOf().fieldOf("amplitudes").forGetter(DoublePerlinNoiseSampler.NoiseParameters::amplitudes)
					)
					.apply(instance, DoublePerlinNoiseSampler.NoiseParameters::new)
		);
		public static final Codec<RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters>> REGISTRY_ENTRY_CODEC = RegistryElementCodec.of(
			RegistryKeys.NOISE_PARAMETERS, CODEC
		);

		public NoiseParameters(int firstOctave, List<Double> amplitudes) {
			this(firstOctave, new DoubleArrayList(amplitudes));
		}

		public NoiseParameters(int firstOctave, double firstAmplitude, double... amplitudes) {
			this(firstOctave, Util.make(new DoubleArrayList(amplitudes), doubleArrayList -> doubleArrayList.add(0, firstAmplitude)));
		}
	}
}
