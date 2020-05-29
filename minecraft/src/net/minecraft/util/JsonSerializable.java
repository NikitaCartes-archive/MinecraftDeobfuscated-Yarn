package net.minecraft.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public interface JsonSerializable<T> {
	void toJson(JsonObject jsonObject, T object, JsonSerializationContext jsonSerializationContext);

	T fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext);
}
