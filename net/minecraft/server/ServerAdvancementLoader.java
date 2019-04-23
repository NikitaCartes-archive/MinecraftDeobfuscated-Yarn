/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.advancement.AdvancementPositioner;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.LowercaseEnumTypeAdapterFactory;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ServerAdvancementLoader
implements SynchronousResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(Advancement.Task.class, (jsonElement, type, jsonDeserializationContext) -> {
        JsonObject jsonObject = JsonHelper.asObject(jsonElement, "advancement");
        return Advancement.Task.fromJson(jsonObject, jsonDeserializationContext);
    }).registerTypeAdapter((Type)((Object)AdvancementRewards.class), new AdvancementRewards.Deserializer()).registerTypeHierarchyAdapter(Component.class, new Component.Serializer()).registerTypeHierarchyAdapter(Style.class, new Style.Serializer()).registerTypeAdapterFactory(new LowercaseEnumTypeAdapterFactory()).create();
    private static final AdvancementManager MANAGER = new AdvancementManager();
    public static final int PATH_PREFIX_LENGTH = "advancements/".length();
    public static final int FILE_EXTENSION_LENGTH = ".json".length();
    private boolean errored;

    private Map<Identifier, Advancement.Task> scanAdvancements(ResourceManager resourceManager) {
        HashMap<Identifier, Advancement.Task> map = Maps.newHashMap();
        for (Identifier identifier : resourceManager.findResources("advancements", string -> string.endsWith(".json"))) {
            String string2 = identifier.getPath();
            Identifier identifier2 = new Identifier(identifier.getNamespace(), string2.substring(PATH_PREFIX_LENGTH, string2.length() - FILE_EXTENSION_LENGTH));
            try {
                Resource resource = resourceManager.getResource(identifier);
                Throwable throwable = null;
                try {
                    Advancement.Task task = JsonHelper.deserialize(GSON, IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8), Advancement.Task.class);
                    if (task == null) {
                        LOGGER.error("Couldn't load custom advancement {} from {} as it's empty or null", (Object)identifier2, (Object)identifier);
                        continue;
                    }
                    map.put(identifier2, task);
                } catch (Throwable throwable2) {
                    throwable = throwable2;
                    throw throwable2;
                } finally {
                    if (resource == null) continue;
                    if (throwable != null) {
                        try {
                            resource.close();
                        } catch (Throwable throwable3) {
                            throwable.addSuppressed(throwable3);
                        }
                        continue;
                    }
                    resource.close();
                }
            } catch (JsonParseException | IllegalArgumentException | InvalidIdentifierException runtimeException) {
                LOGGER.error("Parsing error loading custom advancement {}: {}", (Object)identifier2, (Object)runtimeException.getMessage());
                this.errored = true;
            } catch (IOException iOException) {
                LOGGER.error("Couldn't read custom advancement {} from {}", (Object)identifier2, (Object)identifier, (Object)iOException);
                this.errored = true;
            }
        }
        return map;
    }

    @Nullable
    public Advancement get(Identifier identifier) {
        return MANAGER.get(identifier);
    }

    public Collection<Advancement> getAdvancements() {
        return MANAGER.getAdvancements();
    }

    @Override
    public void apply(ResourceManager resourceManager) {
        this.errored = false;
        MANAGER.clear();
        Map<Identifier, Advancement.Task> map = this.scanAdvancements(resourceManager);
        MANAGER.load(map);
        for (Advancement advancement : MANAGER.getRoots()) {
            if (advancement.getDisplay() == null) continue;
            AdvancementPositioner.arrangeForTree(advancement);
        }
    }
}

