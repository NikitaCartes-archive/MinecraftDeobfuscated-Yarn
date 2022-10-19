/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.resource.featuretoggle.ToggleableFeature;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryKey;

/**
 * A wrapper of a registry, used in command arguments.
 * 
 * <p>The methods in this class in most cases perform the same as ones defined
 * in {@link Registry}. However, subclasses may have slightly different behaviors
 * to make it easier to handle user-passed arguments. For example, {@link #getEntryList(TagKey)}
 * may return an empty {@link RegistryEntryList} instead of {@link Optional#empty()}.
 * 
 * <p>A wrapper without any special behaviors can be created by calling {@link of(Registry)}.
 */
public interface CommandRegistryWrapper<T> {
    /**
     * @see Registry#getEntry
     */
    public Optional<RegistryEntry.Reference<T>> getEntry(RegistryKey<T> var1);

    /**
     * {@return a stream of registry keys defined in the wrapped registry}
     * 
     * @see Registry#getKeys
     */
    public Stream<RegistryKey<T>> streamKeys();

    /**
     * {@return the entry list for the provided tag}
     * 
     * <p>Implementations have different behaviors for unrecognized tags. The
     * {@linkplain CommandRegistryWrapper.Impl#getEntryList default implementation}
     * returns {@link Optional#empty()}; however, the ones created by
     * {@link CommandRegistryAccess#createWrapper} can create and return a new entry list, or
     * return an empty entry list.
     * 
     * @see Registry#getEntryList
     * @see CommandRegistryAccess.EntryListCreationPolicy
     */
    public Optional<RegistryEntryList.Named<T>> getEntryList(TagKey<T> var1);

    /**
     * @see Registry#streamTags
     */
    public Stream<TagKey<T>> streamTags();

    /**
     * {@return a new wrapper for the {@code registry} without any special behaviors}
     */
    public static <T> Impl<T> of(Registry<T> registry) {
        return new Impl<T>(registry);
    }

    public static class Impl<T>
    implements CommandRegistryWrapper<T> {
        protected final Registry<T> registry;

        public Impl(Registry<T> registry) {
            this.registry = registry;
        }

        @Override
        public Optional<RegistryEntry.Reference<T>> getEntry(RegistryKey<T> key) {
            return this.registry.getEntry(key);
        }

        @Override
        public Stream<RegistryKey<T>> streamKeys() {
            return this.registry.getEntrySet().stream().map(Map.Entry::getKey);
        }

        @Override
        public Optional<RegistryEntryList.Named<T>> getEntryList(TagKey<T> tag) {
            return this.registry.getEntryList(tag);
        }

        @Override
        public Stream<TagKey<T>> streamTags() {
            return this.registry.streamTags();
        }

        public CommandRegistryWrapper<T> withFilter(final Predicate<T> filter) {
            return new CommandRegistryWrapper<T>(){

                @Override
                public Optional<RegistryEntry.Reference<T>> getEntry(RegistryKey<T> key) {
                    return registry.getEntry(key).filter(entry -> filter.test(entry.value()));
                }

                @Override
                public Stream<RegistryKey<T>> streamKeys() {
                    return registry.getEntrySet().stream().filter(entry -> filter.test(entry.getValue())).map(Map.Entry::getKey);
                }

                @Override
                public Optional<RegistryEntryList.Named<T>> getEntryList(TagKey<T> tag) {
                    return this.getEntryList(tag);
                }

                @Override
                public Stream<TagKey<T>> streamTags() {
                    return this.streamTags();
                }
            };
        }

        public CommandRegistryWrapper<T> withFeatureFilter(FeatureSet enabledFeatures) {
            if (ToggleableFeature.FEATURE_ENABLED_REGISTRY_KEYS.contains(this.registry.getKey())) {
                return this.withFilter(object -> ((ToggleableFeature)object).isEnabled(enabledFeatures));
            }
            return this;
        }
    }
}

