package net.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class class_3274 implements class_3270<class_3272> {
	public class_3272 method_14426(JsonObject jsonObject) {
		class_2561 lv = class_2561.class_2562.method_10872(jsonObject.get("description"));
		if (lv == null) {
			throw new JsonParseException("Invalid/missing description!");
		} else {
			int i = class_3518.method_15260(jsonObject, "pack_format");
			return new class_3272(lv, i);
		}
	}

	@Override
	public String method_14420() {
		return "pack";
	}
}
