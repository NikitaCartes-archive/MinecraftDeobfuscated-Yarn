package net.minecraft.util;

import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;

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
	@Nullable
	private V cachedOutput;

	public CachedMapper(Function<K, V> mapper) {
		this.mapper = mapper;
	}

	/**
	 * {@return the mapped {@code input}}
	 */
	public V map(K input) {
		if (this.cachedOutput == null || !Objects.equals(this.cachedInput, input)) {
			this.cachedOutput = (V)this.mapper.apply(input);
			this.cachedInput = input;
		}

		return this.cachedOutput;
	}
}
