package net.minecraft.server;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public class ServerConfigEntry<T> {
	@Nullable
	private final T object;

	public ServerConfigEntry(T object) {
		this.object = object;
	}

	protected ServerConfigEntry(@Nullable T object, JsonObject jsonObject) {
		this.object = object;
	}

	@Nullable
	T getKey() {
		return this.object;
	}

	boolean isInvalid() {
		return false;
	}

	protected void serialize(JsonObject jsonObject) {
	}
}
