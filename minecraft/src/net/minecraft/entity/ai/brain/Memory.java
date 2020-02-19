package net.minecraft.entity.ai.brain;

import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.util.DynamicSerializable;

public class Memory<T> implements DynamicSerializable {
	private final T value;
	private final long expiry;

	public Memory(T value, long expiry) {
		this.value = value;
		this.expiry = expiry;
	}

	public Memory(T value) {
		this(value, Long.MAX_VALUE);
	}

	public Memory(Function<Dynamic<?>, T> valueReader, Dynamic<?> data) {
		this((T)valueReader.apply(data.get("value").get().orElseThrow(RuntimeException::new)), data.get("expiry").asLong(Long.MAX_VALUE));
	}

	/**
	 * Creates a permanent memory.
	 */
	public static <T> Memory<T> permanent(T value) {
		return new Memory<>(value);
	}

	/**
	 * Creates a memory that has an expiry time.
	 */
	public static <T> Memory<T> timed(T value, long expiry) {
		return new Memory<>(value, expiry);
	}

	public long getExpiry() {
		return this.expiry;
	}

	public T getValue() {
		return this.value;
	}

	public boolean isExpired(long time) {
		return this.getTimeTillExpiry(time) <= 0L;
	}

	public long getTimeTillExpiry(long time) {
		return this.expiry - time;
	}

	public String toString() {
		return this.value.toString() + (this.getExpiry() != Long.MAX_VALUE ? " (expiry: " + this.expiry + ")" : "");
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Map<T, T> map = Maps.<T, T>newHashMap();
		map.put(ops.createString("value"), ((DynamicSerializable)this.value).serialize(ops));
		if (this.expiry != Long.MAX_VALUE) {
			map.put(ops.createString("expiry"), ops.createLong(this.expiry));
		}

		return ops.createMap(map);
	}
}
