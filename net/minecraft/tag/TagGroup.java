/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.SetTag;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

/**
 * Contains the set of tags all of the same type.
 */
public interface TagGroup<T> {
    public Map<Identifier, Tag<T>> getTags();

    @Nullable
    default public Tag<T> getTag(Identifier id) {
        return this.getTags().get(id);
    }

    public Tag<T> getTagOrEmpty(Identifier var1);

    @Nullable
    default public Identifier method_34894(Tag.Identified<T> identified) {
        return identified.getId();
    }

    @Nullable
    public Identifier getUncheckedTagId(Tag<T> var1);

    default public boolean method_34895(Identifier identifier) {
        return this.getTags().containsKey(identifier);
    }

    default public Collection<Identifier> getTagIds() {
        return this.getTags().keySet();
    }

    /**
     * Gets the identifiers of all tags an object is applicable to.
     */
    default public Collection<Identifier> getTagsFor(T object) {
        ArrayList<Identifier> list = Lists.newArrayList();
        for (Map.Entry<Identifier, Tag<T>> entry : this.getTags().entrySet()) {
            if (!entry.getValue().contains(object)) continue;
            list.add(entry.getKey());
        }
        return list;
    }

    /**
     * Serializes this tag group.
     */
    default public Serialized serialize(Registry<T> registry) {
        Map<Identifier, Tag<T>> map = this.getTags();
        HashMap map2 = Maps.newHashMapWithExpectedSize(map.size());
        map.forEach((id, tag) -> {
            List list = tag.values();
            IntArrayList intList = new IntArrayList(list.size());
            for (Object object : list) {
                intList.add(registry.getRawId(object));
            }
            map2.put(id, intList);
        });
        return new Serialized(map2);
    }

    /**
     * Deserializes a serialized tag group.
     */
    public static <T> TagGroup<T> deserialize(Serialized serialized, Registry<? extends T> registry) {
        HashMap map = Maps.newHashMapWithExpectedSize(serialized.contents.size());
        serialized.contents.forEach((id, entries) -> {
            ImmutableSet.Builder builder = ImmutableSet.builder();
            IntListIterator intListIterator = entries.iterator();
            while (intListIterator.hasNext()) {
                int i = (Integer)intListIterator.next();
                builder.add(registry.get(i));
            }
            map.put((Identifier)id, Tag.of(builder.build()));
        });
        return TagGroup.create(map);
    }

    public static <T> TagGroup<T> createEmpty() {
        return TagGroup.create(ImmutableBiMap.of());
    }

    public static <T> TagGroup<T> create(Map<Identifier, Tag<T>> tags) {
        final ImmutableBiMap<Identifier, Tag<T>> biMap = ImmutableBiMap.copyOf(tags);
        return new TagGroup<T>(){
            private final Tag<T> emptyTag = SetTag.empty();

            @Override
            public Tag<T> getTagOrEmpty(Identifier id) {
                return biMap.getOrDefault(id, this.emptyTag);
            }

            @Override
            @Nullable
            public Identifier getUncheckedTagId(Tag<T> tag) {
                if (tag instanceof Tag.Identified) {
                    return ((Tag.Identified)tag).getId();
                }
                return (Identifier)biMap.inverse().get(tag);
            }

            @Override
            public Map<Identifier, Tag<T>> getTags() {
                return biMap;
            }
        };
    }

    public static class Serialized {
        private final Map<Identifier, IntList> contents;

        private Serialized(Map<Identifier, IntList> contents) {
            this.contents = contents;
        }

        public void writeBuf(PacketByteBuf buf) {
            buf.writeMap(this.contents, PacketByteBuf::writeIdentifier, PacketByteBuf::writeIntList);
        }

        public static Serialized fromBuf(PacketByteBuf buf) {
            return new Serialized(buf.readMap(PacketByteBuf::readIdentifier, PacketByteBuf::readIntList));
        }
    }
}

