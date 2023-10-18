package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.JsonUtils;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class WorldDownload extends ValueObject {
	private static final Logger LOGGER = LogUtils.getLogger();
	public String downloadLink;
	public String resourcePackUrl;
	public String resourcePackHash;

	public static WorldDownload parse(String json) {
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
		WorldDownload worldDownload = new WorldDownload();

		try {
			worldDownload.downloadLink = JsonUtils.getNullableStringOr("downloadLink", jsonObject, "");
			worldDownload.resourcePackUrl = JsonUtils.getNullableStringOr("resourcePackUrl", jsonObject, "");
			worldDownload.resourcePackHash = JsonUtils.getNullableStringOr("resourcePackHash", jsonObject, "");
		} catch (Exception var5) {
			LOGGER.error("Could not parse WorldDownload: {}", var5.getMessage());
		}

		return worldDownload;
	}
}
