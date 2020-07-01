/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.ValueObject;
import net.minecraft.client.realms.dto.WorldTemplate;
import net.minecraft.client.realms.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class WorldTemplatePaginatedList
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    public List<WorldTemplate> templates;
    public int page;
    public int size;
    public int total;

    public WorldTemplatePaginatedList() {
    }

    public WorldTemplatePaginatedList(int size) {
        this.templates = Collections.emptyList();
        this.page = 0;
        this.size = size;
        this.total = -1;
    }

    public static WorldTemplatePaginatedList parse(String json) {
        WorldTemplatePaginatedList worldTemplatePaginatedList = new WorldTemplatePaginatedList();
        worldTemplatePaginatedList.templates = Lists.newArrayList();
        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
            if (jsonObject.get("templates").isJsonArray()) {
                Iterator<JsonElement> iterator = jsonObject.get("templates").getAsJsonArray().iterator();
                while (iterator.hasNext()) {
                    worldTemplatePaginatedList.templates.add(WorldTemplate.parse(iterator.next().getAsJsonObject()));
                }
            }
            worldTemplatePaginatedList.page = JsonUtils.getIntOr("page", jsonObject, 0);
            worldTemplatePaginatedList.size = JsonUtils.getIntOr("size", jsonObject, 0);
            worldTemplatePaginatedList.total = JsonUtils.getIntOr("total", jsonObject, 0);
        } catch (Exception exception) {
            LOGGER.error("Could not parse WorldTemplatePaginatedList: " + exception.getMessage());
        }
        return worldTemplatePaginatedList;
    }
}

