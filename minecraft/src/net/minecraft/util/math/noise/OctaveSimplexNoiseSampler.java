package net.minecraft.util.math.noise;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.stream.IntStream;
import net.minecraft.world.gen.ChunkRandom;

public class OctaveSimplexNoiseSampler implements NoiseSampler {
	private final SimplexNoiseSampler[] octaveSamplers;
	private final double field_20661;
	private final double field_20662;

	public OctaveSimplexNoiseSampler(ChunkRandom chunkRandom, int i, int j) {
		this(chunkRandom, new IntRBTreeSet(IntStream.rangeClosed(-i, j).toArray()));
	}

	public OctaveSimplexNoiseSampler(ChunkRandom chunkRandom, IntSortedSet intSortedSet) {
		if (intSortedSet.isEmpty()) {
			throw new IllegalArgumentException("Need some octaves!");
		} else {
			int i = -intSortedSet.firstInt();
			int j = intSortedSet.lastInt();
			int k = i + j + 1;
			if (k < 1) {
				throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
			} else {
				SimplexNoiseSampler simplexNoiseSampler = new SimplexNoiseSampler(chunkRandom);
				int l = j;
				this.octaveSamplers = new SimplexNoiseSampler[k];
				if (j >= 0 && j < k && intSortedSet.contains(0)) {
					this.octaveSamplers[j] = simplexNoiseSampler;
				}

				for (int m = j + 1; m < k; m++) {
					if (m >= 0 && intSortedSet.contains(l - m)) {
						this.octaveSamplers[m] = new SimplexNoiseSampler(chunkRandom);
					} else {
						chunkRandom.consume(262);
					}
				}

				if (j > 0) {
					long n = (long)(simplexNoiseSampler.method_22416(simplexNoiseSampler.originX, simplexNoiseSampler.originY, simplexNoiseSampler.originZ) * 9.223372E18F);
					ChunkRandom chunkRandom2 = new ChunkRandom(n);

					for (int o = l - 1; o >= 0; o--) {
						if (o < k && intSortedSet.contains(l - o)) {
							this.octaveSamplers[o] = new SimplexNoiseSampler(chunkRandom2);
						} else {
							chunkRandom2.consume(262);
						}
					}
				}

				this.field_20662 = Math.pow(2.0, (double)j);
				this.field_20661 = 1.0 / (Math.pow(2.0, (double)k) - 1.0);
			}
		}
	}

	public double sample(double d, double e, boolean bl) {
		double f = 0.0;
		double g = this.field_20662;
		double h = this.field_20661;

		for (SimplexNoiseSampler simplexNoiseSampler : this.octaveSamplers) {
			if (simplexNoiseSampler != null) {
				f += simplexNoiseSampler.sample(d * g + (bl ? simplexNoiseSampler.originX : 0.0), e * g + (bl ? simplexNoiseSampler.originY : 0.0)) * h;
			}

			g /= 2.0;
			h *= 2.0;
		}

		return f;
	}

	@Override
	public double sample(double d, double e, double f, double g) {
		return this.sample(d, e, true) * 0.55;
	}
}
