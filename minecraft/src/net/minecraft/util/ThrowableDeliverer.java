package net.minecraft.util;

import javax.annotation.Nullable;

public class ThrowableDeliverer<T extends Throwable> {
	@Nullable
	private T throwable;

	public void add(T throwable) {
		if (this.throwable == null) {
			this.throwable = throwable;
		} else {
			this.throwable.addSuppressed(throwable);
		}
	}

	public void deliver() throws T {
		if (this.throwable != null) {
			throw this.throwable;
		}
	}
}
