package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class BannedPlayerList extends ServerConfigList<GameProfile, BannedPlayerEntry> {
	public BannedPlayerList(File file) {
		super(file);
	}

	@Override
	protected ServerConfigEntry<GameProfile> fromJson(JsonObject jsonObject) {
		return new BannedPlayerEntry(jsonObject);
	}

	public boolean contains(GameProfile gameProfile) {
		return this.contains(gameProfile);
	}

	@Override
	public String[] getNames() {
		String[] strings = new String[this.values().size()];
		int i = 0;

		for (ServerConfigEntry<GameProfile> serverConfigEntry : this.values()) {
			strings[i++] = serverConfigEntry.getKey().getName();
		}

		return strings;
	}

	protected String toString(GameProfile gameProfile) {
		return gameProfile.getId().toString();
	}
}
