package net.minecraft.client.realms.dto;

import com.google.common.base.Joiner;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.client.realms.util.RealmsUtil;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsServer extends ValueObject {
	private static final Logger LOGGER = LogManager.getLogger();
	public long id;
	public String remoteSubscriptionId;
	public String name;
	public String motd;
	public RealmsServer.State state;
	public String owner;
	public String ownerUUID;
	public List<PlayerInfo> players;
	public Map<Integer, RealmsWorldOptions> slots;
	public boolean expired;
	public boolean expiredTrial;
	public int daysLeft;
	public RealmsServer.WorldType worldType;
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

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.motd = description;
	}

	public void updateServerPing(RealmsServerPlayerList serverPlayerList) {
		List<String> list = Lists.<String>newArrayList();
		int i = 0;

		for (String string : serverPlayerList.players) {
			if (!string.equals(MinecraftClient.getInstance().getSession().getUuid())) {
				String string2 = "";

				try {
					string2 = RealmsUtil.uuidToName(string);
				} catch (Exception var8) {
					LOGGER.error("Could not get name for {}", string, var8);
					continue;
				}

				list.add(string2);
				i++;
			}
		}

		this.serverPing.nrOfPlayers = String.valueOf(i);
		this.serverPing.playerList = Joiner.on('\n').join(list);
	}

	public static RealmsServer parse(JsonObject node) {
		RealmsServer realmsServer = new RealmsServer();

		try {
			realmsServer.id = JsonUtils.getLongOr("id", node, -1L);
			realmsServer.remoteSubscriptionId = JsonUtils.getStringOr("remoteSubscriptionId", node, null);
			realmsServer.name = JsonUtils.getStringOr("name", node, null);
			realmsServer.motd = JsonUtils.getStringOr("motd", node, null);
			realmsServer.state = getState(JsonUtils.getStringOr("state", node, RealmsServer.State.CLOSED.name()));
			realmsServer.owner = JsonUtils.getStringOr("owner", node, null);
			if (node.get("players") != null && node.get("players").isJsonArray()) {
				realmsServer.players = parseInvited(node.get("players").getAsJsonArray());
				sortInvited(realmsServer);
			} else {
				realmsServer.players = Lists.<PlayerInfo>newArrayList();
			}

			realmsServer.daysLeft = JsonUtils.getIntOr("daysLeft", node, 0);
			realmsServer.expired = JsonUtils.getBooleanOr("expired", node, false);
			realmsServer.expiredTrial = JsonUtils.getBooleanOr("expiredTrial", node, false);
			realmsServer.worldType = getWorldType(JsonUtils.getStringOr("worldType", node, RealmsServer.WorldType.NORMAL.name()));
			realmsServer.ownerUUID = JsonUtils.getStringOr("ownerUUID", node, "");
			if (node.get("slots") != null && node.get("slots").isJsonArray()) {
				realmsServer.slots = parseSlots(node.get("slots").getAsJsonArray());
			} else {
				realmsServer.slots = getEmptySlots();
			}

			realmsServer.minigameName = JsonUtils.getStringOr("minigameName", node, null);
			realmsServer.activeSlot = JsonUtils.getIntOr("activeSlot", node, -1);
			realmsServer.minigameId = JsonUtils.getIntOr("minigameId", node, -1);
			realmsServer.minigameImage = JsonUtils.getStringOr("minigameImage", node, null);
		} catch (Exception var3) {
			LOGGER.error("Could not parse McoServer: {}", var3.getMessage());
		}

		return realmsServer;
	}

	private static void sortInvited(RealmsServer server) {
		server.players
			.sort(
				(a, b) -> ComparisonChain.start()
						.compareFalseFirst(b.isAccepted(), a.isAccepted())
						.compare(a.getName().toLowerCase(Locale.ROOT), b.getName().toLowerCase(Locale.ROOT))
						.result()
			);
	}

	private static List<PlayerInfo> parseInvited(JsonArray jsonArray) {
		List<PlayerInfo> list = Lists.<PlayerInfo>newArrayList();

		for (JsonElement jsonElement : jsonArray) {
			try {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				PlayerInfo playerInfo = new PlayerInfo();
				playerInfo.setName(JsonUtils.getStringOr("name", jsonObject, null));
				playerInfo.setUuid(JsonUtils.getStringOr("uuid", jsonObject, null));
				playerInfo.setOperator(JsonUtils.getBooleanOr("operator", jsonObject, false));
				playerInfo.setAccepted(JsonUtils.getBooleanOr("accepted", jsonObject, false));
				playerInfo.setOnline(JsonUtils.getBooleanOr("online", jsonObject, false));
				list.add(playerInfo);
			} catch (Exception var6) {
			}
		}

		return list;
	}

	private static Map<Integer, RealmsWorldOptions> parseSlots(JsonArray json) {
		Map<Integer, RealmsWorldOptions> map = Maps.<Integer, RealmsWorldOptions>newHashMap();

		for (JsonElement jsonElement : json) {
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

				int i = JsonUtils.getIntOr("slotId", jsonObject, -1);
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
		Map<Integer, RealmsWorldOptions> map = Maps.<Integer, RealmsWorldOptions>newHashMap();
		map.put(1, RealmsWorldOptions.getEmptyDefaults());
		map.put(2, RealmsWorldOptions.getEmptyDefaults());
		map.put(3, RealmsWorldOptions.getEmptyDefaults());
		return map;
	}

	public static RealmsServer parse(String json) {
		try {
			return parse(new JsonParser().parse(json).getAsJsonObject());
		} catch (Exception var2) {
			LOGGER.error("Could not parse McoServer: {}", var2.getMessage());
			return new RealmsServer();
		}
	}

	private static RealmsServer.State getState(String state) {
		try {
			return RealmsServer.State.valueOf(state);
		} catch (Exception var2) {
			return RealmsServer.State.CLOSED;
		}
	}

	private static RealmsServer.WorldType getWorldType(String state) {
		try {
			return RealmsServer.WorldType.valueOf(state);
		} catch (Exception var2) {
			return RealmsServer.WorldType.NORMAL;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.id, this.name, this.motd, this.state, this.owner, this.expired});
	}

	public boolean equals(Object o) {
		if (o == null) {
			return false;
		} else if (o == this) {
			return true;
		} else if (o.getClass() != this.getClass()) {
			return false;
		} else {
			RealmsServer realmsServer = (RealmsServer)o;
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

	public Map<Integer, RealmsWorldOptions> cloneSlots(Map<Integer, RealmsWorldOptions> slots) {
		Map<Integer, RealmsWorldOptions> map = Maps.<Integer, RealmsWorldOptions>newHashMap();

		for (Entry<Integer, RealmsWorldOptions> entry : slots.entrySet()) {
			map.put(entry.getKey(), ((RealmsWorldOptions)entry.getValue()).clone());
		}

		return map;
	}

	public String getWorldName(int slotId) {
		return this.name + " (" + ((RealmsWorldOptions)this.slots.get(slotId)).getSlotName(slotId) + ")";
	}

	public ServerInfo createServerInfo(String address) {
		return new ServerInfo(this.name, address, false);
	}

	@Environment(EnvType.CLIENT)
	public static class McoServerComparator implements Comparator<RealmsServer> {
		private final String refOwner;

		public McoServerComparator(String owner) {
			this.refOwner = owner;
		}

		public int compare(RealmsServer realmsServer, RealmsServer realmsServer2) {
			return ComparisonChain.start()
				.compareTrueFirst(realmsServer.state == RealmsServer.State.UNINITIALIZED, realmsServer2.state == RealmsServer.State.UNINITIALIZED)
				.compareTrueFirst(realmsServer.expiredTrial, realmsServer2.expiredTrial)
				.compareTrueFirst(realmsServer.owner.equals(this.refOwner), realmsServer2.owner.equals(this.refOwner))
				.compareFalseFirst(realmsServer.expired, realmsServer2.expired)
				.compareTrueFirst(realmsServer.state == RealmsServer.State.OPEN, realmsServer2.state == RealmsServer.State.OPEN)
				.compare(realmsServer.id, realmsServer2.id)
				.result();
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum State {
		CLOSED,
		OPEN,
		UNINITIALIZED;
	}

	@Environment(EnvType.CLIENT)
	public static enum WorldType {
		NORMAL,
		MINIGAME,
		ADVENTUREMAP,
		EXPERIENCE,
		INSPIRATION;
	}
}
