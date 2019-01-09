package net.minecraft;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class class_3335 extends class_3331<GameProfile, class_3336> {
	public class_3335(File file) {
		super(file);
	}

	@Override
	protected class_3330<GameProfile> method_14642(JsonObject jsonObject) {
		return new class_3336(jsonObject);
	}

	public boolean method_14650(GameProfile gameProfile) {
		return this.method_14644(gameProfile);
	}

	@Override
	public String[] method_14636() {
		String[] strings = new String[this.method_14632().size()];
		int i = 0;

		for (class_3330<GameProfile> lv : this.method_14632()) {
			strings[i++] = lv.method_14626().getName();
		}

		return strings;
	}

	protected String method_14649(GameProfile gameProfile) {
		return gameProfile.getId().toString();
	}
}
