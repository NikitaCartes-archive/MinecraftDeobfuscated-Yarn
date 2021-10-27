package net.minecraft.world.gen.random;

public class SimpleRandom implements BaseSimpleRandom {
	private static final int INT_BITS = 48;
	private static final long SEED_MASK = 281474976710655L;
	private static final long MULTIPLIER = 25214903917L;
	private static final long INCREMENT = 11L;
	private long seed;
	private final GaussianGenerator gaussianGenerator = new GaussianGenerator(this);

	public SimpleRandom(long seed) {
		this.setSeed(seed);
	}

	@Override
	public AbstractRandom derive() {
		return new SimpleRandom(this.nextLong());
	}

	@Override
	public RandomDeriver createRandomDeriver() {
		return new AtomicSimpleRandom.RandomDeriver(this.nextLong());
	}

	@Override
	public void setSeed(long l) {
		this.seed = (l ^ 25214903917L) & 281474976710655L;
	}

	@Override
	public int next(int bits) {
		long l = this.seed * 25214903917L + 11L & 281474976710655L;
		this.seed = l;
		return (int)(l >> 48 - bits);
	}

	@Override
	public double nextGaussian() {
		return this.gaussianGenerator.next();
	}
}
