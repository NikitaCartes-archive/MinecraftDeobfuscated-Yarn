/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class TagContainer<T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final int JSON_EXTENSION_LENGTH = ".json".length();
    private final Map<Identifier, Tag<T>> idMap = Maps.newHashMap();
    private final Function<Identifier, Optional<T>> getter;
    private final String dataType;
    private final boolean ordered;
    private final String entryType;

    public TagContainer(Function<Identifier, Optional<T>> function, String string, boolean bl, String string2) {
        this.getter = function;
        this.dataType = string;
        this.ordered = bl;
        this.entryType = string2;
    }

    public void add(Tag<T> tag) {
        if (this.idMap.containsKey(tag.getId())) {
            throw new IllegalArgumentException("Duplicate " + this.entryType + " tag '" + tag.getId() + "'");
        }
        this.idMap.put(tag.getId(), tag);
    }

    @Nullable
    public Tag<T> get(Identifier identifier) {
        return this.idMap.get(identifier);
    }

    public Tag<T> getOrCreate(Identifier identifier) {
        Tag<T> tag = this.idMap.get(identifier);
        if (tag == null) {
            return new Tag(identifier);
        }
        return tag;
    }

    public Collection<Identifier> getKeys() {
        return this.idMap.keySet();
    }

    @Environment(value=EnvType.CLIENT)
    public Collection<Identifier> getTagsFor(T object) {
        ArrayList<Identifier> list = Lists.newArrayList();
        for (Map.Entry<Identifier, Tag<T>> entry : this.idMap.entrySet()) {
            if (!entry.getValue().contains(object)) continue;
            list.add(entry.getKey());
        }
        return list;
    }

    public void clear() {
        this.idMap.clear();
    }

    public CompletableFuture<Map<Identifier, Tag.Builder<T>>> prepareReload(ResourceManager resourceManager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            HashMap map = Maps.newHashMap();
            for (Identifier identifier : resourceManager.findResources(this.dataType, string -> string.endsWith(".json"))) {
                String string2 = identifier.getPath();
                Identifier identifier2 = new Identifier(identifier.getNamespace(), string2.substring(this.dataType.length() + 1, string2.length() - JSON_EXTENSION_LENGTH));
                try {
                    for (Resource resource : resourceManager.getAllResources(identifier)) {
                        try {
                            JsonObject jsonObject = JsonHelper.deserialize(GSON, IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8), JsonObject.class);
                            if (jsonObject == null) {
                                LOGGER.error("Couldn't load {} tag list {} from {} in data pack {} as it's empty or null", (Object)this.entryType, (Object)identifier2, (Object)identifier, (Object)resource.getResourcePackName());
                                continue;
                            }
                            Tag.Builder<T> builder = map.getOrDefault(identifier2, Tag.Builder.create());
                            builder.fromJson(this.getter, jsonObject);
                            map.put(identifier2, builder);
                        } catch (IOException | RuntimeException exception) {
                            LOGGER.error("Couldn't read {} tag list {} from {} in data pack {}", (Object)this.entryType, (Object)identifier2, (Object)identifier, (Object)resource.getResourcePackName(), (Object)exception);
                        } finally {
                            IOUtils.closeQuietly((Closeable)resource);
                        }
                    }
                } catch (IOException iOException) {
                    LOGGER.error("Couldn't read {} tag list {} from {}", (Object)this.entryType, (Object)identifier2, (Object)identifier, (Object)iOException);
                }
            }
            return map;
        }, executor);
    }

    public void applyReload(Map<Identifier, Tag.Builder<T>> map) {
        while (!map.isEmpty()) {
            boolean bl = false;
            Iterator<Map.Entry<Identifier, Tag.Builder<T>>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Identifier, Tag.Builder<T>> entry = iterator.next();
                if (!entry.getValue().applyTagGetter(this::get)) continue;
                bl = true;
                this.add(entry.getValue().build(entry.getKey()));
                iterator.remove();
            }
            if (bl) continue;
            for (Map.Entry<Identifier, Tag.Builder<T>> entry : map.entrySet()) {
                LOGGER.error("Couldn't load {} tag {} as it either references another tag that doesn't exist, or ultimately references itself", (Object)this.entryType, (Object)entry.getKey());
            }
        }
        for (Map.Entry<Identifier, Tag.Builder<T>> entry2 : map.entrySet()) {
            this.add(entry2.getValue().ordered(this.ordered).build(entry2.getKey()));
        }
    }

    public Map<Identifier, Tag<T>> getEntries() {
        return this.idMap;
    }
}

