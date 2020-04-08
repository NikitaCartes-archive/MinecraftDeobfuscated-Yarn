package net.minecraft.tag;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
		private final List<Tag.class_5145> entries = Lists.<Tag.class_5145>newArrayList();

		public static Tag.Builder create() {
			return new Tag.Builder();
		}

		public Tag.Builder method_27064(Tag.class_5145 arg) {
			this.entries.add(arg);
			return this;
		}

		public Tag.Builder method_27065(Tag.Entry entry, String string) {
			return this.method_27064(new Tag.class_5145(entry, string));
		}

		public Tag.Builder add(Identifier id, String string) {
			return this.method_27065(new Tag.ObjectEntry(id), string);
		}

		public Tag.Builder addTag(Identifier id, String string) {
			return this.method_27065(new Tag.TagEntry(id), string);
		}

		public <T> Optional<Tag<T>> build(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter) {
			ImmutableSet.Builder<T> builder = ImmutableSet.builder();

			for (Tag.class_5145 lv : this.entries) {
				if (!lv.method_27067().resolve(tagGetter, objectGetter, builder::add)) {
					return Optional.empty();
				}
			}

			return Optional.of(Tag.of(builder.build()));
		}

		public Stream<Tag.class_5145> streamEntries() {
			return this.entries.stream();
		}

		public <T> Stream<Tag.class_5145> streamUnresolvedEntries(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter) {
			return this.streamEntries().filter(arg -> !arg.method_27067().resolve(tagGetter, objectGetter, object -> {
				}));
		}

		public Tag.Builder read(JsonObject json, String string) {
			JsonArray jsonArray = JsonHelper.getArray(json, "values");
			List<Tag.Entry> list = Lists.<Tag.Entry>newArrayList();

			for (JsonElement jsonElement : jsonArray) {
				String string2 = JsonHelper.asString(jsonElement, "value");
				if (string2.startsWith("#")) {
					list.add(new Tag.TagEntry(new Identifier(string2.substring(1))));
				} else {
					list.add(new Tag.ObjectEntry(new Identifier(string2)));
				}
			}

			if (JsonHelper.getBoolean(json, "replace", false)) {
				this.entries.clear();
			}

			list.forEach(entry -> this.entries.add(new Tag.class_5145(entry, string)));
			return this;
		}

		public JsonObject toJson() {
			JsonObject jsonObject = new JsonObject();
			JsonArray jsonArray = new JsonArray();

			for (Tag.class_5145 lv : this.entries) {
				lv.method_27067().addToJson(jsonArray);
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

	public static class class_5145 {
		private final Tag.Entry field_23805;
		private final String field_23806;

		private class_5145(Tag.Entry entry, String string) {
			this.field_23805 = entry;
			this.field_23806 = string;
		}

		public Tag.Entry method_27067() {
			return this.field_23805;
		}

		public String toString() {
			return this.field_23805.toString() + " (from " + this.field_23806 + ")";
		}
	}
}
