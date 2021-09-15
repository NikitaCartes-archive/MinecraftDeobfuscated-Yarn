package net.minecraft.world.gen.random;

import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.thread.LockHelper;

public class AtomicSimpleRandom implements BaseSimpleRandom {
	private static final int INT_BITS = 48;
	private static final long SEED_MASK = 281474976710655L;
	private static final long MULTIPLIER = 25214903917L;
	private static final long INCREMENT = 11L;
	private final AtomicLong seed = new AtomicLong();
	private double nextNextGaussian;
	private boolean hasNextGaussian;

	public AtomicSimpleRandom(long seed) {
		this.setSeed(seed);
	}

	@Override
	public AbstractRandom derive() {
		return new AtomicSimpleRandom(this.nextLong());
	}

	@Override
	public void setSeed(long l) {
		if (!this.seed.compareAndSet(this.seed.get(), (l ^ 25214903917L) & 281474976710655L)) {
			throw LockHelper.crash("SimpleRandomSource", null);
		}
	}

	@Override
	public int next(int bits) {
		long l = this.seed.get();
		long m = l * 25214903917L + 11L & 281474976710655L;
		if (!this.seed.compareAndSet(l, m)) {
			throw LockHelper.crash("SimpleRandomSource", null);
		} else {
			return (int)(m >> 48 - bits);
		}
	}

	@Override
	public double nextGaussian() {
		if (this.hasNextGaussian) {
			this.hasNextGaussian = false;
			return this.nextNextGaussian;
		} else {
			double d;
			double e;
			double f;
			do {
				d = 2.0 * this.nextDouble() - 1.0;
				e = 2.0 * this.nextDouble() - 1.0;
				f = MathHelper.square(d) + MathHelper.square(e);
			} while (f >= 1.0 || f == 0.0);

			double g = Math.sqrt(-2.0 * Math.log(f) / f);
			this.nextNextGaussian = e * g;
			this.hasNextGaussian = true;
			return d * g;
		}
	}
}
