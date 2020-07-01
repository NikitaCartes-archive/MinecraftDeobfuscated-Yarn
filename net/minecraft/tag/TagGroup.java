/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.tag.SetTag;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DefaultedRegistry;
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

    default public Identifier getTagId(Tag<T> tag) {
        Identifier identifier = this.getUncheckedTagId(tag);
        if (identifier == null) {
            throw new IllegalStateException("Unrecognized tag");
        }
        return identifier;
    }

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

    default public void toPacket(PacketByteBuf buf, DefaultedRegistry<T> registry) {
        Map<Identifier, Tag<T>> map = this.getTags();
        buf.writeVarInt(map.size());
        for (Map.Entry<Identifier, Tag<T>> entry : map.entrySet()) {
            buf.writeIdentifier(entry.getKey());
            buf.writeVarInt(entry.getValue().values().size());
            for (T object : entry.getValue().values()) {
                buf.writeVarInt(registry.getRawId(object));
            }
        }
    }

    public static <T> TagGroup<T> fromPacket(PacketByteBuf buf, Registry<T> registry) {
        HashMap<Identifier, Tag<T>> map = Maps.newHashMap();
        int i = buf.readVarInt();
        for (int j = 0; j < i; ++j) {
            Identifier identifier = buf.readIdentifier();
            int k = buf.readVarInt();
            ImmutableSet.Builder builder = ImmutableSet.builder();
            for (int l = 0; l < k; ++l) {
                builder.add(registry.get(buf.readVarInt()));
            }
            map.put(identifier, Tag.of(builder.build()));
        }
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
}

