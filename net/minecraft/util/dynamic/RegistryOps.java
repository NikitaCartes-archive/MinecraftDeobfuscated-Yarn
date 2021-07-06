/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.dynamic;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.ForwardingDynamicOps;
import net.minecraft.util.dynamic.RegistryReadingOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistryOps<T>
extends ForwardingDynamicOps<T> {
    static final Logger LOGGER = LogManager.getLogger();
    private static final String JSON_FILE_EXTENSION = ".json";
    private final EntryLoader entryLoader;
    private final DynamicRegistryManager registryManager;
    private final Map<RegistryKey<? extends Registry<?>>, ValueHolder<?>> valueHolders;
    private final RegistryOps<JsonElement> entryOps;

    public static <T> RegistryOps<T> ofLoaded(DynamicOps<T> dynamicOps, ResourceManager resourceManager, DynamicRegistryManager registryManager) {
        return RegistryOps.ofLoaded(dynamicOps, EntryLoader.resourceBacked(resourceManager), registryManager);
    }

    public static <T> RegistryOps<T> ofLoaded(DynamicOps<T> dynamicOps, EntryLoader entryLoader, DynamicRegistryManager registryManager) {
        RegistryOps<T> registryOps = new RegistryOps<T>(dynamicOps, entryLoader, registryManager, Maps.newIdentityHashMap());
        DynamicRegistryManager.load(registryManager, registryOps);
        return registryOps;
    }

    public static <T> RegistryOps<T> of(DynamicOps<T> delegate, ResourceManager resourceManager, DynamicRegistryManager registryManager) {
        return RegistryOps.of(delegate, EntryLoader.resourceBacked(resourceManager), registryManager);
    }

    public static <T> RegistryOps<T> of(DynamicOps<T> delegate, EntryLoader entryLoader, DynamicRegistryManager registryManager) {
        return new RegistryOps<T>(delegate, entryLoader, registryManager, Maps.newIdentityHashMap());
    }

    private RegistryOps(DynamicOps<T> delegate, EntryLoader entryLoader, DynamicRegistryManager registryManager, IdentityHashMap<RegistryKey<? extends Registry<?>>, ValueHolder<?>> valueHolders) {
        super(delegate);
        this.entryLoader = entryLoader;
        this.registryManager = registryManager;
        this.valueHolders = valueHolders;
        this.entryOps = delegate == JsonOps.INSTANCE ? this : new RegistryOps<JsonElement>(JsonOps.INSTANCE, entryLoader, registryManager, valueHolders);
    }

    /**
     * Encode an id for a registry element than a full object if possible.
     * 
     * <p>This method is called by casting an arbitrary dynamic ops to a registry
     * reading ops.
     * 
     * @see RegistryReadingOps#encodeOrId(Object, Object, RegistryKey, Codec)
     */
    protected <E> DataResult<Pair<Supplier<E>, T>> decodeOrId(T object, RegistryKey<? extends Registry<E>> key, Codec<E> codec, boolean allowInlineDefinitions) {
        Optional optional = this.registryManager.getOptionalMutable(key);
        if (!optional.isPresent()) {
            return DataResult.error("Unknown registry: " + key);
        }
        MutableRegistry mutableRegistry = optional.get();
        DataResult dataResult = Identifier.CODEC.decode(this.delegate, object);
        if (!dataResult.result().isPresent()) {
            if (!allowInlineDefinitions) {
                return DataResult.error("Inline definitions not allowed here");
            }
            return codec.decode(this, object).map(pair -> pair.mapFirst(object -> () -> object));
        }
        Pair pair2 = dataResult.result().get();
        Identifier identifier = (Identifier)pair2.getFirst();
        return this.readSupplier(key, mutableRegistry, codec, identifier).map(supplier -> Pair.of(supplier, pair2.getSecond()));
    }

    /**
     * Loads elements into a registry just loaded from a decoder.
     */
    public <E> DataResult<SimpleRegistry<E>> loadToRegistry(SimpleRegistry<E> registry, RegistryKey<? extends Registry<E>> key, Codec<E> codec) {
        Collection<Identifier> collection = this.entryLoader.getKnownEntryPaths(key);
        DataResult<SimpleRegistry<Object>> dataResult = DataResult.success(registry, Lifecycle.stable());
        String string = key.getValue().getPath() + "/";
        for (Identifier identifier : collection) {
            String string2 = identifier.getPath();
            if (!string2.endsWith(JSON_FILE_EXTENSION)) {
                LOGGER.warn("Skipping resource {} since it is not a json file", (Object)identifier);
                continue;
            }
            if (!string2.startsWith(string)) {
                LOGGER.warn("Skipping resource {} since it does not have a registry name prefix", (Object)identifier);
                continue;
            }
            String string3 = string2.substring(string.length(), string2.length() - JSON_FILE_EXTENSION.length());
            Identifier identifier2 = new Identifier(identifier.getNamespace(), string3);
            dataResult = dataResult.flatMap(simpleRegistry -> this.readSupplier(key, (MutableRegistry)simpleRegistry, codec, identifier2).map(supplier -> simpleRegistry));
        }
        return dataResult.setPartial(registry);
    }

    /**
     * Reads a supplier for a registry element.
     * 
     * <p>This logic is used by both {@code decodeOrId} and {@code loadToRegistry}.
     */
    private <E> DataResult<Supplier<E>> readSupplier(RegistryKey<? extends Registry<E>> key, final MutableRegistry<E> registry, Codec<E> codec, Identifier elementId) {
        DataResult<Supplier<E>> dataResult2;
        final RegistryKey registryKey = RegistryKey.of(key, elementId);
        ValueHolder<E> valueHolder = this.getValueHolder(key);
        DataResult dataResult = valueHolder.values.get(registryKey);
        if (dataResult != null) {
            return dataResult;
        }
        com.google.common.base.Supplier<Object> supplier = Suppliers.memoize(() -> {
            Object object = registry.get(registryKey);
            if (object == null) {
                throw new RuntimeException("Error during recursive registry parsing, element resolved too early: " + registryKey);
            }
            return object;
        });
        valueHolder.values.put(registryKey, DataResult.success(supplier));
        Optional optional = this.entryLoader.load(this.entryOps, key, registryKey, codec);
        if (!optional.isPresent()) {
            dataResult2 = DataResult.success(new Supplier<E>(){

                @Override
                public E get() {
                    return registry.get(registryKey);
                }

                public String toString() {
                    return registryKey.toString();
                }
            }, Lifecycle.stable());
        } else {
            DataResult<Pair<Supplier, OptionalInt>> dataResult3 = optional.get();
            Optional optional2 = dataResult3.result();
            if (optional2.isPresent()) {
                Pair pair2 = optional2.get();
                registry.replace(pair2.getSecond(), registryKey, pair2.getFirst(), dataResult3.lifecycle());
            }
            dataResult2 = dataResult3.map(pair -> () -> registry.get(registryKey));
        }
        valueHolder.values.put(registryKey, dataResult2);
        return dataResult2;
    }

    private <E> ValueHolder<E> getValueHolder(RegistryKey<? extends Registry<E>> registryRef) {
        return this.valueHolders.computeIfAbsent(registryRef, registryKey -> new ValueHolder());
    }

    protected <E> DataResult<Registry<E>> getRegistry(RegistryKey<? extends Registry<E>> key) {
        return this.registryManager.getOptionalMutable(key).map(mutableRegistry -> DataResult.success(mutableRegistry, mutableRegistry.getLifecycle())).orElseGet(() -> DataResult.error("Unknown registry: " + key));
    }

    public static interface EntryLoader {
        public Collection<Identifier> getKnownEntryPaths(RegistryKey<? extends Registry<?>> var1);

        public <E> Optional<DataResult<Pair<E, OptionalInt>>> load(DynamicOps<JsonElement> var1, RegistryKey<? extends Registry<E>> var2, RegistryKey<E> var3, Decoder<E> var4);

        public static EntryLoader resourceBacked(final ResourceManager resourceManager) {
            return new EntryLoader(){

                @Override
                public Collection<Identifier> getKnownEntryPaths(RegistryKey<? extends Registry<?>> key) {
                    return resourceManager.findResources(key.getValue().getPath(), name -> name.endsWith(RegistryOps.JSON_FILE_EXTENSION));
                }

                /*
                 * Enabled aggressive exception aggregation
                 */
                @Override
                public <E> Optional<DataResult<Pair<E, OptionalInt>>> load(DynamicOps<JsonElement> dynamicOps, RegistryKey<? extends Registry<E>> registryId, RegistryKey<E> entryId, Decoder<E> decoder) {
                    Identifier identifier = entryId.getValue();
                    Identifier identifier2 = new Identifier(identifier.getNamespace(), registryId.getValue().getPath() + "/" + identifier.getPath() + RegistryOps.JSON_FILE_EXTENSION);
                    if (!resourceManager.containsResource(identifier2)) {
                        return Optional.empty();
                    }
                    try (Resource resource = resourceManager.getResource(identifier2);){
                        Optional<DataResult<Pair<E, OptionalInt>>> optional;
                        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);){
                            JsonParser jsonParser = new JsonParser();
                            JsonElement jsonElement = jsonParser.parse(reader);
                            optional = Optional.of(decoder.parse(dynamicOps, jsonElement).map(object -> Pair.of(object, OptionalInt.empty())));
                        }
                        return optional;
                    } catch (JsonIOException | JsonSyntaxException | IOException exception) {
                        return Optional.of(DataResult.error("Failed to parse " + identifier2 + " file: " + exception.getMessage()));
                    }
                }

                public String toString() {
                    return "ResourceAccess[" + resourceManager + "]";
                }
            };
        }

        public static final class Impl
        implements EntryLoader {
            private final Map<RegistryKey<?>, JsonElement> values = Maps.newIdentityHashMap();
            private final Object2IntMap<RegistryKey<?>> entryToRawId = new Object2IntOpenCustomHashMap(Util.identityHashStrategy());
            private final Map<RegistryKey<?>, Lifecycle> entryToLifecycle = Maps.newIdentityHashMap();

            public <E> void add(DynamicRegistryManager.Impl registryManager, RegistryKey<E> key, Encoder<E> encoder, int rawId, E entry, Lifecycle lifecycle) {
                DataResult<JsonElement> dataResult = encoder.encodeStart(RegistryReadingOps.of(JsonOps.INSTANCE, registryManager), entry);
                Optional<DataResult.PartialResult<JsonElement>> optional = dataResult.error();
                if (optional.isPresent()) {
                    LOGGER.error("Error adding element: {}", (Object)optional.get().message());
                    return;
                }
                this.values.put(key, dataResult.result().get());
                this.entryToRawId.put((RegistryKey<?>)key, rawId);
                this.entryToLifecycle.put(key, lifecycle);
            }

            @Override
            public Collection<Identifier> getKnownEntryPaths(RegistryKey<? extends Registry<?>> key) {
                return this.values.keySet().stream().filter(registryKey2 -> registryKey2.isOf(key)).map(registryKey2 -> new Identifier(registryKey2.getValue().getNamespace(), key.getValue().getPath() + "/" + registryKey2.getValue().getPath() + RegistryOps.JSON_FILE_EXTENSION)).collect(Collectors.toList());
            }

            @Override
            public <E> Optional<DataResult<Pair<E, OptionalInt>>> load(DynamicOps<JsonElement> dynamicOps, RegistryKey<? extends Registry<E>> registryId, RegistryKey<E> entryId, Decoder<E> decoder) {
                JsonElement jsonElement = this.values.get(entryId);
                if (jsonElement == null) {
                    return Optional.of(DataResult.error("Unknown element: " + entryId));
                }
                return Optional.of(decoder.parse(dynamicOps, jsonElement).setLifecycle(this.entryToLifecycle.get(entryId)).map(object -> Pair.of(object, OptionalInt.of(this.entryToRawId.getInt(entryId)))));
            }
        }
    }

    static final class ValueHolder<E> {
        final Map<RegistryKey<E>, DataResult<Supplier<E>>> values = Maps.newIdentityHashMap();

        ValueHolder() {
        }
    }
}

