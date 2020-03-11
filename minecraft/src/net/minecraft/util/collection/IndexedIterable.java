package net.minecraft.util.collection;

import javax.annotation.Nullable;

public interface IndexedIterable<T> extends Iterable<T> {
	@Nullable
	T get(int index);
}
