/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.server.ServerConfigEntry;

public class WhitelistEntry
extends ServerConfigEntry<GameProfile> {
    public WhitelistEntry(GameProfile gameProfile) {
        super(gameProfile);
    }

    public WhitelistEntry(JsonObject jsonObject) {
        super(WhitelistEntry.deserializeProfile(jsonObject), jsonObject);
    }

    @Override
    protected void serialize(JsonObject jsonObject) {
        if (this.getKey() == null) {
            return;
        }
        jsonObject.addProperty("uuid", ((GameProfile)this.getKey()).getId() == null ? "" : ((GameProfile)this.getKey()).getId().toString());
        jsonObject.addProperty("name", ((GameProfile)this.getKey()).getName());
        super.serialize(jsonObject);
    }

    private static GameProfile deserializeProfile(JsonObject jsonObject) {
        UUID uUID;
        if (!jsonObject.has("uuid") || !jsonObject.has("name")) {
            return null;
        }
        String string = jsonObject.get("uuid").getAsString();
        try {
            uUID = UUID.fromString(string);
        } catch (Throwable throwable) {
            return null;
        }
        return new GameProfile(uUID, jsonObject.get("name").getAsString());
    }
}

