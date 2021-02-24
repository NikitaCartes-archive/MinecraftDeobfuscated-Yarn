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
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
    public Identifier getUncheckedTagId(Tag<T> var1);

    default public Collection<Identifier> getTagIds() {
        return this.getTags().keySet();
    }

    /**
     * Gets the identifiers of all tags an object is applicable to.
     */
    @Environment(value=EnvType.CLIENT)
    default public Collection<Identifier> getTagsFor(T object) {
        ArrayList<Identifier> list = Lists.newArrayList();
        for (Map.Entry<Identifier, Tag<T>> entry : this.getTags().entrySet()) {
            if (!entry.getValue().contains(object)) continue;
            list.add(entry.getKey());
        }
        return list;
    }

    default public class_5748 toPacket(Registry<T> registry) {
        Map<Identifier, Tag<T>> map = this.getTags();
        HashMap map2 = Maps.newHashMapWithExpectedSize(map.size());
        map.forEach((identifier, tag) -> {
            List list = tag.values();
            IntArrayList intList = new IntArrayList(list.size());
            for (Object object : list) {
                intList.add(registry.getRawId(object));
            }
            map2.put(identifier, intList);
        });
        return new class_5748(map2);
    }

    @Environment(value=EnvType.CLIENT)
    public static <T> TagGroup<T> method_33155(class_5748 arg, Registry<? extends T> registry) {
        HashMap map = Maps.newHashMapWithExpectedSize(arg.field_28304.size());
        arg.field_28304.forEach((identifier, intList) -> {
            ImmutableSet.Builder builder = ImmutableSet.builder();
            IntListIterator intListIterator = intList.iterator();
            while (intListIterator.hasNext()) {
                int i = (Integer)intListIterator.next();
                builder.add(registry.get(i));
            }
            map.put((Identifier)identifier, Tag.of(builder.build()));
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

    public static class class_5748 {
        private final Map<Identifier, IntList> field_28304;

        private class_5748(Map<Identifier, IntList> map) {
            this.field_28304 = map;
        }

        public void method_33159(PacketByteBuf packetByteBuf) {
            packetByteBuf.method_34063(this.field_28304, PacketByteBuf::writeIdentifier, PacketByteBuf::method_34060);
        }

        public static class_5748 method_33160(PacketByteBuf packetByteBuf) {
            return new class_5748(packetByteBuf.method_34067(PacketByteBuf::readIdentifier, PacketByteBuf::method_34059));
        }
    }
}

