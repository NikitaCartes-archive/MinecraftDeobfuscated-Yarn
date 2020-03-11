/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.server.BanEntry;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class BannedPlayerEntry
extends BanEntry<GameProfile> {
    public BannedPlayerEntry(GameProfile profile) {
        this(profile, (Date)null, (String)null, (Date)null, (String)null);
    }

    public BannedPlayerEntry(GameProfile profile, @Nullable Date created, @Nullable String source, @Nullable Date expiry, @Nullable String reason) {
        super(profile, created, source, expiry, reason);
    }

    public BannedPlayerEntry(JsonObject json) {
        super(BannedPlayerEntry.profileFromJson(json), json);
    }

    @Override
    protected void fromJson(JsonObject json) {
        if (this.getKey() == null) {
            return;
        }
        json.addProperty("uuid", ((GameProfile)this.getKey()).getId() == null ? "" : ((GameProfile)this.getKey()).getId().toString());
        json.addProperty("name", ((GameProfile)this.getKey()).getName());
        super.fromJson(json);
    }

    @Override
    public Text toText() {
        GameProfile gameProfile = (GameProfile)this.getKey();
        return new LiteralText(gameProfile.getName() != null ? gameProfile.getName() : Objects.toString(gameProfile.getId(), "(Unknown)"));
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

