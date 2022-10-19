/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.registry;

import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;

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
    public T value();

    public boolean hasKeyAndValue();

    /**
     * {@return whether the ID of this entry is {@code id}}
     * 
     * <p>This always returns {@code false} for direct entries.
     */
    public boolean matchesId(Identifier var1);

    /**
     * {@return whether the registry key of this entry is {@code key}}
     * 
     * <p>This always returns {@code false} for direct entries.
     */
    public boolean matchesKey(RegistryKey<T> var1);

    /**
     * {@return whether this entry's key matches {@code predicate}}
     * 
     * <p>This always returns {@code false} for direct entries.
     */
    public boolean matches(Predicate<RegistryKey<T>> var1);

    /**
     * {@return whether this entry is in {@code tag}}
     * 
     * <p>This always returns {@code false} for direct entries, since tags are managed by
     * a registry.
     */
    public boolean isIn(TagKey<T> var1);

    /**
     * {@return a stream of the tags of this entry, or an empty stream if this is a direct entry}
     */
    public Stream<TagKey<T>> streamTags();

    /**
     * {@return the object that identifies this registry key}
     * 
     * <p>For direct entries, this is the held value, and for reference entries, this is the
     * key of the entry.
     */
    public Either<RegistryKey<T>, T> getKeyOrValue();

    /**
     * {@return the registry key of this entry, or an empty optional if this is a direct entry}
     */
    public Optional<RegistryKey<T>> getKey();

    /**
     * {@return the type (direct or reference) of this registry entry}
     * 
     * <p>This is different from the types of reference registry entries, i.e.
     * stand-alone or intrusive.
     */
    public Type getType();

    /**
     * {@return whether the registry for the entry is {@code registry}}
     * 
     * <p>This always returns {@code true} for direct entries.
     */
    public boolean matchesRegistry(Registry<T> var1);

    /**
     * {@return a new direct registry entry of {@code value}}
     */
    public static <T> RegistryEntry<T> of(T value) {
        return new Direct<T>(value);
    }

    /**
     * Casts {@code RegistryEntry<? extends T>} to {@code RegistryEntry<T>}.
     * 
     * @return the cast value
     */
    public static <T> RegistryEntry<T> upcast(RegistryEntry<? extends T> entry) {
        return entry;
    }

    public record Direct<T>(T value) implements RegistryEntry<T>
    {
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
        public Type getType() {
            return Type.DIRECT;
        }

        @Override
        public String toString() {
            return "Direct{" + this.value + "}";
        }

        @Override
        public boolean matchesRegistry(Registry<T> registry) {
            return true;
        }

        @Override
        public Stream<TagKey<T>> streamTags() {
            return Stream.of(new TagKey[0]);
        }
    }

    public static class Reference<T>
    implements RegistryEntry<T> {
        private final Registry<T> registry;
        private Set<TagKey<T>> tags = Set.of();
        private final Type referenceType;
        @Nullable
        private RegistryKey<T> registryKey;
        @Nullable
        private T value;

        private Reference(Type referenceType, Registry<T> registry, @Nullable RegistryKey<T> registryKey, @Nullable T value) {
            this.registry = registry;
            this.referenceType = referenceType;
            this.registryKey = registryKey;
            this.value = value;
        }

        public static <T> Reference<T> standAlone(Registry<T> registry, RegistryKey<T> registryKey) {
            return new Reference<Object>(Type.STAND_ALONE, registry, registryKey, null);
        }

        @Deprecated
        public static <T> Reference<T> intrusive(Registry<T> registry, @Nullable T value) {
            return new Reference<T>(Type.INTRUSIVE, registry, null, value);
        }

        public RegistryKey<T> registryKey() {
            if (this.registryKey == null) {
                throw new IllegalStateException("Trying to access unbound value '" + this.value + "' from registry " + this.registry);
            }
            return this.registryKey;
        }

        @Override
        public T value() {
            if (this.value == null) {
                throw new IllegalStateException("Trying to access unbound value '" + this.registryKey + "' from registry " + this.registry);
            }
            return this.value;
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
        public net.minecraft.util.registry.RegistryEntry$Type getType() {
            return net.minecraft.util.registry.RegistryEntry$Type.REFERENCE;
        }

        @Override
        public boolean hasKeyAndValue() {
            return this.registryKey != null && this.value != null;
        }

        void setKeyAndValue(RegistryKey<T> key, T value) {
            if (this.registryKey != null && key != this.registryKey) {
                throw new IllegalStateException("Can't change holder key: existing=" + this.registryKey + ", new=" + key);
            }
            if (this.referenceType == Type.INTRUSIVE && this.value != value) {
                throw new IllegalStateException("Can't change holder " + key + " value: existing=" + this.value + ", new=" + value);
            }
            this.registryKey = key;
            this.value = value;
        }

        void setRegistryKey(RegistryKey<T> registryKey) {
            if (this.registryKey != null && registryKey != this.registryKey) {
                throw new IllegalStateException("Can't change holder key: existing=" + this.registryKey + ", new=" + registryKey);
            }
            this.registryKey = registryKey;
        }

        void setValue(T value) {
            if (this.referenceType == Type.INTRUSIVE && this.value != value) {
                throw new IllegalStateException("Can't change holder " + this.registryKey + " value: existing=" + this.value + ", new=" + value);
            }
            this.value = value;
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

