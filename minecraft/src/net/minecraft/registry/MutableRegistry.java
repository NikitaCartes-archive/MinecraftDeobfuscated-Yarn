package net.minecraft.registry;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;

/**
 * A registry that allows adding or modifying values.
 * Note that in vanilla, all registries are instances of this.
 * 
 * @see Registry
 */
public interface MutableRegistry<T> extends Registry<T> {
	RegistryEntry.Reference<T> add(RegistryKey<T> key, T value, RegistryEntryInfo info);

	/**
	 * {@return whether the registry is empty}
	 */
	boolean isEmpty();

	RegistryEntryLookup<T> createMutableEntryLookup();
}
