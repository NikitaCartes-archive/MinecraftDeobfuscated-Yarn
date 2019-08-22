package net.minecraft.util;

import javax.annotation.Nullable;

public interface IndexedIterable<T> extends Iterable<T> {
	@Nullable
	T get(int i);
}
