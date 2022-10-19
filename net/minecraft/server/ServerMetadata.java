/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import org.jetbrains.annotations.Nullable;

/**
 * Represents metadata sent to the client. This describes the server's message of the day, online players and the protocol version.
 */
public class ServerMetadata {
    public static final int FAVICON_WIDTH = 64;
    public static final int FAVICON_HEIGHT = 64;
    @Nullable
    private Text description;
    @Nullable
    private Players players;
    @Nullable
    private Version version;
    @Nullable
    private String favicon;
    private boolean secureChatEnforced;

    @Nullable
    public Text getDescription() {
        return this.description;
    }

    public void setDescription(Text description) {
        this.description = description;
    }

    @Nullable
    public Players getPlayers() {
        return this.players;
    }

    public void setPlayers(Players players) {
        this.players = players;
    }

    @Nullable
    public Version getVersion() {
        return this.version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public void setFavicon(String favicon) {
        this.favicon = favicon;
    }

    @Nullable
    public String getFavicon() {
        return this.favicon;
    }

    public void setSecureChatEnforced(boolean secureChatEnforced) {
        this.secureChatEnforced = secureChatEnforced;
    }

    public boolean isSecureChatEnforced() {
        return this.secureChatEnforced;
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

        public static class Deserializer
        implements JsonDeserializer<Players>,
        JsonSerializer<Players> {
            @Override
            public Players deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                JsonArray jsonArray;
                JsonObject jsonObject = JsonHelper.asObject(jsonElement, "players");
                Players players = new Players(JsonHelper.getInt(jsonObject, "max"), JsonHelper.getInt(jsonObject, "online"));
                if (JsonHelper.hasArray(jsonObject, "sample") && (jsonArray = JsonHelper.getArray(jsonObject, "sample")).size() > 0) {
                    GameProfile[] gameProfiles = new GameProfile[jsonArray.size()];
                    for (int i = 0; i < gameProfiles.length; ++i) {
                        JsonObject jsonObject2 = JsonHelper.asObject(jsonArray.get(i), "player[" + i + "]");
                        String string = JsonHelper.getString(jsonObject2, "id");
                        gameProfiles[i] = new GameProfile(UUID.fromString(string), JsonHelper.getString(jsonObject2, "name"));
                    }
                    players.setSample(gameProfiles);
                }
                return players;
            }

            @Override
            public JsonElement serialize(Players players, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("max", players.getPlayerLimit());
                jsonObject.addProperty("online", players.getOnlinePlayerCount());
                GameProfile[] gameProfiles = players.getSample();
                if (gameProfiles != null && gameProfiles.length > 0) {
                    JsonArray jsonArray = new JsonArray();
                    for (int i = 0; i < gameProfiles.length; ++i) {
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

            @Override
            public /* synthetic */ JsonElement serialize(Object entry, Type unused, JsonSerializationContext context) {
                return this.serialize((Players)entry, unused, context);
            }

            @Override
            public /* synthetic */ Object deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
                return this.deserialize(json, type, context);
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

        public static class Serializer
        implements JsonDeserializer<Version>,
        JsonSerializer<Version> {
            @Override
            public Version deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                JsonObject jsonObject = JsonHelper.asObject(jsonElement, "version");
                return new Version(JsonHelper.getString(jsonObject, "name"), JsonHelper.getInt(jsonObject, "protocol"));
            }

            @Override
            public JsonElement serialize(Version version, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", version.getGameVersion());
                jsonObject.addProperty("protocol", version.getProtocolVersion());
                return jsonObject;
            }

            @Override
            public /* synthetic */ JsonElement serialize(Object entry, Type unused, JsonSerializationContext context) {
                return this.serialize((Version)entry, unused, context);
            }

            @Override
            public /* synthetic */ Object deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
                return this.deserialize(json, type, context);
            }
        }
    }

    public static class Deserializer
    implements JsonDeserializer<ServerMetadata>,
    JsonSerializer<ServerMetadata> {
        @Override
        public ServerMetadata deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "status");
            ServerMetadata serverMetadata = new ServerMetadata();
            if (jsonObject.has("description")) {
                serverMetadata.setDescription((Text)jsonDeserializationContext.deserialize(jsonObject.get("description"), (Type)((Object)Text.class)));
            }
            if (jsonObject.has("players")) {
                serverMetadata.setPlayers((Players)jsonDeserializationContext.deserialize(jsonObject.get("players"), (Type)((Object)Players.class)));
            }
            if (jsonObject.has("version")) {
                serverMetadata.setVersion((Version)jsonDeserializationContext.deserialize(jsonObject.get("version"), (Type)((Object)Version.class)));
            }
            if (jsonObject.has("favicon")) {
                serverMetadata.setFavicon(JsonHelper.getString(jsonObject, "favicon"));
            }
            if (jsonObject.has("enforcesSecureChat")) {
                serverMetadata.setSecureChatEnforced(JsonHelper.getBoolean(jsonObject, "enforcesSecureChat"));
            }
            return serverMetadata;
        }

        @Override
        public JsonElement serialize(ServerMetadata serverMetadata, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonObject = new JsonObject();
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

        @Override
        public /* synthetic */ JsonElement serialize(Object serverMetadata, Type type, JsonSerializationContext context) {
            return this.serialize((ServerMetadata)serverMetadata, type, context);
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement functionJson, Type unused, JsonDeserializationContext context) throws JsonParseException {
            return this.deserialize(functionJson, unused, context);
        }
    }
}

