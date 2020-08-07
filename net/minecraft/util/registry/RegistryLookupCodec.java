/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.registry;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import java.util.stream.Stream;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public final class RegistryLookupCodec<E>
extends MapCodec<Registry<E>> {
    private final RegistryKey<? extends Registry<E>> registryKey;

    public static <E> RegistryLookupCodec<E> of(RegistryKey<? extends Registry<E>> registryKey) {
        return new RegistryLookupCodec<E>(registryKey);
    }

    private RegistryLookupCodec(RegistryKey<? extends Registry<E>> registryKey) {
        this.registryKey = registryKey;
    }

    @Override
    public <T> RecordBuilder<T> encode(Registry<E> registry, DynamicOps<T> dynamicOps, RecordBuilder<T> recordBuilder) {
        return recordBuilder;
    }

    @Override
    public <T> DataResult<Registry<E>> decode(DynamicOps<T> dynamicOps, MapLike<T> mapLike) {
        if (dynamicOps instanceof RegistryOps) {
            return ((RegistryOps)dynamicOps).method_31152(this.registryKey);
        }
        return DataResult.error("Not a registry ops");
    }

    public String toString() {
        return "RegistryLookupCodec[" + this.registryKey + "]";
    }

    @Override
    public <T> Stream<T> keys(DynamicOps<T> dynamicOps) {
        return Stream.empty();
    }

    @Override
    public /* synthetic */ RecordBuilder encode(Object object, DynamicOps dynamicOps, RecordBuilder recordBuilder) {
        return this.encode((Registry)object, dynamicOps, recordBuilder);
    }
}

