package net.minecraft.world.gen;

public interface WorldGenRandom {
	void setSeed(long seed);

	int nextInt();

	int nextInt(int bound);

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
