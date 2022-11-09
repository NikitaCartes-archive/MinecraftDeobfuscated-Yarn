package net.minecraft.registry;

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

	default RegistryWrapper<T> filter(Predicate<T> filter) {
		return new RegistryWrapper.Delegating<T>(this) {
			@Override
			public Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> key) {
				return this.baseWrapper.getOptional(key).filter(entry -> filter.test(entry.value()));
			}

			@Override
			public Stream<RegistryEntry.Reference<T>> streamEntries() {
				return this.baseWrapper.streamEntries().filter(entry -> filter.test(entry.value()));
			}
		};
	}

	public static class Delegating<T> implements RegistryWrapper<T> {
		protected final RegistryWrapper<T> baseWrapper;

		public Delegating(RegistryWrapper<T> baseWrapper) {
			this.baseWrapper = baseWrapper;
		}

		@Override
		public Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> key) {
			return this.baseWrapper.getOptional(key);
		}

		@Override
		public Stream<RegistryEntry.Reference<T>> streamEntries() {
			return this.baseWrapper.streamEntries();
		}

		@Override
		public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> tag) {
			return this.baseWrapper.getOptional(tag);
		}

		@Override
		public Stream<RegistryEntryList.Named<T>> streamTags() {
			return this.baseWrapper.streamTags();
		}
	}

	public interface Impl<T> extends RegistryWrapper<T>, RegistryEntryOwner<T> {
		RegistryKey<? extends Registry<? extends T>> getRegistryKey();

		Lifecycle getLifecycle();

		default RegistryWrapper<T> withFeatureFilter(FeatureSet enabledFeatures) {
			return (RegistryWrapper<T>)(ToggleableFeature.FEATURE_ENABLED_REGISTRY_KEYS.contains(this.getRegistryKey())
				? this.filter(feature -> ((ToggleableFeature)feature).isEnabled(enabledFeatures))
				: this);
		}

		public abstract static class Delegating<T> implements RegistryWrapper.Impl<T> {
			protected abstract RegistryWrapper.Impl<T> getBase();

			@Override
			public RegistryKey<? extends Registry<? extends T>> getRegistryKey() {
				return this.getBase().getRegistryKey();
			}

			@Override
			public Lifecycle getLifecycle() {
				return this.getBase().getLifecycle();
			}

			@Override
			public Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> key) {
				return this.getBase().getOptional(key);
			}

			@Override
			public Stream<RegistryEntry.Reference<T>> streamEntries() {
				return this.getBase().streamEntries();
			}

			@Override
			public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> tag) {
				return this.getBase().getOptional(tag);
			}

			@Override
			public Stream<RegistryEntryList.Named<T>> streamTags() {
				return this.getBase().streamTags();
			}
		}
	}

	public interface WrapperLookup {
		<T> Optional<RegistryWrapper.Impl<T>> getOptionalWrapper(RegistryKey<? extends Registry<? extends T>> registryRef);

		default <T> RegistryWrapper.Impl<T> getWrapperOrThrow(RegistryKey<? extends Registry<? extends T>> registryRef) {
			return (RegistryWrapper.Impl<T>)this.getOptionalWrapper(registryRef)
				.orElseThrow(() -> new IllegalStateException("Registry " + registryRef.getValue() + " not found"));
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
				public <T> Optional<RegistryWrapper.Impl<T>> getOptionalWrapper(RegistryKey<? extends Registry<? extends T>> registryRef) {
					return Optional.ofNullable((RegistryWrapper.Impl)map.get(registryRef));
				}
			};
		}
	}
}
