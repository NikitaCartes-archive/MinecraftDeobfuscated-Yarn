/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import org.slf4j.Logger;

/**
 * An abstract implementation of resource reloader that reads JSON files
 * into Gson representations in the prepare stage.
 */
public abstract class JsonDataLoader
extends SinglePreparationResourceReloader<Map<Identifier, JsonElement>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String FILE_SUFFIX = ".json";
    private static final int FILE_SUFFIX_LENGTH = ".json".length();
    private final Gson gson;
    private final String dataType;

    public JsonDataLoader(Gson gson, String dataType) {
        this.gson = gson;
        this.dataType = dataType;
    }

    @Override
    protected Map<Identifier, JsonElement> prepare(ResourceManager resourceManager, Profiler profiler) {
        HashMap<Identifier, JsonElement> map = Maps.newHashMap();
        int i = this.dataType.length() + 1;
        for (Map.Entry<Identifier, Resource> entry : resourceManager.findResources(this.dataType, id -> id.getPath().endsWith(FILE_SUFFIX)).entrySet()) {
            Identifier identifier = entry.getKey();
            String string = identifier.getPath();
            Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring(i, string.length() - FILE_SUFFIX_LENGTH));
            try {
                BufferedReader reader = entry.getValue().getReader();
                try {
                    JsonElement jsonElement = JsonHelper.deserialize(this.gson, (Reader)reader, JsonElement.class);
                    if (jsonElement != null) {
                        JsonElement jsonElement2 = map.put(identifier2, jsonElement);
                        if (jsonElement2 == null) continue;
                        throw new IllegalStateException("Duplicate data file ignored with ID " + identifier2);
                    }
                    LOGGER.error("Couldn't load data file {} from {} as it's null or empty", (Object)identifier2, (Object)identifier);
                } finally {
                    if (reader == null) continue;
                    ((Reader)reader).close();
                }
            } catch (JsonParseException | IOException | IllegalArgumentException exception) {
                LOGGER.error("Couldn't parse data file {} from {}", identifier2, identifier, exception);
            }
        }
        return map;
    }

    @Override
    protected /* synthetic */ Object prepare(ResourceManager manager, Profiler profiler) {
        return this.prepare(manager, profiler);
    }
}

