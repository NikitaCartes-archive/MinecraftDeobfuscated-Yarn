package com.mojang.realmsclient.dto;

import com.google.common.collect.ComparisonChain;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4352;
import net.minecraft.class_4431;
import net.minecraft.class_4448;
import net.minecraft.realms.Realms;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsServer extends class_4352 {
	private static final Logger LOGGER = LogManager.getLogger();
	public long id;
	public String remoteSubscriptionId;
	public String name;
	public String motd;
	public RealmsServer.class_4320 state;
	public String owner;
	public String ownerUUID;
	public List<PlayerInfo> players;
	public Map<Integer, RealmsWorldOptions> slots;
	public boolean expired;
	public boolean expiredTrial;
	public int daysLeft;
	public RealmsServer.class_4321 worldType;
	public int activeSlot;
	public String minigameName;
	public int minigameId;
	public String minigameImage;
	public RealmsServerPing serverPing = new RealmsServerPing();

	public String getDescription() {
		return this.motd;
	}

	public String getName() {
		return this.name;
	}

	public String getMinigameName() {
		return this.minigameName;
	}

	public void setName(String string) {
		this.name = string;
	}

	public void setDescription(String string) {
		this.motd = string;
	}

	public void updateServerPing(RealmsServerPlayerList realmsServerPlayerList) {
		StringBuilder stringBuilder = new StringBuilder();
		int i = 0;

		for (String string : realmsServerPlayerList.players) {
			if (!string.equals(Realms.getUUID())) {
				String string2 = "";

				try {
					string2 = class_4448.method_21568(string);
				} catch (Exception var8) {
					LOGGER.error("Could not get name for " + string, (Throwable)var8);
					continue;
				}

				if (stringBuilder.length() > 0) {
					stringBuilder.append("\n");
				}

				stringBuilder.append(string2);
				i++;
			}
		}

		this.serverPing.nrOfPlayers = String.valueOf(i);
		this.serverPing.playerList = stringBuilder.toString();
	}

	public static RealmsServer parse(JsonObject jsonObject) {
		RealmsServer realmsServer = new RealmsServer();

		try {
			realmsServer.id = class_4431.method_21546("id", jsonObject, -1L);
			realmsServer.remoteSubscriptionId = class_4431.method_21547("remoteSubscriptionId", jsonObject, null);
			realmsServer.name = class_4431.method_21547("name", jsonObject, null);
			realmsServer.motd = class_4431.method_21547("motd", jsonObject, null);
			realmsServer.state = getState(class_4431.method_21547("state", jsonObject, RealmsServer.class_4320.CLOSED.name()));
			realmsServer.owner = class_4431.method_21547("owner", jsonObject, null);
			if (jsonObject.get("players") != null && jsonObject.get("players").isJsonArray()) {
				realmsServer.players = parseInvited(jsonObject.get("players").getAsJsonArray());
				sortInvited(realmsServer);
			} else {
				realmsServer.players = new ArrayList();
			}

			realmsServer.daysLeft = class_4431.method_21545("daysLeft", jsonObject, 0);
			realmsServer.expired = class_4431.method_21548("expired", jsonObject, false);
			realmsServer.expiredTrial = class_4431.method_21548("expiredTrial", jsonObject, false);
			realmsServer.worldType = getWorldType(class_4431.method_21547("worldType", jsonObject, RealmsServer.class_4321.NORMAL.name()));
			realmsServer.ownerUUID = class_4431.method_21547("ownerUUID", jsonObject, "");
			if (jsonObject.get("slots") != null && jsonObject.get("slots").isJsonArray()) {
				realmsServer.slots = parseSlots(jsonObject.get("slots").getAsJsonArray());
			} else {
				realmsServer.slots = getEmptySlots();
			}

			realmsServer.minigameName = class_4431.method_21547("minigameName", jsonObject, null);
			realmsServer.activeSlot = class_4431.method_21545("activeSlot", jsonObject, -1);
			realmsServer.minigameId = class_4431.method_21545("minigameId", jsonObject, -1);
			realmsServer.minigameImage = class_4431.method_21547("minigameImage", jsonObject, null);
		} catch (Exception var3) {
			LOGGER.error("Could not parse McoServer: " + var3.getMessage());
		}

		return realmsServer;
	}

	private static void sortInvited(RealmsServer realmsServer) {
		Collections.sort(
			realmsServer.players,
			new Comparator<PlayerInfo>() {
				public int method_20829(PlayerInfo playerInfo, PlayerInfo playerInfo2) {
					return ComparisonChain.start()
						.compare(playerInfo2.getAccepted(), playerInfo.getAccepted())
						.compare(playerInfo.getName().toLowerCase(Locale.ROOT), playerInfo2.getName().toLowerCase(Locale.ROOT))
						.result();
				}
			}
		);
	}

	private static List<PlayerInfo> parseInvited(JsonArray jsonArray) {
		ArrayList<PlayerInfo> arrayList = new ArrayList();

		for (JsonElement jsonElement : jsonArray) {
			try {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				PlayerInfo playerInfo = new PlayerInfo();
				playerInfo.setName(class_4431.method_21547("name", jsonObject, null));
				playerInfo.setUuid(class_4431.method_21547("uuid", jsonObject, null));
				playerInfo.setOperator(class_4431.method_21548("operator", jsonObject, false));
				playerInfo.setAccepted(class_4431.method_21548("accepted", jsonObject, false));
				playerInfo.setOnline(class_4431.method_21548("online", jsonObject, false));
				arrayList.add(playerInfo);
			} catch (Exception var6) {
			}
		}

		return arrayList;
	}

	private static Map<Integer, RealmsWorldOptions> parseSlots(JsonArray jsonArray) {
		Map<Integer, RealmsWorldOptions> map = new HashMap();

		for (JsonElement jsonElement : jsonArray) {
			try {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				JsonParser jsonParser = new JsonParser();
				JsonElement jsonElement2 = jsonParser.parse(jsonObject.get("options").getAsString());
				RealmsWorldOptions realmsWorldOptions;
				if (jsonElement2 == null) {
					realmsWorldOptions = RealmsWorldOptions.getDefaults();
				} else {
					realmsWorldOptions = RealmsWorldOptions.parse(jsonElement2.getAsJsonObject());
				}

				int i = class_4431.method_21545("slotId", jsonObject, -1);
				map.put(i, realmsWorldOptions);
			} catch (Exception var9) {
			}
		}

		for (int j = 1; j <= 3; j++) {
			if (!map.containsKey(j)) {
				map.put(j, RealmsWorldOptions.getEmptyDefaults());
			}
		}

		return map;
	}

	private static Map<Integer, RealmsWorldOptions> getEmptySlots() {
		HashMap<Integer, RealmsWorldOptions> hashMap = new HashMap();
		hashMap.put(1, RealmsWorldOptions.getEmptyDefaults());
		hashMap.put(2, RealmsWorldOptions.getEmptyDefaults());
		hashMap.put(3, RealmsWorldOptions.getEmptyDefaults());
		return hashMap;
	}

	public static RealmsServer parse(String string) {
		RealmsServer realmsServer = new RealmsServer();

		try {
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonObject = jsonParser.parse(string).getAsJsonObject();
			realmsServer = parse(jsonObject);
		} catch (Exception var4) {
			LOGGER.error("Could not parse McoServer: " + var4.getMessage());
		}

		return realmsServer;
	}

	private static RealmsServer.class_4320 getState(String string) {
		try {
			return RealmsServer.class_4320.valueOf(string);
		} catch (Exception var2) {
			return RealmsServer.class_4320.CLOSED;
		}
	}

	private static RealmsServer.class_4321 getWorldType(String string) {
		try {
			return RealmsServer.class_4321.valueOf(string);
		} catch (Exception var2) {
			return RealmsServer.class_4321.NORMAL;
		}
	}

	public int hashCode() {
		return new HashCodeBuilder(17, 37)
			.append(this.id)
			.append(this.name)
			.append(this.motd)
			.append(this.state)
			.append(this.owner)
			.append(this.expired)
			.toHashCode();
	}

	public boolean equals(Object object) {
		if (object == null) {
			return false;
		} else if (object == this) {
			return true;
		} else if (object.getClass() != this.getClass()) {
			return false;
		} else {
			RealmsServer realmsServer = (RealmsServer)object;
			return new EqualsBuilder()
				.append(this.id, realmsServer.id)
				.append(this.name, realmsServer.name)
				.append(this.motd, realmsServer.motd)
				.append(this.state, realmsServer.state)
				.append(this.owner, realmsServer.owner)
				.append(this.expired, realmsServer.expired)
				.append(this.worldType, this.worldType)
				.isEquals();
		}
	}

	public RealmsServer clone() {
		RealmsServer realmsServer = new RealmsServer();
		realmsServer.id = this.id;
		realmsServer.remoteSubscriptionId = this.remoteSubscriptionId;
		realmsServer.name = this.name;
		realmsServer.motd = this.motd;
		realmsServer.state = this.state;
		realmsServer.owner = this.owner;
		realmsServer.players = this.players;
		realmsServer.slots = this.cloneSlots(this.slots);
		realmsServer.expired = this.expired;
		realmsServer.expiredTrial = this.expiredTrial;
		realmsServer.daysLeft = this.daysLeft;
		realmsServer.serverPing = new RealmsServerPing();
		realmsServer.serverPing.nrOfPlayers = this.serverPing.nrOfPlayers;
		realmsServer.serverPing.playerList = this.serverPing.playerList;
		realmsServer.worldType = this.worldType;
		realmsServer.ownerUUID = this.ownerUUID;
		realmsServer.minigameName = this.minigameName;
		realmsServer.activeSlot = this.activeSlot;
		realmsServer.minigameId = this.minigameId;
		realmsServer.minigameImage = this.minigameImage;
		return realmsServer;
	}

	public Map<Integer, RealmsWorldOptions> cloneSlots(Map<Integer, RealmsWorldOptions> map) {
		Map<Integer, RealmsWorldOptions> map2 = new HashMap();

		for (Entry<Integer, RealmsWorldOptions> entry : map.entrySet()) {
			map2.put(entry.getKey(), ((RealmsWorldOptions)entry.getValue()).clone());
		}

		return map2;
	}

	@Environment(EnvType.CLIENT)
	public static class class_4319 implements Comparator<RealmsServer> {
		private final String field_19432;

		public class_4319(String string) {
			this.field_19432 = string;
		}

		public int method_20830(RealmsServer realmsServer, RealmsServer realmsServer2) {
			return ComparisonChain.start()
				.compareTrueFirst(realmsServer.state.equals(RealmsServer.class_4320.UNINITIALIZED), realmsServer2.state.equals(RealmsServer.class_4320.UNINITIALIZED))
				.compareTrueFirst(realmsServer.expiredTrial, realmsServer2.expiredTrial)
				.compareTrueFirst(realmsServer.owner.equals(this.field_19432), realmsServer2.owner.equals(this.field_19432))
				.compareFalseFirst(realmsServer.expired, realmsServer2.expired)
				.compareTrueFirst(realmsServer.state.equals(RealmsServer.class_4320.OPEN), realmsServer2.state.equals(RealmsServer.class_4320.OPEN))
				.compare(realmsServer.id, realmsServer2.id)
				.result();
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum class_4320 {
		CLOSED,
		OPEN,
		UNINITIALIZED;
	}

	@Environment(EnvType.CLIENT)
	public static enum class_4321 {
		NORMAL,
		MINIGAME,
		ADVENTUREMAP,
		EXPERIENCE,
		INSPIRATION;
	}
}
