/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.tag.TagEntry;
import net.minecraft.tag.TagFile;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class TagGroupLoader<T> {
    private static final Logger LOGGER = LogUtils.getLogger();
    final Function<Identifier, Optional<? extends T>> registryGetter;
    private final String dataType;

    public TagGroupLoader(Function<Identifier, Optional<? extends T>> registryGetter, String dataType) {
        this.registryGetter = registryGetter;
        this.dataType = dataType;
    }

    public Map<Identifier, List<TrackedEntry>> loadTags(ResourceManager resourceManager) {
        HashMap<Identifier, List<TrackedEntry>> map = Maps.newHashMap();
        ResourceFinder resourceFinder = ResourceFinder.json(this.dataType);
        for (Map.Entry<Identifier, List<Resource>> entry2 : resourceFinder.findAllResources(resourceManager).entrySet()) {
            Identifier identifier = entry2.getKey();
            Identifier identifier2 = resourceFinder.toResourceId(identifier);
            for (Resource resource : entry2.getValue()) {
                try {
                    BufferedReader reader = resource.getReader();
                    try {
                        JsonElement jsonElement = JsonParser.parseReader(reader);
                        List list = map.computeIfAbsent(identifier2, id -> new ArrayList());
                        TagFile tagFile = (TagFile)TagFile.CODEC.parse(new Dynamic<JsonElement>(JsonOps.INSTANCE, jsonElement)).getOrThrow(false, LOGGER::error);
                        if (tagFile.replace()) {
                            list.clear();
                        }
                        String string = resource.getResourcePackName();
                        tagFile.entries().forEach(entry -> list.add(new TrackedEntry((TagEntry)entry, string)));
                    } finally {
                        if (reader == null) continue;
                        ((Reader)reader).close();
                    }
                } catch (Exception exception) {
                    LOGGER.error("Couldn't read tag list {} from {} in data pack {}", identifier2, identifier, resource.getResourcePackName(), exception);
                }
            }
        }
        return map;
    }

    private static void resolveAll(Map<Identifier, List<TrackedEntry>> tags, Multimap<Identifier, Identifier> referencedTagIdsByTagId, Set<Identifier> alreadyResolved, Identifier tagId, BiConsumer<Identifier, List<TrackedEntry>> resolver) {
        if (!alreadyResolved.add(tagId)) {
            return;
        }
        referencedTagIdsByTagId.get(tagId).forEach(resolvedTagId -> TagGroupLoader.resolveAll(tags, referencedTagIdsByTagId, alreadyResolved, resolvedTagId, resolver));
        List<TrackedEntry> list = tags.get(tagId);
        if (list != null) {
            resolver.accept(tagId, list);
        }
    }

    private static boolean hasCircularDependency(Multimap<Identifier, Identifier> referencedTagIdsByTagId, Identifier tagId, Identifier referencedTagId) {
        Collection<Identifier> collection = referencedTagIdsByTagId.get(referencedTagId);
        if (collection.contains(tagId)) {
            return true;
        }
        return collection.stream().anyMatch(id -> TagGroupLoader.hasCircularDependency(referencedTagIdsByTagId, tagId, id));
    }

    private static void addReference(Multimap<Identifier, Identifier> referencedTagIdsByTagId, Identifier tagId, Identifier referencedTagId) {
        if (!TagGroupLoader.hasCircularDependency(referencedTagIdsByTagId, tagId, referencedTagId)) {
            referencedTagIdsByTagId.put(tagId, referencedTagId);
        }
    }

    private Either<Collection<TrackedEntry>, Collection<T>> resolveAll(TagEntry.ValueGetter<T> valueGetter, List<TrackedEntry> entries) {
        ImmutableSet.Builder builder = ImmutableSet.builder();
        ArrayList<TrackedEntry> list = new ArrayList<TrackedEntry>();
        for (TrackedEntry trackedEntry : entries) {
            if (trackedEntry.entry().resolve(valueGetter, builder::add)) continue;
            list.add(trackedEntry);
        }
        return list.isEmpty() ? Either.right(builder.build()) : Either.left(list);
    }

    public Map<Identifier, Collection<T>> buildGroup(Map<Identifier, List<TrackedEntry>> tags) {
        final HashMap map = Maps.newHashMap();
        TagEntry.ValueGetter valueGetter = new TagEntry.ValueGetter<T>(){

            @Override
            @Nullable
            public T direct(Identifier id) {
                return TagGroupLoader.this.registryGetter.apply(id).orElse(null);
            }

            @Override
            @Nullable
            public Collection<T> tag(Identifier id) {
                return (Collection)map.get(id);
            }
        };
        HashMultimap multimap = HashMultimap.create();
        tags.forEach((tagId, entries) -> entries.forEach(entry -> entry.entry.forEachRequiredTagId(referencedTagId -> TagGroupLoader.addReference(multimap, tagId, referencedTagId))));
        tags.forEach((tagId, entries) -> entries.forEach(entry -> entry.entry.forEachOptionalTagId(referencedTagId -> TagGroupLoader.addReference(multimap, tagId, referencedTagId))));
        HashSet set = Sets.newHashSet();
        tags.keySet().forEach(tagId -> TagGroupLoader.resolveAll(tags, multimap, set, tagId, (tagId2, entries) -> this.resolveAll(valueGetter, (List<TrackedEntry>)entries).ifLeft(missingReferences -> LOGGER.error("Couldn't load tag {} as it is missing following references: {}", tagId2, (Object)missingReferences.stream().map(Objects::toString).collect(Collectors.joining(", ")))).ifRight(resolvedEntries -> map.put((Identifier)tagId2, (Collection)resolvedEntries))));
        return map;
    }

    public Map<Identifier, Collection<T>> load(ResourceManager manager) {
        return this.buildGroup(this.loadTags(manager));
    }

    public record TrackedEntry(TagEntry entry, String source) {
        @Override
        public String toString() {
            return this.entry + " (from " + this.source + ")";
        }
    }
}

