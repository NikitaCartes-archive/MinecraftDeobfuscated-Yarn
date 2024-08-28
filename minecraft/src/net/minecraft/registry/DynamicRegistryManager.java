package net.minecraft.registry;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;

/**
 * A manager of dynamic registries. It allows users to access non-hardcoded
 * registries reliably.
 * 
 * <p>The {@link DynamicRegistryManager.ImmutableImpl}
 * class serves as an immutable implementation of any particular collection
 * or configuration of dynamic registries.
 */
public interface DynamicRegistryManager extends RegistryWrapper.WrapperLookup {
	Logger LOGGER = LogUtils.getLogger();
	DynamicRegistryManager.Immutable EMPTY = new DynamicRegistryManager.ImmutableImpl(Map.of()).toImmutable();

	@Override
	<E> Optional<Registry<E>> getOptional(RegistryKey<? extends Registry<? extends E>> registryRef);

	default <E> Registry<E> getOrThrow(RegistryKey<? extends Registry<? extends E>> key) {
		return (Registry<E>)this.getOptional(key).orElseThrow(() -> new IllegalStateException("Missing registry: " + key));
	}

	Stream<DynamicRegistryManager.Entry<?>> streamAllRegistries();

	@Override
	default Stream<RegistryKey<? extends Registry<?>>> streamAllRegistryKeys() {
		return this.streamAllRegistries().map(registry -> registry.key);
	}

	static DynamicRegistryManager.Immutable of(Registry<? extends Registry<?>> registries) {
		return new DynamicRegistryManager.Immutable() {
			@Override
			public <T> Optional<Registry<T>> getOptional(RegistryKey<? extends Registry<? extends T>> registryRef) {
				Registry<Registry<T>> registry = (Registry<Registry<T>>)registries;
				return registry.getOptionalValue((RegistryKey<Registry<T>>)registryRef);
			}

			@Override
			public Stream<DynamicRegistryManager.Entry<?>> streamAllRegistries() {
				return registries.getEntrySet().stream().map(DynamicRegistryManager.Entry::of);
			}

			@Override
			public DynamicRegistryManager.Immutable toImmutable() {
				return this;
			}
		};
	}

	default DynamicRegistryManager.Immutable toImmutable() {
		class Immutablized extends DynamicRegistryManager.ImmutableImpl implements DynamicRegistryManager.Immutable {
			protected Immutablized(final Stream<DynamicRegistryManager.Entry<?>> entryStream) {
				super(entryStream);
			}
		}

		return new Immutablized(this.streamAllRegistries().map(DynamicRegistryManager.Entry::freeze));
	}

	public static record Entry<T>(RegistryKey<? extends Registry<T>> key, Registry<T> value) {

		private static <T, R extends Registry<? extends T>> DynamicRegistryManager.Entry<T> of(
			java.util.Map.Entry<? extends RegistryKey<? extends Registry<?>>, R> entry
		) {
			return of((RegistryKey<? extends Registry<?>>)entry.getKey(), (Registry<?>)entry.getValue());
		}

		private static <T> DynamicRegistryManager.Entry<T> of(RegistryKey<? extends Registry<?>> key, Registry<?> value) {
			return new DynamicRegistryManager.Entry<>((RegistryKey<? extends Registry<T>>)key, (Registry<T>)value);
		}

		private DynamicRegistryManager.Entry<T> freeze() {
			return new DynamicRegistryManager.Entry<>(this.key, this.value.freeze());
		}
	}

	public interface Immutable extends DynamicRegistryManager {
	}

	public static class ImmutableImpl implements DynamicRegistryManager {
		private final Map<? extends RegistryKey<? extends Registry<?>>, ? extends Registry<?>> registries;

		public ImmutableImpl(List<? extends Registry<?>> registries) {
			this.registries = (Map<? extends RegistryKey<? extends Registry<?>>, ? extends Registry<?>>)registries.stream()
				.collect(Collectors.toUnmodifiableMap(Registry::getKey, registry -> registry));
		}

		public ImmutableImpl(Map<? extends RegistryKey<? extends Registry<?>>, ? extends Registry<?>> registries) {
			this.registries = Map.copyOf(registries);
		}

		public ImmutableImpl(Stream<DynamicRegistryManager.Entry<?>> entryStream) {
			this.registries = (Map<? extends RegistryKey<? extends Registry<?>>, ? extends Registry<?>>)entryStream.collect(
				ImmutableMap.toImmutableMap(DynamicRegistryManager.Entry::key, DynamicRegistryManager.Entry::value)
			);
		}

		@Override
		public <E> Optional<Registry<E>> getOptional(RegistryKey<? extends Registry<? extends E>> registryRef) {
			return Optional.ofNullable((Registry)this.registries.get(registryRef)).map(registry -> registry);
		}

		@Override
		public Stream<DynamicRegistryManager.Entry<?>> streamAllRegistries() {
			return this.registries.entrySet().stream().map(DynamicRegistryManager.Entry::of);
		}
	}
}
