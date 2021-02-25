package net.minecraft.tag;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableSet.Builder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Contains the set of tags all of the same type.
 */
public interface TagGroup<T> {
	Map<Identifier, Tag<T>> getTags();

	@Nullable
	default Tag<T> getTag(Identifier id) {
		return (Tag<T>)this.getTags().get(id);
	}

	Tag<T> getTagOrEmpty(Identifier id);

	@Nullable
	Identifier getUncheckedTagId(Tag<T> tag);

	default Collection<Identifier> getTagIds() {
		return this.getTags().keySet();
	}

	/**
	 * Gets the identifiers of all tags an object is applicable to.
	 */
	@Environment(EnvType.CLIENT)
	default Collection<Identifier> getTagsFor(T object) {
		List<Identifier> list = Lists.<Identifier>newArrayList();

		for (Entry<Identifier, Tag<T>> entry : this.getTags().entrySet()) {
			if (((Tag)entry.getValue()).contains(object)) {
				list.add(entry.getKey());
			}
		}

		return list;
	}

	/**
	 * Serializes this tag group.
	 */
	default TagGroup.Serialized serialize(Registry<T> registry) {
		Map<Identifier, Tag<T>> map = this.getTags();
		Map<Identifier, IntList> map2 = Maps.<Identifier, IntList>newHashMapWithExpectedSize(map.size());
		map.forEach((id, tag) -> {
			List<T> list = tag.values();
			IntList intList = new IntArrayList(list.size());

			for (T object : list) {
				intList.add(registry.getRawId(object));
			}

			map2.put(id, intList);
		});
		return new TagGroup.Serialized(map2);
	}

	/**
	 * Deserializes a serialized tag group.
	 */
	@Environment(EnvType.CLIENT)
	static <T> TagGroup<T> deserialize(TagGroup.Serialized serialized, Registry<? extends T> registry) {
		Map<Identifier, Tag<T>> map = Maps.<Identifier, Tag<T>>newHashMapWithExpectedSize(serialized.contents.size());
		serialized.contents.forEach((id, entries) -> {
			Builder<T> builder = ImmutableSet.builder();

			for (int i : entries) {
				builder.add((T)registry.get(i));
			}

			map.put(id, Tag.of(builder.build()));
		});
		return create(map);
	}

	static <T> TagGroup<T> createEmpty() {
		return create(ImmutableBiMap.of());
	}

	static <T> TagGroup<T> create(Map<Identifier, Tag<T>> tags) {
		final BiMap<Identifier, Tag<T>> biMap = ImmutableBiMap.copyOf(tags);
		return new TagGroup<T>() {
			private final Tag<T> emptyTag = SetTag.empty();

			@Override
			public Tag<T> getTagOrEmpty(Identifier id) {
				return (Tag<T>)biMap.getOrDefault(id, this.emptyTag);
			}

			@Nullable
			@Override
			public Identifier getUncheckedTagId(Tag<T> tag) {
				return tag instanceof Tag.Identified ? ((Tag.Identified)tag).getId() : (Identifier)biMap.inverse().get(tag);
			}

			@Override
			public Map<Identifier, Tag<T>> getTags() {
				return biMap;
			}
		};
	}

	/**
	 * A serialization-friendly POJO representation of a {@linkplain
	 * TagGroup tag group}. This allows easy transport of tag groups
	 * over Minecraft network protocol.
	 * 
	 * <p>This stores tag entries with raw integer IDs and requires a registry
	 * for raw ID access to {@linkplain TagGroup#serialize(Registry) serialize}
	 * or {@linkplain TagGroup#deserialize(TagGroup.Serialized, Registry)
	 * deserialize} tag groups.
	 */
	public static class Serialized {
		private final Map<Identifier, IntList> contents;

		private Serialized(Map<Identifier, IntList> contents) {
			this.contents = contents;
		}

		public void writeBuf(PacketByteBuf buf) {
			buf.writeMap(this.contents, PacketByteBuf::writeIdentifier, PacketByteBuf::writeIntList);
		}

		public static TagGroup.Serialized fromBuf(PacketByteBuf buf) {
			return new TagGroup.Serialized(buf.readMap(PacketByteBuf::readIdentifier, PacketByteBuf::readIntList));
		}
	}
}
