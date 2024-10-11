package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.JsonUtils;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsServerAddress extends ValueObject {
	private static final Logger LOGGER = LogUtils.getLogger();
	@Nullable
	public String address;
	@Nullable
	public String resourcePackUrl;
	@Nullable
	public String resourcePackHash;

	public static RealmsServerAddress parse(String json) {
		RealmsServerAddress realmsServerAddress = new RealmsServerAddress();

		try {
			JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
			realmsServerAddress.address = JsonUtils.getNullableStringOr("address", jsonObject, null);
			realmsServerAddress.resourcePackUrl = JsonUtils.getNullableStringOr("resourcePackUrl", jsonObject, null);
			realmsServerAddress.resourcePackHash = JsonUtils.getNullableStringOr("resourcePackHash", jsonObject, null);
		} catch (Exception var3) {
			LOGGER.error("Could not parse RealmsServerAddress: {}", var3.getMessage());
		}

		return realmsServerAddress;
	}
}
