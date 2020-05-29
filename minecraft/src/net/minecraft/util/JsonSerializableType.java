package net.minecraft.util;

public class JsonSerializableType<T> {
	private final JsonSerializable<? extends T> jsonSerializer;

	public JsonSerializableType(JsonSerializable<? extends T> jsonSerializable) {
		this.jsonSerializer = jsonSerializable;
	}

	public JsonSerializable<? extends T> getJsonSerializer() {
		return this.jsonSerializer;
	}
}
