package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;

public class OperatorEntry extends ServerConfigEntry<GameProfile> {
	private final int permissionLevel;
	private final boolean bypassPlayerLimit;

	public OperatorEntry(GameProfile profile, int permissionLevel, boolean bypassPlayerLimit) {
		super(profile);
		this.permissionLevel = permissionLevel;
		this.bypassPlayerLimit = bypassPlayerLimit;
	}

	public OperatorEntry(JsonObject jsonObject) {
		super(getProfileFromJson(jsonObject));
		this.permissionLevel = jsonObject.has("level") ? jsonObject.get("level").getAsInt() : 0;
		this.bypassPlayerLimit = jsonObject.has("bypassesPlayerLimit") && jsonObject.get("bypassesPlayerLimit").getAsBoolean();
	}

	public int getPermissionLevel() {
		return this.permissionLevel;
	}

	public boolean canBypassPlayerLimit() {
		return this.bypassPlayerLimit;
	}

	private static GameProfile getProfileFromJson(JsonObject json) {
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
