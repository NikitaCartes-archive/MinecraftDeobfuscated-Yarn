package net.minecraft.tag;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.SystemUtil;

public class Tag<T> {
	private final Identifier id;
	private final Set<T> values;
	private final Collection<Tag.Entry<T>> entries;

	public Tag(Identifier identifier) {
		this.id = identifier;
		this.values = Collections.emptySet();
		this.entries = Collections.emptyList();
	}

	public Tag(Identifier identifier, Collection<Tag.Entry<T>> collection, boolean bl) {
		this.id = identifier;
		this.values = (Set<T>)(bl ? Sets.<T>newLinkedHashSet() : Sets.<T>newHashSet());
		this.entries = collection;

		for (Tag.Entry<T> entry : collection) {
			entry.build(this.values);
		}
	}

	public JsonObject toJson(Function<T, Identifier> function) {
		JsonObject jsonObject = new JsonObject();
		JsonArray jsonArray = new JsonArray();

		for (Tag.Entry<T> entry : this.entries) {
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

	public Collection<Tag.Entry<T>> entries() {
		return this.entries;
	}

	public T getRandom(Random random) {
		List<T> list = Lists.<T>newArrayList(this.values());
		return (T)list.get(random.nextInt(list.size()));
	}

	public Identifier getId() {
		return this.id;
	}

	public static class Builder<T> {
		private final Set<Tag.Entry<T>> entries = Sets.<Tag.Entry<T>>newLinkedHashSet();
		private boolean ordered;

		public static <T> Tag.Builder<T> create() {
			return new Tag.Builder<>();
		}

		public Tag.Builder<T> add(Tag.Entry<T> entry) {
			this.entries.add(entry);
			return this;
		}

		public Tag.Builder<T> add(T object) {
			this.entries.add(new Tag.CollectionEntry(Collections.singleton(object)));
			return this;
		}

		@SafeVarargs
		public final Tag.Builder<T> add(T... objects) {
			this.entries.add(new Tag.CollectionEntry(Lists.<T>newArrayList(objects)));
			return this;
		}

		public Tag.Builder<T> add(Tag<T> tag) {
			this.entries.add(new Tag.TagEntry<>(tag));
			return this;
		}

		public Tag.Builder<T> ordered(boolean bl) {
			this.ordered = bl;
			return this;
		}

		public boolean applyTagGetter(Function<Identifier, Tag<T>> function) {
			for (Tag.Entry<T> entry : this.entries) {
				if (!entry.applyTagGetter(function)) {
					return false;
				}
			}

			return true;
		}

		public Tag<T> build(Identifier identifier) {
			return new Tag<>(identifier, this.entries, this.ordered);
		}

		public Tag.Builder<T> fromJson(Function<Identifier, Optional<T>> function, JsonObject jsonObject) {
			JsonArray jsonArray = JsonHelper.getArray(jsonObject, "values");
			List<Tag.Entry<T>> list = Lists.<Tag.Entry<T>>newArrayList();

			for (JsonElement jsonElement : jsonArray) {
				String string = JsonHelper.asString(jsonElement, "value");
				if (string.startsWith("#")) {
					list.add(new Tag.TagEntry(new Identifier(string.substring(1))));
				} else {
					Identifier identifier = new Identifier(string);
					list.add(
						new Tag.CollectionEntry(
							Collections.singleton(((Optional)function.apply(identifier)).orElseThrow(() -> new JsonParseException("Unknown value '" + identifier + "'")))
						)
					);
				}
			}

			if (JsonHelper.getBoolean(jsonObject, "replace", false)) {
				this.entries.clear();
			}

			this.entries.addAll(list);
			return this;
		}
	}

	public static class CollectionEntry<T> implements Tag.Entry<T> {
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
				Identifier identifier = (Identifier)function.apply(object);
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

	public interface Entry<T> {
		default boolean applyTagGetter(Function<Identifier, Tag<T>> function) {
			return true;
		}

		void build(Collection<T> collection);

		void toJson(JsonArray jsonArray, Function<T, Identifier> function);
	}

	public static class TagEntry<T> implements Tag.Entry<T> {
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
				this.tag = (Tag<T>)function.apply(this.id);
			}

			return this.tag != null;
		}

		@Override
		public void build(Collection<T> collection) {
			if (this.tag == null) {
				throw (IllegalStateException)SystemUtil.throwOrPause((T)(new IllegalStateException("Cannot build unresolved tag entry")));
			} else {
				collection.addAll(this.tag.values());
			}
		}

		public Identifier getId() {
			if (this.tag != null) {
				return this.tag.getId();
			} else if (this.id != null) {
				return this.id;
			} else {
				throw new IllegalStateException("Cannot serialize an anonymous tag to json!");
			}
		}

		@Override
		public void toJson(JsonArray jsonArray, Function<T, Identifier> function) {
			jsonArray.add("#" + this.getId());
		}
	}
}
