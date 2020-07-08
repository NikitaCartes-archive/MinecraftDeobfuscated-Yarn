package net.minecraft.util.collection;

import javax.annotation.Nullable;

public interface IndexedIterable<T> extends Iterable<T> {
	int getRawId(T object);

	@Nullable
	T get(int index);
}
