package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4352;
import net.minecraft.class_4431;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class WorldTemplate extends class_4352 {
	private static final Logger LOGGER = LogManager.getLogger();
	public String id;
	public String name;
	public String version;
	public String author;
	public String link;
	public String image;
	public String trailer;
	public String recommendedPlayers;
	public WorldTemplate.Type type;

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
			worldTemplate.type = WorldTemplate.Type.valueOf(class_4431.method_21547("type", jsonObject, WorldTemplate.Type.WORLD_TEMPLATE.name()));
		} catch (Exception var3) {
			LOGGER.error("Could not parse WorldTemplate: " + var3.getMessage());
		}

		return worldTemplate;
	}

	@Environment(EnvType.CLIENT)
	public static enum Type {
		WORLD_TEMPLATE,
		MINIGAME,
		ADVENTUREMAP,
		EXPERIENCE,
		INSPIRATION;
	}
}
