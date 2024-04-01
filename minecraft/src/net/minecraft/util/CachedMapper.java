package net.minecraft.util;

import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.class_9631;

/**
 * A mapper that caches the latest input-output pair, so that repeated calls with
 * the same argument do not call the expensive mapper function.
 * 
 * @see Util#cachedMapper
 */
public class CachedMapper<K, V> {
	private final Function<K, V> mapper;
	@Nullable
	private K cachedInput = (K)null;
	private class_9631<V> cachedOutput = class_9631.method_59493();

	public CachedMapper(Function<K, V> function) {
		this.mapper = function;
	}

	/**
	 * {@return the mapped {@code input}}
	 */
	public V map(K input) {
		if (this.cachedOutput.method_59489() || !Objects.equals(this.cachedInput, input)) {
			this.cachedOutput = class_9631.method_59490((V)this.mapper.apply(input));
			this.cachedInput = input;
		}

		return this.cachedOutput.method_59492();
	}
}
