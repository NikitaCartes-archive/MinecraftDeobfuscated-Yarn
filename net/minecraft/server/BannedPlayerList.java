/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.ServerConfigEntry;
import net.minecraft.server.ServerConfigList;

public class BannedPlayerList
extends ServerConfigList<GameProfile, BannedPlayerEntry> {
    public BannedPlayerList(File file) {
        super(file);
    }

    @Override
    protected ServerConfigEntry<GameProfile> fromJson(JsonObject jsonObject) {
        return new BannedPlayerEntry(jsonObject);
    }

    @Override
    public boolean contains(GameProfile gameProfile) {
        return this.contains(gameProfile);
    }

    @Override
    public String[] getNames() {
        String[] strings = new String[this.values().size()];
        int i = 0;
        for (ServerConfigEntry serverConfigEntry : this.values()) {
            strings[i++] = ((GameProfile)serverConfigEntry.getKey()).getName();
        }
        return strings;
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

