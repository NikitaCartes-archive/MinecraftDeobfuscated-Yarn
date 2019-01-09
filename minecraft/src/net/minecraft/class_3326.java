package net.minecraft;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class class_3326 extends class_3331<GameProfile, class_3327> {
	public class_3326(File file) {
		super(file);
	}

	@Override
	protected class_3330<GameProfile> method_14642(JsonObject jsonObject) {
		return new class_3327(jsonObject);
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

	public boolean method_14620(GameProfile gameProfile) {
		class_3327 lv = this.method_14640(gameProfile);
		return lv != null ? lv.method_14622() : false;
	}

	protected String method_14619(GameProfile gameProfile) {
		return gameProfile.getId().toString();
	}
}
