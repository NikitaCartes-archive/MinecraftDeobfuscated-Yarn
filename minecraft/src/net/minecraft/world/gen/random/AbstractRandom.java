package net.minecraft.world.gen.random;

public interface AbstractRandom {
	AbstractRandom derive();

	RandomDeriver createRandomDeriver();

	void setSeed(long seed);

	int nextInt();

	int nextInt(int bound);

	default int nextBetween(int min, int max) {
		return this.nextInt(max - min + 1) + min;
	}

	long nextLong();

	boolean nextBoolean();

	float nextFloat();

	double nextDouble();

	double nextGaussian();

	default void skip(int count) {
		for (int i = 0; i < count; i++) {
			this.nextInt();
		}
	}
}
