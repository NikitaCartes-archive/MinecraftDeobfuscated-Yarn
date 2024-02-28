package net.minecraft.server.world;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public interface OptionalChunk<T> {
	static <T> OptionalChunk<T> of(T chunk) {
		return new OptionalChunk.ActualChunk<>(chunk);
	}

	static <T> OptionalChunk<T> of(String error) {
		return of((Supplier<String>)(() -> error));
	}

	static <T> OptionalChunk<T> of(Supplier<String> error) {
		return new OptionalChunk.LoadFailure<>(error);
	}

	boolean isPresent();

	@Nullable
	T orElse(@Nullable T other);

	@Nullable
	static <R> R orElse(OptionalChunk<? extends R> optionalChunk, @Nullable R other) {
		R object = (R)optionalChunk.orElse(null);
		return object != null ? object : other;
	}

	@Nullable
	String getError();

	OptionalChunk<T> ifPresent(Consumer<T> callback);

	<R> OptionalChunk<R> map(Function<T, R> mapper);

	<E extends Throwable> T orElseThrow(Supplier<E> exceptionSupplier) throws E;

	public static record ActualChunk<T>(T value) implements OptionalChunk<T> {
		@Override
		public boolean isPresent() {
			return true;
		}

		@Override
		public T orElse(@Nullable T other) {
			return this.value;
		}

		@Nullable
		@Override
		public String getError() {
			return null;
		}

		@Override
		public OptionalChunk<T> ifPresent(Consumer<T> callback) {
			callback.accept(this.value);
			return this;
		}

		@Override
		public <R> OptionalChunk<R> map(Function<T, R> mapper) {
			return (OptionalChunk<R>)(new OptionalChunk.ActualChunk<>(mapper.apply(this.value)));
		}

		@Override
		public <E extends Throwable> T orElseThrow(Supplier<E> exceptionSupplier) throws E {
			return this.value;
		}
	}

	public static record LoadFailure<T>(Supplier<String> error) implements OptionalChunk<T> {
		@Override
		public boolean isPresent() {
			return false;
		}

		@Nullable
		@Override
		public T orElse(@Nullable T other) {
			return other;
		}

		@Override
		public String getError() {
			return (String)this.error.get();
		}

		@Override
		public OptionalChunk<T> ifPresent(Consumer<T> callback) {
			return this;
		}

		@Override
		public <R> OptionalChunk<R> map(Function<T, R> mapper) {
			return new OptionalChunk.LoadFailure(this.error);
		}

		@Override
		public <E extends Throwable> T orElseThrow(Supplier<E> exceptionSupplier) throws E {
			throw (Throwable)exceptionSupplier.get();
		}
	}
}
