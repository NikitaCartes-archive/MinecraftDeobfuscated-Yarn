package net.minecraft.server;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public abstract class ServerConfigEntry<T> {
	@Nullable
	private final T object;

	public ServerConfigEntry(@Nullable T object) {
		this.object = object;
	}

	@Nullable
	T getKey() {
		return this.object;
	}

	boolean isInvalid() {
		return false;
	}

	protected abstract void method_24896(JsonObject jsonObject);
}
