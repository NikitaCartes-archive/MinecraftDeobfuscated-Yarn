/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4352;
import net.minecraft.class_4431;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class WorldTemplate
extends class_4352 {
    private static final Logger LOGGER = LogManager.getLogger();
    public String id;
    public String name;
    public String version;
    public String author;
    public String link;
    public String image;
    public String trailer;
    public String recommendedPlayers;
    public class_4323 type;

    public static WorldTemplate parse(JsonObject jsonObject) {
        WorldTemplate worldTemplate = new WorldTemplate();
        try {
            worldTemplate.id = class_4431.method_21547("id", jsonObject, "");
            worldTemplate.name = class_4431.method_21547("name", jsonObject, "");
            worldTemplate.version = class_4431.method_21547("version", jsonObject, "");
            worldTemplate.author = class_4431.method_21547("author", jsonObject, "");
            worldTemplate.link = class_4431.method_21547("link", jsonObject, "");
            worldTemplate.image = class_4431.method_21547("image", jsonObject, null);
            worldTemplate.trailer = class_4431.method_21547("trailer", jsonObject, "");
            worldTemplate.recommendedPlayers = class_4431.method_21547("recommendedPlayers", jsonObject, "");
            worldTemplate.type = class_4323.valueOf(class_4431.method_21547("type", jsonObject, class_4323.WORLD_TEMPLATE.name()));
        } catch (Exception exception) {
            LOGGER.error("Could not parse WorldTemplate: " + exception.getMessage());
        }
        return worldTemplate;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum class_4323 {
        WORLD_TEMPLATE,
        MINIGAME,
        ADVENTUREMAP,
        EXPERIENCE,
        INSPIRATION;

    }
}

