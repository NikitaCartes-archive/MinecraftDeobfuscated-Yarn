package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class OperatorList extends ServerConfigList<GameProfile, OperatorEntry> {
	public OperatorList(File file) {
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

	protected String method_14619(GameProfile gameProfile) {
		return gameProfile.getId().toString();
	}
}
