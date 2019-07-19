package net.minecraft.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Type;
import java.util.UUID;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;

public class ServerMetadata {
	private Text description;
	private ServerMetadata.Players players;
	private ServerMetadata.Version version;
	private String favicon;

	public Text getDescription() {
		return this.description;
	}

	public void setDescription(Text text) {
		this.description = text;
	}

	public ServerMetadata.Players getPlayers() {
		return this.players;
	}

	public void setPlayers(ServerMetadata.Players players) {
		this.players = players;
	}

	public ServerMetadata.Version getVersion() {
		return this.version;
	}

	public void setVersion(ServerMetadata.Version version) {
		this.version = version;
	}

	public void setFavicon(String favicon) {
		this.favicon = favicon;
	}

	public String getFavicon() {
		return this.favicon;
	}

	public static class Deserializer implements JsonDeserializer<ServerMetadata>, JsonSerializer<ServerMetadata> {
		public ServerMetadata deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "status");
			ServerMetadata serverMetadata = new ServerMetadata();
			if (jsonObject.has("description")) {
				serverMetadata.setDescription(jsonDeserializationContext.deserialize(jsonObject.get("description"), Text.class));
			}

			if (jsonObject.has("players")) {
				serverMetadata.setPlayers(jsonDeserializationContext.deserialize(jsonObject.get("players"), ServerMetadata.Players.class));
			}

			if (jsonObject.has("version")) {
				serverMetadata.setVersion(jsonDeserializationContext.deserialize(jsonObject.get("version"), ServerMetadata.Version.class));
			}

			if (jsonObject.has("favicon")) {
				serverMetadata.setFavicon(JsonHelper.getString(jsonObject, "favicon"));
			}

			return serverMetadata;
		}

		public JsonElement serialize(ServerMetadata serverMetadata, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			if (serverMetadata.getDescription() != null) {
				jsonObject.add("description", jsonSerializationContext.serialize(serverMetadata.getDescription()));
			}

			if (serverMetadata.getPlayers() != null) {
				jsonObject.add("players", jsonSerializationContext.serialize(serverMetadata.getPlayers()));
			}

			if (serverMetadata.getVersion() != null) {
				jsonObject.add("version", jsonSerializationContext.serialize(serverMetadata.getVersion()));
			}

			if (serverMetadata.getFavicon() != null) {
				jsonObject.addProperty("favicon", serverMetadata.getFavicon());
			}

			return jsonObject;
		}
	}

	public static class Players {
		private final int max;
		private final int online;
		private GameProfile[] sample;

		public Players(int max, int online) {
			this.max = max;
			this.online = online;
		}

		public int getPlayerLimit() {
			return this.max;
		}

		public int getOnlinePlayerCount() {
			return this.online;
		}

		public GameProfile[] getSample() {
			return this.sample;
		}

		public void setSample(GameProfile[] sample) {
			this.sample = sample;
		}

		public static class Deserializer implements JsonDeserializer<ServerMetadata.Players>, JsonSerializer<ServerMetadata.Players> {
			public ServerMetadata.Players deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
				JsonObject jsonObject = JsonHelper.asObject(jsonElement, "players");
				ServerMetadata.Players players = new ServerMetadata.Players(JsonHelper.getInt(jsonObject, "max"), JsonHelper.getInt(jsonObject, "online"));
				if (JsonHelper.hasArray(jsonObject, "sample")) {
					JsonArray jsonArray = JsonHelper.getArray(jsonObject, "sample");
					if (jsonArray.size() > 0) {
						GameProfile[] gameProfiles = new GameProfile[jsonArray.size()];

						for (int i = 0; i < gameProfiles.length; i++) {
							JsonObject jsonObject2 = JsonHelper.asObject(jsonArray.get(i), "player[" + i + "]");
							String string = JsonHelper.getString(jsonObject2, "id");
							gameProfiles[i] = new GameProfile(UUID.fromString(string), JsonHelper.getString(jsonObject2, "name"));
						}

						players.setSample(gameProfiles);
					}
				}

				return players;
			}

			public JsonElement serialize(ServerMetadata.Players players, Type type, JsonSerializationContext jsonSerializationContext) {
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("max", players.getPlayerLimit());
				jsonObject.addProperty("online", players.getOnlinePlayerCount());
				if (players.getSample() != null && players.getSample().length > 0) {
					JsonArray jsonArray = new JsonArray();

					for (int i = 0; i < players.getSample().length; i++) {
						JsonObject jsonObject2 = new JsonObject();
						UUID uUID = players.getSample()[i].getId();
						jsonObject2.addProperty("id", uUID == null ? "" : uUID.toString());
						jsonObject2.addProperty("name", players.getSample()[i].getName());
						jsonArray.add(jsonObject2);
					}

					jsonObject.add("sample", jsonArray);
				}

				return jsonObject;
			}
		}
	}

	public static class Version {
		private final String gameVersion;
		private final int protocolVersion;

		public Version(String gameVersion, int protocolVersion) {
			this.gameVersion = gameVersion;
			this.protocolVersion = protocolVersion;
		}

		public String getGameVersion() {
			return this.gameVersion;
		}

		public int getProtocolVersion() {
			return this.protocolVersion;
		}

		public static class Serializer implements JsonDeserializer<ServerMetadata.Version>, JsonSerializer<ServerMetadata.Version> {
			public ServerMetadata.Version deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
				JsonObject jsonObject = JsonHelper.asObject(jsonElement, "version");
				return new ServerMetadata.Version(JsonHelper.getString(jsonObject, "name"), JsonHelper.getInt(jsonObject, "protocol"));
			}

			public JsonElement serialize(ServerMetadata.Version version, Type type, JsonSerializationContext jsonSerializationContext) {
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("name", version.getGameVersion());
				jsonObject.addProperty("protocol", version.getProtocolVersion());
				return jsonObject;
			}
		}
	}
}
