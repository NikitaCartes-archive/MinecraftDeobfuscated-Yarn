package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.JsonUtils;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsServerAddress extends ValueObject {
	private static final Logger LOGGER = LogUtils.getLogger();
	public String address;
	public String resourcePackUrl;
	public String resourcePackHash;

	public static RealmsServerAddress parse(String json) {
		JsonParser jsonParser = new JsonParser();
		RealmsServerAddress realmsServerAddress = new RealmsServerAddress();

		try {
			JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
			realmsServerAddress.address = JsonUtils.getNullableStringOr("address", jsonObject, null);
			realmsServerAddress.resourcePackUrl = JsonUtils.getNullableStringOr("resourcePackUrl", jsonObject, null);
			realmsServerAddress.resourcePackHash = JsonUtils.getNullableStringOr("resourcePackHash", jsonObject, null);
		} catch (Exception var4) {
			LOGGER.error("Could not parse RealmsServerAddress: {}", var4.getMessage());
		}

		return realmsServerAddress;
	}
}
