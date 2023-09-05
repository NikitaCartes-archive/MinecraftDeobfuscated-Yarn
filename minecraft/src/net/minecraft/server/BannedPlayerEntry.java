package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.text.Text;

public class BannedPlayerEntry extends BanEntry<GameProfile> {
	public BannedPlayerEntry(@Nullable GameProfile profile) {
		this(profile, null, null, null, null);
	}

	public BannedPlayerEntry(@Nullable GameProfile profile, @Nullable Date created, @Nullable String source, @Nullable Date expiry, @Nullable String reason) {
		super(profile, created, source, expiry, reason);
	}

	public BannedPlayerEntry(JsonObject json) {
		super(profileFromJson(json), json);
	}

	@Override
	protected void write(JsonObject json) {
		if (this.getKey() != null) {
			json.addProperty("uuid", this.getKey().getId().toString());
			json.addProperty("name", this.getKey().getName());
			super.write(json);
		}
	}

	@Override
	public Text toText() {
		GameProfile gameProfile = this.getKey();
		return gameProfile != null ? Text.literal(gameProfile.getName()) : Text.translatable("commands.banlist.entry.unknown");
	}

	@Nullable
	private static GameProfile profileFromJson(JsonObject json) {
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
