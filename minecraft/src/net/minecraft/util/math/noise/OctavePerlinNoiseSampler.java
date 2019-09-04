package net.minecraft.util.math.noise;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.ChunkRandom;

public class OctavePerlinNoiseSampler implements NoiseSampler {
	private final PerlinNoiseSampler[] octaveSamplers;
	private final double field_20659;
	private final double field_20660;

	public OctavePerlinNoiseSampler(ChunkRandom chunkRandom, int i, int j) {
		this(chunkRandom, new IntRBTreeSet(IntStream.rangeClosed(-i, j).toArray()));
	}

	public OctavePerlinNoiseSampler(ChunkRandom chunkRandom, IntSortedSet intSortedSet) {
		if (intSortedSet.isEmpty()) {
			throw new IllegalArgumentException("Need some octaves!");
		} else {
			int i = -intSortedSet.firstInt();
			int j = intSortedSet.lastInt();
			int k = i + j + 1;
			if (k < 1) {
				throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
			} else {
				PerlinNoiseSampler perlinNoiseSampler = new PerlinNoiseSampler(chunkRandom);
				int l = j;
				this.octaveSamplers = new PerlinNoiseSampler[k];
				if (j >= 0 && j < k && intSortedSet.contains(0)) {
					this.octaveSamplers[j] = perlinNoiseSampler;
				}

				for (int m = j + 1; m < k; m++) {
					if (m >= 0 && intSortedSet.contains(l - m)) {
						this.octaveSamplers[m] = new PerlinNoiseSampler(chunkRandom);
					} else {
						chunkRandom.consume(262);
					}
				}

				if (j > 0) {
					long n = (long)(perlinNoiseSampler.sample(0.0, 0.0, 0.0, 0.0, 0.0) * 9.223372E18F);
					ChunkRandom chunkRandom2 = new ChunkRandom(n);

					for (int o = l - 1; o >= 0; o--) {
						if (o < k && intSortedSet.contains(l - o)) {
							this.octaveSamplers[o] = new PerlinNoiseSampler(chunkRandom2);
						} else {
							chunkRandom2.consume(262);
						}
					}
				}

				this.field_20660 = Math.pow(2.0, (double)j);
				this.field_20659 = 1.0 / (Math.pow(2.0, (double)k) - 1.0);
			}
		}
	}

	public double sample(double d, double e, double f) {
		return this.sample(d, e, f, 0.0, 0.0, false);
	}

	public double sample(double d, double e, double f, double g, double h, boolean bl) {
		double i = 0.0;
		double j = this.field_20660;
		double k = this.field_20659;

		for (PerlinNoiseSampler perlinNoiseSampler : this.octaveSamplers) {
			if (perlinNoiseSampler != null) {
				i += perlinNoiseSampler.sample(
						maintainPrecision(d * j), bl ? -perlinNoiseSampler.originY : maintainPrecision(e * j), maintainPrecision(f * j), g * j, h * j
					)
					* k;
			}

			j /= 2.0;
			k *= 2.0;
		}

		return i;
	}

	@Nullable
	public PerlinNoiseSampler getOctave(int i) {
		return this.octaveSamplers[i];
	}

	public static double maintainPrecision(double d) {
		return d - (double)MathHelper.lfloor(d / 3.3554432E7 + 0.5) * 3.3554432E7;
	}

	@Override
	public double sample(double d, double e, double f, double g) {
		return this.sample(d, e, 0.0, f, g, false);
	}
}
