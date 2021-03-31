/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.util.annotation.Debug;

public class Memory<T> {
    private final T value;
    private long expiry;

    public Memory(T value, long expiry) {
        this.value = value;
        this.expiry = expiry;
    }

    public void tick() {
        if (this.isTimed()) {
            --this.expiry;
        }
    }

    /**
     * Creates a memory without an expiry time.
     */
    public static <T> Memory<T> permanent(T value) {
        return new Memory<T>(value, Long.MAX_VALUE);
    }

    /**
     * Creates a memory that has an expiry time.
     */
    public static <T> Memory<T> timed(T value, long expiry) {
        return new Memory<T>(value, expiry);
    }

    public long method_35127() {
        return this.expiry;
    }

    public T getValue() {
        return this.value;
    }

    public boolean isExpired() {
        return this.expiry <= 0L;
    }

    public String toString() {
        return this.value + (this.isTimed() ? " (ttl: " + this.expiry + ")" : "");
    }

    @Debug
    public boolean isTimed() {
        return this.expiry != Long.MAX_VALUE;
    }

    public static <T> Codec<Memory<T>> createCodec(Codec<T> codec) {
        return RecordCodecBuilder.create(instance -> instance.group(((MapCodec)codec.fieldOf("value")).forGetter(memory -> memory.value), Codec.LONG.optionalFieldOf("ttl").forGetter(memory -> memory.isTimed() ? Optional.of(memory.expiry) : Optional.empty())).apply((Applicative<Memory, ?>)instance, (object, optional) -> new Memory<Object>(object, optional.orElse(Long.MAX_VALUE))));
    }
}

