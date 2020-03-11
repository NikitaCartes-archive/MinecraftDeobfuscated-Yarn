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
    public WhitelistEntry(GameProfile profile) {
        super(profile);
    }

    public WhitelistEntry(JsonObject json) {
        super(WhitelistEntry.profileFromJson(json));
    }

    @Override
    protected void fromJson(JsonObject json) {
        if (this.getKey() == null) {
            return;
        }
        json.addProperty("uuid", ((GameProfile)this.getKey()).getId() == null ? "" : ((GameProfile)this.getKey()).getId().toString());
        json.addProperty("name", ((GameProfile)this.getKey()).getName());
    }

    private static GameProfile profileFromJson(JsonObject json) {
        UUID uUID;
        if (!json.has("uuid") || !json.has("name")) {
            return null;
        }
        String string = json.get("uuid").getAsString();
        try {
            uUID = UUID.fromString(string);
        } catch (Throwable throwable) {
            return null;
        }
        return new GameProfile(uUID, json.get("name").getAsString());
    }
}

