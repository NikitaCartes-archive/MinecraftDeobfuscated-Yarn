package net.minecraft.command.argument.packrat;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import java.util.Objects;
import javax.annotation.Nullable;

public final class ParseResults {
	private final Object2ObjectMap<Symbol<?>, Object> results = new Object2ObjectArrayMap<>();

	public <T> void put(Symbol<T> symbol, @Nullable T value) {
		this.results.put(symbol, value);
	}

	@Nullable
	public <T> T get(Symbol<T> symbol) {
		return (T)this.results.get(symbol);
	}

	public <T> T getOrThrow(Symbol<T> symbol) {
		return (T)Objects.requireNonNull(this.get(symbol));
	}

	public <T> T getOrDefault(Symbol<T> symbol, T fallback) {
		return (T)Objects.requireNonNullElse(this.get(symbol), fallback);
	}

	@Nullable
	@SafeVarargs
	public final <T> T getAny(Symbol<T>... symbols) {
		for (Symbol<T> symbol : symbols) {
			T object = this.get(symbol);
			if (object != null) {
				return object;
			}
		}

		return null;
	}

	@SafeVarargs
	public final <T> T getAnyOrThrow(Symbol<T>... symbols) {
		return (T)Objects.requireNonNull(this.getAny(symbols));
	}

	public String toString() {
		return this.results.toString();
	}

	public void putAll(ParseResults results) {
		this.results.putAll(results.results);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			return o instanceof ParseResults parseResults ? this.results.equals(parseResults.results) : false;
		}
	}

	public int hashCode() {
		return this.results.hashCode();
	}
}
