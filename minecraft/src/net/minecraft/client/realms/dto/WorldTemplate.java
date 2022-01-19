package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.JsonUtils;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class WorldTemplate extends ValueObject {
	private static final Logger LOGGER = LogUtils.getLogger();
	public String id = "";
	public String name = "";
	public String version = "";
	public String author = "";
	public String link = "";
	@Nullable
	public String image;
	public String trailer = "";
	public String recommendedPlayers = "";
	public WorldTemplate.WorldTemplateType type = WorldTemplate.WorldTemplateType.WORLD_TEMPLATE;

	public static WorldTemplate parse(JsonObject node) {
		WorldTemplate worldTemplate = new WorldTemplate();

		try {
			worldTemplate.id = JsonUtils.getStringOr("id", node, "");
			worldTemplate.name = JsonUtils.getStringOr("name", node, "");
			worldTemplate.version = JsonUtils.getStringOr("version", node, "");
			worldTemplate.author = JsonUtils.getStringOr("author", node, "");
			worldTemplate.link = JsonUtils.getStringOr("link", node, "");
			worldTemplate.image = JsonUtils.getStringOr("image", node, null);
			worldTemplate.trailer = JsonUtils.getStringOr("trailer", node, "");
			worldTemplate.recommendedPlayers = JsonUtils.getStringOr("recommendedPlayers", node, "");
			worldTemplate.type = WorldTemplate.WorldTemplateType.valueOf(JsonUtils.getStringOr("type", node, WorldTemplate.WorldTemplateType.WORLD_TEMPLATE.name()));
		} catch (Exception var3) {
			LOGGER.error("Could not parse WorldTemplate: {}", var3.getMessage());
		}

		return worldTemplate;
	}

	@Environment(EnvType.CLIENT)
	public static enum WorldTemplateType {
		WORLD_TEMPLATE,
		MINIGAME,
		ADVENTUREMAP,
		EXPERIENCE,
		INSPIRATION;
	}
}
