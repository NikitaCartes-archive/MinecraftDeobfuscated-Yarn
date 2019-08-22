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
import net.minecraft.util.SystemUtil;
import org.jetbrains.annotations.Nullable;

public class Tag<T> {
    private final Identifier id;
    private final Set<T> values;
    private final Collection<Entry<T>> entries;

    public Tag(Identifier identifier) {
        this.id = identifier;
        this.values = Collections.emptySet();
        this.entries = Collections.emptyList();
    }

    public Tag(Identifier identifier, Collection<Entry<T>> collection, boolean bl) {
        this.id = identifier;
        this.values = bl ? Sets.newLinkedHashSet() : Sets.newHashSet();
        this.entries = collection;
        for (Entry<T> entry : collection) {
            entry.build(this.values);
        }
    }

    public JsonObject toJson(Function<T, Identifier> function) {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        for (Entry<T> entry : this.entries) {
            entry.toJson(jsonArray, function);
        }
        jsonObject.addProperty("replace", false);
        jsonObject.add("values", jsonArray);
        return jsonObject;
    }

    public boolean contains(T object) {
        return this.values.contains(object);
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

        public TagEntry(Identifier identifier) {
            this.id = identifier;
        }

        public TagEntry(Tag<T> tag) {
            this.id = tag.getId();
            this.tag = tag;
        }

        @Override
        public boolean applyTagGetter(Function<Identifier, Tag<T>> function) {
            if (this.tag == null) {
                this.tag = function.apply(this.id);
            }
            return this.tag != null;
        }

        @Override
        public void build(Collection<T> collection) {
            if (this.tag == null) {
                throw SystemUtil.method_22320(new IllegalStateException("Cannot build unresolved tag entry"));
            }
            collection.addAll(this.tag.values());
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
        public void toJson(JsonArray jsonArray, Function<T, Identifier> function) {
            jsonArray.add("#" + this.getId());
        }
    }

    public static class CollectionEntry<T>
    implements Entry<T> {
        private final Collection<T> values;

        public CollectionEntry(Collection<T> collection) {
            this.values = collection;
        }

        @Override
        public void build(Collection<T> collection) {
            collection.addAll(this.values);
        }

        @Override
        public void toJson(JsonArray jsonArray, Function<T, Identifier> function) {
            for (T object : this.values) {
                Identifier identifier = function.apply(object);
                if (identifier == null) {
                    throw new IllegalStateException("Unable to serialize an anonymous value to json!");
                }
                jsonArray.add(identifier.toString());
            }
        }

        public Collection<T> getValues() {
            return this.values;
        }
    }

    public static interface Entry<T> {
        default public boolean applyTagGetter(Function<Identifier, Tag<T>> function) {
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

        public Builder<T> add(T object) {
            this.entries.add(new CollectionEntry<T>(Collections.singleton(object)));
            return this;
        }

        @SafeVarargs
        public final Builder<T> add(T ... objects) {
            this.entries.add(new CollectionEntry<T>(Lists.newArrayList(objects)));
            return this;
        }

        public Builder<T> add(Tag<T> tag) {
            this.entries.add(new TagEntry<T>(tag));
            return this;
        }

        public Builder<T> ordered(boolean bl) {
            this.ordered = bl;
            return this;
        }

        public boolean applyTagGetter(Function<Identifier, Tag<T>> function) {
            for (Entry<T> entry : this.entries) {
                if (entry.applyTagGetter(function)) continue;
                return false;
            }
            return true;
        }

        public Tag<T> build(Identifier identifier) {
            return new Tag<T>(identifier, this.entries, this.ordered);
        }

        public Builder<T> fromJson(Function<Identifier, Optional<T>> function, JsonObject jsonObject) {
            JsonArray jsonArray = JsonHelper.getArray(jsonObject, "values");
            ArrayList list = Lists.newArrayList();
            for (JsonElement jsonElement : jsonArray) {
                String string = JsonHelper.asString(jsonElement, "value");
                if (string.startsWith("#")) {
                    list.add(new TagEntry(new Identifier(string.substring(1))));
                    continue;
                }
                Identifier identifier = new Identifier(string);
                list.add(new CollectionEntry<T>(Collections.singleton(function.apply(identifier).orElseThrow(() -> new JsonParseException("Unknown value '" + identifier + "'")))));
            }
            if (JsonHelper.getBoolean(jsonObject, "replace", false)) {
                this.entries.clear();
            }
            this.entries.addAll(list);
            return this;
        }
    }
}

