package net.minecraft;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public class class_3891 {
	private final class_1792 field_17171;
	private final class_1856 field_17172;
	private final float field_17173;
	private final int field_17174;
	private final class_161.class_162 field_17175 = class_161.class_162.method_707();
	private String field_17176;

	public class_3891(class_1856 arg, class_1935 arg2, float f, int i) {
		this.field_17171 = arg2.method_8389();
		this.field_17172 = arg;
		this.field_17173 = f;
		this.field_17174 = i;
	}

	public static class_3891 method_17176(class_1856 arg, class_1935 arg2, float f, int i) {
		return new class_3891(arg, arg2, f, i);
	}

	public class_3891 method_17177(String string, class_184 arg) {
		this.field_17175.method_709(string, arg);
		return this;
	}

	public void method_17178(Consumer<class_2444> consumer, String string) {
		class_2960 lv = class_2378.field_11142.method_10221(this.field_17171);
		if (new class_2960(string).equals(lv)) {
			throw new IllegalStateException("Blasting Recipe " + string + " should remove its 'save' argument");
		} else {
			this.method_17179(consumer, new class_2960(string));
		}
	}

	public void method_17179(Consumer<class_2444> consumer, class_2960 arg) {
		this.method_17180(arg);
		this.field_17175
			.method_708(new class_2960("recipes/root"))
			.method_709("has_the_recipe", new class_2119.class_2121(arg))
			.method_703(class_170.class_171.method_753(arg))
			.method_704(class_193.field_1257);
		consumer.accept(
			new class_3891.class_3892(
				arg,
				this.field_17176 == null ? "" : this.field_17176,
				this.field_17172,
				this.field_17171,
				this.field_17173,
				this.field_17174,
				this.field_17175,
				new class_2960(arg.method_12836(), "recipes/" + this.field_17171.method_7859().method_7751() + "/" + arg.method_12832())
			)
		);
	}

	private void method_17180(class_2960 arg) {
		if (this.field_17175.method_710().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + arg);
		}
	}

	public static class class_3892 implements class_2444 {
		private final class_2960 field_17177;
		private final String field_17178;
		private final class_1856 field_17179;
		private final class_1792 field_17180;
		private final float field_17181;
		private final int field_17182;
		private final class_161.class_162 field_17183;
		private final class_2960 field_17184;

		public class_3892(class_2960 arg, String string, class_1856 arg2, class_1792 arg3, float f, int i, class_161.class_162 arg4, class_2960 arg5) {
			this.field_17177 = arg;
			this.field_17178 = string;
			this.field_17179 = arg2;
			this.field_17180 = arg3;
			this.field_17181 = f;
			this.field_17182 = i;
			this.field_17183 = arg4;
			this.field_17184 = arg5;
		}

		@Override
		public JsonObject method_10416() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("type", "blasting");
			if (!this.field_17178.isEmpty()) {
				jsonObject.addProperty("group", this.field_17178);
			}

			jsonObject.add("ingredient", this.field_17179.method_8089());
			jsonObject.addProperty("result", class_2378.field_11142.method_10221(this.field_17180).toString());
			jsonObject.addProperty("experience", this.field_17181);
			jsonObject.addProperty("cookingtime", this.field_17182);
			return jsonObject;
		}

		@Override
		public class_2960 method_10417() {
			return this.field_17177;
		}

		@Nullable
		@Override
		public JsonObject method_10415() {
			return this.field_17183.method_698();
		}

		@Nullable
		@Override
		public class_2960 method_10418() {
			return this.field_17184;
		}
	}
}
