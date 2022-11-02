/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.registry;

import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.featuretoggle.ToggleableFeature;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryEntryLookup;
import net.minecraft.util.registry.RegistryEntryOwner;
import net.minecraft.util.registry.RegistryKey;

/**
 * A read-only wrapper of a registry.
 */
public interface RegistryWrapper<T>
extends RegistryEntryLookup<T> {
    /**
     * {@return a stream of registry keys defined in the wrapped registry}
     * 
     * @see Registry#getKeys
     */
    public Stream<RegistryEntry.Reference<T>> streamEntries();

    default public Stream<RegistryKey<T>> streamKeys() {
        return this.streamEntries().map(RegistryEntry.Reference::registryKey);
    }

    /**
     * @see Registry#streamTags
     */
    public Stream<RegistryEntryList.Named<T>> streamTags();

    default public Stream<TagKey<T>> streamTagKeys() {
        return this.streamTags().map(RegistryEntryList.Named::getTag);
    }

    default public RegistryWrapper<T> filter(final Predicate<T> filter) {
        return new Delegating<T>(this){

            @Override
            public Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> key) {
                return this.baseWrapper.getOptional(key).filter((? super T entry) -> filter.test(entry.value()));
            }

            @Override
            public Stream<RegistryEntry.Reference<T>> streamEntries() {
                return this.baseWrapper.streamEntries().filter((? super T entry) -> filter.test(entry.value()));
            }
        };
    }

    public static interface WrapperLookup {
        public <T> Optional<Impl<T>> getOptionalWrapper(RegistryKey<? extends Registry<? extends T>> var1);

        default public <T> Impl<T> getWrapperOrThrow(RegistryKey<? extends Registry<? extends T>> registryRef) {
            return this.getOptionalWrapper(registryRef).orElseThrow(() -> new IllegalStateException("Registry " + registryRef.getValue() + " not found"));
        }

        default public RegistryEntryLookup.RegistryLookup createRegistryLookup() {
            return new RegistryEntryLookup.RegistryLookup(){

                @Override
                public <T> Optional<RegistryEntryLookup<T>> getOptional(RegistryKey<? extends Registry<? extends T>> registryRef) {
                    return this.getOptionalWrapper(registryRef).map(lookup -> lookup);
                }
            };
        }

        public static WrapperLookup of(Stream<Impl<?>> wrappers) {
            final Map<RegistryKey, Impl> map = wrappers.collect(Collectors.toUnmodifiableMap(Impl::getRegistryKey, wrapper -> wrapper));
            return new WrapperLookup(){

                @Override
                public <T> Optional<Impl<T>> getOptionalWrapper(RegistryKey<? extends Registry<? extends T>> registryRef) {
                    return Optional.ofNullable((Impl)map.get(registryRef));
                }
            };
        }
    }

    public static class Delegating<T>
    implements RegistryWrapper<T> {
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

    public static interface Impl<T>
    extends RegistryWrapper<T>,
    RegistryEntryOwner<T> {
        public RegistryKey<? extends Registry<? extends T>> getRegistryKey();

        public Lifecycle getLifecycle();

        default public RegistryWrapper<T> withFeatureFilter(FeatureSet enabledFeatures) {
            if (ToggleableFeature.FEATURE_ENABLED_REGISTRY_KEYS.contains(this.getRegistryKey())) {
                return this.filter(feature -> ((ToggleableFeature)feature).isEnabled(enabledFeatures));
            }
            return this;
        }

        public static abstract class Delegating<T>
        implements Impl<T> {
            protected abstract Impl<T> getBase();

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
}

