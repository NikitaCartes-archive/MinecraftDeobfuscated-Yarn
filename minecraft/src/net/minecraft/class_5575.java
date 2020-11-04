package net.minecraft;

import javax.annotation.Nullable;

public interface class_5575<B, T extends B> {
	static <B, T extends B> class_5575<B, T> method_31795(Class<T> class_) {
		return new class_5575<B, T>() {
			@Nullable
			@Override
			public T method_31796(B object) {
				return (T)(class_.isInstance(object) ? object : null);
			}

			@Override
			public Class<? extends B> method_31794() {
				return class_;
			}
		};
	}

	@Nullable
	T method_31796(B object);

	Class<? extends B> method_31794();
}
