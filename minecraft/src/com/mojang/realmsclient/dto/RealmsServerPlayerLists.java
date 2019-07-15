package com.mojang.realmsclient.dto;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4352;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsServerPlayerLists extends class_4352 {
	private static final Logger LOGGER = LogManager.getLogger();
	public List<RealmsServerPlayerList> servers;

	public static RealmsServerPlayerLists parse(String string) {
		RealmsServerPlayerLists realmsServerPlayerLists = new RealmsServerPlayerLists();
		realmsServerPlayerLists.servers = new ArrayList();

		try {
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonObject = jsonParser.parse(string).getAsJsonObject();
			if (jsonObject.get("lists").isJsonArray()) {
				JsonArray jsonArray = jsonObject.get("lists").getAsJsonArray();
				Iterator<JsonElement> iterator = jsonArray.iterator();

				while (iterator.hasNext()) {
					realmsServerPlayerLists.servers.add(RealmsServerPlayerList.parse(((JsonElement)iterator.next()).getAsJsonObject()));
				}
			}
		} catch (Exception var6) {
			LOGGER.error("Could not parse RealmsServerPlayerLists: " + var6.getMessage());
		}

		return realmsServerPlayerLists;
	}
}