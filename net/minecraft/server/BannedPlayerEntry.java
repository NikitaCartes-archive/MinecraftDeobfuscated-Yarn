/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.BanEntry;
import org.jetbrains.annotations.Nullable;

public class BannedPlayerEntry
extends BanEntry<GameProfile> {
    public BannedPlayerEntry(GameProfile gameProfile) {
        this(gameProfile, (Date)null, (String)null, (Date)null, (String)null);
    }

    public BannedPlayerEntry(GameProfile gameProfile, @Nullable Date date, @Nullable String string, @Nullable Date date2, @Nullable String string2) {
        super(gameProfile, date, string, date2, string2);
    }

    public BannedPlayerEntry(JsonObject jsonObject) {
        super(BannedPlayerEntry.getProfileFromJson(jsonObject), jsonObject);
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

    @Override
    public Component asTextComponent() {
        GameProfile gameProfile = (GameProfile)this.getKey();
        return new TextComponent(gameProfile.getName() != null ? gameProfile.getName() : Objects.toString(gameProfile.getId(), "(Unknown)"));
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

