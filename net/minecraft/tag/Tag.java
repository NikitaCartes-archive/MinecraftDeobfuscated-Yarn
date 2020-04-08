/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
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
    public boolean contains(T var1);

    public List<T> values();

    default public T getRandom(Random random) {
        List<T> list = this.values();
        return list.get(random.nextInt(list.size()));
    }

    public static <T> Tag<T> of(final Set<T> set) {
        final ImmutableList<T> immutableList = ImmutableList.copyOf(set);
        return new Tag<T>(){

            @Override
            public boolean contains(T entry) {
                return set.contains(entry);
            }

            @Override
            public List<T> values() {
                return immutableList;
            }
        };
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
        private final List<class_5145> entries = Lists.newArrayList();

        public static Builder create() {
            return new Builder();
        }

        public Builder method_27064(class_5145 arg) {
            this.entries.add(arg);
            return this;
        }

        public Builder method_27065(Entry entry, String string) {
            return this.method_27064(new class_5145(entry, string));
        }

        public Builder add(Identifier id, String string) {
            return this.method_27065(new ObjectEntry(id), string);
        }

        public Builder addTag(Identifier id, String string) {
            return this.method_27065(new TagEntry(id), string);
        }

        public <T> Optional<Tag<T>> build(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter) {
            ImmutableSet.Builder builder = ImmutableSet.builder();
            for (class_5145 lv : this.entries) {
                if (lv.method_27067().resolve(tagGetter, objectGetter, builder::add)) continue;
                return Optional.empty();
            }
            return Optional.of(Tag.of(builder.build()));
        }

        public Stream<class_5145> streamEntries() {
            return this.entries.stream();
        }

        public <T> Stream<class_5145> streamUnresolvedEntries(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter) {
            return this.streamEntries().filter(arg -> !arg.method_27067().resolve(tagGetter, objectGetter, object -> {}));
        }

        public Builder read(JsonObject json, String string) {
            JsonArray jsonArray = JsonHelper.getArray(json, "values");
            ArrayList<Entry> list = Lists.newArrayList();
            for (JsonElement jsonElement : jsonArray) {
                String string2 = JsonHelper.asString(jsonElement, "value");
                if (string2.startsWith("#")) {
                    list.add(new TagEntry(new Identifier(string2.substring(1))));
                    continue;
                }
                list.add(new ObjectEntry(new Identifier(string2)));
            }
            if (JsonHelper.getBoolean(json, "replace", false)) {
                this.entries.clear();
            }
            list.forEach(entry -> this.entries.add(new class_5145((Entry)entry, string)));
            return this;
        }

        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            JsonArray jsonArray = new JsonArray();
            for (class_5145 lv : this.entries) {
                lv.method_27067().addToJson(jsonArray);
            }
            jsonObject.addProperty("replace", false);
            jsonObject.add("values", jsonArray);
            return jsonObject;
        }
    }

    public static class class_5145 {
        private final Entry field_23805;
        private final String field_23806;

        private class_5145(Entry entry, String string) {
            this.field_23805 = entry;
            this.field_23806 = string;
        }

        public Entry method_27067() {
            return this.field_23805;
        }

        public String toString() {
            return this.field_23805.toString() + " (from " + this.field_23806 + ")";
        }
    }
}

