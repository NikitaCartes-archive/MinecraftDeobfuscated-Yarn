package net.minecraft.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public interface ErrorReporter {
	ErrorReporter makeChild(String name);

	void report(String message);

	public static class Impl implements ErrorReporter {
		private final Multimap<String, String> errors;
		private final Supplier<String> pathSupplier;
		@Nullable
		private String path;

		public Impl() {
			this(HashMultimap.create(), () -> "");
		}

		private Impl(Multimap<String, String> errors, Supplier<String> pathSupplier) {
			this.errors = errors;
			this.pathSupplier = pathSupplier;
		}

		private String getPath() {
			if (this.path == null) {
				this.path = (String)this.pathSupplier.get();
			}

			return this.path;
		}

		@Override
		public ErrorReporter makeChild(String name) {
			return new ErrorReporter.Impl(this.errors, () -> this.getPath() + name);
		}

		@Override
		public void report(String message) {
			this.errors.put(this.getPath(), message);
		}

		public Multimap<String, String> getErrors() {
			return ImmutableMultimap.copyOf(this.errors);
		}
	}
}
