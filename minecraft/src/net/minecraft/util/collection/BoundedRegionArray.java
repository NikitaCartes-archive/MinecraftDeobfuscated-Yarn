package net.minecraft.util.collection;

import java.util.Locale;
import java.util.function.Consumer;

public class BoundedRegionArray<T> {
	private final int minX;
	private final int minZ;
	private final int maxX;
	private final int maxZ;
	private final Object[] array;

	public static <T> BoundedRegionArray<T> create(int centerX, int centerZ, int radius, BoundedRegionArray.Getter<T> getter) {
		int i = centerX - radius;
		int j = centerZ - radius;
		int k = 2 * radius + 1;
		return new BoundedRegionArray<>(i, j, k, k, getter);
	}

	private BoundedRegionArray(int minX, int minZ, int maxX, int maxZ, BoundedRegionArray.Getter<T> getter) {
		this.minX = minX;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxZ = maxZ;
		this.array = new Object[this.maxX * this.maxZ];

		for (int i = minX; i < minX + maxX; i++) {
			for (int j = minZ; j < minZ + maxZ; j++) {
				this.array[this.toIndex(i, j)] = getter.get(i, j);
			}
		}
	}

	public void forEach(Consumer<T> callback) {
		for (Object object : this.array) {
			callback.accept(object);
		}
	}

	public T get(int x, int z) {
		if (!this.isWithinBounds(x, z)) {
			throw new IllegalArgumentException("Requested out of range value (" + x + "," + z + ") from " + this);
		} else {
			return (T)this.array[this.toIndex(x, z)];
		}
	}

	public boolean isWithinBounds(int x, int z) {
		int i = x - this.minX;
		int j = z - this.minZ;
		return i >= 0 && i < this.maxX && j >= 0 && j < this.maxZ;
	}

	public String toString() {
		return String.format(Locale.ROOT, "StaticCache2D[%d, %d, %d, %d]", this.minX, this.minZ, this.minX + this.maxX, this.minZ + this.maxZ);
	}

	private int toIndex(int x, int z) {
		int i = x - this.minX;
		int j = z - this.minZ;
		return i * this.maxZ + j;
	}

	@FunctionalInterface
	public interface Getter<T> {
		T get(int x, int z);
	}
}
