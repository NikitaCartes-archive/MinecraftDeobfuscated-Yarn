package net.minecraft.registry;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryOwner;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.featuretoggle.ToggleableFeature;

/**
 * A read-only wrapper of a registry.
 */
public interface RegistryWrapper<T> extends RegistryEntryLookup<T> {
	/**
	 * {@return a stream of registry keys defined in the wrapped registry}
	 * 
	 * @see Registry#getKeys
	 */
	Stream<RegistryEntry.Reference<T>> streamEntries();

	default Stream<RegistryKey<T>> streamKeys() {
		return this.streamEntries().map(RegistryEntry.Reference::registryKey);
	}

	Stream<RegistryEntryList.Named<T>> getTags();

	default Stream<TagKey<T>> streamTagKeys() {
		return this.getTags().map(RegistryEntryList.Named::getTag);
	}

	public interface Impl<T> extends RegistryWrapper<T>, RegistryEntryOwner<T> {
		RegistryKey<? extends Registry<? extends T>> getKey();

		Lifecycle getLifecycle();

		default RegistryWrapper.Impl<T> withFeatureFilter(FeatureSet enabledFeatures) {
			return ToggleableFeature.FEATURE_ENABLED_REGISTRY_KEYS.contains(this.getKey())
				? this.withPredicateFilter(feature -> ((ToggleableFeature)feature).isEnabled(enabledFeatures))
				: this;
		}

		default RegistryWrapper.Impl<T> withPredicateFilter(Predicate<T> predicate) {
			return new RegistryWrapper.Impl.Delegating<T>() {
				@Override
				public RegistryWrapper.Impl<T> getBase() {
					return Impl.this;
				}

				@Override
				public Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> key) {
					return this.getBase().getOptional(key).filter(entry -> predicate.test(entry.value()));
				}

				@Override
				public Stream<RegistryEntry.Reference<T>> streamEntries() {
					return this.getBase().streamEntries().filter(entry -> predicate.test(entry.value()));
				}
			};
		}

		public interface Delegating<T> extends RegistryWrapper.Impl<T> {
			RegistryWrapper.Impl<T> getBase();

			@Override
			default RegistryKey<? extends Registry<? extends T>> getKey() {
				return this.getBase().getKey();
			}

			@Override
			default Lifecycle getLifecycle() {
				return this.getBase().getLifecycle();
			}

			@Override
			default Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> key) {
				return this.getBase().getOptional(key);
			}

			@Override
			default Stream<RegistryEntry.Reference<T>> streamEntries() {
				return this.getBase().streamEntries();
			}

			@Override
			default Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> tag) {
				return this.getBase().getOptional(tag);
			}

			@Override
			default Stream<RegistryEntryList.Named<T>> getTags() {
				return this.getBase().getTags();
			}
		}
	}

	public interface WrapperLookup extends RegistryEntryLookup.RegistryLookup {
		Stream<RegistryKey<? extends Registry<?>>> streamAllRegistryKeys();

		default Stream<RegistryWrapper.Impl<?>> stream() {
			return this.streamAllRegistryKeys().map(this::getOrThrow);
		}

		@Override
		<T> Optional<? extends RegistryWrapper.Impl<T>> getOptional(RegistryKey<? extends Registry<? extends T>> registryRef);

		default <T> RegistryWrapper.Impl<T> getOrThrow(RegistryKey<? extends Registry<? extends T>> registryRef) {
			return (RegistryWrapper.Impl<T>)this.getOptional(registryRef)
				.orElseThrow(() -> new IllegalStateException("Registry " + registryRef.getValue() + " not found"));
		}

		default <V> RegistryOps<V> getOps(DynamicOps<V> delegate) {
			return RegistryOps.of(delegate, this);
		}

		static RegistryWrapper.WrapperLookup of(Stream<RegistryWrapper.Impl<?>> wrappers) {
			final Map<RegistryKey<? extends Registry<?>>, RegistryWrapper.Impl<?>> map = (Map<RegistryKey<? extends Registry<?>>, RegistryWrapper.Impl<?>>)wrappers.collect(
				Collectors.toUnmodifiableMap(RegistryWrapper.Impl::getKey, wrapper -> wrapper)
			);
			return new RegistryWrapper.WrapperLookup() {
				@Override
				public Stream<RegistryKey<? extends Registry<?>>> streamAllRegistryKeys() {
					return map.keySet().stream();
				}

				@Override
				public <T> Optional<RegistryWrapper.Impl<T>> getOptional(RegistryKey<? extends Registry<? extends T>> registryRef) {
					return Optional.ofNullable((RegistryWrapper.Impl)map.get(registryRef));
				}
			};
		}

		default Lifecycle getLifecycle() {
			return (Lifecycle)this.stream().map(RegistryWrapper.Impl::getLifecycle).reduce(Lifecycle.stable(), Lifecycle::add);
		}
	}
}
