package net.minecraft.client.realms.dto;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.ProfileResult;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsServerPlayerList extends ValueObject {
	private static final Logger LOGGER = LogUtils.getLogger();
	public Map<Long, List<ProfileResult>> serverIdToPlayers = Map.of();

	public static RealmsServerPlayerList parse(String json) {
		RealmsServerPlayerList realmsServerPlayerList = new RealmsServerPlayerList();
		Builder<Long, List<ProfileResult>> builder = ImmutableMap.builder();

		try {
			JsonObject jsonObject = JsonHelper.deserialize(json);
			if (JsonHelper.hasArray(jsonObject, "lists")) {
				for (JsonElement jsonElement : jsonObject.getAsJsonArray("lists")) {
					JsonObject jsonObject2 = jsonElement.getAsJsonObject();
					String string = JsonUtils.getNullableStringOr("playerList", jsonObject2, null);
					List<ProfileResult> list;
					if (string != null) {
						JsonElement jsonElement2 = JsonParser.parseString(string);
						if (jsonElement2.isJsonArray()) {
							list = parsePlayers(jsonElement2.getAsJsonArray());
						} else {
							list = Lists.<ProfileResult>newArrayList();
						}
					} else {
						list = Lists.<ProfileResult>newArrayList();
					}

					builder.put(JsonUtils.getLongOr("serverId", jsonObject2, -1L), list);
				}
			}
		} catch (Exception var11) {
			LOGGER.error("Could not parse RealmsServerPlayerLists: {}", var11.getMessage());
		}

		realmsServerPlayerList.serverIdToPlayers = builder.build();
		return realmsServerPlayerList;
	}

	private static List<ProfileResult> parsePlayers(JsonArray jsonArray) {
		List<ProfileResult> list = new ArrayList(jsonArray.size());
		MinecraftSessionService minecraftSessionService = MinecraftClient.getInstance().getSessionService();

		for (JsonElement jsonElement : jsonArray) {
			if (jsonElement.isJsonObject()) {
				UUID uUID = JsonUtils.getUuidOr("playerId", jsonElement.getAsJsonObject(), null);
				if (uUID != null && !MinecraftClient.getInstance().uuidEquals(uUID)) {
					try {
						ProfileResult profileResult = minecraftSessionService.fetchProfile(uUID, false);
						if (profileResult != null) {
							list.add(profileResult);
						}
					} catch (Exception var7) {
						LOGGER.error("Could not get name for {}", uUID, var7);
					}
				}
			}
		}

		return list;
	}

	public List<ProfileResult> get(long serverId) {
		List<ProfileResult> list = (List<ProfileResult>)this.serverIdToPlayers.get(serverId);
		return list != null ? list : List.of();
	}
}
