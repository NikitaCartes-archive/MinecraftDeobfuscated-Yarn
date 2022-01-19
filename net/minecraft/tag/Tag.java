/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.tag.SetTag;
import net.minecraft.tag.TagGroup;
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
    public static <T> Codec<Tag<T>> codec(Supplier<TagGroup<T>> groupGetter) {
        return Identifier.CODEC.flatXmap(id -> Optional.ofNullable(((TagGroup)groupGetter.get()).getTag((Identifier)id)).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown tag: " + id)), tag -> Optional.ofNullable(((TagGroup)groupGetter.get()).getUncheckedTagId(tag)).map(DataResult::success).orElseGet(() -> DataResult.error("Unknown tag: " + tag)));
    }

    public boolean contains(T var1);

    public List<T> values();

    default public Optional<T> getRandom(Random random) {
        List<T> list = this.values();
        int i = list.size();
        if (i > 0) {
            return Optional.of(list.get(random.nextInt(i)));
        }
        return Optional.empty();
    }

    public static <T> Tag<T> of(Set<T> values) {
        return SetTag.of(values);
    }

    public static interface Identified<T>
    extends Tag<T> {
        public Identifier getId();
    }

    public static class OptionalTagEntry
    implements Entry {
        private final Identifier id;

        public OptionalTagEntry(Identifier id) {
            this.id = id;
        }

        @Override
        public <T> boolean resolve(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter, Consumer<T> collector) {
            Tag<T> tag = tagGetter.apply(this.id);
            if (tag != null) {
                tag.values().forEach(collector);
            }
            return true;
        }

        @Override
        public void addToJson(JsonArray json) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", "#" + this.id);
            jsonObject.addProperty("required", false);
            json.add(jsonObject);
        }

        public String toString() {
            return "#" + this.id + "?";
        }

        @Override
        public void forEachGroupId(Consumer<Identifier> consumer) {
            consumer.accept(this.id);
        }

        @Override
        public boolean canAdd(Predicate<Identifier> existenceTest, Predicate<Identifier> duplicationTest) {
            return true;
        }
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

        @Override
        public boolean canAdd(Predicate<Identifier> existenceTest, Predicate<Identifier> duplicationTest) {
            return duplicationTest.test(this.id);
        }

        @Override
        public void forEachTagId(Consumer<Identifier> consumer) {
            consumer.accept(this.id);
        }
    }

    public static class OptionalObjectEntry
    implements Entry {
        private final Identifier id;

        public OptionalObjectEntry(Identifier id) {
            this.id = id;
        }

        @Override
        public <T> boolean resolve(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter, Consumer<T> collector) {
            T object = objectGetter.apply(this.id);
            if (object != null) {
                collector.accept(object);
            }
            return true;
        }

        @Override
        public void addToJson(JsonArray json) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", this.id.toString());
            jsonObject.addProperty("required", false);
            json.add(jsonObject);
        }

        @Override
        public boolean canAdd(Predicate<Identifier> existenceTest, Predicate<Identifier> duplicationTest) {
            return true;
        }

        public String toString() {
            return this.id + "?";
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

        @Override
        public boolean canAdd(Predicate<Identifier> existenceTest, Predicate<Identifier> duplicationTest) {
            return existenceTest.test(this.id);
        }

        public String toString() {
            return this.id.toString();
        }
    }

    public static interface Entry {
        public <T> boolean resolve(Function<Identifier, Tag<T>> var1, Function<Identifier, T> var2, Consumer<T> var3);

        public void addToJson(JsonArray var1);

        default public void forEachTagId(Consumer<Identifier> consumer) {
        }

        default public void forEachGroupId(Consumer<Identifier> consumer) {
        }

        public boolean canAdd(Predicate<Identifier> var1, Predicate<Identifier> var2);
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

        public Builder addOptional(Identifier id, String source) {
            return this.add(new OptionalObjectEntry(id), source);
        }

        public Builder addTag(Identifier id, String source) {
            return this.add(new TagEntry(id), source);
        }

        public Builder addOptionalTag(Identifier id, String source) {
            return this.add(new OptionalTagEntry(id), source);
        }

        public <T> Either<Collection<TrackedEntry>, Tag<T>> build(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter) {
            ImmutableSet.Builder builder = ImmutableSet.builder();
            ArrayList<TrackedEntry> list = Lists.newArrayList();
            for (TrackedEntry trackedEntry : this.entries) {
                if (trackedEntry.getEntry().resolve(tagGetter, objectGetter, builder::add)) continue;
                list.add(trackedEntry);
            }
            return list.isEmpty() ? Either.right(Tag.of(builder.build())) : Either.left(list);
        }

        public Stream<TrackedEntry> streamEntries() {
            return this.entries.stream();
        }

        public void forEachTagId(Consumer<Identifier> consumer) {
            this.entries.forEach(trackedEntry -> trackedEntry.entry.forEachTagId(consumer));
        }

        public void forEachGroupId(Consumer<Identifier> consumer) {
            this.entries.forEach(trackedEntry -> trackedEntry.entry.forEachGroupId(consumer));
        }

        public Builder read(JsonObject json, String source) {
            JsonArray jsonArray = JsonHelper.getArray(json, "values");
            ArrayList<Entry> list = Lists.newArrayList();
            for (JsonElement jsonElement : jsonArray) {
                list.add(Builder.resolveEntry(jsonElement));
            }
            if (JsonHelper.getBoolean(json, "replace", false)) {
                this.entries.clear();
            }
            list.forEach(entry -> this.entries.add(new TrackedEntry((Entry)entry, source)));
            return this;
        }

        private static Entry resolveEntry(JsonElement json) {
            Identifier identifier;
            boolean bl;
            String string;
            if (json.isJsonObject()) {
                JsonObject jsonObject = json.getAsJsonObject();
                string = JsonHelper.getString(jsonObject, "id");
                bl = JsonHelper.getBoolean(jsonObject, "required", true);
            } else {
                string = JsonHelper.asString(json, "id");
                bl = true;
            }
            if (string.startsWith("#")) {
                identifier = new Identifier(string.substring(1));
                return bl ? new TagEntry(identifier) : new OptionalTagEntry(identifier);
            }
            identifier = new Identifier(string);
            return bl ? new ObjectEntry(identifier) : new OptionalObjectEntry(identifier);
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
        final Entry entry;
        private final String source;

        TrackedEntry(Entry entry, String source) {
            this.entry = entry;
            this.source = source;
        }

        public Entry getEntry() {
            return this.entry;
        }

        public String getSource() {
            return this.source;
        }

        public String toString() {
            return this.entry + " (from " + this.source + ")";
        }
    }
}

