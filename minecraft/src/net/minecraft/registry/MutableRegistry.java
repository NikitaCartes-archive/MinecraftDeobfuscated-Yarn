package net.minecraft.registry;

import com.mojang.serialization.Lifecycle;
import net.minecraft.registry.entry.RegistryEntry;

/**
 * A registry that allows adding or modifying values.
 * Note that in vanilla, all registries are instances of this.
 * 
 * @see Registry
 */
public interface MutableRegistry<T> extends Registry<T> {
	RegistryEntry<T> set(int rawId, RegistryKey<T> key, T value, Lifecycle lifecycle);

	RegistryEntry.Reference<T> add(RegistryKey<T> key, T entry, Lifecycle lifecycle);

	/**
	 * {@return whether the registry is empty}
	 */
	boolean isEmpty();

	RegistryEntryLookup<T> createMutableEntryLookup();
}
