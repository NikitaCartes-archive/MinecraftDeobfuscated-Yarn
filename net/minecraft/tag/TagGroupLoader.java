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
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagGroupLoader<T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final String JSON_EXTENSION = ".json";
    private static final int JSON_EXTENSION_LENGTH = ".json".length();
    private final Function<Identifier, Optional<T>> registryGetter;
    private final String dataType;

    public TagGroupLoader(Function<Identifier, Optional<T>> registryGetter, String dataType) {
        this.registryGetter = registryGetter;
        this.dataType = dataType;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<Identifier, Tag.Builder> loadTags(ResourceManager manager) {
        HashMap<Identifier, Tag.Builder> map = Maps.newHashMap();
        for (Identifier identifier2 : manager.findResources(this.dataType, string -> string.endsWith(JSON_EXTENSION))) {
            String string2 = identifier2.getPath();
            Identifier identifier22 = new Identifier(identifier2.getNamespace(), string2.substring(this.dataType.length() + 1, string2.length() - JSON_EXTENSION_LENGTH));
            try {
                for (Resource resource : manager.getAllResources(identifier2)) {
                    try {
                        InputStream inputStream = resource.getInputStream();
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));){
                            JsonObject jsonObject = JsonHelper.deserialize(GSON, (Reader)reader, JsonObject.class);
                            if (jsonObject == null) {
                                LOGGER.error("Couldn't load tag list {} from {} in data pack {} as it is empty or null", (Object)identifier22, (Object)identifier2, (Object)resource.getResourcePackName());
                                continue;
                            }
                            map.computeIfAbsent(identifier22, identifier -> Tag.Builder.create()).read(jsonObject, resource.getResourcePackName());
                        } finally {
                            if (inputStream == null) continue;
                            inputStream.close();
                        }
                    } catch (IOException | RuntimeException exception) {
                        LOGGER.error("Couldn't read tag list {} from {} in data pack {}", (Object)identifier22, (Object)identifier2, (Object)resource.getResourcePackName(), (Object)exception);
                    } finally {
                        IOUtils.closeQuietly((Closeable)resource);
                    }
                }
            } catch (IOException iOException) {
                LOGGER.error("Couldn't read tag list {} from {}", (Object)identifier22, (Object)identifier2, (Object)iOException);
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

    public TagGroup<T> buildGroup(Map<Identifier, Tag.Builder> tags) {
        HashMap map = Maps.newHashMap();
        Function<Identifier, Tag> function = map::get;
        Function<Identifier, Object> function2 = identifier -> this.registryGetter.apply((Identifier)identifier).orElse(null);
        HashMultimap multimap = HashMultimap.create();
        tags.forEach((identifier, builder) -> builder.forEachTagId(identifier2 -> TagGroupLoader.method_32844(multimap, identifier, identifier2)));
        tags.forEach((identifier, builder) -> builder.forEachGroupId(identifier2 -> TagGroupLoader.method_32844(multimap, identifier, identifier2)));
        HashSet set = Sets.newHashSet();
        tags.keySet().forEach(identifier2 -> TagGroupLoader.method_32839(tags, multimap, set, identifier2, (identifier, builder) -> builder.build(function, function2).ifLeft(collection -> LOGGER.error("Couldn't load tag {} as it is missing following references: {}", identifier, (Object)collection.stream().map(Objects::toString).collect(Collectors.joining(",")))).ifRight(tag -> map.put((Identifier)identifier, tag))));
        return TagGroup.create(map);
    }

    public TagGroup<T> load(ResourceManager manager) {
        return this.buildGroup(this.loadTags(manager));
    }
}

