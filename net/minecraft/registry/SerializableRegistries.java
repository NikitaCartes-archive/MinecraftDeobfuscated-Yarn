/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.registry;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.network.message.MessageType;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;

public class SerializableRegistries {
    private static final Map<RegistryKey<? extends Registry<?>>, Info<?>> REGISTRIES = Util.make(() -> {
        ImmutableMap.Builder<RegistryKey<Registry<?>>, Info<?>> builder = ImmutableMap.builder();
        SerializableRegistries.add(builder, RegistryKeys.BIOME, Biome.NETWORK_CODEC);
        SerializableRegistries.add(builder, RegistryKeys.MESSAGE_TYPE, MessageType.CODEC);
        SerializableRegistries.add(builder, RegistryKeys.TRIM_PATTERN, ArmorTrimPattern.CODEC);
        SerializableRegistries.add(builder, RegistryKeys.TRIM_MATERIAL, ArmorTrimMaterial.CODEC);
        SerializableRegistries.add(builder, RegistryKeys.DIMENSION_TYPE, DimensionType.CODEC);
        SerializableRegistries.add(builder, RegistryKeys.DAMAGE_TYPE, DamageType.CODEC);
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
        return Optional.ofNullable(REGISTRIES.get(registryRef)).map(info -> info.networkCodec()).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Unknown or not serializable registry: " + registryRef));
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

    public static Stream<DynamicRegistryManager.Entry<?>> streamDynamicEntries(CombinedDynamicRegistries<ServerDynamicRegistryType> combinedRegistries) {
        return SerializableRegistries.stream(combinedRegistries.getSucceedingRegistryManagers(ServerDynamicRegistryType.WORLDGEN));
    }

    public static Stream<DynamicRegistryManager.Entry<?>> streamRegistryManagerEntries(CombinedDynamicRegistries<ServerDynamicRegistryType> combinedRegistries) {
        Stream<DynamicRegistryManager.Entry<?>> stream = combinedRegistries.get(ServerDynamicRegistryType.STATIC).streamAllRegistries();
        Stream<DynamicRegistryManager.Entry<?>> stream2 = SerializableRegistries.streamDynamicEntries(combinedRegistries);
        return Stream.concat(stream2, stream);
    }

    record Info<E>(RegistryKey<? extends Registry<E>> key, Codec<E> networkCodec) {
    }
}

