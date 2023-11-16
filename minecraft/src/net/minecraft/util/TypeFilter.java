package net.minecraft.util;

import javax.annotation.Nullable;

/**
 * A filter that determines if an object of some supertype {@code B} can be
 * treated as an object of some subtype {@code T}.
 * 
 * @param <B> the base type that's the input to the filter
 * @param <T> the desired type of this filter
 */
public interface TypeFilter<B, T extends B> {
	/**
	 * Creates a filter whose filtering condition is whether the object is an instance of the given class.
	 */
	static <B, T extends B> TypeFilter<B, T> instanceOf(Class<T> cls) {
		return new TypeFilter<B, T>() {
			@Nullable
			@Override
			public T downcast(B obj) {
				return (T)(cls.isInstance(obj) ? obj : null);
			}

			@Override
			public Class<? extends B> getBaseClass() {
				return cls;
			}
		};
	}

	/**
	 * Creates a filter whose filtering condition is whether the object's class is equal to the given class.
	 */
	static <B, T extends B> TypeFilter<B, T> equals(Class<T> cls) {
		return new TypeFilter<B, T>() {
			@Nullable
			@Override
			public T downcast(B obj) {
				return (T)(cls.equals(obj.getClass()) ? obj : null);
			}

			@Override
			public Class<? extends B> getBaseClass() {
				return cls;
			}
		};
	}

	/**
	 * Checks if the argument can be converted to the type {@code T} and returns the argument, or {@code null} otherwise.
	 */
	@Nullable
	T downcast(B obj);

	Class<? extends B> getBaseClass();
}
