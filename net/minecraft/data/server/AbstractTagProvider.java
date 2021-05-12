/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractTagProvider<T>
implements DataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    protected final DataGenerator root;
    protected final Registry<T> registry;
    private final Map<Identifier, Tag.Builder> tagBuilders = Maps.newLinkedHashMap();

    protected AbstractTagProvider(DataGenerator root, Registry<T> registry) {
        this.root = root;
        this.registry = registry;
    }

    protected abstract void configure();

    @Override
    public void run(DataCache cache) {
        this.tagBuilders.clear();
        this.configure();
        this.tagBuilders.forEach((id, builder) -> {
            List list = builder.streamEntries().filter(trackedEntry -> !trackedEntry.getEntry().canAdd(this.registry::containsId, this.tagBuilders::containsKey)).collect(Collectors.toList());
            if (!list.isEmpty()) {
                throw new IllegalArgumentException(String.format("Couldn't define tag %s as it is missing following references: %s", id, list.stream().map(Objects::toString).collect(Collectors.joining(","))));
            }
            JsonObject jsonObject = builder.toJson();
            Path path = this.getOutput((Identifier)id);
            try {
                String string = GSON.toJson(jsonObject);
                String string2 = SHA1.hashUnencodedChars(string).toString();
                if (!Objects.equals(cache.getOldSha1(path), string2) || !Files.exists(path, new LinkOption[0])) {
                    Files.createDirectories(path.getParent(), new FileAttribute[0]);
                    try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, new OpenOption[0]);){
                        bufferedWriter.write(string);
                    }
                }
                cache.updateSha1(path, string2);
            } catch (IOException iOException) {
                LOGGER.error("Couldn't save tags to {}", (Object)path, (Object)iOException);
            }
        });
    }

    protected abstract Path getOutput(Identifier var1);

    protected ObjectBuilder<T> getOrCreateTagBuilder(Tag.Identified<T> tag) {
        Tag.Builder builder = this.getTagBuilder(tag);
        return new ObjectBuilder<T>(builder, this.registry, "vanilla");
    }

    protected Tag.Builder getTagBuilder(Tag.Identified<T> tag) {
        return this.tagBuilders.computeIfAbsent(tag.getId(), id -> new Tag.Builder());
    }

    protected static class ObjectBuilder<T> {
        private final Tag.Builder builder;
        private final Registry<T> registry;
        private final String source;

        ObjectBuilder(Tag.Builder builder, Registry<T> registry, String source) {
            this.builder = builder;
            this.registry = registry;
            this.source = source;
        }

        public ObjectBuilder<T> add(T element) {
            this.builder.add(this.registry.getId(element), this.source);
            return this;
        }

        public ObjectBuilder<T> add(Identifier id) {
            this.builder.addOptional(id, this.source);
            return this;
        }

        public ObjectBuilder<T> addTag(Tag.Identified<T> identifiedTag) {
            this.builder.addTag(identifiedTag.getId(), this.source);
            return this;
        }

        public ObjectBuilder<T> addTag(Identifier id) {
            this.builder.addOptionalTag(id, this.source);
            return this;
        }

        @SafeVarargs
        public final ObjectBuilder<T> add(T ... elements) {
            Stream.of(elements).map(this.registry::getId).forEach(id -> this.builder.add((Identifier)id, this.source));
            return this;
        }
    }
}

