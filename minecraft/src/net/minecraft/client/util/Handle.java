package net.minecraft.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface Handle<T> {
	Handle<?> EMPTY = () -> {
		throw new IllegalStateException("Cannot dereference handle with no underlying resource");
	};

	static <T> Handle<T> empty() {
		return (Handle<T>)EMPTY;
	}

	T get();
}
