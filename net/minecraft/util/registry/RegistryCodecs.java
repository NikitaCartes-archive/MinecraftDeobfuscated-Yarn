/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.registry;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.dynamic.RegistryLoader;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryEntryListCodec;
import net.minecraft.util.registry.RegistryFixedCodec;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

/**
 * A utility class for serialization of registries using codecs.
 */
public class RegistryCodecs {
    private static <T> MapCodec<RegistryManagerEntry<T>> managerEntry(RegistryKey<? extends Registry<T>> registryRef, MapCodec<T> elementCodec) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec)RegistryKey.createCodec(registryRef).fieldOf("name")).forGetter(RegistryManagerEntry::key), ((MapCodec)Codec.INT.fieldOf("id")).forGetter(RegistryManagerEntry::rawId), elementCodec.forGetter(RegistryManagerEntry::value)).apply((Applicative<RegistryManagerEntry, ?>)instance, RegistryManagerEntry::new));
    }

    public static <T> Codec<Registry<T>> createRegistryCodec(RegistryKey<? extends Registry<T>> registryRef, Lifecycle lifecycle, Codec<T> elementCodec) {
        return RegistryCodecs.managerEntry(registryRef, elementCodec.fieldOf("element")).codec().listOf().xmap(entries -> {
            SimpleRegistry mutableRegistry = new SimpleRegistry(registryRef, lifecycle, null);
            for (RegistryManagerEntry registryManagerEntry : entries) {
                ((MutableRegistry)mutableRegistry).set(registryManagerEntry.rawId(), registryManagerEntry.key(), registryManagerEntry.value(), lifecycle);
            }
            return mutableRegistry;
        }, registry -> {
            ImmutableList.Builder builder = ImmutableList.builder();
            for (Object object : registry) {
                builder.add(new RegistryManagerEntry(registry.getKey(object).get(), registry.getRawId(object), object));
            }
            return builder.build();
        });
    }

    public static <E> Codec<Registry<E>> dynamicRegistry(RegistryKey<? extends Registry<E>> registryRef, Lifecycle lifecycle, Codec<E> elementCodec) {
        Codec<Map<RegistryKey<E>, E>> codec = RegistryCodecs.registryMap(registryRef, elementCodec);
        Encoder<Registry> encoder = codec.comap(registry -> ImmutableMap.copyOf(registry.getEntrySet()));
        return Codec.of(encoder, RegistryCodecs.createRegistryDecoder(registryRef, elementCodec, codec, lifecycle), "DataPackRegistryCodec for " + registryRef);
    }

    private static <E> Decoder<Registry<E>> createRegistryDecoder(final RegistryKey<? extends Registry<E>> registryRef, final Codec<E> codec, Decoder<Map<RegistryKey<E>, E>> entryMapDecoder, Lifecycle lifecycle) {
        final Decoder<MutableRegistry> decoder = entryMapDecoder.map(map -> {
            SimpleRegistry mutableRegistry = new SimpleRegistry(registryRef, lifecycle, null);
            map.forEach((key, value) -> mutableRegistry.add(key, value, lifecycle));
            return mutableRegistry;
        });
        return new Decoder<Registry<E>>(){

            @Override
            public <T> DataResult<Pair<Registry<E>, T>> decode(DynamicOps<T> ops, T input) {
                DataResult dataResult = decoder.decode(ops, input);
                if (ops instanceof RegistryOps) {
                    RegistryOps registryOps = (RegistryOps)ops;
                    return registryOps.getLoaderAccess().map((? super T loaderAccess) -> this.load(dataResult, registryOps, loaderAccess.loader())).orElseGet(() -> DataResult.error("Can't load registry with this ops"));
                }
                return dataResult.map((? super R pair) -> pair.mapFirst(registry -> registry));
            }

            private <T> DataResult<Pair<Registry<E>, T>> load(DataResult<Pair<MutableRegistry<E>, T>> result, RegistryOps<?> ops, RegistryLoader loader) {
                return result.flatMap((? super R pair) -> loader.load((MutableRegistry)pair.getFirst(), registryRef, codec, ops.getEntryOps()).map((? super R registry) -> Pair.of(registry, pair.getSecond())));
            }
        };
    }

    private static <T> Codec<Map<RegistryKey<T>, T>> registryMap(RegistryKey<? extends Registry<T>> registryRef, Codec<T> elementCodec) {
        return Codec.unboundedMap(RegistryKey.createCodec(registryRef), elementCodec);
    }

    public static <E> Codec<RegistryEntryList<E>> entryList(RegistryKey<? extends Registry<E>> registryRef, Codec<E> elementCodec) {
        return RegistryCodecs.entryList(registryRef, elementCodec, false);
    }

    /**
     * @param alwaysSerializeAsList whether to always serialize the list as a list
     * instead of serializing as one entry if the length is {@code 0}
     */
    public static <E> Codec<RegistryEntryList<E>> entryList(RegistryKey<? extends Registry<E>> registryRef, Codec<E> elementCodec, boolean alwaysSerializeAsList) {
        return RegistryEntryListCodec.create(registryRef, RegistryElementCodec.of(registryRef, elementCodec), alwaysSerializeAsList);
    }

    public static <E> Codec<RegistryEntryList<E>> entryList(RegistryKey<? extends Registry<E>> registryRef) {
        return RegistryCodecs.entryList(registryRef, false);
    }

    /**
     * @param alwaysSerializeAsList whether to always serialize the list as a list
     * instead of serializing as one entry if the length is {@code 0}
     */
    public static <E> Codec<RegistryEntryList<E>> entryList(RegistryKey<? extends Registry<E>> registryRef, boolean alwaysSerializeAsList) {
        return RegistryEntryListCodec.create(registryRef, RegistryFixedCodec.of(registryRef), alwaysSerializeAsList);
    }

    record RegistryManagerEntry<T>(RegistryKey<T> key, int rawId, T value) {
    }
}

