package net.minecraft.util.registry;

import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;

/**
 * An object holding a value that can be registered in a registry. In most cases, the
 * value is already registered in a registry ("reference entry"), hence the name;
 * however, it is possible to create a registry entry by direct reference
 * ("direct entry"). This is useful for data packs, as they can define
 * one-time use values directly without having to register them every time.
 * 
 * <p>Registry entries do not define {@code equals} method. Instead, compare the result
 * of {@link #getKeyOrValue}.
 * 
 * <p>Reference registry entries also hold their {@linkplain TagKey tags}. For more
 * information on type-specific behaviors, including "intrusive" and "stand-alone"
 * reference registry entries, see the respective class documentations.
 * 
 * <p>A registry entry is sometimes referred to as a "holder" in error messages.
 * 
 * @see RegistryEntry.Direct
 * @see RegistryEntry.Reference
 * @see Registry#entryOf
 * @see Registry#getEntry
 */
public interface RegistryEntry<T> {
	T value();

	boolean hasKeyAndValue();

	/**
	 * {@return whether the ID of this entry is {@code id}}
	 * 
	 * <p>This always returns {@code false} for direct entries.
	 */
	boolean matchesId(Identifier id);

	/**
	 * {@return whether the registry key of this entry is {@code key}}
	 * 
	 * <p>This always returns {@code false} for direct entries.
	 */
	boolean matchesKey(RegistryKey<T> key);

	/**
	 * {@return whether this entry's key matches {@code predicate}}
	 * 
	 * <p>This always returns {@code false} for direct entries.
	 */
	boolean matches(Predicate<RegistryKey<T>> predicate);

	/**
	 * {@return whether this entry is in {@code tag}}
	 * 
	 * <p>This always returns {@code false} for direct entries, since tags are managed by
	 * a registry.
	 */
	boolean isIn(TagKey<T> tag);

	/**
	 * {@return a stream of the tags of this entry, or an empty stream if this is a direct entry}
	 */
	Stream<TagKey<T>> streamTags();

	/**
	 * {@return the object that identifies this registry key}
	 * 
	 * <p>For direct entries, this is the held value, and for reference entries, this is the
	 * key of the entry.
	 */
	Either<RegistryKey<T>, T> getKeyOrValue();

	/**
	 * {@return the registry key of this entry, or an empty optional if this is a direct entry}
	 */
	Optional<RegistryKey<T>> getKey();

	/**
	 * {@return the type (direct or reference) of this registry entry}
	 * 
	 * <p>This is different from the types of reference registry entries, i.e.
	 * stand-alone or intrusive.
	 */
	RegistryEntry.Type getType();

	boolean ownerEquals(RegistryEntryOwner<T> owner);

	/**
	 * {@return a new direct registry entry of {@code value}}
	 */
	static <T> RegistryEntry<T> of(T value) {
		return new RegistryEntry.Direct<>(value);
	}

	/**
	 * A direct registry entry holds the value directly. The value does not have to be
	 * registered in a registry. Therefore, they receive no ID or registry key, and they
	 * cannot be tagged.
	 * 
	 * <p>This is most often used in data packs to inline one-time use values directly.
	 */
	public static record Direct<T>(T value) implements RegistryEntry<T> {
		@Override
		public boolean hasKeyAndValue() {
			return true;
		}

		@Override
		public boolean matchesId(Identifier id) {
			return false;
		}

		@Override
		public boolean matchesKey(RegistryKey<T> key) {
			return false;
		}

		@Override
		public boolean isIn(TagKey<T> tag) {
			return false;
		}

		@Override
		public boolean matches(Predicate<RegistryKey<T>> predicate) {
			return false;
		}

		@Override
		public Either<RegistryKey<T>, T> getKeyOrValue() {
			return Either.right(this.value);
		}

		@Override
		public Optional<RegistryKey<T>> getKey() {
			return Optional.empty();
		}

		@Override
		public RegistryEntry.Type getType() {
			return RegistryEntry.Type.DIRECT;
		}

		public String toString() {
			return "Direct{" + this.value + "}";
		}

		@Override
		public boolean ownerEquals(RegistryEntryOwner<T> owner) {
			return true;
		}

		@Override
		public Stream<TagKey<T>> streamTags() {
			return Stream.of();
		}
	}

	/**
	 * A reference registry entry holds the value by reference. The value is previously
	 * registered in a registry, so they can be referred to by their registry keys.
	 * This object also holds the entry's tags.
	 * 
	 * <p>There are two types of reference registry entries.
	 * 
	 * <ul>
	 * <li><strong>Stand-alone</strong> registry entries are first instantiated by its key,
	 * and the value is set when registering the value. This is used by most of the registries.</li>
	 * <li><strong>Intrusive</strong> registry entries are registry entries tied to a specific
	 * registerable object at instantiation time. When instantiating those, it promises
	 * that the object is later registered - which, if broken, will result in a crash.
	 * This is used for {@link Registry#BLOCK}, {@link Registry#ITEM}, {@link Registry#FLUID},
	 * {@link Registry#ENTITY_TYPE}, and {@link Registry#GAME_EVENT} registries. This type
	 * exists for historical reasons and is deprecated.</li>
	 * </ul>
	 * 
	 * <p>Therefore, it is very important to construct any intrusive-entry type object
	 * and register at the same time. For example, a mod that conditionally registers an
	 * {@link net.minecraft.item.Item} has to create an instance only if the condition is met.
	 * (See {@link Registry} for a code example.)
	 * 
	 * <p>When a reference registry entry is first instantiated, it only has either the key
	 * or the value (depending on the type). They are later filled when registering the
	 * entry. Attempting to call methods before those fields are filled
	 * can cause a crash. Note that if you are just getting the entry from a registry, this
	 * should not be a problem.
	 * 
	 * @see Registry#entryOf
	 * @see Registry#getEntry
	 */
	public static class Reference<T> implements RegistryEntry<T> {
		private final RegistryEntryOwner<T> owner;
		private Set<TagKey<T>> tags = Set.of();
		private final RegistryEntry.Reference.Type referenceType;
		@Nullable
		private RegistryKey<T> registryKey;
		@Nullable
		private T value;

		private Reference(RegistryEntry.Reference.Type referenceType, RegistryEntryOwner<T> owner, @Nullable RegistryKey<T> registryKey, @Nullable T value) {
			this.owner = owner;
			this.referenceType = referenceType;
			this.registryKey = registryKey;
			this.value = value;
		}

		/**
		 * {@return a new stand-alone registry entry}
		 * 
		 * <p>This should not be called manually. Call {@link Registry#entryOf} or
		 * {@link Registry#getEntry} instead.
		 * 
		 * <p>Callers are responsible for filling the value later by calling {@link
		 * #setValue}.
		 */
		public static <T> RegistryEntry.Reference<T> standAlone(RegistryEntryOwner<T> owner, RegistryKey<T> registryKey) {
			return new RegistryEntry.Reference<>(RegistryEntry.Reference.Type.STAND_ALONE, owner, registryKey, null);
		}

		/**
		 * {@return a new intrusive registry entry}
		 * 
		 * <p>This should not be called manually. Call {@link Registry#entryOf} or
		 * {@link Registry#getEntry} instead.
		 * 
		 * <p>Callers are responsible for filling the key later by calling {@link
		 * #setRegistryKey}.
		 * 
		 * @deprecated Intrusive holders exist for legacy reasons only.
		 */
		@Deprecated
		public static <T> RegistryEntry.Reference<T> intrusive(RegistryEntryOwner<T> owner, @Nullable T value) {
			return new RegistryEntry.Reference<>(RegistryEntry.Reference.Type.INTRUSIVE, owner, null, value);
		}

		/**
		 * {@return the registry key of this entry}
		 * 
		 * @throws IllegalStateException if this is an intrusive entry and it is not initialized yet
		 */
		public RegistryKey<T> registryKey() {
			if (this.registryKey == null) {
				throw new IllegalStateException("Trying to access unbound value '" + this.value + "' from registry " + this.owner);
			} else {
				return this.registryKey;
			}
		}

		@Override
		public T value() {
			if (this.value == null) {
				throw new IllegalStateException("Trying to access unbound value '" + this.registryKey + "' from registry " + this.owner);
			} else {
				return this.value;
			}
		}

		@Override
		public boolean matchesId(Identifier id) {
			return this.registryKey().getValue().equals(id);
		}

		@Override
		public boolean matchesKey(RegistryKey<T> key) {
			return this.registryKey() == key;
		}

		@Override
		public boolean isIn(TagKey<T> tag) {
			return this.tags.contains(tag);
		}

		@Override
		public boolean matches(Predicate<RegistryKey<T>> predicate) {
			return predicate.test(this.registryKey());
		}

		@Override
		public boolean ownerEquals(RegistryEntryOwner<T> owner) {
			return this.owner.ownerEquals(owner);
		}

		@Override
		public Either<RegistryKey<T>, T> getKeyOrValue() {
			return Either.left(this.registryKey());
		}

		@Override
		public Optional<RegistryKey<T>> getKey() {
			return Optional.of(this.registryKey());
		}

		@Override
		public RegistryEntry.Type getType() {
			return RegistryEntry.Type.REFERENCE;
		}

		@Override
		public boolean hasKeyAndValue() {
			return this.registryKey != null && this.value != null;
		}

		void setRegistryKey(RegistryKey<T> registryKey) {
			if (this.registryKey != null && registryKey != this.registryKey) {
				throw new IllegalStateException("Can't change holder key: existing=" + this.registryKey + ", new=" + registryKey);
			} else {
				this.registryKey = registryKey;
			}
		}

		void setValue(T value) {
			if (this.referenceType == RegistryEntry.Reference.Type.INTRUSIVE && this.value != value) {
				throw new IllegalStateException("Can't change holder " + this.registryKey + " value: existing=" + this.value + ", new=" + value);
			} else {
				this.value = value;
			}
		}

		void setTags(Collection<TagKey<T>> tags) {
			this.tags = Set.copyOf(tags);
		}

		@Override
		public Stream<TagKey<T>> streamTags() {
			return this.tags.stream();
		}

		public String toString() {
			return "Reference{" + this.registryKey + "=" + this.value + "}";
		}

		/**
		 * The types of reference registry entries.
		 * 
		 * @see RegistryEntry.Reference
		 */
		static enum Type {
			STAND_ALONE,
			INTRUSIVE;
		}
	}

	/**
	 * The types of registry entries.
	 * 
	 * @see RegistryEntry
	 */
	public static enum Type {
		REFERENCE,
		DIRECT;
	}
}
