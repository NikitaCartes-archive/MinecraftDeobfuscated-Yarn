package net.minecraft;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;

public class class_3336 extends class_3309<GameProfile> {
	public class_3336(GameProfile gameProfile) {
		this(gameProfile, null, null, null, null);
	}

	public class_3336(GameProfile gameProfile, @Nullable Date date, @Nullable String string, @Nullable Date date2, @Nullable String string2) {
		super(gameProfile, date, string, date2, string2);
	}

	public class_3336(JsonObject jsonObject) {
		super(method_14651(jsonObject), jsonObject);
	}

	@Override
	protected void method_14628(JsonObject jsonObject) {
		if (this.method_14626() != null) {
			jsonObject.addProperty("uuid", this.method_14626().getId() == null ? "" : this.method_14626().getId().toString());
			jsonObject.addProperty("name", this.method_14626().getName());
			super.method_14628(jsonObject);
		}
	}

	@Override
	public class_2561 method_14504() {
		GameProfile gameProfile = this.method_14626();
		return new class_2585(gameProfile.getName() != null ? gameProfile.getName() : Objects.toString(gameProfile.getId(), "(Unknown)"));
	}

	private static GameProfile method_14651(JsonObject jsonObject) {
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
