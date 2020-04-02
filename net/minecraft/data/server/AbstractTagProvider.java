/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server;

import com.google.common.collect.ImmutableSet;
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
import java.util.function.Function;
import java.util.stream.Collectors;
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
    protected final Map<Identifier, Tag.ObjectBuilder<T>> tagBuilders = Maps.newLinkedHashMap();

    protected AbstractTagProvider(DataGenerator root, Registry<T> registry) {
        this.root = root;
        this.registry = registry;
    }

    protected abstract void configure();

    @Override
    public void run(DataCache cache) {
        this.tagBuilders.clear();
        this.configure();
        Tag tag = Tag.of(ImmutableSet.of());
        Function<Identifier, Tag> function = identifier -> this.tagBuilders.containsKey(identifier) ? tag : null;
        Function<Identifier, Object> function2 = identifier -> this.registry.getOrEmpty((Identifier)identifier).orElse(null);
        this.tagBuilders.forEach((identifier, objectBuilder) -> {
            List list = objectBuilder.streamUnresolvedEntries(function, function2).collect(Collectors.toList());
            if (!list.isEmpty()) {
                throw new IllegalArgumentException(String.format("Couldn't define tag %s as it is missing following references: %s", identifier, list.stream().map(Objects::toString).collect(Collectors.joining(","))));
            }
            JsonObject jsonObject = objectBuilder.toJson();
            Path path = this.getOutput((Identifier)identifier);
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

    protected Tag.ObjectBuilder<T> getOrCreateTagBuilder(Tag.Identified<T> identified) {
        return this.method_27047(identified.getId());
    }

    protected Tag.ObjectBuilder<T> method_27047(Identifier identifier2) {
        return this.tagBuilders.computeIfAbsent(identifier2, identifier -> new Tag.ObjectBuilder<Object>(this.registry::getId));
    }
}

