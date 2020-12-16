package net.minecraft.util.math.noise;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.ints.IntBidirectionalIterator;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.ChunkRandom;

public class OctavePerlinNoiseSampler implements NoiseSampler {
	private final PerlinNoiseSampler[] octaveSamplers;
	private final DoubleList amplitudes;
	private final double persistence;
	private final double lacunarity;

	public OctavePerlinNoiseSampler(ChunkRandom random, IntStream octaves) {
		this(random, (List<Integer>)octaves.boxed().collect(ImmutableList.toImmutableList()));
	}

	public OctavePerlinNoiseSampler(ChunkRandom random, List<Integer> octaves) {
		this(random, new IntRBTreeSet(octaves));
	}

	public static OctavePerlinNoiseSampler create(ChunkRandom random, int offset, DoubleList amplitudes) {
		return new OctavePerlinNoiseSampler(random, Pair.of(offset, amplitudes));
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

	private OctavePerlinNoiseSampler(ChunkRandom random, IntSortedSet octaves) {
		this(random, calculateAmplitudes(octaves));
	}

	private OctavePerlinNoiseSampler(ChunkRandom random, Pair<Integer, DoubleList> offsetAndAmplitudes) {
		int i = offsetAndAmplitudes.getFirst();
		this.amplitudes = offsetAndAmplitudes.getSecond();
		PerlinNoiseSampler perlinNoiseSampler = new PerlinNoiseSampler(random);
		int j = this.amplitudes.size();
		int k = -i;
		this.octaveSamplers = new PerlinNoiseSampler[j];
		if (k >= 0 && k < j) {
			double d = this.amplitudes.getDouble(k);
			if (d != 0.0) {
				this.octaveSamplers[k] = perlinNoiseSampler;
			}
		}

		for (int l = k - 1; l >= 0; l--) {
			if (l < j) {
				double e = this.amplitudes.getDouble(l);
				if (e != 0.0) {
					this.octaveSamplers[l] = new PerlinNoiseSampler(random);
				} else {
					random.consume(262);
				}
			} else {
				random.consume(262);
			}
		}

		if (k < j - 1) {
			long m = (long)(perlinNoiseSampler.sample(0.0, 0.0, 0.0, 0.0, 0.0) * 9.223372E18F);
			ChunkRandom chunkRandom = new ChunkRandom(m);

			for (int n = k + 1; n < j; n++) {
				if (n >= 0) {
					double f = this.amplitudes.getDouble(n);
					if (f != 0.0) {
						this.octaveSamplers[n] = new PerlinNoiseSampler(chunkRandom);
					} else {
						chunkRandom.consume(262);
					}
				} else {
					chunkRandom.consume(262);
				}
			}
		}

		this.lacunarity = Math.pow(2.0, (double)(-k));
		this.persistence = Math.pow(2.0, (double)(j - 1)) / (Math.pow(2.0, (double)j) - 1.0);
	}

	public double sample(double x, double y, double z) {
		return this.sample(x, y, z, 0.0, 0.0, false);
	}

	public double sample(double x, double y, double z, double yScale, double yMax, boolean useOrigin) {
		double d = 0.0;
		double e = this.lacunarity;
		double f = this.persistence;

		for (int i = 0; i < this.octaveSamplers.length; i++) {
			PerlinNoiseSampler perlinNoiseSampler = this.octaveSamplers[i];
			if (perlinNoiseSampler != null) {
				d += this.amplitudes.getDouble(i)
					* perlinNoiseSampler.sample(
						maintainPrecision(x * e), useOrigin ? -perlinNoiseSampler.originY : maintainPrecision(y * e), maintainPrecision(z * e), yScale * e, yMax * e
					)
					* f;
			}

			e *= 2.0;
			f /= 2.0;
		}

		return d;
	}

	@Nullable
	public PerlinNoiseSampler getOctave(int octave) {
		return this.octaveSamplers[this.octaveSamplers.length - 1 - octave];
	}

	public static double maintainPrecision(double value) {
		return value - (double)MathHelper.lfloor(value / 3.3554432E7 + 0.5) * 3.3554432E7;
	}

	@Override
	public double sample(double x, double y, double yScale, double yMax) {
		return this.sample(x, y, 0.0, yScale, yMax, false);
	}
}
