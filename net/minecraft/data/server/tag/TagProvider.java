/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.server.tag;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.registry.tag.TagEntry;
import net.minecraft.registry.tag.TagFile;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.registry.tag.TagManagerLoader;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public abstract class TagProvider<T>
implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final DataOutput.PathResolver pathResolver;
    private final CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture;
    private final CompletableFuture<TagLookup<T>> parentTagLookupFuture;
    protected final RegistryKey<? extends Registry<T>> registryRef;
    private final Map<Identifier, TagBuilder> tagBuilders = Maps.newLinkedHashMap();

    protected TagProvider(DataOutput output, RegistryKey<? extends Registry<T>> registryRef, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture) {
        this(output, registryRef, registryLookupFuture, CompletableFuture.completedFuture(TagLookup.empty()));
    }

    protected TagProvider(DataOutput output, RegistryKey<? extends Registry<T>> registryRef, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookupFuture, CompletableFuture<TagLookup<T>> parentTagLookupFuture) {
        this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, TagManagerLoader.getPath(registryRef));
        this.registryRef = registryRef;
        this.parentTagLookupFuture = parentTagLookupFuture;
        this.registryLookupFuture = registryLookupFuture.thenApply(lookup -> {
            this.tagBuilders.clear();
            this.configure((RegistryWrapper.WrapperLookup)lookup);
            return lookup;
        });
    }

    @Override
    public final String getName() {
        return "Tags for " + this.registryRef.getValue();
    }

    protected abstract void configure(RegistryWrapper.WrapperLookup var1);

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        record RegistryInfo<T>(RegistryWrapper.WrapperLookup contents, TagLookup<T> parent) {
        }
        return ((CompletableFuture)this.getRegistryLookupFuture().thenCombineAsync(this.parentTagLookupFuture, (lookup, parent) -> new RegistryInfo((RegistryWrapper.WrapperLookup)lookup, parent))).thenCompose(info -> {
            RegistryWrapper.Impl impl = info.contents.getWrapperOrThrow(this.registryRef);
            Predicate<Identifier> predicate = id -> impl.getOptional(RegistryKey.of(this.registryRef, id)).isPresent();
            Predicate<Identifier> predicate2 = id -> this.tagBuilders.containsKey(id) || registryInfo.parent.contains(TagKey.of(this.registryRef, id));
            return CompletableFuture.allOf((CompletableFuture[])this.tagBuilders.entrySet().stream().map(entry -> {
                Identifier identifier = (Identifier)entry.getKey();
                TagBuilder tagBuilder = (TagBuilder)entry.getValue();
                List<TagEntry> list = tagBuilder.build();
                List<TagEntry> list2 = list.stream().filter(tagEntry -> !tagEntry.canAdd(predicate, predicate2)).toList();
                if (!list2.isEmpty()) {
                    throw new IllegalArgumentException(String.format(Locale.ROOT, "Couldn't define tag %s as it is missing following references: %s", identifier, list2.stream().map(Objects::toString).collect(Collectors.joining(","))));
                }
                JsonElement jsonElement = TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(list, false)).getOrThrow(false, LOGGER::error);
                Path path = this.pathResolver.resolveJson(identifier);
                return DataProvider.writeToPath(writer, jsonElement, path);
            }).toArray(CompletableFuture[]::new));
        });
    }

    protected ProvidedTagBuilder<T> getOrCreateTagBuilder(TagKey<T> tag) {
        TagBuilder tagBuilder = this.getTagBuilder(tag);
        return new ProvidedTagBuilder(tagBuilder);
    }

    protected TagBuilder getTagBuilder(TagKey<T> tag) {
        return this.tagBuilders.computeIfAbsent(tag.id(), id -> TagBuilder.create());
    }

    public CompletableFuture<TagLookup<T>> getTagLookupFuture() {
        return this.getRegistryLookupFuture().thenApply(registryLookup -> tag -> Optional.ofNullable(this.tagBuilders.get(tag.id())));
    }

    protected CompletableFuture<RegistryWrapper.WrapperLookup> getRegistryLookupFuture() {
        return this.registryLookupFuture;
    }

    @FunctionalInterface
    public static interface TagLookup<T>
    extends Function<TagKey<T>, Optional<TagBuilder>> {
        public static <T> TagLookup<T> empty() {
            return tag -> Optional.empty();
        }

        default public boolean contains(TagKey<T> tag) {
            return ((Optional)this.apply(tag)).isPresent();
        }
    }

    protected static class ProvidedTagBuilder<T> {
        private final TagBuilder builder;

        protected ProvidedTagBuilder(TagBuilder builder) {
            this.builder = builder;
        }

        public final ProvidedTagBuilder<T> add(RegistryKey<T> key) {
            this.builder.add(key.getValue());
            return this;
        }

        @SafeVarargs
        public final ProvidedTagBuilder<T> add(RegistryKey<T> ... keys) {
            for (RegistryKey<T> registryKey : keys) {
                this.builder.add(registryKey.getValue());
            }
            return this;
        }

        public ProvidedTagBuilder<T> addOptional(Identifier id) {
            this.builder.addOptional(id);
            return this;
        }

        public ProvidedTagBuilder<T> addTag(TagKey<T> identifiedTag) {
            this.builder.addTag(identifiedTag.id());
            return this;
        }

        public ProvidedTagBuilder<T> addOptionalTag(Identifier id) {
            this.builder.addOptionalTag(id);
            return this;
        }
    }
}

