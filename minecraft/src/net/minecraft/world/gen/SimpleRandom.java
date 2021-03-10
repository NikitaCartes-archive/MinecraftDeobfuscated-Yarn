package net.minecraft.world.gen;

import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.util.thread.LockHelper;

public class SimpleRandom implements WorldGenRandom {
	private final AtomicLong seed = new AtomicLong();

	public SimpleRandom(long seed) {
		this.setSeed(seed);
	}

	public void setSeed(long seed) {
		if (!this.seed.compareAndSet(this.seed.get(), (seed ^ 25214903917L) & 281474976710655L)) {
			throw LockHelper.crash("SimpleRandomSource", null);
		}
	}

	private int next(int bits) {
		long l = this.seed.get();
		long m = l * 25214903917L + 11L & 281474976710655L;
		if (!this.seed.compareAndSet(l, m)) {
			throw LockHelper.crash("SimpleRandomSource", null);
		} else {
			return (int)(m >> 48 - bits);
		}
	}

	@Override
	public int nextInt() {
		return this.next(32);
	}

	@Override
	public int nextInt(int i) {
		if (i <= 0) {
			throw new IllegalArgumentException("Bound must be positive");
		} else if ((i & i - 1) == 0) {
			return (int)((long)i * (long)this.next(31) >> 31);
		} else {
			int j;
			int k;
			do {
				j = this.next(31);
				k = j % i;
			} while (j - k + (i - 1) < 0);

			return k;
		}
	}

	@Override
	public long nextLong() {
		int i = this.next(32);
		int j = this.next(32);
		long l = (long)i << 32;
		return l + (long)j;
	}

	@Override
	public double nextDouble() {
		int i = this.next(26);
		int j = this.next(27);
		long l = ((long)i << 27) + (long)j;
		return (double)l * 1.110223E-16F;
	}
}
