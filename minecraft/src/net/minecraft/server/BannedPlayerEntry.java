package net.minecraft.server;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class BannedPlayerEntry extends BanEntry<GameProfile> {
	public BannedPlayerEntry(GameProfile gameProfile) {
		this(gameProfile, null, null, null, null);
	}

	public BannedPlayerEntry(GameProfile gameProfile, @Nullable Date date, @Nullable String string, @Nullable Date date2, @Nullable String string2) {
		super(gameProfile, date, string, date2, string2);
	}

	public BannedPlayerEntry(JsonObject jsonObject) {
		super(getProfileFromJson(jsonObject), jsonObject);
	}

	@Override
	protected void serialize(JsonObject jsonObject) {
		if (this.getKey() != null) {
			jsonObject.addProperty("uuid", this.getKey().getId() == null ? "" : this.getKey().getId().toString());
			jsonObject.addProperty("name", this.getKey().getName());
			super.serialize(jsonObject);
		}
	}

	@Override
	public Text toText() {
		GameProfile gameProfile = this.getKey();
		return new LiteralText(gameProfile.getName() != null ? gameProfile.getName() : Objects.toString(gameProfile.getId(), "(Unknown)"));
	}

	private static GameProfile getProfileFromJson(JsonObject jsonObject) {
		if (jsonObject.has("uuid") && jsonObject.has("name")) {
			String string = jsonObject.get("uuid").getAsString();

			UUID uUID;
			try {
				uUID = UUID.fromString(string);
			} catch (Throwable var4) {
				return null;
			}

			return new GameProfile(uUID, jsonObject.get("name").getAsString());
		} else {
			return null;
		}
	}
}
