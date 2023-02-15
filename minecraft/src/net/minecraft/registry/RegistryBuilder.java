package net.minecraft.registry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.Lifecycle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class RegistryBuilder {
	private final List<RegistryBuilder.RegistryInfo<?>> registries = new ArrayList();

	static <T> RegistryEntryLookup<T> toLookup(RegistryWrapper.Impl<T> wrapper) {
		return new RegistryBuilder.EntryListCreatingLookup<T>(wrapper) {
			@Override
			public Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> key) {
				return wrapper.getOptional(key);
			}
		};
	}

	public <T> RegistryBuilder addRegistry(
		RegistryKey<? extends Registry<T>> registryRef, Lifecycle lifecycle, RegistryBuilder.BootstrapFunction<T> bootstrapFunction
	) {
		this.registries.add(new RegistryBuilder.RegistryInfo<>(registryRef, lifecycle, bootstrapFunction));
		return this;
	}

	public <T> RegistryBuilder addRegistry(RegistryKey<? extends Registry<T>> registryRef, RegistryBuilder.BootstrapFunction<T> bootstrapFunction) {
		return this.addRegistry(registryRef, Lifecycle.stable(), bootstrapFunction);
	}

	private RegistryBuilder.Registries createBootstrappedRegistries(DynamicRegistryManager registryManager) {
		RegistryBuilder.Registries registries = RegistryBuilder.Registries.of(registryManager, this.registries.stream().map(RegistryBuilder.RegistryInfo::key));
		this.registries.forEach(registry -> registry.runBootstrap(registries));
		return registries;
	}

	public RegistryWrapper.WrapperLookup createWrapperLookup(DynamicRegistryManager baseRegistryManager) {
		RegistryBuilder.Registries registries = this.createBootstrappedRegistries(baseRegistryManager);
		Stream<RegistryWrapper.Impl<?>> stream = baseRegistryManager.streamAllRegistries().map(entry -> entry.value().getReadOnlyWrapper());
		Stream<RegistryWrapper.Impl<?>> stream2 = this.registries.stream().map(info -> info.init(registries).toWrapper());
		RegistryWrapper.WrapperLookup wrapperLookup = RegistryWrapper.WrapperLookup.of(Stream.concat(stream, stream2.peek(registries::addOwner)));
		registries.validateReferences();
		registries.throwErrors();
		return wrapperLookup;
	}

	public RegistryWrapper.WrapperLookup createWrapperLookup(DynamicRegistryManager baseRegistryManager, RegistryWrapper.WrapperLookup wrapperLookup) {
		RegistryBuilder.Registries registries = this.createBootstrappedRegistries(baseRegistryManager);
		Map<RegistryKey<? extends Registry<?>>, RegistryBuilder.InitializedRegistry<?>> map = new HashMap();
		registries.streamRegistries().forEach(registry -> map.put(registry.key, registry));
		this.registries.stream().map(info -> info.init(registries)).forEach(registry -> map.put(registry.key, registry));
		Stream<RegistryWrapper.Impl<?>> stream = baseRegistryManager.streamAllRegistries().map(entry -> entry.value().getReadOnlyWrapper());
		RegistryWrapper.WrapperLookup wrapperLookup2 = RegistryWrapper.WrapperLookup.of(
			Stream.concat(stream, map.values().stream().map(RegistryBuilder.InitializedRegistry::toWrapper).peek(registries::addOwner))
		);
		registries.setReferenceEntryValues(wrapperLookup);
		registries.validateReferences();
		registries.throwErrors();
		return wrapperLookup2;
	}

	/**
	 * An owner that owns multiple registries and holds the owner object for those.
	 * {@link #ownerEquals} returns {@code true} for all owners {@linkplain #addOwner previously
	 * added}.
	 */
	static class AnyOwner implements RegistryEntryOwner<Object> {
		private final Set<RegistryEntryOwner<?>> owners = Sets.newIdentityHashSet();

		@Override
		public boolean ownerEquals(RegistryEntryOwner<Object> other) {
			return this.owners.contains(other);
		}

		public void addOwner(RegistryEntryOwner<?> owner) {
			this.owners.add(owner);
		}
	}

	@FunctionalInterface
	public interface BootstrapFunction<T> {
		void run(Registerable<T> registerable);
	}

	static record EntryAssociatedValue<T>(RegistryBuilder.RegisteredValue<T> value, Optional<RegistryEntry.Reference<T>> entry) {
	}

	abstract static class EntryListCreatingLookup<T> implements RegistryEntryLookup<T> {
		protected final RegistryEntryOwner<T> entryOwner;

		protected EntryListCreatingLookup(RegistryEntryOwner<T> entryOwner) {
			this.entryOwner = entryOwner;
		}

		@Override
		public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> tag) {
			return Optional.of(RegistryEntryList.of(this.entryOwner, tag));
		}
	}

	static record InitializedRegistry<T>(
		RegistryKey<? extends Registry<? extends T>> key, Lifecycle lifecycle, Map<RegistryKey<T>, RegistryBuilder.EntryAssociatedValue<T>> values
	) {

		public RegistryWrapper.Impl<T> toWrapper() {
			return new RegistryWrapper.Impl<T>() {
				private final Map<RegistryKey<T>, RegistryEntry.Reference<T>> keysToEntries = (Map<RegistryKey<T>, RegistryEntry.Reference<T>>)InitializedRegistry.this.values
					.entrySet()
					.stream()
					.collect(
						Collectors.toUnmodifiableMap(
							Entry::getKey,
							entry -> {
								RegistryBuilder.EntryAssociatedValue<T> entryAssociatedValue = (RegistryBuilder.EntryAssociatedValue<T>)entry.getValue();
								RegistryEntry.Reference<T> reference = (RegistryEntry.Reference<T>)entryAssociatedValue.entry()
									.orElseGet(() -> RegistryEntry.Reference.standAlone(this, (RegistryKey<T>)entry.getKey()));
								reference.setValue(entryAssociatedValue.value().value());
								return reference;
							}
						)
					);

				@Override
				public RegistryKey<? extends Registry<? extends T>> getRegistryKey() {
					return InitializedRegistry.this.key;
				}

				@Override
				public Lifecycle getLifecycle() {
					return InitializedRegistry.this.lifecycle;
				}

				@Override
				public Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> key) {
					return Optional.ofNullable((RegistryEntry.Reference)this.keysToEntries.get(key));
				}

				@Override
				public Stream<RegistryEntry.Reference<T>> streamEntries() {
					return this.keysToEntries.values().stream();
				}

				@Override
				public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> tag) {
					return Optional.empty();
				}

				@Override
				public Stream<RegistryEntryList.Named<T>> streamTags() {
					return Stream.empty();
				}
			};
		}
	}

	static record RegisteredValue<T>(T value, Lifecycle lifecycle) {
	}

	static record Registries(
		RegistryBuilder.AnyOwner owner,
		RegistryBuilder.StandAloneEntryCreatingLookup lookup,
		Map<Identifier, RegistryEntryLookup<?>> registries,
		Map<RegistryKey<?>, RegistryBuilder.RegisteredValue<?>> registeredValues,
		List<RuntimeException> errors
	) {

		public static RegistryBuilder.Registries of(DynamicRegistryManager dynamicRegistryManager, Stream<RegistryKey<? extends Registry<?>>> registryRefs) {
			RegistryBuilder.AnyOwner anyOwner = new RegistryBuilder.AnyOwner();
			List<RuntimeException> list = new ArrayList();
			RegistryBuilder.StandAloneEntryCreatingLookup standAloneEntryCreatingLookup = new RegistryBuilder.StandAloneEntryCreatingLookup(anyOwner);
			Builder<Identifier, RegistryEntryLookup<?>> builder = ImmutableMap.builder();
			dynamicRegistryManager.streamAllRegistries()
				.forEach(entry -> builder.put(entry.key().getValue(), RegistryBuilder.toLookup(entry.value().getReadOnlyWrapper())));
			registryRefs.forEach(registryRef -> builder.put(registryRef.getValue(), standAloneEntryCreatingLookup));
			return new RegistryBuilder.Registries(anyOwner, standAloneEntryCreatingLookup, builder.build(), new HashMap(), list);
		}

		public <T> Registerable<T> createRegisterable() {
			return new Registerable<T>() {
				@Override
				public RegistryEntry.Reference<T> register(RegistryKey<T> key, T value, Lifecycle lifecycle) {
					RegistryBuilder.RegisteredValue<?> registeredValue = (RegistryBuilder.RegisteredValue<?>)Registries.this.registeredValues
						.put(key, new RegistryBuilder.RegisteredValue(value, lifecycle));
					if (registeredValue != null) {
						Registries.this.errors.add(new IllegalStateException("Duplicate registration for " + key + ", new=" + value + ", old=" + registeredValue.value));
					}

					return Registries.this.lookup.getOrCreate(key);
				}

				@Override
				public <S> RegistryEntryLookup<S> getRegistryLookup(RegistryKey<? extends Registry<? extends S>> registryRef) {
					return (RegistryEntryLookup<S>)Registries.this.registries.getOrDefault(registryRef.getValue(), Registries.this.lookup);
				}
			};
		}

		public void validateReferences() {
			for (RegistryKey<Object> registryKey : this.lookup.keysToEntries.keySet()) {
				this.errors.add(new IllegalStateException("Unreferenced key: " + registryKey));
			}

			this.registeredValues.forEach((key, value) -> this.errors.add(new IllegalStateException("Orpaned value " + value.value + " for key " + key)));
		}

		public void throwErrors() {
			if (!this.errors.isEmpty()) {
				IllegalStateException illegalStateException = new IllegalStateException("Errors during registry creation");

				for (RuntimeException runtimeException : this.errors) {
					illegalStateException.addSuppressed(runtimeException);
				}

				throw illegalStateException;
			}
		}

		public void addOwner(RegistryEntryOwner<?> owner) {
			this.owner.addOwner(owner);
		}

		public void setReferenceEntryValues(RegistryWrapper.WrapperLookup lookup) {
			Map<Identifier, Optional<? extends RegistryWrapper<Object>>> map = new HashMap();
			Iterator<Entry<RegistryKey<Object>, RegistryEntry.Reference<Object>>> iterator = this.lookup.keysToEntries.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<RegistryKey<Object>, RegistryEntry.Reference<Object>> entry = (Entry<RegistryKey<Object>, RegistryEntry.Reference<Object>>)iterator.next();
				RegistryKey<Object> registryKey = (RegistryKey<Object>)entry.getKey();
				RegistryEntry.Reference<Object> reference = (RegistryEntry.Reference<Object>)entry.getValue();
				((Optional)map.computeIfAbsent(registryKey.getRegistry(), registryId -> lookup.getOptionalWrapper(RegistryKey.ofRegistry(registryId))))
					.flatMap(entryLookup -> entryLookup.getOptional(registryKey))
					.ifPresent(entryx -> {
						reference.setValue(entryx.value());
						iterator.remove();
					});
			}
		}

		public Stream<RegistryBuilder.InitializedRegistry<?>> streamRegistries() {
			return this.lookup
				.keysToEntries
				.keySet()
				.stream()
				.map(RegistryKey::getRegistry)
				.distinct()
				.map(registry -> new RegistryBuilder.InitializedRegistry(RegistryKey.ofRegistry(registry), Lifecycle.stable(), Map.of()));
		}
	}

	static record RegistryInfo<T>(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle, RegistryBuilder.BootstrapFunction<T> bootstrap) {
		void runBootstrap(RegistryBuilder.Registries registries) {
			this.bootstrap.run(registries.createRegisterable());
		}

		public RegistryBuilder.InitializedRegistry<T> init(RegistryBuilder.Registries registries) {
			Map<RegistryKey<T>, RegistryBuilder.EntryAssociatedValue<T>> map = new HashMap();
			Iterator<Entry<RegistryKey<?>, RegistryBuilder.RegisteredValue<?>>> iterator = registries.registeredValues.entrySet().iterator();

			while (iterator.hasNext()) {
				Entry<RegistryKey<?>, RegistryBuilder.RegisteredValue<?>> entry = (Entry<RegistryKey<?>, RegistryBuilder.RegisteredValue<?>>)iterator.next();
				RegistryKey<?> registryKey = (RegistryKey<?>)entry.getKey();
				if (registryKey.isOf(this.key)) {
					RegistryBuilder.RegisteredValue<T> registeredValue = (RegistryBuilder.RegisteredValue<T>)entry.getValue();
					RegistryEntry.Reference<T> reference = (RegistryEntry.Reference<T>)registries.lookup.keysToEntries.remove(registryKey);
					map.put(registryKey, new RegistryBuilder.EntryAssociatedValue<>(registeredValue, Optional.ofNullable(reference)));
					iterator.remove();
				}
			}

			return new RegistryBuilder.InitializedRegistry<>(this.key, this.lifecycle, map);
		}
	}

	static class StandAloneEntryCreatingLookup extends RegistryBuilder.EntryListCreatingLookup<Object> {
		final Map<RegistryKey<Object>, RegistryEntry.Reference<Object>> keysToEntries = new HashMap();

		public StandAloneEntryCreatingLookup(RegistryEntryOwner<Object> registryEntryOwner) {
			super(registryEntryOwner);
		}

		@Override
		public Optional<RegistryEntry.Reference<Object>> getOptional(RegistryKey<Object> key) {
			return Optional.of(this.getOrCreate(key));
		}

		<T> RegistryEntry.Reference<T> getOrCreate(RegistryKey<T> key) {
			return (RegistryEntry.Reference<T>)this.keysToEntries.computeIfAbsent(key, key2 -> RegistryEntry.Reference.standAlone(this.entryOwner, key2));
		}
	}
}
