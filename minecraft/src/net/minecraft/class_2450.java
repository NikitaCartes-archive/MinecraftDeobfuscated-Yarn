package net.minecraft;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2450 {
	private static final Logger field_11397 = LogManager.getLogger();
	private final class_1792 field_11396;
	private final int field_11395;
	private final List<class_1856> field_11394 = Lists.<class_1856>newArrayList();
	private final class_161.class_162 field_11393 = class_161.class_162.method_707();
	private String field_11398;

	public class_2450(class_1935 arg, int i) {
		this.field_11396 = arg.method_8389();
		this.field_11395 = i;
	}

	public static class_2450 method_10447(class_1935 arg) {
		return new class_2450(arg, 1);
	}

	public static class_2450 method_10448(class_1935 arg, int i) {
		return new class_2450(arg, i);
	}

	public class_2450 method_10446(class_3494<class_1792> arg) {
		return this.method_10451(class_1856.method_8106(arg));
	}

	public class_2450 method_10454(class_1935 arg) {
		return this.method_10449(arg, 1);
	}

	public class_2450 method_10449(class_1935 arg, int i) {
		for (int j = 0; j < i; j++) {
			this.method_10451(class_1856.method_8091(arg));
		}

		return this;
	}

	public class_2450 method_10451(class_1856 arg) {
		return this.method_10453(arg, 1);
	}

	public class_2450 method_10453(class_1856 arg, int i) {
		for (int j = 0; j < i; j++) {
			this.field_11394.add(arg);
		}

		return this;
	}

	public class_2450 method_10442(String string, class_184 arg) {
		this.field_11393.method_709(string, arg);
		return this;
	}

	public class_2450 method_10452(String string) {
		this.field_11398 = string;
		return this;
	}

	public void method_10444(Consumer<class_2444> consumer) {
		this.method_10443(consumer, class_2378.field_11142.method_10221(this.field_11396));
	}

	public void method_10450(Consumer<class_2444> consumer, String string) {
		class_2960 lv = class_2378.field_11142.method_10221(this.field_11396);
		if (new class_2960(string).equals(lv)) {
			throw new IllegalStateException("Shapeless Recipe " + string + " should remove its 'save' argument");
		} else {
			this.method_10443(consumer, new class_2960(string));
		}
	}

	public void method_10443(Consumer<class_2444> consumer, class_2960 arg) {
		this.method_10445(arg);
		this.field_11393
			.method_708(new class_2960("recipes/root"))
			.method_709("has_the_recipe", new class_2119.class_2121(arg))
			.method_703(class_170.class_171.method_753(arg))
			.method_704(class_193.field_1257);
		consumer.accept(
			new class_2450.class_2451(
				arg,
				this.field_11396,
				this.field_11395,
				this.field_11398 == null ? "" : this.field_11398,
				this.field_11394,
				this.field_11393,
				new class_2960(arg.method_12836(), "recipes/" + this.field_11396.method_7859().method_7751() + "/" + arg.method_12832())
			)
		);
	}

	private void method_10445(class_2960 arg) {
		if (this.field_11393.method_710().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + arg);
		}
	}

	public static class class_2451 implements class_2444 {
		private final class_2960 field_11402;
		private final class_1792 field_11403;
		private final int field_11400;
		private final String field_11399;
		private final List<class_1856> field_11404;
		private final class_161.class_162 field_11401;
		private final class_2960 field_11405;

		public class_2451(class_2960 arg, class_1792 arg2, int i, String string, List<class_1856> list, class_161.class_162 arg3, class_2960 arg4) {
			this.field_11402 = arg;
			this.field_11403 = arg2;
			this.field_11400 = i;
			this.field_11399 = string;
			this.field_11404 = list;
			this.field_11401 = arg3;
			this.field_11405 = arg4;
		}

		@Override
		public void method_10416(JsonObject jsonObject) {
			if (!this.field_11399.isEmpty()) {
				jsonObject.addProperty("group", this.field_11399);
			}

			JsonArray jsonArray = new JsonArray();

			for (class_1856 lv : this.field_11404) {
				jsonArray.add(lv.method_8089());
			}

			jsonObject.add("ingredients", jsonArray);
			JsonObject jsonObject2 = new JsonObject();
			jsonObject2.addProperty("item", class_2378.field_11142.method_10221(this.field_11403).toString());
			if (this.field_11400 > 1) {
				jsonObject2.addProperty("count", this.field_11400);
			}

			jsonObject.add("result", jsonObject2);
		}

		@Override
		public class_1865<?> method_17800() {
			return class_1865.field_9031;
		}

		@Override
		public class_2960 method_10417() {
			return this.field_11402;
		}

		@Nullable
		@Override
		public JsonObject method_10415() {
			return this.field_11401.method_698();
		}

		@Nullable
		@Override
		public class_2960 method_10418() {
			return this.field_11405;
		}
	}
}
