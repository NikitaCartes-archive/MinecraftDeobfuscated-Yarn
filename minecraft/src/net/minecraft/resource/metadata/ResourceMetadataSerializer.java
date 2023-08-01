package net.minecraft.resource.metadata;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.util.Util;

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
				return Util.getResult(codec.parse(JsonOps.INSTANCE, json), JsonParseException::new);
			}

			@Override
			public JsonObject toJson(T metadata) {
				return Util.getResult(codec.encodeStart(JsonOps.INSTANCE, metadata), IllegalArgumentException::new).getAsJsonObject();
			}
		};
	}
}
