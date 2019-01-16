package net.minecraft.util;

import javax.annotation.Nullable;

public interface IntIterable<T> extends Iterable<T> {
	@Nullable
	T getInt(int i);
}
