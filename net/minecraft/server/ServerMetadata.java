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
import net.minecraft.network.chat.Component;
import net.minecraft.util.JsonHelper;

public class ServerMetadata {
    private Component description;
    private Players players;
    private Version version;
    private String favicon;

    public Component getDescription() {
        return this.description;
    }

    public void setDescription(Component component) {
        this.description = component;
    }

    public Players getPlayers() {
        return this.players;
    }

    public void setPlayers(Players players) {
        this.players = players;
    }

    public Version getVersion() {
        return this.version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public void setFavicon(String string) {
        this.favicon = string;
    }

    public String getFavicon() {
        return this.favicon;
    }

    public static class Deserializer
    implements JsonDeserializer<ServerMetadata>,
    JsonSerializer<ServerMetadata> {
        public ServerMetadata method_12691(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "status");
            ServerMetadata serverMetadata = new ServerMetadata();
            if (jsonObject.has("description")) {
                serverMetadata.setDescription((Component)jsonDeserializationContext.deserialize(jsonObject.get("description"), (Type)((Object)Component.class)));
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
            return serverMetadata;
        }

        public JsonElement method_12692(ServerMetadata serverMetadata, Type type, JsonSerializationContext jsonSerializationContext) {
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

        @Override
        public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
            return this.method_12692((ServerMetadata)object, type, jsonSerializationContext);
        }

        @Override
        public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return this.method_12691(jsonElement, type, jsonDeserializationContext);
        }
    }

    public static class Version {
        private final String gameVersion;
        private final int protocolVersion;

        public Version(String string, int i) {
            this.gameVersion = string;
            this.protocolVersion = i;
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
            public Version method_12695(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                JsonObject jsonObject = JsonHelper.asObject(jsonElement, "version");
                return new Version(JsonHelper.getString(jsonObject, "name"), JsonHelper.getInt(jsonObject, "protocol"));
            }

            public JsonElement method_12696(Version version, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("name", version.getGameVersion());
                jsonObject.addProperty("protocol", version.getProtocolVersion());
                return jsonObject;
            }

            @Override
            public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
                return this.method_12696((Version)object, type, jsonSerializationContext);
            }

            @Override
            public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return this.method_12695(jsonElement, type, jsonDeserializationContext);
            }
        }
    }

    public static class Players {
        private final int max;
        private final int online;
        private GameProfile[] sample;

        public Players(int i, int j) {
            this.max = i;
            this.online = j;
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

        public void setSample(GameProfile[] gameProfiles) {
            this.sample = gameProfiles;
        }

        public static class Deserializer
        implements JsonDeserializer<Players>,
        JsonSerializer<Players> {
            public Players method_12689(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
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

            public JsonElement method_12690(Players players, Type type, JsonSerializationContext jsonSerializationContext) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("max", players.getPlayerLimit());
                jsonObject.addProperty("online", players.getOnlinePlayerCount());
                if (players.getSample() != null && players.getSample().length > 0) {
                    JsonArray jsonArray = new JsonArray();
                    for (int i = 0; i < players.getSample().length; ++i) {
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

            @Override
            public /* synthetic */ JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
                return this.method_12690((Players)object, type, jsonSerializationContext);
            }

            @Override
            public /* synthetic */ Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return this.method_12689(jsonElement, type, jsonDeserializationContext);
            }
        }
    }
}

