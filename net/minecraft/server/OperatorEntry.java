/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.server.ServerConfigEntry;

public class OperatorEntry
extends ServerConfigEntry<GameProfile> {
    private final int permissionLevel;
    private final boolean bypassPlayerLimit;

    public OperatorEntry(GameProfile profile, int permissionLevel, boolean bypassPlayerLimit) {
        super(profile);
        this.permissionLevel = permissionLevel;
        this.bypassPlayerLimit = bypassPlayerLimit;
    }

    public OperatorEntry(JsonObject json) {
        super(OperatorEntry.getProfileFromJson(json));
        this.permissionLevel = json.has("level") ? json.get("level").getAsInt() : 0;
        this.bypassPlayerLimit = json.has("bypassesPlayerLimit") && json.get("bypassesPlayerLimit").getAsBoolean();
    }

    public int getPermissionLevel() {
        return this.permissionLevel;
    }

    public boolean canBypassPlayerLimit() {
        return this.bypassPlayerLimit;
    }

    @Override
    protected void fromJson(JsonObject json) {
        if (this.getKey() == null) {
            return;
        }
        json.addProperty("uuid", ((GameProfile)this.getKey()).getId() == null ? "" : ((GameProfile)this.getKey()).getId().toString());
        json.addProperty("name", ((GameProfile)this.getKey()).getName());
        json.addProperty("level", this.permissionLevel);
        json.addProperty("bypassesPlayerLimit", this.bypassPlayerLimit);
    }

    private static GameProfile getProfileFromJson(JsonObject json) {
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

