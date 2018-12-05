package net.minecraft.util;

import java.util.function.Supplier;

public class LazyCachedSupplier<T> {
	private Supplier<T> supplier;
	private T value;

	public LazyCachedSupplier(Supplier<T> supplier) {
		this.supplier = supplier;
	}

	public T get() {
		Supplier<T> supplier = this.supplier;
		if (supplier != null) {
			this.value = (T)supplier.get();
			this.supplier = null;
		}

		return this.value;
	}
}
