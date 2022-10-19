package net.minecraft.resource.metadata;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

public interface ResourceMetadataSerializer<T> extends ResourceMetadataReader<T> {
	JsonObject toJson(T metadata);

	static <T> ResourceMetadataSerializer<T> fromCodec(String key, Codec<T> codec) {
		return new ResourceMetadataSerializer<T>() {
			@Override
			public String getKey() {
				return key;
			}

			@Override
			public T fromJson(JsonObject json) {
				return codec.parse(JsonOps.INSTANCE, json).getOrThrow(false, error -> {
				});
			}

			@Override
			public JsonObject toJson(T metadata) {
				return codec.encodeStart(JsonOps.INSTANCE, metadata).getOrThrow(false, error -> {
				}).getAsJsonObject();
			}
		};
	}
}
