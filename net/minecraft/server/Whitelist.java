/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.Objects;
import net.minecraft.server.ServerConfigEntry;
import net.minecraft.server.ServerConfigList;
import net.minecraft.server.WhitelistEntry;

public class Whitelist
extends ServerConfigList<GameProfile, WhitelistEntry> {
    public Whitelist(File file) {
        super(file);
    }

    @Override
    protected ServerConfigEntry<GameProfile> fromJson(JsonObject json) {
        return new WhitelistEntry(json);
    }

    public boolean isAllowed(GameProfile profile) {
        return this.contains(profile);
    }

    @Override
    public String[] getNames() {
        return (String[])this.values().stream().map(ServerConfigEntry::getKey).filter(Objects::nonNull).map(GameProfile::getName).toArray(String[]::new);
    }

    @Override
    protected String toString(GameProfile gameProfile) {
        return gameProfile.getId().toString();
    }

    @Override
    protected /* synthetic */ String toString(Object profile) {
        return this.toString((GameProfile)profile);
    }
}

