/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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
import net.minecraft.util.SystemUtil;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class TagContainer<T> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private static final int JSON_EXTENSION_LENGTH = ".json".length();
    private Map<Identifier, Tag<T>> idMap = ImmutableMap.of();
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

    public CompletableFuture<Map<Identifier, Tag.Builder<T>>> prepareReload(ResourceManager resourceManager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            HashMap<Identifier, Tag.Builder> map = Maps.newHashMap();
            for (Identifier identifier2 : resourceManager.findResources(this.dataType, string -> string.endsWith(".json"))) {
                String string2 = identifier2.getPath();
                Identifier identifier22 = new Identifier(identifier2.getNamespace(), string2.substring(this.dataType.length() + 1, string2.length() - JSON_EXTENSION_LENGTH));
                try {
                    for (Resource resource : resourceManager.getAllResources(identifier2)) {
                        try {
                            InputStream inputStream = resource.getInputStream();
                            Throwable throwable = null;
                            try {
                                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                                Throwable throwable2 = null;
                                try {
                                    JsonObject jsonObject = JsonHelper.deserialize(GSON, (Reader)reader, JsonObject.class);
                                    if (jsonObject == null) {
                                        LOGGER.error("Couldn't load {} tag list {} from {} in data pack {} as it's empty or null", (Object)this.entryType, (Object)identifier22, (Object)identifier2, (Object)resource.getResourcePackName());
                                        continue;
                                    }
                                    map.computeIfAbsent(identifier22, identifier -> SystemUtil.consume(Tag.Builder.create(), builder -> builder.ordered(this.ordered))).fromJson(this.getter, jsonObject);
                                } catch (Throwable throwable3) {
                                    throwable2 = throwable3;
                                    throw throwable3;
                                } finally {
                                    if (reader == null) continue;
                                    if (throwable2 != null) {
                                        try {
                                            ((Reader)reader).close();
                                        } catch (Throwable throwable4) {
                                            throwable2.addSuppressed(throwable4);
                                        }
                                        continue;
                                    }
                                    ((Reader)reader).close();
                                }
                            } catch (Throwable throwable5) {
                                throwable = throwable5;
                                throw throwable5;
                            } finally {
                                if (inputStream == null) continue;
                                if (throwable != null) {
                                    try {
                                        inputStream.close();
                                    } catch (Throwable throwable6) {
                                        throwable.addSuppressed(throwable6);
                                    }
                                    continue;
                                }
                                inputStream.close();
                            }
                        } catch (IOException | RuntimeException exception) {
                            LOGGER.error("Couldn't read {} tag list {} from {} in data pack {}", (Object)this.entryType, (Object)identifier22, (Object)identifier2, (Object)resource.getResourcePackName(), (Object)exception);
                        } finally {
                            IOUtils.closeQuietly((Closeable)resource);
                        }
                    }
                } catch (IOException iOException) {
                    LOGGER.error("Couldn't read {} tag list {} from {}", (Object)this.entryType, (Object)identifier22, (Object)identifier2, (Object)iOException);
                }
            }
            return map;
        }, executor);
    }

    public void applyReload(Map<Identifier, Tag.Builder<T>> map) {
        HashMap map2 = Maps.newHashMap();
        while (!map.isEmpty()) {
            boolean bl = false;
            Iterator<Map.Entry<Identifier, Tag.Builder<T>>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Identifier, Tag.Builder<T>> entry = iterator.next();
                Tag.Builder builder2 = entry.getValue();
                if (!builder2.applyTagGetter(map2::get)) continue;
                bl = true;
                Identifier identifier2 = entry.getKey();
                map2.put(identifier2, builder2.build(identifier2));
                iterator.remove();
            }
            if (bl) continue;
            map.forEach((identifier, builder) -> LOGGER.error("Couldn't load {} tag {} as it either references another tag that doesn't exist, or ultimately references itself", (Object)this.entryType, identifier));
            break;
        }
        map.forEach((identifier, builder) -> map2.put((Identifier)identifier, builder.build((Identifier)identifier)));
        this.method_20735(map2);
    }

    protected void method_20735(Map<Identifier, Tag<T>> map) {
        this.idMap = ImmutableMap.copyOf(map);
    }

    public Map<Identifier, Tag<T>> getEntries() {
        return this.idMap;
    }
}

