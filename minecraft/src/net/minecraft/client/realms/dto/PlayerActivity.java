package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.JsonUtils;

@Environment(EnvType.CLIENT)
public class PlayerActivity extends ValueObject {
	public String profileUuid;
	public long joinTime;
	public long leaveTime;

	public static PlayerActivity parse(JsonObject json) {
		PlayerActivity playerActivity = new PlayerActivity();

		try {
			playerActivity.profileUuid = JsonUtils.getNullableStringOr("profileUuid", json, null);
			playerActivity.joinTime = JsonUtils.getLongOr("joinTime", json, Long.MIN_VALUE);
			playerActivity.leaveTime = JsonUtils.getLongOr("leaveTime", json, Long.MIN_VALUE);
		} catch (Exception var3) {
		}

		return playerActivity;
	}
}
