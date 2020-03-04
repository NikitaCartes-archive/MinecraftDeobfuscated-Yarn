package net.minecraft.screen;

/**
 * A {@link PropertyDelegate} that is implemented using an int array.
 */
public class ArrayPropertyDelegate implements PropertyDelegate {
	private final int[] data;

	public ArrayPropertyDelegate(int size) {
		this.data = new int[size];
	}

	@Override
	public int get(int index) {
		return this.data[index];
	}

	@Override
	public void set(int index, int value) {
		this.data[index] = value;
	}

	@Override
	public int size() {
		return this.data.length;
	}
}
