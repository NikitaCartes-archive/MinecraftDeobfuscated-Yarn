package net.minecraft.world.gen;

public interface WorldGenRandom {
	int nextInt();

	int nextInt(int bound);

	long nextLong();

	double nextDouble();

	default void skip(int count) {
		for (int i = 0; i < count; i++) {
			this.nextInt();
		}
	}
}
