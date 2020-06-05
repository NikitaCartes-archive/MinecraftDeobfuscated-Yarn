package net.minecraft.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public interface JsonSerializer<T> {
	void toJson(JsonObject json, T object, JsonSerializationContext context);

	T fromJson(JsonObject json, JsonDeserializationContext context);
}
