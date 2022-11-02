package net.minecraft.util.registry;

import com.mojang.serialization.Lifecycle;

/**
 * A registry that allows adding or modifying values.
 * Note that in vanilla, all registries are instances of this.
 * 
 * @see Registry
 */
public abstract class MutableRegistry<T> extends Registry<T> {
	public MutableRegistry(RegistryKey<? extends Registry<T>> registryKey, Lifecycle lifecycle) {
		super(registryKey, lifecycle);
	}

	public abstract RegistryEntry<T> set(int rawId, RegistryKey<T> key, T value, Lifecycle lifecycle);

	public abstract RegistryEntry.Reference<T> add(RegistryKey<T> key, T entry, Lifecycle lifecycle);

	/**
	 * {@return whether the registry is empty}
	 */
	public abstract boolean isEmpty();

	public abstract RegistryEntryLookup<T> createMutableEntryLookup();
}
