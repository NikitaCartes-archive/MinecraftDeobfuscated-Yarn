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
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.mutable.MutableObject;

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

	static <T> RegistryWrapper.Impl<T> createWrapper(
		RegistryKey<? extends Registry<? extends T>> registryRef, Lifecycle lifecycle, Map<RegistryKey<T>, RegistryEntry.Reference<T>> entries
	) {
		return new RegistryWrapper.Impl<T>() {
			@Override
			public RegistryKey<? extends Registry<? extends T>> getRegistryKey() {
				return registryRef;
			}

			@Override
			public Lifecycle getLifecycle() {
				return lifecycle;
			}

			@Override
			public Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> key) {
				return Optional.ofNullable((RegistryEntry.Reference)entries.get(key));
			}

			@Override
			public Stream<RegistryEntry.Reference<T>> streamEntries() {
				return entries.values().stream();
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

	private static RegistryWrapper.WrapperLookup createWrapperLookup(DynamicRegistryManager registryManager, Stream<RegistryWrapper.Impl<?>> additionalRegistries) {
		Stream<RegistryWrapper.Impl<?>> stream = registryManager.streamAllRegistries().map(entry -> entry.value().getReadOnlyWrapper());
		return RegistryWrapper.WrapperLookup.of(Stream.concat(stream, additionalRegistries));
	}

	public RegistryWrapper.WrapperLookup createWrapperLookup(DynamicRegistryManager registryManager) {
		RegistryBuilder.Registries registries = this.createBootstrappedRegistries(registryManager);
		Stream<RegistryWrapper.Impl<?>> stream = this.registries.stream().map(info -> info.init(registries).toWrapper(registries.owner));
		RegistryWrapper.WrapperLookup wrapperLookup = createWrapperLookup(registryManager, stream);
		registries.checkUnreferencedKeys();
		registries.checkOrphanedValues();
		registries.throwErrors();
		return wrapperLookup;
	}

	private RegistryWrapper.WrapperLookup createFullWrapperLookup(
		DynamicRegistryManager registryManager,
		RegistryWrapper.WrapperLookup base,
		RegistryCloner.CloneableRegistries cloneableRegistries,
		Map<RegistryKey<? extends Registry<?>>, RegistryBuilder.InitializedRegistry<?>> initializedRegistries,
		RegistryWrapper.WrapperLookup patches
	) {
		RegistryBuilder.AnyOwner anyOwner = new RegistryBuilder.AnyOwner();
		MutableObject<RegistryWrapper.WrapperLookup> mutableObject = new MutableObject<>();
		List<RegistryWrapper.Impl<?>> list = (List<RegistryWrapper.Impl<?>>)initializedRegistries.keySet()
			.stream()
			.map(registryRef -> this.applyPatches(anyOwner, cloneableRegistries, registryRef, patches, base, mutableObject))
			.peek(anyOwner::addOwner)
			.collect(Collectors.toUnmodifiableList());
		RegistryWrapper.WrapperLookup wrapperLookup = createWrapperLookup(registryManager, list.stream());
		mutableObject.setValue(wrapperLookup);
		return wrapperLookup;
	}

	private <T> RegistryWrapper.Impl<T> applyPatches(
		RegistryEntryOwner<T> owner,
		RegistryCloner.CloneableRegistries cloneableRegistries,
		RegistryKey<? extends Registry<? extends T>> registryRef,
		RegistryWrapper.WrapperLookup patches,
		RegistryWrapper.WrapperLookup base,
		MutableObject<RegistryWrapper.WrapperLookup> lazyWrapper
	) {
		RegistryCloner<T> registryCloner = cloneableRegistries.get(registryRef);
		if (registryCloner == null) {
			throw new NullPointerException("No cloner for " + registryRef.getValue());
		} else {
			Map<RegistryKey<T>, RegistryEntry.Reference<T>> map = new HashMap();
			RegistryWrapper.Impl<T> impl = patches.getWrapperOrThrow(registryRef);
			impl.streamEntries().forEach(entry -> {
				RegistryKey<T> registryKey = entry.registryKey();
				RegistryBuilder.LazyReferenceEntry<T> lazyReferenceEntry = new RegistryBuilder.LazyReferenceEntry<>(owner, registryKey);
				lazyReferenceEntry.supplier = () -> registryCloner.clone((T)entry.value(), patches, lazyWrapper.getValue());
				map.put(registryKey, lazyReferenceEntry);
			});
			RegistryWrapper.Impl<T> impl2 = base.getWrapperOrThrow(registryRef);
			impl2.streamEntries().forEach(entry -> {
				RegistryKey<T> registryKey = entry.registryKey();
				map.computeIfAbsent(registryKey, key -> {
					RegistryBuilder.LazyReferenceEntry<T> lazyReferenceEntry = new RegistryBuilder.LazyReferenceEntry<>(owner, registryKey);
					lazyReferenceEntry.supplier = () -> registryCloner.clone((T)entry.value(), base, lazyWrapper.getValue());
					return lazyReferenceEntry;
				});
			});
			Lifecycle lifecycle = impl.getLifecycle().add(impl2.getLifecycle());
			return createWrapper(registryRef, lifecycle, map);
		}
	}

	public RegistryBuilder.FullPatchesRegistriesPair createWrapperLookup(
		DynamicRegistryManager baseRegistryManager, RegistryWrapper.WrapperLookup wrapperLookup, RegistryCloner.CloneableRegistries cloneableRegistries
	) {
		RegistryBuilder.Registries registries = this.createBootstrappedRegistries(baseRegistryManager);
		Map<RegistryKey<? extends Registry<?>>, RegistryBuilder.InitializedRegistry<?>> map = new HashMap();
		this.registries.stream().map(info -> info.init(registries)).forEach(registry -> map.put(registry.key, registry));
		Set<RegistryKey<? extends Registry<?>>> set = (Set<RegistryKey<? extends Registry<?>>>)baseRegistryManager.streamAllRegistryKeys()
			.collect(Collectors.toUnmodifiableSet());
		wrapperLookup.streamAllRegistryKeys()
			.filter(key -> !set.contains(key))
			.forEach(key -> map.putIfAbsent(key, new RegistryBuilder.InitializedRegistry(key, Lifecycle.stable(), Map.of())));
		Stream<RegistryWrapper.Impl<?>> stream = map.values().stream().map(registry -> registry.toWrapper(registries.owner));
		RegistryWrapper.WrapperLookup wrapperLookup2 = createWrapperLookup(baseRegistryManager, stream);
		registries.checkOrphanedValues();
		registries.throwErrors();
		RegistryWrapper.WrapperLookup wrapperLookup3 = this.createFullWrapperLookup(baseRegistryManager, wrapperLookup, cloneableRegistries, map, wrapperLookup2);
		return new RegistryBuilder.FullPatchesRegistriesPair(wrapperLookup3, wrapperLookup2);
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

		public <T> RegistryEntryOwner<T> downcast() {
			return this;
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

	public static record FullPatchesRegistriesPair(RegistryWrapper.WrapperLookup full, RegistryWrapper.WrapperLookup patches) {
	}

	static record InitializedRegistry<T>(
		RegistryKey<? extends Registry<? extends T>> key, Lifecycle lifecycle, Map<RegistryKey<T>, RegistryBuilder.EntryAssociatedValue<T>> values
	) {

		public RegistryWrapper.Impl<T> toWrapper(RegistryBuilder.AnyOwner anyOwner) {
			Map<RegistryKey<T>, RegistryEntry.Reference<T>> map = (Map<RegistryKey<T>, RegistryEntry.Reference<T>>)this.values
				.entrySet()
				.stream()
				.collect(
					Collectors.toUnmodifiableMap(
						Entry::getKey,
						entry -> {
							RegistryBuilder.EntryAssociatedValue<T> entryAssociatedValue = (RegistryBuilder.EntryAssociatedValue<T>)entry.getValue();
							RegistryEntry.Reference<T> reference = (RegistryEntry.Reference<T>)entryAssociatedValue.entry()
								.orElseGet(() -> RegistryEntry.Reference.standAlone(anyOwner.downcast(), (RegistryKey<T>)entry.getKey()));
							reference.setValue(entryAssociatedValue.value().value());
							return reference;
						}
					)
				);
			RegistryWrapper.Impl<T> impl = RegistryBuilder.createWrapper(this.key, this.lifecycle, map);
			anyOwner.addOwner(impl);
			return impl;
		}
	}

	static class LazyReferenceEntry<T> extends RegistryEntry.Reference<T> {
		@Nullable
		Supplier<T> supplier;

		protected LazyReferenceEntry(RegistryEntryOwner<T> owner, @Nullable RegistryKey<T> key) {
			super(RegistryEntry.Reference.Type.STAND_ALONE, owner, key, null);
		}

		@Override
		protected void setValue(T value) {
			super.setValue(value);
			this.supplier = null;
		}

		@Override
		public T value() {
			if (this.supplier != null) {
				this.setValue((T)this.supplier.get());
			}

			return super.value();
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

		public void checkOrphanedValues() {
			this.registeredValues.forEach((key, value) -> this.errors.add(new IllegalStateException("Orpaned value " + value.value + " for key " + key)));
		}

		public void checkUnreferencedKeys() {
			for (RegistryKey<Object> registryKey : this.lookup.keysToEntries.keySet()) {
				this.errors.add(new IllegalStateException("Unreferenced key: " + registryKey));
			}
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
