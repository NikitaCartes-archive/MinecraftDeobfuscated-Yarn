package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.Objects;

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
		return (String[])this.values().stream().map(ServerConfigEntry::getKey).filter(Objects::nonNull).map(GameProfile::getName).toArray(String[]::new);
	}

	protected String toString(GameProfile gameProfile) {
		return gameProfile.getId().toString();
	}
}
