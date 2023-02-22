/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.registry;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.dynamic.ForwardingDynamicOps;

public class RegistryOps<T>
extends ForwardingDynamicOps<T> {
    private final RegistryInfoGetter registryInfoGetter;

    private static RegistryInfoGetter caching(final RegistryInfoGetter registryInfoGetter) {
        return new RegistryInfoGetter(){
            private final Map<RegistryKey<? extends Registry<?>>, Optional<? extends RegistryInfo<?>>> registryRefToInfo = new HashMap();

            @Override
            public <T> Optional<RegistryInfo<T>> getRegistryInfo(RegistryKey<? extends Registry<? extends T>> registryRef) {
                return this.registryRefToInfo.computeIfAbsent(registryRef, registryInfoGetter::getRegistryInfo);
            }
        };
    }

    public static <T> RegistryOps<T> of(DynamicOps<T> delegate, final RegistryWrapper.WrapperLookup wrapperLookup) {
        return RegistryOps.of(delegate, RegistryOps.caching(new RegistryInfoGetter(){

            public <E> Optional<RegistryInfo<E>> getRegistryInfo(RegistryKey<? extends Registry<? extends E>> registryRef) {
                return wrapperLookup.getOptionalWrapper(registryRef).map(wrapper -> new RegistryInfo(wrapper, wrapper, wrapper.getLifecycle()));
            }
        }));
    }

    public static <T> RegistryOps<T> of(DynamicOps<T> delegate, RegistryInfoGetter registryInfoGetter) {
        return new RegistryOps<T>(delegate, registryInfoGetter);
    }

    private RegistryOps(DynamicOps<T> delegate, RegistryInfoGetter registryInfoGetter) {
        super(delegate);
        this.registryInfoGetter = registryInfoGetter;
    }

    public <E> Optional<RegistryEntryOwner<E>> getOwner(RegistryKey<? extends Registry<? extends E>> registryRef) {
        return this.registryInfoGetter.getRegistryInfo(registryRef).map(RegistryInfo::owner);
    }

    public <E> Optional<RegistryEntryLookup<E>> getEntryLookup(RegistryKey<? extends Registry<? extends E>> registryRef) {
        return this.registryInfoGetter.getRegistryInfo(registryRef).map(RegistryInfo::entryLookup);
    }

    public static <E, O> RecordCodecBuilder<O, RegistryEntryLookup<E>> getEntryLookupCodec(RegistryKey<? extends Registry<? extends E>> registryRef) {
        return Codecs.createContextRetrievalCodec(ops -> {
            if (ops instanceof RegistryOps) {
                RegistryOps registryOps = (RegistryOps)ops;
                return registryOps.registryInfoGetter.getRegistryInfo(registryRef).map(info -> DataResult.success(info.entryLookup(), info.elementsLifecycle())).orElseGet(() -> DataResult.error(() -> "Unknown registry: " + registryRef));
            }
            return DataResult.error(() -> "Not a registry ops");
        }).forGetter(object -> null);
    }

    public static <E, O> RecordCodecBuilder<O, RegistryEntry.Reference<E>> getEntryCodec(RegistryKey<E> key) {
        RegistryKey registryKey = RegistryKey.ofRegistry(key.getRegistry());
        return Codecs.createContextRetrievalCodec(ops -> {
            if (ops instanceof RegistryOps) {
                RegistryOps registryOps = (RegistryOps)ops;
                return registryOps.registryInfoGetter.getRegistryInfo(registryKey).flatMap(info -> info.entryLookup().getOptional(key)).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Can't find value: " + key));
            }
            return DataResult.error(() -> "Not a registry ops");
        }).forGetter(object -> null);
    }

    public static interface RegistryInfoGetter {
        public <T> Optional<RegistryInfo<T>> getRegistryInfo(RegistryKey<? extends Registry<? extends T>> var1);
    }

    public record RegistryInfo<T>(RegistryEntryOwner<T> owner, RegistryEntryLookup<T> entryLookup, Lifecycle elementsLifecycle) {
    }
}

