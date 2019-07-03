package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4352;
import net.minecraft.class_4431;

@Environment(EnvType.CLIENT)
public class ServerActivity extends class_4352 {
	public String profileUuid;
	public long joinTime;
	public long leaveTime;

	public static ServerActivity parse(JsonObject jsonObject) {
		ServerActivity serverActivity = new ServerActivity();

		try {
			serverActivity.profileUuid = class_4431.method_21547("profileUuid", jsonObject, null);
			serverActivity.joinTime = class_4431.method_21546("joinTime", jsonObject, Long.MIN_VALUE);
			serverActivity.leaveTime = class_4431.method_21546("leaveTime", jsonObject, Long.MIN_VALUE);
		} catch (Exception var3) {
		}

		return serverActivity;
	}
}
