package net.minecraft.container;

public interface PropertyDelegate {
	int get(int key);

	void set(int key, int value);

	int size();
}
