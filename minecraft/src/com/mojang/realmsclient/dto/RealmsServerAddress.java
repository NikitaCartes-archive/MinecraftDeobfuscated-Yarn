package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4352;
import net.minecraft.class_4431;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsServerAddress extends class_4352 {
	private static final Logger LOGGER = LogManager.getLogger();
	public String address;
	public String resourcePackUrl;
	public String resourcePackHash;

	public static RealmsServerAddress parse(String string) {
		JsonParser jsonParser = new JsonParser();
		RealmsServerAddress realmsServerAddress = new RealmsServerAddress();

		try {
			JsonObject jsonObject = jsonParser.parse(string).getAsJsonObject();
			realmsServerAddress.address = class_4431.method_21547("address", jsonObject, null);
			realmsServerAddress.resourcePackUrl = class_4431.method_21547("resourcePackUrl", jsonObject, null);
			realmsServerAddress.resourcePackHash = class_4431.method_21547("resourcePackHash", jsonObject, null);
		} catch (Exception var4) {
			LOGGER.error("Could not parse RealmsServerAddress: " + var4.getMessage());
		}

		return realmsServerAddress;
	}
}
