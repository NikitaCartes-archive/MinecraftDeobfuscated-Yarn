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

    public OperatorEntry(GameProfile gameProfile, int i, boolean bl) {
        super(gameProfile);
        this.permissionLevel = i;
        this.bypassPlayerLimit = bl;
    }

    public OperatorEntry(JsonObject jsonObject) {
        super(OperatorEntry.getProfileFromJson(jsonObject), jsonObject);
        this.permissionLevel = jsonObject.has("level") ? jsonObject.get("level").getAsInt() : 0;
        this.bypassPlayerLimit = jsonObject.has("bypassesPlayerLimit") && jsonObject.get("bypassesPlayerLimit").getAsBoolean();
    }

    public int getPermissionLevel() {
        return this.permissionLevel;
    }

    public boolean canBypassPlayerLimit() {
        return this.bypassPlayerLimit;
    }

    @Override
    protected void serialize(JsonObject jsonObject) {
        if (this.getKey() == null) {
            return;
        }
        jsonObject.addProperty("uuid", ((GameProfile)this.getKey()).getId() == null ? "" : ((GameProfile)this.getKey()).getId().toString());
        jsonObject.addProperty("name", ((GameProfile)this.getKey()).getName());
        super.serialize(jsonObject);
        jsonObject.addProperty("level", this.permissionLevel);
        jsonObject.addProperty("bypassesPlayerLimit", this.bypassPlayerLimit);
    }

    private static GameProfile getProfileFromJson(JsonObject jsonObject) {
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

