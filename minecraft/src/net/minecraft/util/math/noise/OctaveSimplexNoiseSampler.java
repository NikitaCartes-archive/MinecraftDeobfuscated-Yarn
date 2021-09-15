package net.minecraft.util.math.noise;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.ChunkRandom;

public class OctaveSimplexNoiseSampler implements NoiseSampler {
	private final SimplexNoiseSampler[] octaveSamplers;
	private final double persistence;
	private final double lacunarity;

	public OctaveSimplexNoiseSampler(AbstractRandom random, IntStream octaves) {
		this(random, (List<Integer>)octaves.boxed().collect(ImmutableList.toImmutableList()));
	}

	public OctaveSimplexNoiseSampler(AbstractRandom random, List<Integer> octaves) {
		this(random, new IntRBTreeSet(octaves));
	}

	private OctaveSimplexNoiseSampler(AbstractRandom random, IntSortedSet octaves) {
		if (octaves.isEmpty()) {
			throw new IllegalArgumentException("Need some octaves!");
		} else {
			int i = -octaves.firstInt();
			int j = octaves.lastInt();
			int k = i + j + 1;
			if (k < 1) {
				throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
			} else {
				SimplexNoiseSampler simplexNoiseSampler = new SimplexNoiseSampler(random);
				int l = j;
				this.octaveSamplers = new SimplexNoiseSampler[k];
				if (j >= 0 && j < k && octaves.contains(0)) {
					this.octaveSamplers[j] = simplexNoiseSampler;
				}

				for (int m = j + 1; m < k; m++) {
					if (m >= 0 && octaves.contains(l - m)) {
						this.octaveSamplers[m] = new SimplexNoiseSampler(random);
					} else {
						random.skip(262);
					}
				}

				if (j > 0) {
					long n = (long)(simplexNoiseSampler.sample(simplexNoiseSampler.originX, simplexNoiseSampler.originY, simplexNoiseSampler.originZ) * 9.223372E18F);
					AbstractRandom abstractRandom = new ChunkRandom(n);

					for (int o = l - 1; o >= 0; o--) {
						if (o < k && octaves.contains(l - o)) {
							this.octaveSamplers[o] = new SimplexNoiseSampler(abstractRandom);
						} else {
							abstractRandom.skip(262);
						}
					}
				}

				this.lacunarity = Math.pow(2.0, (double)j);
				this.persistence = 1.0 / (Math.pow(2.0, (double)k) - 1.0);
			}
		}
	}

	public double sample(double x, double y, boolean useOrigin) {
		double d = 0.0;
		double e = this.lacunarity;
		double f = this.persistence;

		for (SimplexNoiseSampler simplexNoiseSampler : this.octaveSamplers) {
			if (simplexNoiseSampler != null) {
				d += simplexNoiseSampler.sample(x * e + (useOrigin ? simplexNoiseSampler.originX : 0.0), y * e + (useOrigin ? simplexNoiseSampler.originY : 0.0)) * f;
			}

			e /= 2.0;
			f *= 2.0;
		}

		return d;
	}

	@Override
	public double sample(double x, double y, double yScale, double yMax) {
		return this.sample(x, y, true) * 0.55;
	}
}
