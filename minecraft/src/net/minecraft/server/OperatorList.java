package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.Objects;

public class OperatorList extends ServerConfigList<GameProfile, OperatorEntry> {
	public OperatorList(File file) {
		super(file);
	}

	@Override
	protected ServerConfigEntry<GameProfile> fromJson(JsonObject json) {
		return new OperatorEntry(json);
	}

	@Override
	public String[] getNames() {
		return (String[])this.values().stream().map(ServerConfigEntry::getKey).filter(Objects::nonNull).map(GameProfile::getName).toArray(String[]::new);
	}

	public boolean canBypassPlayerLimit(GameProfile profile) {
		OperatorEntry operatorEntry = this.get(profile);
		return operatorEntry != null ? operatorEntry.canBypassPlayerLimit() : false;
	}

	protected String toString(GameProfile gameProfile) {
		return gameProfile.getId().toString();
	}
}
