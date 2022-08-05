package net.minecraft.util;

import com.google.common.base.Suppliers;
import java.util.function.Supplier;

/**
 * A class that lazily evaluates a value.
 * 
 * @deprecated Use {@link com.google.common.base.Suppliers#memoize} instead.
 */
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
