package net.minecraft.util;

import javax.annotation.Nullable;

public interface Clearable {
	void clear();

	static void clear(@Nullable Object object) {
		if (object instanceof Clearable) {
			((Clearable)object).clear();
		}
	}
}
