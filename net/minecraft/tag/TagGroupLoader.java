/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
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
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceRef;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;

public class TagGroupLoader<T> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    private static final String JSON_EXTENSION = ".json";
    private static final int JSON_EXTENSION_LENGTH = ".json".length();
    private final Function<Identifier, Optional<T>> registryGetter;
    private final String dataType;

    public TagGroupLoader(Function<Identifier, Optional<T>> registryGetter, String dataType) {
        this.registryGetter = registryGetter;
        this.dataType = dataType;
    }

    public Map<Identifier, Tag.Builder> loadTags(ResourceManager manager) {
        HashMap<Identifier, Tag.Builder> map = Maps.newHashMap();
        for (Map.Entry<Identifier, List<ResourceRef>> entry : manager.findAllResources(this.dataType, identifier -> identifier.getPath().endsWith(JSON_EXTENSION)).entrySet()) {
            Identifier identifier2 = entry.getKey();
            String string = identifier2.getPath();
            Identifier identifier22 = new Identifier(identifier2.getNamespace(), string.substring(this.dataType.length() + 1, string.length() - JSON_EXTENSION_LENGTH));
            for (ResourceRef resourceRef : entry.getValue()) {
                try {
                    Resource resource = resourceRef.open();
                    try {
                        InputStream inputStream = resource.getInputStream();
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));){
                            JsonObject jsonObject = JsonHelper.deserialize(GSON, (Reader)reader, JsonObject.class);
                            if (jsonObject == null) {
                                throw new NullPointerException("Invalid JSON contents");
                            }
                            map.computeIfAbsent(identifier22, identifier -> Tag.Builder.create()).read(jsonObject, resourceRef.getPackName());
                        } finally {
                            if (inputStream == null) continue;
                            inputStream.close();
                        }
                    } finally {
                        if (resource == null) continue;
                        resource.close();
                    }
                } catch (Exception exception) {
                    LOGGER.error("Couldn't read tag list {} from {} in data pack {}", identifier22, identifier2, resourceRef.getPackName(), exception);
                }
            }
        }
        return map;
    }

    private static void method_32839(Map<Identifier, Tag.Builder> map, Multimap<Identifier, Identifier> multimap, Set<Identifier> set, Identifier identifier2, BiConsumer<Identifier, Tag.Builder> biConsumer) {
        if (!set.add(identifier2)) {
            return;
        }
        multimap.get(identifier2).forEach(identifier -> TagGroupLoader.method_32839(map, multimap, set, identifier, biConsumer));
        Tag.Builder builder = map.get(identifier2);
        if (builder != null) {
            biConsumer.accept(identifier2, builder);
        }
    }

    private static boolean method_32836(Multimap<Identifier, Identifier> multimap, Identifier identifier, Identifier identifier22) {
        Collection<Identifier> collection = multimap.get(identifier22);
        if (collection.contains(identifier)) {
            return true;
        }
        return collection.stream().anyMatch(identifier2 -> TagGroupLoader.method_32836(multimap, identifier, identifier2));
    }

    private static void method_32844(Multimap<Identifier, Identifier> multimap, Identifier identifier, Identifier identifier2) {
        if (!TagGroupLoader.method_32836(multimap, identifier, identifier2)) {
            multimap.put(identifier, identifier2);
        }
    }

    public Map<Identifier, Tag<T>> buildGroup(Map<Identifier, Tag.Builder> tags) {
        HashMap map = Maps.newHashMap();
        Function<Identifier, Tag> function = map::get;
        Function<Identifier, Object> function2 = id -> this.registryGetter.apply((Identifier)id).orElse(null);
        HashMultimap multimap = HashMultimap.create();
        tags.forEach((identifier, builder) -> builder.forEachTagId(identifier2 -> TagGroupLoader.method_32844(multimap, identifier, identifier2)));
        tags.forEach((identifier, builder) -> builder.forEachGroupId(identifier2 -> TagGroupLoader.method_32844(multimap, identifier, identifier2)));
        HashSet set = Sets.newHashSet();
        tags.keySet().forEach(identifier2 -> TagGroupLoader.method_32839(tags, multimap, set, identifier2, (identifier, builder) -> builder.build(function, function2).ifLeft(collection -> LOGGER.error("Couldn't load tag {} as it is missing following references: {}", identifier, (Object)collection.stream().map(Objects::toString).collect(Collectors.joining(", ")))).ifRight(tag -> map.put((Identifier)identifier, (Tag)tag))));
        return map;
    }

    public Map<Identifier, Tag<T>> load(ResourceManager manager) {
        return this.buildGroup(this.loadTags(manager));
    }
}

