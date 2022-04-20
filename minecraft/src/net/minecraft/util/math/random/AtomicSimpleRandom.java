package net.minecraft.util.math.random;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.thread.LockHelper;
import net.minecraft.world.gen.random.GaussianGenerator;

public class AtomicSimpleRandom implements BaseSimpleRandom {
	private static final int INT_BITS = 48;
	private static final long SEED_MASK = 281474976710655L;
	private static final long MULTIPLIER = 25214903917L;
	private static final long INCREMENT = 11L;
	private final AtomicLong seed = new AtomicLong();
	private final GaussianGenerator gaussianGenerator = new GaussianGenerator(this);

	public AtomicSimpleRandom(long seed) {
		this.setSeed(seed);
	}

	@Override
	public AbstractRandom derive() {
		return new AtomicSimpleRandom(this.nextLong());
	}

	@Override
	public net.minecraft.util.math.random.RandomDeriver createRandomDeriver() {
		return new AtomicSimpleRandom.RandomDeriver(this.nextLong());
	}

	@Override
	public void setSeed(long seed) {
		if (!this.seed.compareAndSet(this.seed.get(), (seed ^ 25214903917L) & 281474976710655L)) {
			throw LockHelper.crash("LegacyRandomSource", null);
		} else {
			this.gaussianGenerator.reset();
		}
	}

	@Override
	public int next(int bits) {
		long l = this.seed.get();
		long m = l * 25214903917L + 11L & 281474976710655L;
		if (!this.seed.compareAndSet(l, m)) {
			throw LockHelper.crash("LegacyRandomSource", null);
		} else {
			return (int)(m >> 48 - bits);
		}
	}

	@Override
	public double nextGaussian() {
		return this.gaussianGenerator.next();
	}

	public static class RandomDeriver implements net.minecraft.util.math.random.RandomDeriver {
		private final long seed;

		public RandomDeriver(long seed) {
			this.seed = seed;
		}

		@Override
		public AbstractRandom createRandom(int x, int y, int z) {
			long l = MathHelper.hashCode(x, y, z);
			long m = l ^ this.seed;
			return new AtomicSimpleRandom(m);
		}

		@Override
		public AbstractRandom createRandom(String string) {
			int i = string.hashCode();
			return new AtomicSimpleRandom((long)i ^ this.seed);
		}

		@VisibleForTesting
		@Override
		public void addDebugInfo(StringBuilder info) {
			info.append("LegacyPositionalRandomFactory{").append(this.seed).append("}");
		}
	}
}
