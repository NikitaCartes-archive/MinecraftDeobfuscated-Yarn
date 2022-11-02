/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.registry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.serialization.Lifecycle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registerable;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.util.registry.RegistryEntryLookup;
import net.minecraft.util.registry.RegistryEntryOwner;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.RegistryWrapper;

public class RegistryBuilder {
    private final List<RegistryInfo<?>> registries = new ArrayList();

    static <T> RegistryEntryLookup<T> toLookup(final RegistryWrapper.Impl<T> wrapper) {
        return new EntryListCreatingLookup<T>(wrapper){

            @Override
            public Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> key) {
                return wrapper.getOptional(key);
            }
        };
    }

    public <T> RegistryBuilder addRegistry(RegistryKey<? extends Registry<T>> registryRef, Lifecycle lifecycle, BootstrapFunction<T> bootstrapFunction) {
        this.registries.add(new RegistryInfo<T>(registryRef, lifecycle, bootstrapFunction));
        return this;
    }

    public <T> RegistryBuilder addRegistry(RegistryKey<? extends Registry<T>> registryRef, BootstrapFunction<T> bootstrapFunction) {
        return this.addRegistry(registryRef, Lifecycle.stable(), bootstrapFunction);
    }

    private Registries createBootstrappedRegistries(DynamicRegistryManager registryManager) {
        Registries registries = Registries.of(registryManager, this.registries.stream().map(RegistryInfo::key));
        this.registries.forEach(registry -> registry.runBootstrap(registries));
        return registries;
    }

    public RegistryWrapper.WrapperLookup createWrapperLookup(DynamicRegistryManager baseRegistryManager) {
        Registries registries = this.createBootstrappedRegistries(baseRegistryManager);
        Stream<RegistryWrapper.Impl> stream = baseRegistryManager.streamAllRegistries().map(entry -> entry.value().getReadOnlyWrapper());
        Stream<RegistryWrapper.Impl> stream2 = this.registries.stream().map(info -> info.init(registries).toWrapper());
        RegistryWrapper.WrapperLookup wrapperLookup = RegistryWrapper.WrapperLookup.of(Stream.concat(stream, stream2.peek(registries::addOwner)));
        registries.validateReferences();
        registries.throwErrors();
        return wrapperLookup;
    }

    public RegistryWrapper.WrapperLookup createWrapperLookup(DynamicRegistryManager baseRegistryManager, RegistryWrapper.WrapperLookup wrapperLookup) {
        Registries registries = this.createBootstrappedRegistries(baseRegistryManager);
        Stream<RegistryWrapper.Impl> stream = baseRegistryManager.streamAllRegistries().map(entry -> entry.value().getReadOnlyWrapper());
        Stream<RegistryWrapper.Impl> stream2 = this.registries.stream().map(info -> info.init(registries).toWrapper());
        RegistryWrapper.WrapperLookup wrapperLookup2 = RegistryWrapper.WrapperLookup.of(Stream.concat(stream, stream2.peek(registries::addOwner)));
        registries.setReferenceEntryValues(wrapperLookup);
        registries.validateReferences();
        registries.throwErrors();
        return wrapperLookup2;
    }

    record RegistryInfo<T>(RegistryKey<? extends Registry<T>> key, Lifecycle lifecycle, BootstrapFunction<T> bootstrap) {
        void runBootstrap(Registries registries) {
            this.bootstrap.run(registries.createRegisterable());
        }

        public InitializedRegistry<T> init(Registries registries) {
            HashMap map = new HashMap();
            Iterator<Map.Entry<RegistryKey<?>, RegisteredValue<?>>> iterator = registries.registeredValues.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<RegistryKey<?>, RegisteredValue<?>> entry = iterator.next();
                RegistryKey<?> registryKey = entry.getKey();
                if (!registryKey.isOf(this.key)) continue;
                RegistryKey<?> registryKey2 = registryKey;
                RegisteredValue<?> registeredValue = entry.getValue();
                RegistryEntry.Reference<Object> reference = registries.lookup.keysToEntries.remove(registryKey);
                map.put(registryKey2, new EntryAssociatedValue(registeredValue, Optional.ofNullable(reference)));
                iterator.remove();
            }
            return new InitializedRegistry(this, map);
        }
    }

    @FunctionalInterface
    public static interface BootstrapFunction<T> {
        public void run(Registerable<T> var1);
    }

    record Registries(AnyOwner owner, StandAloneEntryCreatingLookup lookup, Map<Identifier, RegistryEntryLookup<?>> registries, Map<RegistryKey<?>, RegisteredValue<?>> registeredValues, List<RuntimeException> errors) {
        public static Registries of(DynamicRegistryManager dynamicRegistryManager, Stream<RegistryKey<? extends Registry<?>>> registryRefs) {
            AnyOwner anyOwner = new AnyOwner();
            ArrayList<RuntimeException> list = new ArrayList<RuntimeException>();
            StandAloneEntryCreatingLookup standAloneEntryCreatingLookup = new StandAloneEntryCreatingLookup(anyOwner);
            ImmutableMap.Builder builder = ImmutableMap.builder();
            dynamicRegistryManager.streamAllRegistries().forEach(entry -> builder.put(entry.key().getValue(), RegistryBuilder.toLookup(entry.value().getReadOnlyWrapper())));
            registryRefs.forEach(registryRef -> builder.put(registryRef.getValue(), standAloneEntryCreatingLookup));
            return new Registries(anyOwner, standAloneEntryCreatingLookup, builder.build(), new HashMap(), list);
        }

        public <T> Registerable<T> createRegisterable() {
            return new Registerable<T>(){

                @Override
                public RegistryEntry.Reference<T> register(RegistryKey<T> key, T value, Lifecycle lifecycle) {
                    RegisteredValue registeredValue = registeredValues.put(key, new RegisteredValue(value, lifecycle));
                    if (registeredValue != null) {
                        errors.add(new IllegalStateException("Duplicate registration for " + key + ", new=" + value + ", old=" + registeredValue.value));
                    }
                    return lookup.getOrCreate(key);
                }

                @Override
                public <S> RegistryEntryLookup<S> getRegistryLookup(RegistryKey<? extends Registry<? extends S>> registryRef) {
                    return registries.getOrDefault(registryRef.getValue(), lookup);
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
            HashMap<Identifier, Optional> map = new HashMap<Identifier, Optional>();
            Iterator<Map.Entry<RegistryKey<Object>, RegistryEntry.Reference<Object>>> iterator = this.lookup.keysToEntries.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<RegistryKey<Object>, RegistryEntry.Reference<Object>> entry2 = iterator.next();
                RegistryKey<Object> registryKey = entry2.getKey();
                RegistryEntry.Reference<Object> reference = entry2.getValue();
                map.computeIfAbsent(registryKey.getRegistry(), registryId -> lookup.getOptionalWrapper(RegistryKey.ofRegistry(registryId))).flatMap(entryLookup -> entryLookup.getOptional(registryKey)).ifPresent(entry -> {
                    reference.setValue(entry.value());
                    iterator.remove();
                });
            }
        }
    }

    record InitializedRegistry<T>(RegistryInfo<T> stub, Map<RegistryKey<T>, EntryAssociatedValue<T>> values) {
        public RegistryWrapper.Impl<T> toWrapper() {
            return new RegistryWrapper.Impl<T>(){
                private final Map<RegistryKey<T>, RegistryEntry.Reference<T>> keysToEntries;
                {
                    this.keysToEntries = values.entrySet().stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, entry -> {
                        EntryAssociatedValue entryAssociatedValue = (EntryAssociatedValue)entry.getValue();
                        RegistryEntry.Reference reference = entryAssociatedValue.entry().orElseGet(() -> RegistryEntry.Reference.standAlone(this, (RegistryKey)entry.getKey()));
                        reference.setValue(entryAssociatedValue.value().value());
                        return reference;
                    }));
                }

                @Override
                public RegistryKey<? extends Registry<? extends T>> getRegistryKey() {
                    return stub.key();
                }

                @Override
                public Lifecycle getLifecycle() {
                    return stub.lifecycle();
                }

                @Override
                public Optional<RegistryEntry.Reference<T>> getOptional(RegistryKey<T> key) {
                    return Optional.ofNullable(this.keysToEntries.get(key));
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

    record EntryAssociatedValue<T>(RegisteredValue<T> value, Optional<RegistryEntry.Reference<T>> entry) {
    }

    record RegisteredValue<T>(T value, Lifecycle lifecycle) {
    }

    static class StandAloneEntryCreatingLookup
    extends EntryListCreatingLookup<Object> {
        final Map<RegistryKey<Object>, RegistryEntry.Reference<Object>> keysToEntries = new HashMap<RegistryKey<Object>, RegistryEntry.Reference<Object>>();

        public StandAloneEntryCreatingLookup(RegistryEntryOwner<Object> registryEntryOwner) {
            super(registryEntryOwner);
        }

        @Override
        public Optional<RegistryEntry.Reference<Object>> getOptional(RegistryKey<Object> key) {
            return Optional.of(this.getOrCreate(key));
        }

        <T> RegistryEntry.Reference<T> getOrCreate(RegistryKey<T> key) {
            return this.keysToEntries.computeIfAbsent(key, key2 -> RegistryEntry.Reference.standAlone(this.entryOwner, key2));
        }
    }

    static class AnyOwner
    implements RegistryEntryOwner<Object> {
        private final Set<RegistryEntryOwner<?>> owners = Sets.newIdentityHashSet();

        AnyOwner() {
        }

        @Override
        public boolean ownerEquals(RegistryEntryOwner<Object> other) {
            return this.owners.contains(other);
        }

        public void addOwner(RegistryEntryOwner<?> owner) {
            this.owners.add(owner);
        }
    }

    static abstract class EntryListCreatingLookup<T>
    implements RegistryEntryLookup<T> {
        protected final RegistryEntryOwner<T> entryOwner;

        protected EntryListCreatingLookup(RegistryEntryOwner<T> entryOwner) {
            this.entryOwner = entryOwner;
        }

        @Override
        public Optional<RegistryEntryList.Named<T>> getOptional(TagKey<T> tag) {
            return Optional.of(RegistryEntryList.of(this.entryOwner, tag));
        }
    }
}

