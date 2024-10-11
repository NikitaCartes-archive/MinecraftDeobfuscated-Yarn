package net.minecraft.client.realms.dto;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.util.Util;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsServer extends ValueObject {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final int NO_PARENT = -1;
	public long id;
	@Nullable
	public String remoteSubscriptionId;
	@Nullable
	public String name;
	public String description;
	public RealmsServer.State state;
	@Nullable
	public String owner;
	public UUID ownerUUID = Util.NIL_UUID;
	public List<PlayerInfo> players;
	public Map<Integer, RealmsWorldOptions> slots;
	public boolean expired;
	public boolean expiredTrial;
	public int daysLeft;
	public RealmsServer.WorldType worldType;
	public boolean hardcore;
	public int gameMode;
	public int activeSlot;
	@Nullable
	public String minigameName;
	public int minigameId;
	@Nullable
	public String minigameImage;
	public long parentWorldId = -1L;
	@Nullable
	public String parentWorldName;
	public String activeVersion = "";
	public RealmsServer.Compatibility compatibility = RealmsServer.Compatibility.UNVERIFIABLE;

	public String getDescription() {
		return this.description;
	}

	@Nullable
	public String getName() {
		return this.name;
	}

	@Nullable
	public String getMinigameName() {
		return this.minigameName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static RealmsServer parse(JsonObject node) {
		RealmsServer realmsServer = new RealmsServer();

		try {
			realmsServer.id = JsonUtils.getLongOr("id", node, -1L);
			realmsServer.remoteSubscriptionId = JsonUtils.getNullableStringOr("remoteSubscriptionId", node, null);
			realmsServer.name = JsonUtils.getNullableStringOr("name", node, null);
			realmsServer.description = JsonUtils.getNullableStringOr("motd", node, "");
			realmsServer.state = getState(JsonUtils.getNullableStringOr("state", node, RealmsServer.State.CLOSED.name()));
			realmsServer.owner = JsonUtils.getNullableStringOr("owner", node, null);
			if (node.get("players") != null && node.get("players").isJsonArray()) {
				realmsServer.players = parseInvited(node.get("players").getAsJsonArray());
				sortInvited(realmsServer);
			} else {
				realmsServer.players = Lists.<PlayerInfo>newArrayList();
			}

			realmsServer.daysLeft = JsonUtils.getIntOr("daysLeft", node, 0);
			realmsServer.expired = JsonUtils.getBooleanOr("expired", node, false);
			realmsServer.expiredTrial = JsonUtils.getBooleanOr("expiredTrial", node, false);
			realmsServer.worldType = getWorldType(JsonUtils.getNullableStringOr("worldType", node, RealmsServer.WorldType.NORMAL.name()));
			realmsServer.hardcore = JsonUtils.getBooleanOr("isHardcore", node, false);
			realmsServer.gameMode = JsonUtils.getIntOr("gameMode", node, -1);
			realmsServer.ownerUUID = JsonUtils.getUuidOr("ownerUUID", node, Util.NIL_UUID);
			if (node.get("slots") != null && node.get("slots").isJsonArray()) {
				realmsServer.slots = parseSlots(node.get("slots").getAsJsonArray());
			} else {
				realmsServer.slots = getEmptySlots();
			}

			realmsServer.minigameName = JsonUtils.getNullableStringOr("minigameName", node, null);
			realmsServer.activeSlot = JsonUtils.getIntOr("activeSlot", node, -1);
			realmsServer.minigameId = JsonUtils.getIntOr("minigameId", node, -1);
			realmsServer.minigameImage = JsonUtils.getNullableStringOr("minigameImage", node, null);
			realmsServer.parentWorldId = JsonUtils.getLongOr("parentWorldId", node, -1L);
			realmsServer.parentWorldName = JsonUtils.getNullableStringOr("parentWorldName", node, null);
			realmsServer.activeVersion = JsonUtils.getNullableStringOr("activeVersion", node, "");
			realmsServer.compatibility = getCompatibility(JsonUtils.getNullableStringOr("compatibility", node, RealmsServer.Compatibility.UNVERIFIABLE.name()));
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
				playerInfo.setName(JsonUtils.getNullableStringOr("name", jsonObject, null));
				playerInfo.setUuid(JsonUtils.getUuidOr("uuid", jsonObject, Util.NIL_UUID));
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
				JsonElement jsonElement2 = JsonParser.parseString(jsonObject.get("options").getAsString());
				RealmsWorldSettings realmsWorldSettings = parseSettings(jsonObject.get("settings"));
				RealmsWorldOptions realmsWorldOptions;
				if (jsonElement2 == null) {
					realmsWorldOptions = RealmsWorldOptions.getDefaults();
				} else {
					realmsWorldOptions = RealmsWorldOptions.parse(jsonElement2.getAsJsonObject(), realmsWorldSettings);
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

	private static RealmsWorldSettings parseSettings(JsonElement json) {
		boolean bl = false;
		if (json.isJsonArray()) {
			for (JsonElement jsonElement : json.getAsJsonArray()) {
				JsonObject jsonObject = jsonElement.getAsJsonObject();
				bl = isSet(jsonObject, "hardcore", bl);
			}
		}

		return new RealmsWorldSettings(bl);
	}

	private static boolean isSet(JsonObject json, String name, boolean defaultValue) {
		String string = JsonUtils.getNullableStringOr("name", json, null);
		return string != null && string.equals(name) ? JsonUtils.getBooleanOr("value", json, defaultValue) : defaultValue;
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

	private static RealmsServer.WorldType getWorldType(String worldType) {
		try {
			return RealmsServer.WorldType.valueOf(worldType);
		} catch (Exception var2) {
			return RealmsServer.WorldType.NORMAL;
		}
	}

	public static RealmsServer.Compatibility getCompatibility(@Nullable String compatibility) {
		try {
			return RealmsServer.Compatibility.valueOf(compatibility);
		} catch (Exception var2) {
			return RealmsServer.Compatibility.UNVERIFIABLE;
		}
	}

	public boolean isCompatible() {
		return this.compatibility.isCompatible();
	}

	public boolean needsUpgrade() {
		return this.compatibility.needsUpgrade();
	}

	public boolean needsDowngrade() {
		return this.compatibility.needsDowngrade();
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.id, this.name, this.description, this.state, this.owner, this.expired});
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
				.append(this.description, realmsServer.description)
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
		realmsServer.description = this.description;
		realmsServer.state = this.state;
		realmsServer.owner = this.owner;
		realmsServer.players = this.players;
		realmsServer.slots = this.cloneSlots(this.slots);
		realmsServer.expired = this.expired;
		realmsServer.expiredTrial = this.expiredTrial;
		realmsServer.daysLeft = this.daysLeft;
		realmsServer.worldType = this.worldType;
		realmsServer.hardcore = this.hardcore;
		realmsServer.gameMode = this.gameMode;
		realmsServer.ownerUUID = this.ownerUUID;
		realmsServer.minigameName = this.minigameName;
		realmsServer.activeSlot = this.activeSlot;
		realmsServer.minigameId = this.minigameId;
		realmsServer.minigameImage = this.minigameImage;
		realmsServer.parentWorldName = this.parentWorldName;
		realmsServer.parentWorldId = this.parentWorldId;
		realmsServer.activeVersion = this.activeVersion;
		realmsServer.compatibility = this.compatibility;
		return realmsServer;
	}

	public Map<Integer, RealmsWorldOptions> cloneSlots(Map<Integer, RealmsWorldOptions> slots) {
		Map<Integer, RealmsWorldOptions> map = Maps.<Integer, RealmsWorldOptions>newHashMap();

		for (Entry<Integer, RealmsWorldOptions> entry : slots.entrySet()) {
			map.put((Integer)entry.getKey(), ((RealmsWorldOptions)entry.getValue()).clone());
		}

		return map;
	}

	public boolean isPrerelease() {
		return this.parentWorldId != -1L;
	}

	public boolean isMinigame() {
		return this.worldType == RealmsServer.WorldType.MINIGAME;
	}

	public String getWorldName(int slotId) {
		return this.name == null
			? ((RealmsWorldOptions)this.slots.get(slotId)).getSlotName(slotId)
			: this.name + " (" + ((RealmsWorldOptions)this.slots.get(slotId)).getSlotName(slotId) + ")";
	}

	public ServerInfo createServerInfo(String address) {
		return new ServerInfo((String)Objects.requireNonNullElse(this.name, "unknown server"), address, ServerInfo.ServerType.REALM);
	}

	@Environment(EnvType.CLIENT)
	public static enum Compatibility {
		UNVERIFIABLE,
		INCOMPATIBLE,
		RELEASE_TYPE_INCOMPATIBLE,
		NEEDS_DOWNGRADE,
		NEEDS_UPGRADE,
		COMPATIBLE;

		public boolean isCompatible() {
			return this == COMPATIBLE;
		}

		public boolean needsUpgrade() {
			return this == NEEDS_UPGRADE;
		}

		public boolean needsDowngrade() {
			return this == NEEDS_DOWNGRADE;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class McoServerComparator implements Comparator<RealmsServer> {
		private final String refOwner;

		public McoServerComparator(String owner) {
			this.refOwner = owner;
		}

		public int compare(RealmsServer realmsServer, RealmsServer realmsServer2) {
			return ComparisonChain.start()
				.compareTrueFirst(realmsServer.isPrerelease(), realmsServer2.isPrerelease())
				.compareTrueFirst(realmsServer.state == RealmsServer.State.UNINITIALIZED, realmsServer2.state == RealmsServer.State.UNINITIALIZED)
				.compareTrueFirst(realmsServer.expiredTrial, realmsServer2.expiredTrial)
				.compareTrueFirst(Objects.equals(realmsServer.owner, this.refOwner), Objects.equals(realmsServer2.owner, this.refOwner))
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
