package net.minecraft.server.config;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class WhitelistList extends ServerConfigList<GameProfile, WhitelistEntry> {
	public WhitelistList(File file) {
		super(file);
	}

	@Override
	protected ServerConfigEntry<GameProfile> fromJson(JsonObject jsonObject) {
		return new WhitelistEntry(jsonObject);
	}

	public boolean method_14653(GameProfile gameProfile) {
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

	protected String method_14652(GameProfile gameProfile) {
		return gameProfile.getId().toString();
	}
}
