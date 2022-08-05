package net.minecraft.tag;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;

public class TagPacketSerializer {
	public static Map<RegistryKey<? extends Registry<?>>, TagPacketSerializer.Serialized> serializeTags(DynamicRegistryManager dynamicRegistryManager) {
		return (Map<RegistryKey<? extends Registry<?>>, TagPacketSerializer.Serialized>)dynamicRegistryManager.streamSyncedRegistries()
			.map(registry -> Pair.of(registry.key(), serializeTags(registry.value())))
			.filter(pair -> !((TagPacketSerializer.Serialized)pair.getSecond()).isEmpty())
			.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
	}

	private static <T> TagPacketSerializer.Serialized serializeTags(Registry<T> registry) {
		Map<Identifier, IntList> map = new HashMap();
		registry.streamTagsAndEntries().forEach(pair -> {
			RegistryEntryList<T> registryEntryList = (RegistryEntryList<T>)pair.getSecond();
			IntList intList = new IntArrayList(registryEntryList.size());

			for (RegistryEntry<T> registryEntry : registryEntryList) {
				if (registryEntry.getType() != RegistryEntry.Type.REFERENCE) {
					throw new IllegalStateException("Can't serialize unregistered value " + registryEntry);
				}

				intList.add(registry.getRawId(registryEntry.value()));
			}

			map.put(((TagKey)pair.getFirst()).id(), intList);
		});
		return new TagPacketSerializer.Serialized(map);
	}

	public static <T> void loadTags(
		RegistryKey<? extends Registry<T>> registryKey, Registry<T> registry, TagPacketSerializer.Serialized serialized, TagPacketSerializer.Loader<T> loader
	) {
		serialized.contents.forEach((tagId, rawIds) -> {
			TagKey<T> tagKey = TagKey.of(registryKey, tagId);
			List<RegistryEntry<T>> list = rawIds.intStream().mapToObj(registry::getEntry).flatMap(Optional::stream).toList();
			loader.accept(tagKey, list);
		});
	}

	@FunctionalInterface
	public interface Loader<T> {
		void accept(TagKey<T> tag, List<RegistryEntry<T>> entries);
	}

	/**
	 * A serialization-friendly POJO representation of a {@linkplain
	 * net.minecraft.util.registry.RegistryEntryList registry entry list} of tags.
	 * This allows easy transport of tags over Minecraft network protocol.
	 * 
	 * <p>This stores tag entries with raw integer IDs and requires a registry
	 * for raw ID access to serialize or deserialize tags.
	 */
	public static final class Serialized {
		final Map<Identifier, IntList> contents;

		Serialized(Map<Identifier, IntList> contents) {
			this.contents = contents;
		}

		public void writeBuf(PacketByteBuf buf) {
			buf.writeMap(this.contents, PacketByteBuf::writeIdentifier, PacketByteBuf::writeIntList);
		}

		public static TagPacketSerializer.Serialized fromBuf(PacketByteBuf buf) {
			return new TagPacketSerializer.Serialized(buf.readMap(PacketByteBuf::readIdentifier, PacketByteBuf::readIntList));
		}

		public boolean isEmpty() {
			return this.contents.isEmpty();
		}
	}
}
