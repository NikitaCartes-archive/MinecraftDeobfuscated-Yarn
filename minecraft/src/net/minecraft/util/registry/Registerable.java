package net.minecraft.util.registry;

import com.mojang.serialization.Lifecycle;

public interface Registerable<T> {
	RegistryEntry.Reference<T> register(RegistryKey<T> key, T value, Lifecycle lifecycle);

	default RegistryEntry.Reference<T> register(RegistryKey<T> key, T value) {
		return this.register(key, value, Lifecycle.stable());
	}

	<S> RegistryEntryLookup<S> getRegistryLookup(RegistryKey<? extends Registry<? extends S>> registryRef);
}
