/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

    public static class ObjectBuilder<T>
    extends Builder {
        private final Function<T, Identifier> idGetter;

        public ObjectBuilder(Function<T, Identifier> idGetter) {
            this.idGetter = idGetter;
        }

        public ObjectBuilder<T> add(T element) {
            this.add(this.idGetter.apply(element));
            return this;
        }

        public ObjectBuilder<T> add(Collection<T> elements) {
            elements.stream().map(this.idGetter).forEach(this::add);
            return this;
        }

        @SafeVarargs
        public final ObjectBuilder<T> add(T ... elements) {
            this.add((Collection<T>)Arrays.asList(elements));
            return this;
        }

        public ObjectBuilder<T> addTag(Identified<T> identifiedTag) {
            this.addTag(identifiedTag.getId());
            return this;
        }
    }

    public static class Builder {
        private final Set<Entry> entries = Sets.newLinkedHashSet();

        public static Builder create() {
            return new Builder();
        }

        public Builder add(Entry entry) {
            this.entries.add(entry);
            return this;
        }

        public Builder add(Identifier id) {
            return this.add(new ObjectEntry(id));
        }

        public Builder addTag(Identifier id) {
            return this.add(new TagEntry(id));
        }

        public <T> Optional<Tag<T>> build(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter) {
            ImmutableSet.Builder builder = ImmutableSet.builder();
            for (Entry entry : this.entries) {
                if (entry.resolve(tagGetter, objectGetter, builder::add)) continue;
                return Optional.empty();
            }
            return Optional.of(Tag.of(builder.build()));
        }

        public Stream<Entry> streamEntries() {
            return this.entries.stream();
        }

        public <T> Stream<Entry> streamUnresolvedEntries(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter) {
            return this.streamEntries().filter(entry -> !entry.resolve(tagGetter, objectGetter, object -> {}));
        }

        public Builder read(JsonObject json) {
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
            this.entries.addAll(list);
            return this;
        }

        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            JsonArray jsonArray = new JsonArray();
            for (Entry entry : this.entries) {
                entry.addToJson(jsonArray);
            }
            jsonObject.addProperty("replace", false);
            jsonObject.add("values", jsonArray);
            return jsonObject;
        }
    }
}

