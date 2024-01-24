package net.minecraft.registry;

import com.mojang.serialization.DynamicOps;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class SerializableRegistries {
	private static final Set<RegistryKey<? extends Registry<?>>> SYNCED_REGISTRIES = (Set<RegistryKey<? extends Registry<?>>>)RegistryLoader.SYNCED_REGISTRIES
		.stream()
		.map(RegistryLoader.Entry::key)
		.collect(Collectors.toUnmodifiableSet());

	public static void forEachSyncedRegistry(
		DynamicOps<NbtElement> nbtOps,
		DynamicRegistryManager registryManager,
		BiConsumer<RegistryKey<? extends Registry<?>>, List<SerializableRegistries.SerializedRegistryEntry>> callback
	) {
		RegistryLoader.SYNCED_REGISTRIES.forEach(entry -> serialize(nbtOps, entry, registryManager, callback));
	}

	private static <T> void serialize(
		DynamicOps<NbtElement> nbtOps,
		RegistryLoader.Entry<T> entry,
		DynamicRegistryManager registryManager,
		BiConsumer<RegistryKey<? extends Registry<?>>, List<SerializableRegistries.SerializedRegistryEntry>> callback
	) {
		registryManager.getOptional(entry.key())
			.ifPresent(
				registry -> {
					List<SerializableRegistries.SerializedRegistryEntry> list = new ArrayList(registry.size());
					registry.streamEntries()
						.forEach(
							registryEntry -> {
								NbtElement nbtElement = Util.getResult(
									entry.elementCodec().encodeStart(nbtOps, (T)registryEntry.value()),
									error -> new IllegalArgumentException("Failed to serialize " + registryEntry.registryKey() + ": " + error)
								);
								list.add(new SerializableRegistries.SerializedRegistryEntry(registryEntry.registryKey().getValue(), nbtElement));
							}
						);
					callback.accept(registry.getKey(), list);
				}
			);
	}

	private static Stream<DynamicRegistryManager.Entry<?>> stream(DynamicRegistryManager dynamicRegistryManager) {
		return dynamicRegistryManager.streamAllRegistries().filter(registry -> SYNCED_REGISTRIES.contains(registry.key()));
	}

	public static Stream<DynamicRegistryManager.Entry<?>> streamDynamicEntries(CombinedDynamicRegistries<ServerDynamicRegistryType> combinedRegistries) {
		return stream(combinedRegistries.getSucceedingRegistryManagers(ServerDynamicRegistryType.WORLDGEN));
	}

	public static Stream<DynamicRegistryManager.Entry<?>> streamRegistryManagerEntries(CombinedDynamicRegistries<ServerDynamicRegistryType> combinedRegistries) {
		Stream<DynamicRegistryManager.Entry<?>> stream = combinedRegistries.get(ServerDynamicRegistryType.STATIC).streamAllRegistries();
		Stream<DynamicRegistryManager.Entry<?>> stream2 = streamDynamicEntries(combinedRegistries);
		return Stream.concat(stream2, stream);
	}

	public static record SerializedRegistryEntry(Identifier id, NbtElement data) {
		public static final PacketCodec<ByteBuf, SerializableRegistries.SerializedRegistryEntry> PACKET_CODEC = PacketCodec.tuple(
			Identifier.PACKET_CODEC,
			SerializableRegistries.SerializedRegistryEntry::id,
			PacketCodecs.NBT_ELEMENT,
			SerializableRegistries.SerializedRegistryEntry::data,
			SerializableRegistries.SerializedRegistryEntry::new
		);
	}
}
