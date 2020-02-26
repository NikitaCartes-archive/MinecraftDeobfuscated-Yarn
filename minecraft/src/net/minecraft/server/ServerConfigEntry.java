package net.minecraft.server;

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
}
