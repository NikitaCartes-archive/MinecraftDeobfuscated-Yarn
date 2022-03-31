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

public interface RegistryEntry<T> {
	T value();

	boolean hasKeyAndValue();

	boolean matchesId(Identifier id);

	boolean matchesKey(RegistryKey<T> key);

	boolean matches(Predicate<RegistryKey<T>> predicate);

	boolean isIn(TagKey<T> tag);

	Stream<TagKey<T>> streamTags();

	Either<RegistryKey<T>, T> getKeyOrValue();

	Optional<RegistryKey<T>> getKey();

	RegistryEntry.Type getType();

	boolean matchesRegistry(Registry<T> registry);

	static <T> RegistryEntry<T> of(T value) {
		return new RegistryEntry.Direct<>(value);
	}

	static <T> RegistryEntry<T> upcast(RegistryEntry<? extends T> entry) {
		return (RegistryEntry<T>)entry;
	}

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
		public boolean matchesRegistry(Registry<T> registry) {
			return true;
		}

		@Override
		public Stream<TagKey<T>> streamTags() {
			return Stream.of();
		}
	}

	public static class Reference<T> implements RegistryEntry<T> {
		private final Registry<T> registry;
		private Set<TagKey<T>> tags = Set.of();
		private final RegistryEntry.Reference.Type referenceType;
		@Nullable
		private RegistryKey<T> registryKey;
		@Nullable
		private T value;

		private Reference(RegistryEntry.Reference.Type referenceType, Registry<T> registry, @Nullable RegistryKey<T> registryKey, @Nullable T value) {
			this.registry = registry;
			this.referenceType = referenceType;
			this.registryKey = registryKey;
			this.value = value;
		}

		public static <T> RegistryEntry.Reference<T> standAlone(Registry<T> registry, RegistryKey<T> registryKey) {
			return new RegistryEntry.Reference<>(RegistryEntry.Reference.Type.STAND_ALONE, registry, registryKey, null);
		}

		@Deprecated
		public static <T> RegistryEntry.Reference<T> intrusive(Registry<T> registry, @Nullable T registryKey) {
			return new RegistryEntry.Reference<>(RegistryEntry.Reference.Type.INTRUSIVE, registry, null, registryKey);
		}

		public RegistryKey<T> registryKey() {
			if (this.registryKey == null) {
				throw new IllegalStateException("Trying to access unbound value '" + this.value + "' from registry " + this.registry);
			} else {
				return this.registryKey;
			}
		}

		@Override
		public T value() {
			if (this.value == null) {
				throw new IllegalStateException("Trying to access unbound value '" + this.registryKey + "' from registry " + this.registry);
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
		public boolean matchesRegistry(Registry<T> registry) {
			return this.registry == registry;
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

		void setKeyAndValue(RegistryKey<T> key, T value) {
			if (this.registryKey != null && key != this.registryKey) {
				throw new IllegalStateException("Can't change holder key: existing=" + this.registryKey + ", new=" + key);
			} else if (this.referenceType == RegistryEntry.Reference.Type.INTRUSIVE && this.value != value) {
				throw new IllegalStateException("Can't change holder " + key + " value: existing=" + this.value + ", new=" + value);
			} else {
				this.registryKey = key;
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

		static enum Type {
			STAND_ALONE,
			INTRUSIVE;
		}
	}

	public static enum Type {
		REFERENCE,
		DIRECT;
	}
}
