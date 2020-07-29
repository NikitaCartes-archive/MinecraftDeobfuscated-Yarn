package net.minecraft.util.registry;

import com.mojang.serialization.Lifecycle;

public abstract class MutableRegistry<T> extends Registry<T> {
	public MutableRegistry(RegistryKey<? extends Registry<T>> registryKey, Lifecycle lifecycle) {
		super(registryKey, lifecycle);
	}

	public abstract <V extends T> V set(int rawId, RegistryKey<T> key, V entry);

	public abstract <V extends T> V add(RegistryKey<T> key, V entry);

	public abstract <V extends T> V method_31062(RegistryKey<T> registryKey, V object);
}
