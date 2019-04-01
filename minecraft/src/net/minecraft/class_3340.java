package net.minecraft;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;

public class class_3340 extends class_3330<GameProfile> {
	public class_3340(GameProfile gameProfile) {
		super(gameProfile);
	}

	public class_3340(JsonObject jsonObject) {
		super(method_14656(jsonObject), jsonObject);
	}

	@Override
	protected void method_14628(JsonObject jsonObject) {
		if (this.method_14626() != null) {
			jsonObject.addProperty("uuid", this.method_14626().getId() == null ? "" : this.method_14626().getId().toString());
			jsonObject.addProperty("name", this.method_14626().getName());
			super.method_14628(jsonObject);
		}
	}

	private static GameProfile method_14656(JsonObject jsonObject) {
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
