package net.minecraft.tag;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
	boolean contains(T entry);

	List<T> values();

	default T getRandom(Random random) {
		List<T> list = this.values();
		return (T)list.get(random.nextInt(list.size()));
	}

	static <T> Tag<T> of(Set<T> set) {
		final ImmutableList<T> immutableList = ImmutableList.copyOf(set);
		return new Tag<T>() {
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

	/**
	 * A builder class to ease the creation of tags. It can also be used as a
	 * mutable form of a tag.
	 */
	public static class Builder {
		private final Set<Tag.Entry> entries = Sets.<Tag.Entry>newLinkedHashSet();

		public static Tag.Builder create() {
			return new Tag.Builder();
		}

		public Tag.Builder add(Tag.Entry entry) {
			this.entries.add(entry);
			return this;
		}

		public Tag.Builder add(Identifier id) {
			return this.add(new Tag.ObjectEntry(id));
		}

		public Tag.Builder addTag(Identifier id) {
			return this.add(new Tag.TagEntry(id));
		}

		public <T> Optional<Tag<T>> build(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter) {
			ImmutableSet.Builder<T> builder = ImmutableSet.builder();

			for (Tag.Entry entry : this.entries) {
				if (!entry.resolve(tagGetter, objectGetter, builder::add)) {
					return Optional.empty();
				}
			}

			return Optional.of(Tag.of(builder.build()));
		}

		public Stream<Tag.Entry> streamEntries() {
			return this.entries.stream();
		}

		public <T> Stream<Tag.Entry> streamUnresolvedEntries(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter) {
			return this.streamEntries().filter(entry -> !entry.resolve(tagGetter, objectGetter, object -> {
				}));
		}

		public Tag.Builder read(JsonObject json) {
			JsonArray jsonArray = JsonHelper.getArray(json, "values");
			List<Tag.Entry> list = Lists.<Tag.Entry>newArrayList();

			for (JsonElement jsonElement : jsonArray) {
				String string = JsonHelper.asString(jsonElement, "value");
				if (string.startsWith("#")) {
					list.add(new Tag.TagEntry(new Identifier(string.substring(1))));
				} else {
					list.add(new Tag.ObjectEntry(new Identifier(string)));
				}
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

			for (Tag.Entry entry : this.entries) {
				entry.addToJson(jsonArray);
			}

			jsonObject.addProperty("replace", false);
			jsonObject.add("values", jsonArray);
			return jsonObject;
		}
	}

	public interface Entry {
		<T> boolean resolve(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter, Consumer<T> collector);

		void addToJson(JsonArray json);
	}

	public interface Identified<T> extends Tag<T> {
		Identifier getId();
	}

	public static class ObjectBuilder<T> extends Tag.Builder {
		private final Function<T, Identifier> idGetter;

		public ObjectBuilder(Function<T, Identifier> idGetter) {
			this.idGetter = idGetter;
		}

		public Tag.ObjectBuilder<T> add(T element) {
			this.add((Identifier)this.idGetter.apply(element));
			return this;
		}

		public Tag.ObjectBuilder<T> add(Collection<T> elements) {
			elements.stream().map(this.idGetter).forEach(this::add);
			return this;
		}

		@SafeVarargs
		public final Tag.ObjectBuilder<T> add(T... elements) {
			this.add(Arrays.asList(elements));
			return this;
		}

		public Tag.ObjectBuilder<T> addTag(Tag.Identified<T> identifiedTag) {
			this.addTag(identifiedTag.getId());
			return this;
		}
	}

	public static class ObjectEntry implements Tag.Entry {
		private final Identifier id;

		public ObjectEntry(Identifier id) {
			this.id = id;
		}

		@Override
		public <T> boolean resolve(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter, Consumer<T> collector) {
			T object = (T)objectGetter.apply(this.id);
			if (object == null) {
				return false;
			} else {
				collector.accept(object);
				return true;
			}
		}

		@Override
		public void addToJson(JsonArray json) {
			json.add(this.id.toString());
		}

		public String toString() {
			return this.id.toString();
		}
	}

	public static class TagEntry implements Tag.Entry {
		private final Identifier id;

		public TagEntry(Identifier id) {
			this.id = id;
		}

		@Override
		public <T> boolean resolve(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter, Consumer<T> collector) {
			Tag<T> tag = (Tag<T>)tagGetter.apply(this.id);
			if (tag == null) {
				return false;
			} else {
				tag.values().forEach(collector);
				return true;
			}
		}

		@Override
		public void addToJson(JsonArray json) {
			json.add("#" + this.id);
		}

		public String toString() {
			return "#" + this.id;
		}
	}
}
