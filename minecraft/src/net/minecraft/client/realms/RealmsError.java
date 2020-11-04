package net.minecraft.client.realms;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsError {
	private static final Logger LOGGER = LogManager.getLogger();
	private final String errorMessage;
	private final int errorCode;

	private RealmsError(String errorMessage, int errorCode) {
		this.errorMessage = errorMessage;
		this.errorCode = errorCode;
	}

	public static RealmsError create(String error) {
		try {
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonObject = jsonParser.parse(error).getAsJsonObject();
			String string = JsonUtils.getStringOr("errorMsg", jsonObject, "");
			int i = JsonUtils.getIntOr("errorCode", jsonObject, -1);
			return new RealmsError(string, i);
		} catch (Exception var5) {
			LOGGER.error("Could not parse RealmsError: {}", var5.getMessage());
			LOGGER.error("The error was: {}", error);
			return new RealmsError("Failed to parse response from server", -1);
		}
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

	public int getErrorCode() {
		return this.errorCode;
	}
}
