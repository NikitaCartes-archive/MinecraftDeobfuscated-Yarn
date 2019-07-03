package com.mojang.realmsclient.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4352;
import net.minecraft.class_4431;

@Environment(EnvType.CLIENT)
public class ServerActivityList extends class_4352 {
	public long periodInMillis;
	public List<ServerActivity> serverActivities = new ArrayList();

	public static ServerActivityList parse(String string) {
		ServerActivityList serverActivityList = new ServerActivityList();
		JsonParser jsonParser = new JsonParser();

		try {
			JsonElement jsonElement = jsonParser.parse(string);
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			serverActivityList.periodInMillis = class_4431.method_21546("periodInMillis", jsonObject, -1L);
			JsonElement jsonElement2 = jsonObject.get("playerActivityDto");
			if (jsonElement2 != null && jsonElement2.isJsonArray()) {
				for (JsonElement jsonElement3 : jsonElement2.getAsJsonArray()) {
					ServerActivity serverActivity = ServerActivity.parse(jsonElement3.getAsJsonObject());
					serverActivityList.serverActivities.add(serverActivity);
				}
			}
		} catch (Exception var10) {
		}

		return serverActivityList;
	}
}
