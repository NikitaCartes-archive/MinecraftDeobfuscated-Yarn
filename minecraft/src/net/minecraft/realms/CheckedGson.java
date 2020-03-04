package net.minecraft.realms;

import com.google.gson.Gson;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Checks so that only intended pojos are passed to the GSON (handles
 * serialization after obfuscation).
 */
@Environment(EnvType.CLIENT)
public class CheckedGson {
	private final Gson gson = new Gson();

	public String toJson(RealmsSerializable serializable) {
		return this.gson.toJson(serializable);
	}

	public <T extends RealmsSerializable> T fromJson(String json, Class<T> type) {
		return this.gson.fromJson(json, type);
	}
}
