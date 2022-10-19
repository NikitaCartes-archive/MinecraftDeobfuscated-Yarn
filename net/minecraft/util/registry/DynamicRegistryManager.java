/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.registry;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.slf4j.Logger;

/**
 * A manager of dynamic registries. It allows users to access non-hardcoded
 * registries reliably.
 * 
 * <p>The {@link DynamicRegistryManager.ImmutableImpl}
 * class serves as an immutable implementation of any particular collection
 * or configuration of dynamic registries.
 */
public interface DynamicRegistryManager {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Immutable EMPTY = new ImmutableImpl(Map.of()).toImmutable();

    public <E> Optional<Registry<E>> getOptional(RegistryKey<? extends Registry<? extends E>> var1);

    /**
     * Retrieves a registry from this manager, or throws an exception when the registry
     * does not exist.
     * 
     * @throws IllegalStateException if the registry does not exist
     */
    default public <E> Registry<E> get(RegistryKey<? extends Registry<? extends E>> key) {
        return this.getOptional(key).orElseThrow(() -> new IllegalStateException("Missing registry: " + key));
    }

    public Stream<Entry<?>> streamAllRegistries();

    public static Immutable of(final Registry<? extends Registry<?>> registries) {
        return new Immutable(){

            public <T> Optional<Registry<T>> getOptional(RegistryKey<? extends Registry<? extends T>> key) {
                Registry registry = registries;
                return registry.getOrEmpty(key);
            }

            @Override
            public Stream<Entry<?>> streamAllRegistries() {
                return registries.getEntrySet().stream().map(Entry::of);
            }

            @Override
            public Immutable toImmutable() {
                return this;
            }
        };
    }

    default public Immutable toImmutable() {
        class Immutablized
        extends ImmutableImpl
        implements Immutable {
            protected Immutablized(Stream<Entry<?>> entryStream) {
                super(entryStream);
            }
        }
        return new Immutablized(this.streamAllRegistries().map(Entry::freeze));
    }

    default public Lifecycle getRegistryLifecycle() {
        return this.streamAllRegistries().map(entry -> entry.value.getLifecycle()).reduce(Lifecycle.stable(), Lifecycle::add);
    }

    public record Entry<T>(RegistryKey<? extends Registry<T>> key, Registry<T> value) {
        private static <T, R extends Registry<? extends T>> Entry<T> of(Map.Entry<? extends RegistryKey<? extends Registry<?>>, R> entry) {
            return Entry.of(entry.getKey(), (Registry)entry.getValue());
        }

        private static <T> Entry<T> of(RegistryKey<? extends Registry<?>> key, Registry<?> value) {
            return new Entry(key, value);
        }

        private Entry<T> freeze() {
            return new Entry<T>(this.key, this.value.freeze());
        }
    }

    public static class ImmutableImpl
    implements DynamicRegistryManager {
        private final Map<? extends RegistryKey<? extends Registry<?>>, ? extends Registry<?>> registries;

        public ImmutableImpl(List<? extends Registry<?>> registries) {
            this.registries = registries.stream().collect(Collectors.toUnmodifiableMap(Registry::getKey, registry -> registry));
        }

        public ImmutableImpl(Map<? extends RegistryKey<? extends Registry<?>>, ? extends Registry<?>> registries) {
            this.registries = Map.copyOf(registries);
        }

        public ImmutableImpl(Stream<Entry<?>> entryStream) {
            this.registries = entryStream.collect(ImmutableMap.toImmutableMap(Entry::key, Entry::value));
        }

        @Override
        public <E> Optional<Registry<E>> getOptional(RegistryKey<? extends Registry<? extends E>> key) {
            return Optional.ofNullable(this.registries.get(key)).map(registry -> registry);
        }

        @Override
        public Stream<Entry<?>> streamAllRegistries() {
            return this.registries.entrySet().stream().map(Entry::of);
        }
    }

    public static interface Immutable
    extends DynamicRegistryManager {
    }
}

