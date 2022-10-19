/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.registry;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.network.message.MessageType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.CombinedDynamicRegistries;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.ServerDynamicRegistryType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;

public class SerializableRegistries {
    private static final Map<RegistryKey<? extends Registry<?>>, Info<?>> REGISTRIES = Util.make(() -> {
        ImmutableMap.Builder<RegistryKey<Registry<?>>, Info<?>> builder = ImmutableMap.builder();
        SerializableRegistries.add(builder, Registry.BIOME_KEY, Biome.NETWORK_CODEC);
        SerializableRegistries.add(builder, Registry.MESSAGE_TYPE_KEY, MessageType.CODEC);
        SerializableRegistries.add(builder, Registry.DIMENSION_TYPE_KEY, DimensionType.CODEC);
        return builder.build();
    });
    public static final Codec<DynamicRegistryManager> CODEC = SerializableRegistries.createCodec();

    private static <E> void add(ImmutableMap.Builder<RegistryKey<? extends Registry<?>>, Info<?>> builder, RegistryKey<? extends Registry<E>> key, Codec<E> networkCodec) {
        builder.put(key, new Info<E>(key, networkCodec));
    }

    private static Stream<DynamicRegistryManager.Entry<?>> stream(DynamicRegistryManager dynamicRegistryManager) {
        return dynamicRegistryManager.streamAllRegistries().filter(entry -> REGISTRIES.containsKey(entry.key()));
    }

    private static <E> DataResult<? extends Codec<E>> getNetworkCodec(RegistryKey<? extends Registry<E>> registryRef) {
        return Optional.ofNullable(REGISTRIES.get(registryRef)).map(info -> info.networkCodec()).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown or not serializable registry: " + registryRef));
    }

    private static <E> Codec<DynamicRegistryManager> createCodec() {
        Codec<RegistryKey> codec = Identifier.CODEC.xmap(RegistryKey::ofRegistry, RegistryKey::getValue);
        Codec<Registry> codec2 = codec.partialDispatch("type", registry -> DataResult.success(registry.getKey()), registryRef -> SerializableRegistries.getNetworkCodec(registryRef).map(codec -> RegistryCodecs.createRegistryCodec(registryRef, Lifecycle.experimental(), codec)));
        UnboundedMapCodec<RegistryKey, Registry> unboundedMapCodec = Codec.unboundedMap(codec, codec2);
        return SerializableRegistries.createDynamicRegistryManagerCodec(unboundedMapCodec);
    }

    private static <K extends RegistryKey<? extends Registry<?>>, V extends Registry<?>> Codec<DynamicRegistryManager> createDynamicRegistryManagerCodec(UnboundedMapCodec<K, V> networkCodec) {
        return networkCodec.xmap(DynamicRegistryManager.ImmutableImpl::new, registryManager -> SerializableRegistries.stream(registryManager).collect(ImmutableMap.toImmutableMap(entry -> entry.key(), entry -> entry.value())));
    }

    public static Stream<DynamicRegistryManager.Entry<?>> streamRegistryManagerEntries(CombinedDynamicRegistries<ServerDynamicRegistryType> combinedRegistries) {
        Stream<DynamicRegistryManager.Entry<?>> stream = combinedRegistries.get(ServerDynamicRegistryType.STATIC).streamAllRegistries();
        Stream<DynamicRegistryManager.Entry<?>> stream2 = SerializableRegistries.stream(combinedRegistries.getSucceedingRegistryManagers(ServerDynamicRegistryType.WORLDGEN));
        return Stream.concat(stream2, stream);
    }

    record Info<E>(RegistryKey<? extends Registry<E>> key, Codec<E> networkCodec) {
    }
}

