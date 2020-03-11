package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class Whitelist extends ServerConfigList<GameProfile, WhitelistEntry> {
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
