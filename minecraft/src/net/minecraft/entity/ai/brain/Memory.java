package net.minecraft.entity.ai.brain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;

public class Memory<T> {
	private final T value;
	private long expiry;

	public Memory(T value, long expiry) {
		this.value = value;
		this.expiry = expiry;
	}

	public void tick() {
		if (this.method_24914()) {
			this.expiry--;
		}
	}

	public static <T> Memory<T> method_28355(T object) {
		return new Memory<>(object, Long.MAX_VALUE);
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
		return this.value + (this.method_24914() ? " (ttl: " + this.expiry + ")" : "");
	}

	public boolean method_24914() {
		return this.expiry != Long.MAX_VALUE;
	}

	public static <T> Codec<Memory<T>> createCodec(Codec<T> codec) {
		return RecordCodecBuilder.create(
			instance -> instance.group(
						codec.fieldOf("value").forGetter(memory -> memory.value),
						Codec.LONG.optionalFieldOf("ttl").forGetter(memory -> memory.method_24914() ? Optional.of(memory.expiry) : Optional.empty())
					)
					.apply(instance, (object, optional) -> new Memory<>(object, (Long)optional.orElse(Long.MAX_VALUE)))
		);
	}
}
