package net.minecraft;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public class class_3981 {
	private final class_1792 field_17690;
	private final class_1856 field_17691;
	private final int field_17692;
	private final class_161.class_162 field_17693 = class_161.class_162.method_707();
	private String field_17694;
	private final class_1865<?> field_17695;

	public class_3981(class_1865<?> arg, class_1856 arg2, class_1935 arg3, int i) {
		this.field_17695 = arg;
		this.field_17690 = arg3.method_8389();
		this.field_17691 = arg2;
		this.field_17692 = i;
	}

	public static class_3981 method_17968(class_1856 arg, class_1935 arg2) {
		return new class_3981(class_1865.field_17640, arg, arg2, 1);
	}

	public static class_3981 method_17969(class_1856 arg, class_1935 arg2, int i) {
		return new class_3981(class_1865.field_17640, arg, arg2, i);
	}

	public class_3981 method_17970(String string, class_184 arg) {
		this.field_17693.method_709(string, arg);
		return this;
	}

	public void method_17971(Consumer<class_2444> consumer, String string) {
		class_2960 lv = class_2378.field_11142.method_10221(this.field_17690);
		if (new class_2960(string).equals(lv)) {
			throw new IllegalStateException("Single Item Recipe " + string + " should remove its 'save' argument");
		} else {
			this.method_17972(consumer, new class_2960(string));
		}
	}

	public void method_17972(Consumer<class_2444> consumer, class_2960 arg) {
		this.method_17973(arg);
		this.field_17693
			.method_708(new class_2960("recipes/root"))
			.method_709("has_the_recipe", new class_2119.class_2121(arg))
			.method_703(class_170.class_171.method_753(arg))
			.method_704(class_193.field_1257);
		consumer.accept(
			new class_3981.class_3982(
				arg,
				this.field_17695,
				this.field_17694 == null ? "" : this.field_17694,
				this.field_17691,
				this.field_17690,
				this.field_17692,
				this.field_17693,
				new class_2960(arg.method_12836(), "recipes/" + this.field_17690.method_7859().method_7751() + "/" + arg.method_12832())
			)
		);
	}

	private void method_17973(class_2960 arg) {
		if (this.field_17693.method_710().isEmpty()) {
			throw new IllegalStateException("No way of obtaining recipe " + arg);
		}
	}

	public static class class_3982 implements class_2444 {
		private final class_2960 field_17696;
		private final String field_17697;
		private final class_1856 field_17698;
		private final class_1792 field_17699;
		private final int field_17700;
		private final class_161.class_162 field_17701;
		private final class_2960 field_17702;
		private final class_1865<?> field_17703;

		public class_3982(class_2960 arg, class_1865<?> arg2, String string, class_1856 arg3, class_1792 arg4, int i, class_161.class_162 arg5, class_2960 arg6) {
			this.field_17696 = arg;
			this.field_17703 = arg2;
			this.field_17697 = string;
			this.field_17698 = arg3;
			this.field_17699 = arg4;
			this.field_17700 = i;
			this.field_17701 = arg5;
			this.field_17702 = arg6;
		}

		@Override
		public void method_10416(JsonObject jsonObject) {
			if (!this.field_17697.isEmpty()) {
				jsonObject.addProperty("group", this.field_17697);
			}

			jsonObject.add("ingredient", this.field_17698.method_8089());
			jsonObject.addProperty("result", class_2378.field_11142.method_10221(this.field_17699).toString());
			jsonObject.addProperty("count", this.field_17700);
		}

		@Override
		public class_2960 method_10417() {
			return this.field_17696;
		}

		@Override
		public class_1865<?> method_17800() {
			return this.field_17703;
		}

		@Nullable
		@Override
		public JsonObject method_10415() {
			return this.field_17701.method_698();
		}

		@Nullable
		@Override
		public class_2960 method_10418() {
			return this.field_17702;
		}
	}
}
