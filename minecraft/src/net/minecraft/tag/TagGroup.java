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

	default TagGroup.class_5748 toPacket(Registry<T> registry) {
		Map<Identifier, Tag<T>> map = this.getTags();
		Map<Identifier, IntList> map2 = Maps.<Identifier, IntList>newHashMapWithExpectedSize(map.size());
		map.forEach((identifier, tag) -> {
			List<T> list = tag.values();
			IntList intList = new IntArrayList(list.size());

			for (T object : list) {
				intList.add(registry.getRawId(object));
			}

			map2.put(identifier, intList);
		});
		return new TagGroup.class_5748(map2);
	}

	@Environment(EnvType.CLIENT)
	static <T> TagGroup<T> method_33155(TagGroup.class_5748 arg, Registry<? extends T> registry) {
		Map<Identifier, Tag<T>> map = Maps.<Identifier, Tag<T>>newHashMapWithExpectedSize(arg.field_28304.size());
		arg.field_28304.forEach((identifier, intList) -> {
			Builder<T> builder = ImmutableSet.builder();

			for (int i : intList) {
				builder.add((T)registry.get(i));
			}

			map.put(identifier, Tag.of(builder.build()));
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

	public static class class_5748 {
		private final Map<Identifier, IntList> field_28304;

		private class_5748(Map<Identifier, IntList> map) {
			this.field_28304 = map;
		}

		public void method_33159(PacketByteBuf packetByteBuf) {
			packetByteBuf.writeVarInt(this.field_28304.size());

			for (Entry<Identifier, IntList> entry : this.field_28304.entrySet()) {
				packetByteBuf.writeIdentifier((Identifier)entry.getKey());
				packetByteBuf.writeVarInt(((IntList)entry.getValue()).size());
				((IntList)entry.getValue()).forEach(packetByteBuf::writeVarInt);
			}
		}

		public static TagGroup.class_5748 method_33160(PacketByteBuf packetByteBuf) {
			Map<Identifier, IntList> map = Maps.<Identifier, IntList>newHashMap();
			int i = packetByteBuf.readVarInt();

			for (int j = 0; j < i; j++) {
				Identifier identifier = packetByteBuf.readIdentifier();
				int k = packetByteBuf.readVarInt();
				IntList intList = new IntArrayList(k);

				for (int l = 0; l < k; l++) {
					intList.add(packetByteBuf.readVarInt());
				}

				map.put(identifier, intList);
			}

			return new TagGroup.class_5748(map);
		}
	}
}
