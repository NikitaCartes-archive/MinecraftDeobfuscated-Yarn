package net.minecraft;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;

public class class_3327 extends class_3330<GameProfile> {
	private final int field_14366;
	private final boolean field_14367;

	public class_3327(GameProfile gameProfile, int i, boolean bl) {
		super(gameProfile);
		this.field_14366 = i;
		this.field_14367 = bl;
	}

	public class_3327(JsonObject jsonObject) {
		super(method_14621(jsonObject), jsonObject);
		this.field_14366 = jsonObject.has("level") ? jsonObject.get("level").getAsInt() : 0;
		this.field_14367 = jsonObject.has("bypassesPlayerLimit") && jsonObject.get("bypassesPlayerLimit").getAsBoolean();
	}

	public int method_14623() {
		return this.field_14366;
	}

	public boolean method_14622() {
		return this.field_14367;
	}

	@Override
	protected void method_14628(JsonObject jsonObject) {
		if (this.method_14626() != null) {
			jsonObject.addProperty("uuid", this.method_14626().getId() == null ? "" : this.method_14626().getId().toString());
			jsonObject.addProperty("name", this.method_14626().getName());
			super.method_14628(jsonObject);
			jsonObject.addProperty("level", this.field_14366);
			jsonObject.addProperty("bypassesPlayerLimit", this.field_14367);
		}
	}

	private static GameProfile method_14621(JsonObject jsonObject) {
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
