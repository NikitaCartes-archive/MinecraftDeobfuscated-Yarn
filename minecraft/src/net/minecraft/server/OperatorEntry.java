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

	public OperatorEntry(JsonObject json) {
		super(getProfileFromJson(json));
		this.permissionLevel = json.has("level") ? json.get("level").getAsInt() : 0;
		this.bypassPlayerLimit = json.has("bypassesPlayerLimit") && json.get("bypassesPlayerLimit").getAsBoolean();
	}

	public int getPermissionLevel() {
		return this.permissionLevel;
	}

	public boolean canBypassPlayerLimit() {
		return this.bypassPlayerLimit;
	}

	@Override
	protected void fromJson(JsonObject json) {
		if (this.getKey() != null) {
			json.addProperty("uuid", this.getKey().getId() == null ? "" : this.getKey().getId().toString());
			json.addProperty("name", this.getKey().getName());
			json.addProperty("level", this.permissionLevel);
			json.addProperty("bypassesPlayerLimit", this.bypassPlayerLimit);
		}
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
