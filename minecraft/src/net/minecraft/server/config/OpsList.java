package net.minecraft.server.config;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class OpsList extends ServerConfigList<GameProfile, OperatorEntry> {
	public OpsList(File file) {
		super(file);
	}

	@Override
	protected ServerConfigEntry<GameProfile> fromJson(JsonObject jsonObject) {
		return new OperatorEntry(jsonObject);
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

	public boolean isOp(GameProfile gameProfile) {
		OperatorEntry operatorEntry = this.get(gameProfile);
		return operatorEntry != null ? operatorEntry.canBypassPlayerLimit() : false;
	}

	protected String getId(GameProfile gameProfile) {
		return gameProfile.getId().toString();
	}
}
