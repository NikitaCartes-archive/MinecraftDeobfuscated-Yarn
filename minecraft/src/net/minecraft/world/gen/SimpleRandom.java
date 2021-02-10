package net.minecraft.world.gen;

import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.util.thread.LockHelper;

public class SimpleRandom implements WorldGenRandom {
	private final AtomicLong seed = new AtomicLong();
	private boolean field_28767 = false;

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
		return ((long)this.nextInt() << 32) + (long)this.nextInt();
	}

	@Override
	public double nextDouble() {
		return (double)(((long)this.next(26) << 27) + (long)this.next(27)) * 1.110223E-16F;
	}
}
