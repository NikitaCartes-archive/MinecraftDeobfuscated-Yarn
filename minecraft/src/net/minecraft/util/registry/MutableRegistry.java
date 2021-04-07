package net.minecraft.util.registry;

import com.mojang.serialization.Lifecycle;
import java.util.OptionalInt;

public abstract class MutableRegistry<T> extends Registry<T> {
	public MutableRegistry(RegistryKey<? extends Registry<T>> registryKey, Lifecycle lifecycle) {
		super(registryKey, lifecycle);
	}

	public abstract <V extends T> V set(int rawId, RegistryKey<T> key, V entry, Lifecycle lifecycle);

	public abstract <V extends T> V add(RegistryKey<T> key, V entry, Lifecycle lifecycle);

	/**
	 * If the given key is already present in the registry, replaces the entry associated with the given
	 * key with the new entry. This method asserts that the raw ID is equal to the value already in
	 * the registry. The raw ID not being present may lead to buggy behavior.
	 * 
	 * <p>If the given key is not already present in the registry, adds the entry to the registry. If
	 * {@code rawId} is present, then this method gives the entry this raw ID. Otherwise, uses the
	 * next available ID.
	 */
	public abstract <V extends T> V replace(OptionalInt rawId, RegistryKey<T> key, V newEntry, Lifecycle lifecycle);

	public abstract boolean method_35863();
}
