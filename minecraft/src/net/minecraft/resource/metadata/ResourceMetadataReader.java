package net.minecraft.resource.metadata;

import com.google.gson.JsonObject;

public interface ResourceMetadataReader<T> {
	String getKey();

	T read(JsonObject jsonObject);
}
