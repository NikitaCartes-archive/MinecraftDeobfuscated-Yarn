package net.minecraft.util.collection;

import javax.annotation.Nullable;

public interface IndexedIterable<T> extends Iterable<T> {
	int field_34829 = -1;

	int getRawId(T entry);

	@Nullable
	T get(int index);

	int size();
}
