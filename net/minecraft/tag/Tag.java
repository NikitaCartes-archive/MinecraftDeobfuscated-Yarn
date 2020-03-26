/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.tag;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

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
public class Tag<T> {
    private final Identifier id;
    private final Set<T> values;
    private final Collection<Entry<T>> entries;

    public Tag(Identifier id) {
        this.id = id;
        this.values = Collections.emptySet();
        this.entries = Collections.emptyList();
    }

    public Tag(Identifier id, Collection<Entry<T>> entries, boolean ordered) {
        this.id = id;
        this.values = ordered ? Sets.newLinkedHashSet() : Sets.newHashSet();
        this.entries = entries;
        for (Entry<T> entry : entries) {
            entry.build(this.values);
        }
    }

    public JsonObject toJson(Function<T, Identifier> idGetter) {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        for (Entry<T> entry : this.entries) {
            entry.toJson(jsonArray, idGetter);
        }
        jsonObject.addProperty("replace", false);
        jsonObject.add("values", jsonArray);
        return jsonObject;
    }

    public boolean contains(T entry) {
        return this.values.contains(entry);
    }

    public Collection<T> values() {
        return this.values;
    }

    public Collection<Entry<T>> entries() {
        return this.entries;
    }

    public T getRandom(Random random) {
        ArrayList<T> list = Lists.newArrayList(this.values());
        return (T)list.get(random.nextInt(list.size()));
    }

    public Identifier getId() {
        return this.id;
    }

    public static class TagEntry<T>
    implements Entry<T> {
        @Nullable
        private final Identifier id;
        @Nullable
        private Tag<T> tag;

        public TagEntry(Identifier id) {
            this.id = id;
        }

        public TagEntry(Tag<T> tag) {
            this.id = tag.getId();
            this.tag = tag;
        }

        @Override
        public boolean applyTagGetter(Function<Identifier, Tag<T>> tagGetter) {
            if (this.tag == null) {
                this.tag = tagGetter.apply(this.id);
            }
            return this.tag != null;
        }

        @Override
        public void build(Collection<T> targetCollection) {
            if (this.tag == null) {
                throw Util.throwOrPause(new IllegalStateException("Cannot build unresolved tag entry"));
            }
            targetCollection.addAll(this.tag.values());
        }

        public Identifier getId() {
            if (this.tag != null) {
                return this.tag.getId();
            }
            if (this.id != null) {
                return this.id;
            }
            throw new IllegalStateException("Cannot serialize an anonymous tag to json!");
        }

        @Override
        public void toJson(JsonArray targetArray, Function<T, Identifier> idGetter) {
            targetArray.add("#" + this.getId());
        }
    }

    public static class CollectionEntry<T>
    implements Entry<T> {
        private final Collection<T> values;

        public CollectionEntry(Collection<T> values) {
            this.values = values;
        }

        @Override
        public void build(Collection<T> targetCollection) {
            targetCollection.addAll(this.values);
        }

        @Override
        public void toJson(JsonArray targetArray, Function<T, Identifier> idGetter) {
            for (T object : this.values) {
                Identifier identifier = idGetter.apply(object);
                if (identifier == null) {
                    throw new IllegalStateException("Unable to serialize an anonymous value to json!");
                }
                targetArray.add(identifier.toString());
            }
        }

        public Collection<T> getValues() {
            return this.values;
        }
    }

    public static interface Entry<T> {
        default public boolean applyTagGetter(Function<Identifier, Tag<T>> tagGetter) {
            return true;
        }

        public void build(Collection<T> var1);

        public void toJson(JsonArray var1, Function<T, Identifier> var2);
    }

    public static class Builder<T> {
        private final Set<Entry<T>> entries = Sets.newLinkedHashSet();
        private boolean ordered;

        public static <T> Builder<T> create() {
            return new Builder<T>();
        }

        public Builder<T> add(Entry<T> entry) {
            this.entries.add(entry);
            return this;
        }

        public Builder<T> add(T entry) {
            this.entries.add(new CollectionEntry<T>(Collections.singleton(entry)));
            return this;
        }

        @SafeVarargs
        public final Builder<T> add(T ... entries) {
            this.entries.add(new CollectionEntry<T>(Lists.newArrayList(entries)));
            return this;
        }

        public Builder<T> add(Tag<T> tag) {
            this.entries.add(new TagEntry<T>(tag));
            return this;
        }

        public Builder<T> ordered(boolean ordered) {
            this.ordered = ordered;
            return this;
        }

        public boolean applyTagGetter(Function<Identifier, Tag<T>> tagGetter) {
            for (Entry<T> entry : this.entries) {
                if (entry.applyTagGetter(tagGetter)) continue;
                return false;
            }
            return true;
        }

        public Tag<T> build(Identifier id) {
            return new Tag<T>(id, this.entries, this.ordered);
        }

        public Builder<T> fromJson(Function<Identifier, Optional<T>> entryGetter, JsonObject json) {
            JsonArray jsonArray = JsonHelper.getArray(json, "values");
            ArrayList list = Lists.newArrayList();
            for (JsonElement jsonElement : jsonArray) {
                String string = JsonHelper.asString(jsonElement, "value");
                if (string.startsWith("#")) {
                    list.add(new TagEntry(new Identifier(string.substring(1))));
                    continue;
                }
                Identifier identifier = new Identifier(string);
                list.add(new CollectionEntry<T>(Collections.singleton(entryGetter.apply(identifier).orElseThrow(() -> new JsonParseException("Unknown value '" + identifier + "'")))));
            }
            if (JsonHelper.getBoolean(json, "replace", false)) {
                this.entries.clear();
            }
            this.entries.addAll(list);
            return this;
        }
    }
}

