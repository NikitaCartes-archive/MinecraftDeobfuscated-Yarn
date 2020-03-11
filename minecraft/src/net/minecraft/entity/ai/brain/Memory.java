package net.minecraft.entity.ai.brain;

import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.util.dynamic.DynamicSerializable;

public class Memory<T> implements DynamicSerializable {
	private final T value;
	private long expiry;

	public Memory(T value, long expiry) {
		this.value = value;
		this.expiry = expiry;
	}

	public Memory(T value) {
		this(value, Long.MAX_VALUE);
	}

	public Memory(Function<Dynamic<?>, T> valueReader, Dynamic<?> data) {
		this((T)valueReader.apply(data.get("value").get().orElseThrow(RuntimeException::new)), data.get("ttl").asLong(Long.MAX_VALUE));
	}

	public void method_24913() {
		if (this.method_24914()) {
			this.expiry--;
		}
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

	public T getValue() {
		return this.value;
	}

	public boolean isExpired() {
		return this.expiry <= 0L;
	}

	public String toString() {
		return this.value.toString() + (this.method_24914() ? " (ttl: " + this.expiry + ")" : "");
	}

	public boolean method_24914() {
		return this.expiry != Long.MAX_VALUE;
	}

	@Override
	public <T> T serialize(DynamicOps<T> ops) {
		Map<T, T> map = Maps.<T, T>newHashMap();
		map.put(ops.createString("value"), ((DynamicSerializable)this.value).serialize(ops));
		if (this.method_24914()) {
			map.put(ops.createString("ttl"), ops.createLong(this.expiry));
		}

		return ops.createMap(map);
	}
}
