package net.minecraft.util.registry;

import java.util.Optional;
import net.minecraft.tag.TagKey;

public interface RegistryEntryLookup<T> {
	Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> key);

	default RegistryEntry.Reference<T> getOrThrow(RegistryKey<T> key) {
		return (RegistryEntry.Reference<T>)this.getOptional(key).orElseThrow(() -> new IllegalStateException("Missing element " + key));
	}

	Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> tag);

	default RegistryEntryList.Named<T> getOrThrow(TagKey<T> tag) {
		return (RegistryEntryList.Named<T>)this.getOptional(tag).orElseThrow(() -> new IllegalStateException("Missing tag " + tag));
	}

	public interface RegistryLookup {
		<T> Optional<RegistryEntryLookup<T>> getOptional(RegistryKey<? extends Registry<? extends T>> registryRef);

		default <T> RegistryEntryLookup<T> getOrThrow(RegistryKey<? extends Registry<? extends T>> registryRef) {
			return (RegistryEntryLookup<T>)this.getOptional(registryRef)
				.orElseThrow(() -> new IllegalStateException("Registry " + registryRef.getValue() + " not found"));
		}
	}
}
