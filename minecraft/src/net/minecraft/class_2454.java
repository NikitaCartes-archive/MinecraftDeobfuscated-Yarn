package net.minecraft;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public class class_2454 {
	private final class_1792 field_11417;
	private final class_1856 field_11418;
	private final float field_11414;
	private final int field_11415;
	private final class_161.class_162 field_11416 = class_161.class_162.method_707();
	private String field_11419;
	private final class_3957<?> field_17599;

	private class_2454(class_1935 arg, class_1856 arg2, float f, int i, class_3957<?> arg3) {
		this.field_11417 = arg.method_8389();
		this.field_11418 = arg2;
		this.field_11414 = f;
		this.field_11415 = i;
		this.field_17599 = arg3;
	}

	public static class_2454 method_17801(class_1856 arg, class_1935 arg2, float f, int i, class_3957<?> arg3) {
		return new class_2454(arg2, arg, f, i, arg3);
	}

	public static class_2454 method_10473(class_1856 arg, class_1935 arg2, float f, int i) {
		return method_17801(arg, arg2, f, i, class_1865.field_17084);
	}

	public static class_2454 method_17802(class_1856 arg, class_1935 arg2, float f, int i) {
		return method_17801(arg, arg2, f, i, class_1865.field_9042);
	}

	public class_2454 method_10469(String string, class_184 arg) {
		this.field_11416.method_709(string, arg);
		return this;
	}

	public void method_10470(Consumer<class_2444> consumer) {
		this.method_10468(consumer, class_2378.field_11142.method_10221(this.field_11417));
	}

	public void method_10472(Consumer<class_2444> consumer, String string) {
		class_2960 lv = class_2378.field_11142.method_10221(this.field_11417);
		class_2960 lv2 = new class_2960(string);
		if (lv2.equals(lv)) {
			throw new IllegalStateException("Recipe " + lv2 + " should remove its 'save' argument");
		} else {
			this.method_10468(consumer, lv2);
		}
	}

	public void method_10468(Consumer<class_2444> consumer, class_2960 arg) {
		this.method_10471(arg);
		this.field_11416
			.method_708(new class_2960("recipes/root"))
			.method_709("has_the_recipe", new class_2119.class_2121(arg))
			.method_703(class_170.class_171.method_753(arg))
			.method_704(class_193.field_1257);
		consumer.accept(
			new class_2454.class_2455(
				arg,
				this.field_11419 == null ? "" : this.field_11419,
				this.field_11418,
				this.field_11417,
				this.field_11414,
				this.field_11415,
				this.field_11416,
				new class_2960(arg.method_12836(), "recipes/" + this.field_11417.method_7859().method_7751() + "/" + arg.method_12832()),
				(class_1865<? extends class_1874>)this.field_17599
			)
		);
	}

	private void method_10471(class_2960 arg) {
		if (this.field_11416.method_710().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + arg);
		}
	}

	public static class class_2455 implements class_2444 {
		private final class_2960 field_11424;
		private final String field_11426;
		private final class_1856 field_11425;
		private final class_1792 field_11428;
		private final float field_11421;
		private final int field_11422;
		private final class_161.class_162 field_11423;
		private final class_2960 field_11427;
		private final class_1865<? extends class_1874> field_17600;

		public class_2455(
			class_2960 arg,
			String string,
			class_1856 arg2,
			class_1792 arg3,
			float f,
			int i,
			class_161.class_162 arg4,
			class_2960 arg5,
			class_1865<? extends class_1874> arg6
		) {
			this.field_11424 = arg;
			this.field_11426 = string;
			this.field_11425 = arg2;
			this.field_11428 = arg3;
			this.field_11421 = f;
			this.field_11422 = i;
			this.field_11423 = arg4;
			this.field_11427 = arg5;
			this.field_17600 = arg6;
		}

		@Override
		public void method_10416(JsonObject jsonObject) {
			if (!this.field_11426.isEmpty()) {
				jsonObject.addProperty("group", this.field_11426);
			}

			jsonObject.add("ingredient", this.field_11425.method_8089());
			jsonObject.addProperty("result", class_2378.field_11142.method_10221(this.field_11428).toString());
			jsonObject.addProperty("experience", this.field_11421);
			jsonObject.addProperty("cookingtime", this.field_11422);
		}

		@Override
		public class_1865<?> method_17800() {
			return this.field_17600;
		}

		@Override
		public class_2960 method_10417() {
			return this.field_11424;
		}

		@Nullable
		@Override
		public JsonObject method_10415() {
			return this.field_11423.method_698();
		}

		@Nullable
		@Override
		public class_2960 method_10418() {
			return this.field_11427;
		}
	}
}
