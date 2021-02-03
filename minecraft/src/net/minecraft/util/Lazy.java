package net.minecraft.util;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;

@Deprecated
public class Lazy<T> {
	private final Supplier<T> supplier;

	public Lazy(Supplier<T> delegate) {
		this.supplier = Suppliers.memoize(delegate::get);
	}

	public T get() {
		return (T)this.supplier.get();
	}
}
