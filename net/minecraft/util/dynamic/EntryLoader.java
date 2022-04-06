/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.dynamic;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.RegistryOps;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.slf4j.Logger;

public interface EntryLoader {
    /**
     * @return A collection of file Identifiers of all known entries of the given registry.
     * Note that these are file Identifiers for use in a resource manager, not the logical names of the entries.
     */
    public <E> Map<RegistryKey<E>, Parseable<E>> getKnownEntryPaths(RegistryKey<? extends Registry<E>> var1);

    public <E> Optional<Parseable<E>> createParseable(RegistryKey<E> var1);

    public static EntryLoader resourceBacked(final ResourceManager resourceManager) {
        return new EntryLoader(){
            private static final String JSON = ".json";

            @Override
            public <E> Map<RegistryKey<E>, Parseable<E>> getKnownEntryPaths(RegistryKey<? extends Registry<E>> key) {
                String string = _1.getPath(key.getValue());
                HashMap map = Maps.newHashMap();
                resourceManager.findResources(string, id -> id.getPath().endsWith(JSON)).forEach((id, resourceRef) -> {
                    String string2 = id.getPath();
                    String string3 = string2.substring(string.length() + 1, string2.length() - JSON.length());
                    RegistryKey registryKey2 = RegistryKey.of(key, new Identifier(id.getNamespace(), string3));
                    map.put(registryKey2, (jsonOps, decoder) -> {
                        DataResult dataResult;
                        block8: {
                            BufferedReader reader = resourceRef.getReader();
                            try {
                                dataResult = this.parse(jsonOps, decoder, reader);
                                if (reader == null) break block8;
                            } catch (Throwable throwable) {
                                try {
                                    if (reader != null) {
                                        try {
                                            ((Reader)reader).close();
                                        } catch (Throwable throwable2) {
                                            throwable.addSuppressed(throwable2);
                                        }
                                    }
                                    throw throwable;
                                } catch (JsonIOException | JsonSyntaxException | IOException exception) {
                                    return DataResult.error("Failed to parse " + id + " file: " + exception.getMessage());
                                }
                            }
                            ((Reader)reader).close();
                        }
                        return dataResult;
                    });
                });
                return map;
            }

            @Override
            public <E> Optional<Parseable<E>> createParseable(RegistryKey<E> key) {
                Identifier identifier = _1.createId(key);
                return resourceManager.getResource(identifier).map(resource -> (jsonOps, decoder) -> {
                    DataResult dataResult;
                    block8: {
                        BufferedReader reader = resource.getReader();
                        try {
                            dataResult = this.parse(jsonOps, decoder, reader);
                            if (reader == null) break block8;
                        } catch (Throwable throwable) {
                            try {
                                if (reader != null) {
                                    try {
                                        ((Reader)reader).close();
                                    } catch (Throwable throwable2) {
                                        throwable.addSuppressed(throwable2);
                                    }
                                }
                                throw throwable;
                            } catch (JsonIOException | JsonSyntaxException | IOException exception) {
                                return DataResult.error("Failed to parse " + identifier + " file: " + exception.getMessage());
                            }
                        }
                        ((Reader)reader).close();
                    }
                    return dataResult;
                });
            }

            private <E> DataResult<Entry<E>> parse(DynamicOps<JsonElement> jsonOps, Decoder<E> decoder, Reader reader) throws IOException {
                JsonElement jsonElement = JsonParser.parseReader(reader);
                return decoder.parse(jsonOps, jsonElement).map(Entry::of);
            }

            private static String getPath(Identifier id) {
                return id.getPath();
            }

            private static <E> Identifier createId(RegistryKey<E> rootKey) {
                return new Identifier(rootKey.getValue().getNamespace(), _1.getPath(rootKey.getRegistry()) + "/" + rootKey.getValue().getPath() + JSON);
            }

            public String toString() {
                return "ResourceAccess[" + resourceManager + "]";
            }
        };
    }

    public static final class Impl
    implements EntryLoader {
        private static final Logger LOGGER = LogUtils.getLogger();
        private final Map<RegistryKey<?>, Element> values = Maps.newIdentityHashMap();

        public <E> void add(DynamicRegistryManager registryManager, RegistryKey<E> key, Encoder<E> encoder, int rawId, E entry, Lifecycle lifecycle) {
            DataResult<JsonElement> dataResult = encoder.encodeStart(RegistryOps.of(JsonOps.INSTANCE, registryManager), entry);
            Optional<DataResult.PartialResult<JsonElement>> optional = dataResult.error();
            if (optional.isPresent()) {
                LOGGER.error("Error adding element: {}", (Object)optional.get().message());
            } else {
                this.values.put(key, new Element(dataResult.result().get(), rawId, lifecycle));
            }
        }

        @Override
        public <E> Map<RegistryKey<E>, Parseable<E>> getKnownEntryPaths(RegistryKey<? extends Registry<E>> key) {
            return this.values.entrySet().stream().filter(entry -> ((RegistryKey)entry.getKey()).isOf(key)).collect(Collectors.toMap(entry -> (RegistryKey)entry.getKey(), entry -> ((Element)entry.getValue())::parse));
        }

        @Override
        public <E> Optional<Parseable<E>> createParseable(RegistryKey<E> key) {
            Element element = this.values.get(key);
            if (element == null) {
                DataResult dataResult = DataResult.error("Unknown element: " + key);
                return Optional.of((jsonOps, decoder) -> dataResult);
            }
            return Optional.of(element::parse);
        }

        record Element(JsonElement data, int id, Lifecycle lifecycle) {
            public <E> DataResult<Entry<E>> parse(DynamicOps<JsonElement> jsonOps, Decoder<E> decoder) {
                return decoder.parse(jsonOps, this.data).setLifecycle(this.lifecycle).map(value -> Entry.of(value, this.id));
            }
        }
    }

    @FunctionalInterface
    public static interface Parseable<E> {
        public DataResult<Entry<E>> parseElement(DynamicOps<JsonElement> var1, Decoder<E> var2);
    }

    public record Entry<E>(E value, OptionalInt fixedId) {
        public static <E> Entry<E> of(E value) {
            return new Entry<E>(value, OptionalInt.empty());
        }

        public static <E> Entry<E> of(E value, int id) {
            return new Entry<E>(value, OptionalInt.of(id));
        }
    }
}

