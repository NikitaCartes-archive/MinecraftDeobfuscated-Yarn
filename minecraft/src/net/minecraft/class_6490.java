package net.minecraft;

import java.util.function.IntConsumer;

public interface class_6490 {
	int setAndGetOldValue(int i, int j);

	void set(int i, int j);

	int get(int i);

	long[] getStorage();

	int getSize();

	int getElementBits();

	void forEach(IntConsumer intConsumer);
}
