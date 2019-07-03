package net.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_4345 {
	private static final Logger field_19593 = LogManager.getLogger();
	private String field_19594;
	private int field_19595;

	public class_4345(String string) {
		try {
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonObject = jsonParser.parse(string).getAsJsonObject();
			this.field_19594 = class_4431.method_21547("errorMsg", jsonObject, "");
			this.field_19595 = class_4431.method_21545("errorCode", jsonObject, -1);
		} catch (Exception var4) {
			field_19593.error("Could not parse RealmsError: " + var4.getMessage());
			field_19593.error("The error was: " + string);
		}
	}

	public String method_21036() {
		return this.field_19594;
	}

	public int method_21037() {
		return this.field_19595;
	}
}
