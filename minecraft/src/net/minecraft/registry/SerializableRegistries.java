package net.minecraft.registry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;

public class SerializableRegistries {
	private static final Map<RegistryKey<? extends Registry<?>>, SerializableRegistries.Info<?>> REGISTRIES = Util.make(() -> {
		Builder<RegistryKey<? extends Registry<?>>, SerializableRegistries.Info<?>> builder = ImmutableMap.builder();
		add(builder, RegistryKeys.BIOME, Biome.NETWORK_CODEC);
		add(builder, RegistryKeys.MESSAGE_TYPE, MessageType.CODEC);
		add(builder, RegistryKeys.TRIM_PATTERN, ArmorTrimPattern.CODEC);
		add(builder, RegistryKeys.TRIM_MATERIAL, ArmorTrimMaterial.CODEC);
		add(builder, RegistryKeys.DIMENSION_TYPE, DimensionType.CODEC);
		add(builder, RegistryKeys.DAMAGE_TYPE, DamageType.CODEC);
		return builder.build();
	});
	public static final Codec<DynamicRegistryManager> CODEC = createCodec();

	private static <E> void add(
		Builder<RegistryKey<? extends Registry<?>>, SerializableRegistries.Info<?>> builder, RegistryKey<? extends Registry<E>> key, Codec<E> networkCodec
	) {
		builder.put(key, new SerializableRegistries.Info<>(key, networkCodec));
	}

	private static Stream<DynamicRegistryManager.Entry<?>> stream(DynamicRegistryManager dynamicRegistryManager) {
		return dynamicRegistryManager.streamAllRegistries().filter(entry -> REGISTRIES.containsKey(entry.key()));
	}

	private static <E> DataResult<? extends Codec<E>> getNetworkCodec(RegistryKey<? extends Registry<E>> registryRef) {
		return (DataResult<? extends Codec<E>>)Optional.ofNullable((SerializableRegistries.Info)REGISTRIES.get(registryRef))
			.map(info -> info.networkCodec())
			.map(DataResult::success)
			.orElseGet(() -> DataResult.error(() -> "Unknown or not serializable registry: " + registryRef));
	}

	private static <E> Codec<DynamicRegistryManager> createCodec() {
		Codec<RegistryKey<? extends Registry<E>>> codec = Identifier.CODEC.xmap(RegistryKey::ofRegistry, RegistryKey::getValue);
		Codec<Registry<E>> codec2 = codec.partialDispatch(
			"type",
			registry -> DataResult.success(registry.getKey()),
			registryRef -> getNetworkCodec(registryRef).map(codecx -> RegistryCodecs.createRegistryCodec(registryRef, Lifecycle.experimental(), codecx))
		);
		UnboundedMapCodec<? extends RegistryKey<? extends Registry<?>>, ? extends Registry<?>> unboundedMapCodec = Codec.unboundedMap(codec, codec2);
		return createDynamicRegistryManagerCodec(unboundedMapCodec);
	}

	private static <K extends RegistryKey<? extends Registry<?>>, V extends Registry<?>> Codec<DynamicRegistryManager> createDynamicRegistryManagerCodec(
		UnboundedMapCodec<K, V> networkCodec
	) {
		return networkCodec.xmap(
			DynamicRegistryManager.ImmutableImpl::new,
			registryManager -> (Map)stream(registryManager).collect(ImmutableMap.toImmutableMap(entry -> entry.key(), entry -> entry.value()))
		);
	}

	public static Stream<DynamicRegistryManager.Entry<?>> streamDynamicEntries(CombinedDynamicRegistries<ServerDynamicRegistryType> combinedRegistries) {
		return stream(combinedRegistries.getSucceedingRegistryManagers(ServerDynamicRegistryType.WORLDGEN));
	}

	public static Stream<DynamicRegistryManager.Entry<?>> streamRegistryManagerEntries(CombinedDynamicRegistries<ServerDynamicRegistryType> combinedRegistries) {
		Stream<DynamicRegistryManager.Entry<?>> stream = combinedRegistries.get(ServerDynamicRegistryType.STATIC).streamAllRegistries();
		Stream<DynamicRegistryManager.Entry<?>> stream2 = streamDynamicEntries(combinedRegistries);
		return Stream.concat(stream2, stream);
	}

	static record Info<E>(RegistryKey<? extends Registry<E>> key, Codec<E> networkCodec) {
	}
}
