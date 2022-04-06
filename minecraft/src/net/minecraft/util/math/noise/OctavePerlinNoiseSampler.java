package net.minecraft.util.math.noise;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.math.random.RandomDeriver;

public class OctavePerlinNoiseSampler {
	private static final int field_31704 = 33554432;
	private final PerlinNoiseSampler[] octaveSamplers;
	private final int firstOctave;
	private final DoubleList amplitudes;
	private final double persistence;
	private final double lacunarity;
	private final double field_36632;

	@Deprecated
	public static OctavePerlinNoiseSampler createLegacy(AbstractRandom random, IntStream intStream) {
		return new OctavePerlinNoiseSampler(
			random, calculateAmplitudes(new IntRBTreeSet((Collection<? extends Integer>)intStream.boxed().collect(ImmutableList.toImmutableList()))), false
		);
	}

	@Deprecated
	public static OctavePerlinNoiseSampler createLegacy(AbstractRandom random, int offset, DoubleList amplitudes) {
		return new OctavePerlinNoiseSampler(random, Pair.of(offset, amplitudes), false);
	}

	public static OctavePerlinNoiseSampler create(AbstractRandom random, IntStream intStream) {
		return create(random, (List<Integer>)intStream.boxed().collect(ImmutableList.toImmutableList()));
	}

	public static OctavePerlinNoiseSampler create(AbstractRandom random, List<Integer> list) {
		return new OctavePerlinNoiseSampler(random, calculateAmplitudes(new IntRBTreeSet(list)), true);
	}

	public static OctavePerlinNoiseSampler create(AbstractRandom random, int offset, double firstAmplitude, double... amplitudes) {
		DoubleArrayList doubleArrayList = new DoubleArrayList(amplitudes);
		doubleArrayList.add(0, firstAmplitude);
		return new OctavePerlinNoiseSampler(random, Pair.of(offset, doubleArrayList), true);
	}

	public static OctavePerlinNoiseSampler create(AbstractRandom random, int offset, DoubleList amplitudes) {
		return new OctavePerlinNoiseSampler(random, Pair.of(offset, amplitudes), true);
	}

	private static Pair<Integer, DoubleList> calculateAmplitudes(IntSortedSet octaves) {
		if (octaves.isEmpty()) {
			throw new IllegalArgumentException("Need some octaves!");
		} else {
			int i = -octaves.firstInt();
			int j = octaves.lastInt();
			int k = i + j + 1;
			if (k < 1) {
				throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
			} else {
				DoubleList doubleList = new DoubleArrayList(new double[k]);
				IntBidirectionalIterator intBidirectionalIterator = octaves.iterator();

				while (intBidirectionalIterator.hasNext()) {
					int l = intBidirectionalIterator.nextInt();
					doubleList.set(l + i, 1.0);
				}

				return Pair.of(-i, doubleList);
			}
		}
	}

	protected OctavePerlinNoiseSampler(AbstractRandom random, Pair<Integer, DoubleList> pair, boolean xoroshiro) {
		this.firstOctave = pair.getFirst();
		this.amplitudes = pair.getSecond();
		int i = this.amplitudes.size();
		int j = -this.firstOctave;
		this.octaveSamplers = new PerlinNoiseSampler[i];
		if (xoroshiro) {
			RandomDeriver randomDeriver = random.createRandomDeriver();

			for (int k = 0; k < i; k++) {
				if (this.amplitudes.getDouble(k) != 0.0) {
					int l = this.firstOctave + k;
					this.octaveSamplers[k] = new PerlinNoiseSampler(randomDeriver.createRandom("octave_" + l));
				}
			}
		} else {
			PerlinNoiseSampler perlinNoiseSampler = new PerlinNoiseSampler(random);
			if (j >= 0 && j < i) {
				double d = this.amplitudes.getDouble(j);
				if (d != 0.0) {
					this.octaveSamplers[j] = perlinNoiseSampler;
				}
			}

			for (int kx = j - 1; kx >= 0; kx--) {
				if (kx < i) {
					double e = this.amplitudes.getDouble(kx);
					if (e != 0.0) {
						this.octaveSamplers[kx] = new PerlinNoiseSampler(random);
					} else {
						skipCalls(random);
					}
				} else {
					skipCalls(random);
				}
			}

			if (Arrays.stream(this.octaveSamplers).filter(Objects::nonNull).count() != this.amplitudes.stream().filter(double_ -> double_ != 0.0).count()) {
				throw new IllegalStateException("Failed to create correct number of noise levels for given non-zero amplitudes");
			}

			if (j < i - 1) {
				throw new IllegalArgumentException("Positive octaves are temporarily disabled");
			}
		}

		this.lacunarity = Math.pow(2.0, (double)(-j));
		this.persistence = Math.pow(2.0, (double)(i - 1)) / (Math.pow(2.0, (double)i) - 1.0);
		this.field_36632 = this.method_40557(2.0);
	}

	protected double method_40555() {
		return this.field_36632;
	}

	private static void skipCalls(AbstractRandom random) {
		random.skip(262);
	}

	public double sample(double x, double y, double z) {
		return this.sample(x, y, z, 0.0, 0.0, false);
	}

	@Deprecated
	public double sample(double x, double y, double z, double yScale, double yMax, boolean useOrigin) {
		double d = 0.0;
		double e = this.lacunarity;
		double f = this.persistence;

		for (int i = 0; i < this.octaveSamplers.length; i++) {
			PerlinNoiseSampler perlinNoiseSampler = this.octaveSamplers[i];
			if (perlinNoiseSampler != null) {
				double g = perlinNoiseSampler.sample(
					maintainPrecision(x * e), useOrigin ? -perlinNoiseSampler.originY : maintainPrecision(y * e), maintainPrecision(z * e), yScale * e, yMax * e
				);
				d += this.amplitudes.getDouble(i) * g * f;
			}

			e *= 2.0;
			f /= 2.0;
		}

		return d;
	}

	public double method_40556(double d) {
		return this.method_40557(d + 2.0);
	}

	private double method_40557(double d) {
		double e = 0.0;
		double f = this.persistence;

		for (int i = 0; i < this.octaveSamplers.length; i++) {
			PerlinNoiseSampler perlinNoiseSampler = this.octaveSamplers[i];
			if (perlinNoiseSampler != null) {
				e += this.amplitudes.getDouble(i) * d * f;
			}

			f /= 2.0;
		}

		return e;
	}

	@Nullable
	public PerlinNoiseSampler getOctave(int octave) {
		return this.octaveSamplers[this.octaveSamplers.length - 1 - octave];
	}

	public static double maintainPrecision(double value) {
		return value - (double)MathHelper.lfloor(value / 3.3554432E7 + 0.5) * 3.3554432E7;
	}

	protected int getFirstOctave() {
		return this.firstOctave;
	}

	protected DoubleList getAmplitudes() {
		return this.amplitudes;
	}

	@VisibleForTesting
	public void addDebugInfo(StringBuilder info) {
		info.append("PerlinNoise{");
		List<String> list = this.amplitudes.stream().map(double_ -> String.format("%.2f", double_)).toList();
		info.append("first octave: ").append(this.firstOctave).append(", amplitudes: ").append(list).append(", noise levels: [");

		for (int i = 0; i < this.octaveSamplers.length; i++) {
			info.append(i).append(": ");
			PerlinNoiseSampler perlinNoiseSampler = this.octaveSamplers[i];
			if (perlinNoiseSampler == null) {
				info.append("null");
			} else {
				perlinNoiseSampler.addDebugInfo(info);
			}

			info.append(", ");
		}

		info.append("]");
		info.append("}");
	}
}
