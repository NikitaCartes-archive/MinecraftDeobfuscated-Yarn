/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;

public class TagPacketSerializer {
    public static Map<RegistryKey<? extends Registry<?>>, Serialized> serializeTags(DynamicRegistryManager dynamicRegistryManager) {
        return dynamicRegistryManager.streamSyncedRegistries().map(entry -> Pair.of(entry.key(), TagPacketSerializer.serializeTags(entry.value()))).filter(pair -> !((Serialized)pair.getSecond()).isEmpty()).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    private static <T> Serialized serializeTags(Registry<T> registry) {
        HashMap<Identifier, IntList> map = new HashMap<Identifier, IntList>();
        registry.streamTagsAndEntries().forEach(pair -> {
            RegistryEntryList registryEntryList = (RegistryEntryList)pair.getSecond();
            IntArrayList intList = new IntArrayList(registryEntryList.size());
            for (RegistryEntry registryEntry : registryEntryList) {
                if (registryEntry.getType() != RegistryEntry.Type.REFERENCE) {
                    throw new IllegalStateException("Can't serialize unregistered value " + registryEntry);
                }
                intList.add(registry.getRawId(registryEntry.value()));
            }
            map.put(((TagKey)pair.getFirst()).id(), intList);
        });
        return new Serialized(map);
    }

    public static <T> void loadTags(RegistryKey<? extends Registry<T>> registryKey, Registry<T> registry, Serialized serialized, Loader<T> loader) {
        serialized.contents.forEach((tagId, rawIds) -> {
            TagKey tagKey = TagKey.of(registryKey, tagId);
            List list = rawIds.intStream().mapToObj(registry::getEntry).flatMap(Optional::stream).toList();
            loader.accept(tagKey, list);
        });
    }

    public static final class Serialized {
        final Map<Identifier, IntList> contents;

        Serialized(Map<Identifier, IntList> contents) {
            this.contents = contents;
        }

        public void writeBuf(PacketByteBuf buf) {
            buf.writeMap(this.contents, PacketByteBuf::writeIdentifier, PacketByteBuf::writeIntList);
        }

        public static Serialized fromBuf(PacketByteBuf buf) {
            return new Serialized(buf.readMap(PacketByteBuf::readIdentifier, PacketByteBuf::readIntList));
        }

        public boolean isEmpty() {
            return this.contents.isEmpty();
        }
    }

    @FunctionalInterface
    public static interface Loader<T> {
        public void accept(TagKey<T> var1, List<RegistryEntry<T>> var2);
    }
}

