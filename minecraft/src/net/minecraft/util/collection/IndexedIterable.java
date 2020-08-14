package net.minecraft.util.collection;

import javax.annotation.Nullable;

public interface IndexedIterable<T> extends Iterable<T> {
	int getRawId(T entry);

	@Nullable
	T get(int index);
}
