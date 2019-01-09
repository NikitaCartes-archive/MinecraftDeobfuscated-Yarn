package net.minecraft;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public class class_3893 {
	private final class_1792 field_17185;
	private final class_1856 field_17186;
	private final float field_17187;
	private final int field_17188;
	private final class_161.class_162 field_17189 = class_161.class_162.method_707();
	private String field_17190;

	public class_3893(class_1856 arg, class_1935 arg2, float f, int i) {
		this.field_17185 = arg2.method_8389();
		this.field_17186 = arg;
		this.field_17187 = f;
		this.field_17188 = i;
	}

	public static class_3893 method_17181(class_1856 arg, class_1935 arg2, float f, int i) {
		return new class_3893(arg, arg2, f, i);
	}

	public class_3893 method_17182(String string, class_184 arg) {
		this.field_17189.method_709(string, arg);
		return this;
	}

	public void method_17183(Consumer<class_2444> consumer, String string, String string2) {
		class_2960 lv = class_2378.field_11142.method_10221(this.field_17185);
		class_2960 lv2 = new class_2960(string + string2);
		if (lv2.equals(lv)) {
			throw new IllegalStateException("Recipe " + lv2 + " should remove its 'save' argument");
		} else {
			this.method_17185(lv2);
			this.field_17189
				.method_708(new class_2960("recipes/root"))
				.method_709("has_the_recipe", new class_2119.class_2121(lv2))
				.method_703(class_170.class_171.method_753(lv2))
				.method_704(class_193.field_1257);
			consumer.accept(
				new class_3893.class_3894(
					lv2,
					this.field_17190 == null ? "" : this.field_17190,
					this.field_17186,
					this.field_17185,
					this.field_17187,
					this.field_17188,
					this.field_17189,
					new class_2960(lv2.method_12836(), "recipes/" + this.field_17185.method_7859().method_7751() + "/" + lv2.method_12832()),
					string2
				)
			);
		}
	}

	private void method_17185(class_2960 arg) {
		if (this.field_17189.method_710().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + arg);
		}
	}

	public static class class_3894 implements class_2444 {
		private final class_2960 field_17191;
		private final String field_17192;
		private final class_1856 field_17193;
		private final class_1792 field_17194;
		private final float field_17195;
		private final int field_17196;
		private final class_161.class_162 field_17197;
		private final class_2960 field_17198;
		private final String field_17432;

		public class_3894(class_2960 arg, String string, class_1856 arg2, class_1792 arg3, float f, int i, class_161.class_162 arg4, class_2960 arg5, String string2) {
			this.field_17191 = arg;
			this.field_17192 = string;
			this.field_17193 = arg2;
			this.field_17194 = arg3;
			this.field_17195 = f;
			this.field_17196 = i;
			this.field_17197 = arg4;
			this.field_17198 = arg5;
			this.field_17432 = string2;
		}

		@Override
		public JsonObject method_10416() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("type", this.field_17432);
			if (!this.field_17192.isEmpty()) {
				jsonObject.addProperty("group", this.field_17192);
			}

			jsonObject.add("ingredient", this.field_17193.method_8089());
			jsonObject.addProperty("result", class_2378.field_11142.method_10221(this.field_17194).toString());
			jsonObject.addProperty("experience", this.field_17195);
			jsonObject.addProperty("cookingtime", this.field_17196);
			return jsonObject;
		}

		@Override
		public class_2960 method_10417() {
			return this.field_17191;
		}

		@Nullable
		@Override
		public JsonObject method_10415() {
			return this.field_17197.method_698();
		}

		@Nullable
		@Override
		public class_2960 method_10418() {
			return this.field_17198;
		}
	}
}
