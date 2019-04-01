package net.minecraft;

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

public class class_3494<T> {
	private final class_2960 field_15580;
	private final Set<T> field_15579;
	private final Collection<class_3494.class_3496<T>> field_15578;

	public class_3494(class_2960 arg) {
		this.field_15580 = arg;
		this.field_15579 = Collections.emptySet();
		this.field_15578 = Collections.emptyList();
	}

	public class_3494(class_2960 arg, Collection<class_3494.class_3496<T>> collection, boolean bl) {
		this.field_15580 = arg;
		this.field_15579 = (Set<T>)(bl ? Sets.<T>newLinkedHashSet() : Sets.<T>newHashSet());
		this.field_15578 = collection;

		for (class_3494.class_3496<T> lv : collection) {
			lv.method_15157(this.field_15579);
		}
	}

	public JsonObject method_15140(Function<T, class_2960> function) {
		JsonObject jsonObject = new JsonObject();
		JsonArray jsonArray = new JsonArray();

		for (class_3494.class_3496<T> lv : this.field_15578) {
			lv.method_15155(jsonArray, function);
		}

		jsonObject.addProperty("replace", false);
		jsonObject.add("values", jsonArray);
		return jsonObject;
	}

	public boolean method_15141(T object) {
		return this.field_15579.contains(object);
	}

	public Collection<T> method_15138() {
		return this.field_15579;
	}

	public Collection<class_3494.class_3496<T>> method_15139() {
		return this.field_15578;
	}

	public T method_15142(Random random) {
		List<T> list = Lists.<T>newArrayList(this.method_15138());
		return (T)list.get(random.nextInt(list.size()));
	}

	public class_2960 method_15143() {
		return this.field_15580;
	}

	public static class class_3495<T> {
		private final Set<class_3494.class_3496<T>> field_15582 = Sets.<class_3494.class_3496<T>>newLinkedHashSet();
		private boolean field_15581;

		public static <T> class_3494.class_3495<T> method_15146() {
			return new class_3494.class_3495<>();
		}

		public class_3494.class_3495<T> method_15149(class_3494.class_3496<T> arg) {
			this.field_15582.add(arg);
			return this;
		}

		public class_3494.class_3495<T> method_15153(T object) {
			this.field_15582.add(new class_3494.class_3498(Collections.singleton(object)));
			return this;
		}

		@SafeVarargs
		public final class_3494.class_3495<T> method_15150(T... objects) {
			this.field_15582.add(new class_3494.class_3498(Lists.<T>newArrayList(objects)));
			return this;
		}

		public class_3494.class_3495<T> method_15151(Collection<T> collection) {
			this.field_15582.add(new class_3494.class_3498(collection));
			return this;
		}

		public class_3494.class_3495<T> method_15145(class_2960 arg) {
			this.field_15582.add(new class_3494.class_3497(arg));
			return this;
		}

		public class_3494.class_3495<T> method_15148(class_3494<T> arg) {
			this.field_15582.add(new class_3494.class_3497<>(arg));
			return this;
		}

		public class_3494.class_3495<T> method_15154(boolean bl) {
			this.field_15581 = bl;
			return this;
		}

		public boolean method_15152(Function<class_2960, class_3494<T>> function) {
			for (class_3494.class_3496<T> lv : this.field_15582) {
				if (!lv.method_15156(function)) {
					return false;
				}
			}

			return true;
		}

		public class_3494<T> method_15144(class_2960 arg) {
			return new class_3494<>(arg, this.field_15582, this.field_15581);
		}

		public class_3494.class_3495<T> method_15147(Function<class_2960, Optional<T>> function, JsonObject jsonObject) {
			JsonArray jsonArray = class_3518.method_15261(jsonObject, "values");
			if (class_3518.method_15258(jsonObject, "replace", false)) {
				this.field_15582.clear();
			}

			for (JsonElement jsonElement : jsonArray) {
				String string = class_3518.method_15287(jsonElement, "value");
				if (string.startsWith("#")) {
					this.method_15145(new class_2960(string.substring(1)));
				} else {
					class_2960 lv = new class_2960(string);
					this.method_15153((T)((Optional)function.apply(lv)).orElseThrow(() -> new JsonParseException("Unknown value '" + lv + "'")));
				}
			}

			return this;
		}
	}

	public interface class_3496<T> {
		default boolean method_15156(Function<class_2960, class_3494<T>> function) {
			return true;
		}

		void method_15157(Collection<T> collection);

		void method_15155(JsonArray jsonArray, Function<T, class_2960> function);
	}

	public static class class_3497<T> implements class_3494.class_3496<T> {
		@Nullable
		private final class_2960 field_15584;
		@Nullable
		private class_3494<T> field_15583;

		public class_3497(class_2960 arg) {
			this.field_15584 = arg;
		}

		public class_3497(class_3494<T> arg) {
			this.field_15584 = arg.method_15143();
			this.field_15583 = arg;
		}

		@Override
		public boolean method_15156(Function<class_2960, class_3494<T>> function) {
			if (this.field_15583 == null) {
				this.field_15583 = (class_3494<T>)function.apply(this.field_15584);
			}

			return this.field_15583 != null;
		}

		@Override
		public void method_15157(Collection<T> collection) {
			if (this.field_15583 == null) {
				throw new IllegalStateException("Cannot build unresolved tag entry");
			} else {
				collection.addAll(this.field_15583.method_15138());
			}
		}

		public class_2960 method_15158() {
			if (this.field_15583 != null) {
				return this.field_15583.method_15143();
			} else if (this.field_15584 != null) {
				return this.field_15584;
			} else {
				throw new IllegalStateException("Cannot serialize an anonymous tag to json!");
			}
		}

		@Override
		public void method_15155(JsonArray jsonArray, Function<T, class_2960> function) {
			jsonArray.add("#" + this.method_15158());
		}
	}

	public static class class_3498<T> implements class_3494.class_3496<T> {
		private final Collection<T> field_15585;

		public class_3498(Collection<T> collection) {
			this.field_15585 = collection;
		}

		@Override
		public void method_15157(Collection<T> collection) {
			collection.addAll(this.field_15585);
		}

		@Override
		public void method_15155(JsonArray jsonArray, Function<T, class_2960> function) {
			for (T object : this.field_15585) {
				class_2960 lv = (class_2960)function.apply(object);
				if (lv == null) {
					throw new IllegalStateException("Unable to serialize an anonymous value to json!");
				}

				jsonArray.add(lv.toString());
			}
		}

		public Collection<T> method_15159() {
			return this.field_15585;
		}
	}
}
