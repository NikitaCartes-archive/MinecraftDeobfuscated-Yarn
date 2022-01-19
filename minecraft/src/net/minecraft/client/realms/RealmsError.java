package net.minecraft.client.realms;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.JsonUtils;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsError {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final String errorMessage;
	private final int errorCode;

	private RealmsError(String errorMessage, int errorCode) {
		this.errorMessage = errorMessage;
		this.errorCode = errorCode;
	}

	@Nullable
	public static RealmsError create(String error) {
		if (Strings.isNullOrEmpty(error)) {
			return null;
		} else {
			try {
				JsonObject jsonObject = JsonParser.parseString(error).getAsJsonObject();
				String string = JsonUtils.getStringOr("errorMsg", jsonObject, "");
				int i = JsonUtils.getIntOr("errorCode", jsonObject, -1);
				return new RealmsError(string, i);
			} catch (Exception var4) {
				LOGGER.error("Could not parse RealmsError: {}", var4.getMessage());
				LOGGER.error("The error was: {}", error);
				return null;
			}
		}
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

	public int getErrorCode() {
		return this.errorCode;
	}
}
