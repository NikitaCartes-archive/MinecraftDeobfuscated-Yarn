package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;

public class WhitelistEntry extends ServerConfigEntry<GameProfile> {
	public WhitelistEntry(GameProfile gameProfile) {
		super(gameProfile);
	}

	public WhitelistEntry(JsonObject jsonObject) {
		super(deserializeProfile(jsonObject));
	}

	private static GameProfile deserializeProfile(JsonObject json) {
		if (json.has("uuid") && json.has("name")) {
			String string = json.get("uuid").getAsString();

			UUID uUID;
			try {
				uUID = UUID.fromString(string);
			} catch (Throwable var4) {
				return null;
			}

			return new GameProfile(uUID, json.get("name").getAsString());
		} else {
			return null;
		}
	}
}
