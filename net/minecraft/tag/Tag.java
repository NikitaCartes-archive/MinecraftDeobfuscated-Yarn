/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.tag.SetTag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

/**
 * A tag is a set of objects.
 * 
 * <p>Tags simplifies reference to multiple objects, especially for
 * predicate (testing against) purposes.
 * 
 * <p>A tag is immutable by design. It has a builder, which is a mutable
 * equivalent.
 * 
 * <p>Its entries' iteration may be ordered
 * or unordered, depending on the configuration from the tag builder.
 */
public interface Tag<T> {
    public static <T> Codec<Tag<T>> method_28134(Supplier<TagContainer<T>> supplier) {
        return Identifier.CODEC.flatXmap(identifier -> Optional.ofNullable(((TagContainer)supplier.get()).get((Identifier)identifier)).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown tag: " + identifier)), tag -> Optional.ofNullable(((TagContainer)supplier.get()).getId(tag)).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown tag: " + tag)));
    }

    public boolean contains(T var1);

    public List<T> values();

    default public T getRandom(Random random) {
        List<T> list = this.values();
        return list.get(random.nextInt(list.size()));
    }

    public static <T> Tag<T> of(Set<T> set) {
        return SetTag.method_29900(set);
    }

    public static interface Identified<T>
    extends Tag<T> {
        public Identifier getId();
    }

    public static class TagEntry
    implements Entry {
        private final Identifier id;

        public TagEntry(Identifier id) {
            this.id = id;
        }

        @Override
        public <T> boolean resolve(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter, Consumer<T> collector) {
            Tag<T> tag = tagGetter.apply(this.id);
            if (tag == null) {
                return false;
            }
            tag.values().forEach(collector);
            return true;
        }

        @Override
        public void addToJson(JsonArray json) {
            json.add("#" + this.id);
        }

        public String toString() {
            return "#" + this.id;
        }
    }

    public static class ObjectEntry
    implements Entry {
        private final Identifier id;

        public ObjectEntry(Identifier id) {
            this.id = id;
        }

        @Override
        public <T> boolean resolve(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter, Consumer<T> collector) {
            T object = objectGetter.apply(this.id);
            if (object == null) {
                return false;
            }
            collector.accept(object);
            return true;
        }

        @Override
        public void addToJson(JsonArray json) {
            json.add(this.id.toString());
        }

        public String toString() {
            return this.id.toString();
        }
    }

    public static interface Entry {
        public <T> boolean resolve(Function<Identifier, Tag<T>> var1, Function<Identifier, T> var2, Consumer<T> var3);

        public void addToJson(JsonArray var1);
    }

    public static class Builder {
        private final List<TrackedEntry> entries = Lists.newArrayList();

        public static Builder create() {
            return new Builder();
        }

        public Builder add(TrackedEntry trackedEntry) {
            this.entries.add(trackedEntry);
            return this;
        }

        public Builder add(Entry entry, String source) {
            return this.add(new TrackedEntry(entry, source));
        }

        public Builder add(Identifier id, String source) {
            return this.add(new ObjectEntry(id), source);
        }

        public Builder addTag(Identifier id, String source) {
            return this.add(new TagEntry(id), source);
        }

        public <T> Optional<Tag<T>> build(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter) {
            ImmutableSet.Builder builder = ImmutableSet.builder();
            for (TrackedEntry trackedEntry : this.entries) {
                if (trackedEntry.getEntry().resolve(tagGetter, objectGetter, builder::add)) continue;
                return Optional.empty();
            }
            return Optional.of(Tag.of(builder.build()));
        }

        public Stream<TrackedEntry> streamEntries() {
            return this.entries.stream();
        }

        public <T> Stream<TrackedEntry> streamUnresolvedEntries(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter) {
            return this.streamEntries().filter(trackedEntry -> !trackedEntry.getEntry().resolve(tagGetter, objectGetter, object -> {}));
        }

        public Builder read(JsonObject json, String source) {
            JsonArray jsonArray = JsonHelper.getArray(json, "values");
            ArrayList<Entry> list = Lists.newArrayList();
            for (JsonElement jsonElement : jsonArray) {
                String string = JsonHelper.asString(jsonElement, "value");
                if (string.startsWith("#")) {
                    list.add(new TagEntry(new Identifier(string.substring(1))));
                    continue;
                }
                list.add(new ObjectEntry(new Identifier(string)));
            }
            if (JsonHelper.getBoolean(json, "replace", false)) {
                this.entries.clear();
            }
            list.forEach(entry -> this.entries.add(new TrackedEntry((Entry)entry, source)));
            return this;
        }

        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            JsonArray jsonArray = new JsonArray();
            for (TrackedEntry trackedEntry : this.entries) {
                trackedEntry.getEntry().addToJson(jsonArray);
            }
            jsonObject.addProperty("replace", false);
            jsonObject.add("values", jsonArray);
            return jsonObject;
        }
    }

    public static class TrackedEntry {
        private final Entry entry;
        private final String source;

        private TrackedEntry(Entry entry, String source) {
            this.entry = entry;
            this.source = source;
        }

        public Entry getEntry() {
            return this.entry;
        }

        public String toString() {
            return this.entry.toString() + " (from " + this.source + ")";
        }
    }
}

