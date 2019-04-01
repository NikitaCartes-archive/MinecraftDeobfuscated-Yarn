package net.minecraft;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public interface class_2444 {
	void method_10416(JsonObject jsonObject);

	default JsonObject method_17799() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("type", class_2378.field_17598.method_10221(this.method_17800()).toString());
		this.method_10416(jsonObject);
		return jsonObject;
	}

	class_2960 method_10417();

	class_1865<?> method_17800();

	@Nullable
	JsonObject method_10415();

	@Nullable
	class_2960 method_10418();
}
