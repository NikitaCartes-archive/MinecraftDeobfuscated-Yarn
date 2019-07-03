package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4352;
import net.minecraft.class_4431;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class WorldDownload extends class_4352 {
	private static final Logger LOGGER = LogManager.getLogger();
	public String downloadLink;
	public String resourcePackUrl;
	public String resourcePackHash;

	public static WorldDownload parse(String string) {
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = jsonParser.parse(string).getAsJsonObject();
		WorldDownload worldDownload = new WorldDownload();

		try {
			worldDownload.downloadLink = class_4431.method_21547("downloadLink", jsonObject, "");
			worldDownload.resourcePackUrl = class_4431.method_21547("resourcePackUrl", jsonObject, "");
			worldDownload.resourcePackHash = class_4431.method_21547("resourcePackHash", jsonObject, "");
		} catch (Exception var5) {
			LOGGER.error("Could not parse WorldDownload: " + var5.getMessage());
		}

		return worldDownload;
	}
}
