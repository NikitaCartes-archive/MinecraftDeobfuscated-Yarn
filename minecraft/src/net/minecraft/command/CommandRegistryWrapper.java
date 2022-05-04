package net.minecraft.command;

import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Stream;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;

/**
 * A wrapper of a registry, used in command arguments.
 * 
 * <p>The methods in this class in most cases perform the same as ones defined
 * in {@link Registry}. However, subclasses may have slightly different behaviors
 * to make it easier to handle user-passed arguments. For example, {@link #getEntryList(TagKey)}
 * may return an empty {@link RegistryEntryList} instead of {@link Optional#empty()}.
 * 
 * <p>A wrapper without any special behaviors can be created by calling {#link of(Registry)}.
 */
public interface CommandRegistryWrapper<T> {
	/**
	 * @see Registry#getEntry
	 */
	Optional<RegistryEntry<T>> getEntry(RegistryKey<T> key);

	/**
	 * {@return a stream of registry keys defined in the wrapped registry}
	 * 
	 * @see Registry#getKeys
	 */
	Stream<RegistryKey<T>> streamKeys();

	/**
	 * {@return the entry list for the provided tag}
	 * 
	 * <p>Implementations have different behaviors for unrecognized tags. The
	 * {@linkplain CommandRegistryWrapper.Impl#getEntryList default implementation}
	 * returns {@link Optional#empty()}; however, the ones created by
	 * {@link CommandRegistryAccess#createWrapper} can create and return a new entry list, or
	 * return an empty entry list.
	 * 
	 * @see Registry#getEntryList
	 * @see CommandRegistryAccess.EntryListCreationPolicy
	 */
	Optional<? extends RegistryEntryList<T>> getEntryList(TagKey<T> tag);

	/**
	 * @see Registry#streamTags
	 */
	Stream<TagKey<T>> streamTags();

	/**
	 * {@return a new wrapper for the {@code registry} without any special behaviors}
	 */
	static <T> CommandRegistryWrapper<T> of(Registry<T> registry) {
		return new CommandRegistryWrapper.Impl<>(registry);
	}

	public static class Impl<T> implements CommandRegistryWrapper<T> {
		protected final Registry<T> registry;

		public Impl(Registry<T> registry) {
			this.registry = registry;
		}

		@Override
		public Optional<RegistryEntry<T>> getEntry(RegistryKey<T> key) {
			return this.registry.getEntry(key);
		}

		@Override
		public Stream<RegistryKey<T>> streamKeys() {
			return this.registry.getEntrySet().stream().map(Entry::getKey);
		}

		@Override
		public Optional<? extends RegistryEntryList<T>> getEntryList(TagKey<T> tag) {
			return this.registry.getEntryList(tag);
		}

		@Override
		public Stream<TagKey<T>> streamTags() {
			return this.registry.streamTags();
		}
	}
}
