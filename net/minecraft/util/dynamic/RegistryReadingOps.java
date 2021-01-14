/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.dynamic;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.ForwardingDynamicOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

/**
 * A dynamic ops that encode an id for a registry element rather than a full object.
 * 
 * @see RegistryElementCodec#encode(Object, DynamicOps, Object)
 */
public class RegistryReadingOps<T>
extends ForwardingDynamicOps<T> {
    private final DynamicRegistryManager manager;

    public static <T> RegistryReadingOps<T> of(DynamicOps<T> delegate, DynamicRegistryManager tracker) {
        return new RegistryReadingOps<T>(delegate, tracker);
    }

    private RegistryReadingOps(DynamicOps<T> delegate, DynamicRegistryManager tracker) {
        super(delegate);
        this.manager = tracker;
    }

    /**
     * Encode an id for a registry element than a full object if possible.
     * 
     * <p>This method is called by casting an arbitrary dynamic ops to a registry
     * reading ops.
     * 
     * @see RegistryOps#decodeOrId(Object, RegistryKey, Codec, boolean)
     */
    protected <E> DataResult<T> encodeOrId(E input, T prefix, RegistryKey<? extends Registry<E>> registryReference, Codec<E> codec) {
        MutableRegistry mutableRegistry;
        Optional<RegistryKey<E>> optional2;
        Optional optional = this.manager.getOptionalMutable(registryReference);
        if (optional.isPresent() && (optional2 = (mutableRegistry = optional.get()).getKey(input)).isPresent()) {
            RegistryKey<E> registryKey = optional2.get();
            return Identifier.CODEC.encode(registryKey.getValue(), this.delegate, prefix);
        }
        return codec.encode(input, this, prefix);
    }
}

