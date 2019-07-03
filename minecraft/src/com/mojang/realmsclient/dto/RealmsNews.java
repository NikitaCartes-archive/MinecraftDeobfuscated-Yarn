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
public class RealmsNews extends class_4352 {
	private static final Logger LOGGER = LogManager.getLogger();
	public String newsLink;

	public static RealmsNews parse(String string) {
		RealmsNews realmsNews = new RealmsNews();

		try {
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonObject = jsonParser.parse(string).getAsJsonObject();
			realmsNews.newsLink = class_4431.method_21547("newsLink", jsonObject, null);
		} catch (Exception var4) {
			LOGGER.error("Could not parse RealmsNews: " + var4.getMessage());
		}

		return realmsNews;
	}
}
