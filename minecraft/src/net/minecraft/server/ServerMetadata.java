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
import javax.annotation.Nullable;
import net.minecraft.text.Text;
import net.minecraft.util.JsonHelper;

/**
 * Represents metadata sent to the client. This describes the server's message of the day, online players and the protocol version.
 */
public class ServerMetadata {
	public static final int FAVICON_WIDTH = 64;
	public static final int FAVICON_HEIGHT = 64;
	@Nullable
	private Text description;
	@Nullable
	private ServerMetadata.Players players;
	@Nullable
	private ServerMetadata.Version version;
	@Nullable
	private String favicon;
	private boolean previewsChat;
	private boolean secureChatEnforced;

	@Nullable
	public Text getDescription() {
		return this.description;
	}

	public void setDescription(Text description) {
		this.description = description;
	}

	@Nullable
	public ServerMetadata.Players getPlayers() {
		return this.players;
	}

	public void setPlayers(ServerMetadata.Players players) {
		this.players = players;
	}

	@Nullable
	public ServerMetadata.Version getVersion() {
		return this.version;
	}

	public void setVersion(ServerMetadata.Version version) {
		this.version = version;
	}

	public void setFavicon(String favicon) {
		this.favicon = favicon;
	}

	@Nullable
	public String getFavicon() {
		return this.favicon;
	}

	public void setPreviewsChat(boolean previewsChat) {
		this.previewsChat = previewsChat;
	}

	public boolean shouldPreviewChat() {
		return this.previewsChat;
	}

	public void setSecureChatEnforced(boolean secureChatEnforced) {
		this.secureChatEnforced = secureChatEnforced;
	}

	public boolean isSecureChatEnforced() {
		return this.secureChatEnforced;
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

			if (jsonObject.has("previewsChat")) {
				serverMetadata.setPreviewsChat(JsonHelper.getBoolean(jsonObject, "previewsChat"));
			}

			if (jsonObject.has("enforcesSecureChat")) {
				serverMetadata.setSecureChatEnforced(JsonHelper.getBoolean(jsonObject, "enforcesSecureChat"));
			}

			return serverMetadata;
		}

		public JsonElement serialize(ServerMetadata serverMetadata, Type type, JsonSerializationContext jsonSerializationContext) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("previewsChat", serverMetadata.shouldPreviewChat());
			jsonObject.addProperty("enforcesSecureChat", serverMetadata.isSecureChatEnforced());
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
		@Nullable
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

		@Nullable
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
				GameProfile[] gameProfiles = players.getSample();
				if (gameProfiles != null && gameProfiles.length > 0) {
					JsonArray jsonArray = new JsonArray();

					for (int i = 0; i < gameProfiles.length; i++) {
						JsonObject jsonObject2 = new JsonObject();
						UUID uUID = gameProfiles[i].getId();
						jsonObject2.addProperty("id", uUID == null ? "" : uUID.toString());
						jsonObject2.addProperty("name", gameProfiles[i].getName());
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
