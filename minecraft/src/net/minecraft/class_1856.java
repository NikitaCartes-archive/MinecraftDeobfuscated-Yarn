package net.minecraft;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public final class class_1856 implements Predicate<class_1799> {
	private static final Predicate<? super class_1856.class_1859> field_9020 = arg -> !arg.method_8108().stream().allMatch(class_1799::method_7960);
	public static final class_1856 field_9017 = new class_1856(Stream.empty());
	private final class_1856.class_1859[] field_9019;
	private class_1799[] field_9018;
	private IntList field_9016;

	private class_1856(Stream<? extends class_1856.class_1859> stream) {
		this.field_9019 = (class_1856.class_1859[])stream.filter(field_9020).toArray(class_1856.class_1859[]::new);
	}

	@Environment(EnvType.CLIENT)
	public class_1799[] method_8105() {
		this.method_8096();
		return this.field_9018;
	}

	private void method_8096() {
		if (this.field_9018 == null) {
			this.field_9018 = (class_1799[])Arrays.stream(this.field_9019).flatMap(arg -> arg.method_8108().stream()).distinct().toArray(class_1799[]::new);
		}
	}

	public boolean method_8093(@Nullable class_1799 arg) {
		if (arg == null) {
			return false;
		} else if (this.field_9019.length == 0) {
			return arg.method_7960();
		} else {
			this.method_8096();

			for (class_1799 lv : this.field_9018) {
				if (lv.method_7909() == arg.method_7909()) {
					return true;
				}
			}

			return false;
		}
	}

	public IntList method_8100() {
		if (this.field_9016 == null) {
			this.method_8096();
			this.field_9016 = new IntArrayList(this.field_9018.length);

			for (class_1799 lv : this.field_9018) {
				this.field_9016.add(class_1662.method_7408(lv));
			}

			this.field_9016.sort(IntComparators.NATURAL_COMPARATOR);
		}

		return this.field_9016;
	}

	public void method_8088(class_2540 arg) {
		this.method_8096();
		arg.method_10804(this.field_9018.length);

		for (int i = 0; i < this.field_9018.length; i++) {
			arg.method_10793(this.field_9018[i]);
		}
	}

	public JsonElement method_8089() {
		if (this.field_9019.length == 1) {
			return this.field_9019[0].method_8109();
		} else {
			JsonArray jsonArray = new JsonArray();

			for (class_1856.class_1859 lv : this.field_9019) {
				jsonArray.add(lv.method_8109());
			}

			return jsonArray;
		}
	}

	public boolean method_8103() {
		return this.field_9019.length == 0 && (this.field_9018 == null || this.field_9018.length == 0) && (this.field_9016 == null || this.field_9016.isEmpty());
	}

	private static class_1856 method_8092(Stream<? extends class_1856.class_1859> stream) {
		class_1856 lv = new class_1856(stream);
		return lv.field_9019.length == 0 ? field_9017 : lv;
	}

	public static class_1856 method_8091(class_1935... args) {
		return method_8092(Arrays.stream(args).map(arg -> new class_1856.class_1857(new class_1799(arg))));
	}

	@Environment(EnvType.CLIENT)
	public static class_1856 method_8101(class_1799... args) {
		return method_8092(Arrays.stream(args).map(arg -> new class_1856.class_1857(arg)));
	}

	public static class_1856 method_8106(class_3494<class_1792> arg) {
		return method_8092(Stream.of(new class_1856.class_1858(arg)));
	}

	public static class_1856 method_8086(class_2540 arg) {
		int i = arg.method_10816();
		return method_8092(Stream.generate(() -> new class_1856.class_1857(arg.method_10819())).limit((long)i));
	}

	public static class_1856 method_8102(@Nullable JsonElement jsonElement) {
		if (jsonElement == null || jsonElement.isJsonNull()) {
			throw new JsonSyntaxException("Item cannot be null");
		} else if (jsonElement.isJsonObject()) {
			return method_8092(Stream.of(method_8107(jsonElement.getAsJsonObject())));
		} else if (jsonElement.isJsonArray()) {
			JsonArray jsonArray = jsonElement.getAsJsonArray();
			if (jsonArray.size() == 0) {
				throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
			} else {
				return method_8092(StreamSupport.stream(jsonArray.spliterator(), false).map(jsonElementx -> method_8107(class_3518.method_15295(jsonElementx, "item"))));
			}
		} else {
			throw new JsonSyntaxException("Expected item to be object or array of objects");
		}
	}

	public static class_1856.class_1859 method_8107(JsonObject jsonObject) {
		if (jsonObject.has("item") && jsonObject.has("tag")) {
			throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
		} else if (jsonObject.has("item")) {
			class_2960 lv = new class_2960(class_3518.method_15265(jsonObject, "item"));
			class_1792 lv2 = (class_1792)class_2378.field_11142.method_17966(lv).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + lv + "'"));
			return new class_1856.class_1857(new class_1799(lv2));
		} else if (jsonObject.has("tag")) {
			class_2960 lv = new class_2960(class_3518.method_15265(jsonObject, "tag"));
			class_3494<class_1792> lv3 = class_3489.method_15106().method_15193(lv);
			if (lv3 == null) {
				throw new JsonSyntaxException("Unknown item tag '" + lv + "'");
			} else {
				return new class_1856.class_1858(lv3);
			}
		} else {
			throw new JsonParseException("An ingredient entry needs either a tag or an item");
		}
	}

	static class class_1857 implements class_1856.class_1859 {
		private final class_1799 field_9021;

		private class_1857(class_1799 arg) {
			this.field_9021 = arg;
		}

		@Override
		public Collection<class_1799> method_8108() {
			return Collections.singleton(this.field_9021);
		}

		@Override
		public JsonObject method_8109() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("item", class_2378.field_11142.method_10221(this.field_9021.method_7909()).toString());
			return jsonObject;
		}
	}

	static class class_1858 implements class_1856.class_1859 {
		private final class_3494<class_1792> field_9022;

		private class_1858(class_3494<class_1792> arg) {
			this.field_9022 = arg;
		}

		@Override
		public Collection<class_1799> method_8108() {
			List<class_1799> list = Lists.<class_1799>newArrayList();

			for (class_1792 lv : this.field_9022.method_15138()) {
				list.add(new class_1799(lv));
			}

			return list;
		}

		@Override
		public JsonObject method_8109() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("tag", this.field_9022.method_15143().toString());
			return jsonObject;
		}
	}

	interface class_1859 {
		Collection<class_1799> method_8108();

		JsonObject method_8109();
	}
}
