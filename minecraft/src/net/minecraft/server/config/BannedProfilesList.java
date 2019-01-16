package net.minecraft.server.config;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class BannedProfilesList extends ServerConfigList<GameProfile, BannedPlayerEntry> {
	public BannedProfilesList(File file) {
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

	protected String getStringKey(GameProfile gameProfile) {
		return gameProfile.getId().toString();
	}
}
