package net.minecraft.client.realms;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Checks so that only intended pojos are passed to the GSON (handles
 * serialization after obfuscation).
 */
@Environment(EnvType.CLIENT)
public class CheckedGson {
	private final Gson GSON = new Gson();

	public String toJson(RealmsSerializable serializable) {
		return this.GSON.toJson(serializable);
	}

	public String toJson(JsonElement json) {
		return this.GSON.toJson(json);
	}

	@Nullable
	public <T extends RealmsSerializable> T fromJson(String json, Class<T> type) {
		return this.GSON.fromJson(json, type);
	}
}
