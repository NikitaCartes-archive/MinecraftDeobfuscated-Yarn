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

	/**
	 * @see Registry#streamTags
	 */
	Stream<RegistryEntryList.Named<T>> streamTags();

	default Stream<TagKey<T>> streamTagKeys() {
		return this.streamTags().map(RegistryEntryList.Named::getTag);
	}

	public interface Impl<T> extends RegistryWrapper<T>, RegistryEntryOwner<T> {
		RegistryKey<? extends Registry<? extends T>> getRegistryKey();

		Lifecycle getLifecycle();

		default RegistryWrapper.Impl<T> withFeatureFilter(FeatureSet enabledFeatures) {
			return ToggleableFeature.FEATURE_ENABLED_REGISTRY_KEYS.contains(this.getRegistryKey())
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
			default RegistryKey<? extends Registry<? extends T>> getRegistryKey() {
				return this.getBase().getRegistryKey();
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
			default Stream<RegistryEntryList.Named<T>> streamTags() {
				return this.getBase().streamTags();
			}
		}
	}

	public interface WrapperLookup {
		Stream<RegistryKey<? extends Registry<?>>> streamAllRegistryKeys();

		<T> Optional<RegistryWrapper.Impl<T>> getOptionalWrapper(RegistryKey<? extends Registry<? extends T>> registryRef);

		default <T> RegistryWrapper.Impl<T> getWrapperOrThrow(RegistryKey<? extends Registry<? extends T>> registryRef) {
			return (RegistryWrapper.Impl<T>)this.getOptionalWrapper(registryRef)
				.orElseThrow(() -> new IllegalStateException("Registry " + registryRef.getValue() + " not found"));
		}

		default <V> RegistryOps<V> getOps(DynamicOps<V> delegate) {
			return RegistryOps.of(delegate, this);
		}

		default RegistryEntryLookup.RegistryLookup createRegistryLookup() {
			return new RegistryEntryLookup.RegistryLookup() {
				@Override
				public <T> Optional<RegistryEntryLookup<T>> getOptional(RegistryKey<? extends Registry<? extends T>> registryRef) {
					return WrapperLookup.this.getOptionalWrapper(registryRef).map(lookup -> lookup);
				}
			};
		}

		static RegistryWrapper.WrapperLookup of(Stream<RegistryWrapper.Impl<?>> wrappers) {
			final Map<RegistryKey<? extends Registry<?>>, RegistryWrapper.Impl<?>> map = (Map<RegistryKey<? extends Registry<?>>, RegistryWrapper.Impl<?>>)wrappers.collect(
				Collectors.toUnmodifiableMap(RegistryWrapper.Impl::getRegistryKey, wrapper -> wrapper)
			);
			return new RegistryWrapper.WrapperLookup() {
				@Override
				public Stream<RegistryKey<? extends Registry<?>>> streamAllRegistryKeys() {
					return map.keySet().stream();
				}

				@Override
				public <T> Optional<RegistryWrapper.Impl<T>> getOptionalWrapper(RegistryKey<? extends Registry<? extends T>> registryRef) {
					return Optional.ofNullable((RegistryWrapper.Impl)map.get(registryRef));
				}
			};
		}
	}
}
