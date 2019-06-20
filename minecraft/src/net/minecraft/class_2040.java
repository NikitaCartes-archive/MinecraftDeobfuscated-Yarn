package net.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public class class_2040 {
	public static final class_2040 field_9581 = new class_2040.class_2041().method_8899();
	@Nullable
	private final Boolean field_9580;
	@Nullable
	private final Boolean field_9582;
	@Nullable
	private final Boolean field_9579;
	@Nullable
	private final Boolean field_9578;
	@Nullable
	private final Boolean field_9583;

	public class_2040(@Nullable Boolean boolean_, @Nullable Boolean boolean2, @Nullable Boolean boolean3, @Nullable Boolean boolean4, @Nullable Boolean boolean5) {
		this.field_9580 = boolean_;
		this.field_9582 = boolean2;
		this.field_9579 = boolean3;
		this.field_9578 = boolean4;
		this.field_9583 = boolean5;
	}

	public boolean method_8892(class_1297 arg) {
		if (this.field_9580 != null && arg.method_5809() != this.field_9580) {
			return false;
		} else if (this.field_9582 != null && arg.method_5715() != this.field_9582) {
			return false;
		} else if (this.field_9579 != null && arg.method_5624() != this.field_9579) {
			return false;
		} else {
			return this.field_9578 != null && arg.method_5681() != this.field_9578
				? false
				: this.field_9583 == null || !(arg instanceof class_1309) || ((class_1309)arg).method_6109() == this.field_9583;
		}
	}

	@Nullable
	private static Boolean method_8895(JsonObject jsonObject, String string) {
		return jsonObject.has(string) ? class_3518.method_15270(jsonObject, string) : null;
	}

	public static class_2040 method_8893(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "entity flags");
			Boolean boolean_ = method_8895(jsonObject, "is_on_fire");
			Boolean boolean2 = method_8895(jsonObject, "is_sneaking");
			Boolean boolean3 = method_8895(jsonObject, "is_sprinting");
			Boolean boolean4 = method_8895(jsonObject, "is_swimming");
			Boolean boolean5 = method_8895(jsonObject, "is_baby");
			return new class_2040(boolean_, boolean2, boolean3, boolean4, boolean5);
		} else {
			return field_9581;
		}
	}

	private void method_8896(JsonObject jsonObject, String string, @Nullable Boolean boolean_) {
		if (boolean_ != null) {
			jsonObject.addProperty(string, boolean_);
		}
	}

	public JsonElement method_8894() {
		if (this == field_9581) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			this.method_8896(jsonObject, "is_on_fire", this.field_9580);
			this.method_8896(jsonObject, "is_sneaking", this.field_9582);
			this.method_8896(jsonObject, "is_sprinting", this.field_9579);
			this.method_8896(jsonObject, "is_swimming", this.field_9578);
			this.method_8896(jsonObject, "is_baby", this.field_9583);
			return jsonObject;
		}
	}

	public static class class_2041 {
		@Nullable
		private Boolean field_9587;
		@Nullable
		private Boolean field_9586;
		@Nullable
		private Boolean field_9588;
		@Nullable
		private Boolean field_9585;
		@Nullable
		private Boolean field_9584;

		public static class_2040.class_2041 method_8897() {
			return new class_2040.class_2041();
		}

		public class_2040.class_2041 method_8898(@Nullable Boolean boolean_) {
			this.field_9587 = boolean_;
			return this;
		}

		public class_2040 method_8899() {
			return new class_2040(this.field_9587, this.field_9586, this.field_9588, this.field_9585, this.field_9584);
		}
	}
}
