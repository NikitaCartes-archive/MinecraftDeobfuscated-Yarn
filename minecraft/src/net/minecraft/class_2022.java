package net.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public class class_2022 {
	public static final class_2022 field_9533 = class_2022.class_2023.method_8855().method_8851();
	private final Boolean field_9535;
	private final Boolean field_9536;
	private final Boolean field_9532;
	private final Boolean field_9531;
	private final Boolean field_9537;
	private final Boolean field_9540;
	private final Boolean field_9541;
	private final Boolean field_9538;
	private final class_2048 field_9534;
	private final class_2048 field_9539;

	public class_2022(
		@Nullable Boolean boolean_,
		@Nullable Boolean boolean2,
		@Nullable Boolean boolean3,
		@Nullable Boolean boolean4,
		@Nullable Boolean boolean5,
		@Nullable Boolean boolean6,
		@Nullable Boolean boolean7,
		@Nullable Boolean boolean8,
		class_2048 arg,
		class_2048 arg2
	) {
		this.field_9535 = boolean_;
		this.field_9536 = boolean2;
		this.field_9532 = boolean3;
		this.field_9531 = boolean4;
		this.field_9537 = boolean5;
		this.field_9540 = boolean6;
		this.field_9541 = boolean7;
		this.field_9538 = boolean8;
		this.field_9534 = arg;
		this.field_9539 = arg2;
	}

	public boolean method_8847(class_3222 arg, class_1282 arg2) {
		return this.method_8845(arg.method_14220(), new class_243(arg.field_5987, arg.field_6010, arg.field_6035), arg2);
	}

	public boolean method_8845(class_3218 arg, class_243 arg2, class_1282 arg3) {
		if (this == field_9533) {
			return true;
		} else if (this.field_9535 != null && this.field_9535 != arg3.method_5533()) {
			return false;
		} else if (this.field_9536 != null && this.field_9536 != arg3.method_5535()) {
			return false;
		} else if (this.field_9532 != null && this.field_9532 != arg3.method_5537()) {
			return false;
		} else if (this.field_9531 != null && this.field_9531 != arg3.method_5538()) {
			return false;
		} else if (this.field_9537 != null && this.field_9537 != arg3.method_5504()) {
			return false;
		} else if (this.field_9540 != null && this.field_9540 != arg3.method_5534()) {
			return false;
		} else if (this.field_9541 != null && this.field_9541 != arg3.method_5527()) {
			return false;
		} else if (this.field_9538 != null && this.field_9538 != (arg3 == class_1282.field_5861)) {
			return false;
		} else {
			return !this.field_9534.method_8909(arg, arg2, arg3.method_5526()) ? false : this.field_9539.method_8909(arg, arg2, arg3.method_5529());
		}
	}

	public static class_2022 method_8846(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "damage type");
			Boolean boolean_ = method_8849(jsonObject, "is_projectile");
			Boolean boolean2 = method_8849(jsonObject, "is_explosion");
			Boolean boolean3 = method_8849(jsonObject, "bypasses_armor");
			Boolean boolean4 = method_8849(jsonObject, "bypasses_invulnerability");
			Boolean boolean5 = method_8849(jsonObject, "bypasses_magic");
			Boolean boolean6 = method_8849(jsonObject, "is_fire");
			Boolean boolean7 = method_8849(jsonObject, "is_magic");
			Boolean boolean8 = method_8849(jsonObject, "is_lightning");
			class_2048 lv = class_2048.method_8913(jsonObject.get("direct_entity"));
			class_2048 lv2 = class_2048.method_8913(jsonObject.get("source_entity"));
			return new class_2022(boolean_, boolean2, boolean3, boolean4, boolean5, boolean6, boolean7, boolean8, lv, lv2);
		} else {
			return field_9533;
		}
	}

	@Nullable
	private static Boolean method_8849(JsonObject jsonObject, String string) {
		return jsonObject.has(string) ? class_3518.method_15270(jsonObject, string) : null;
	}

	public JsonElement method_8848() {
		if (this == field_9533) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			this.method_8850(jsonObject, "is_projectile", this.field_9535);
			this.method_8850(jsonObject, "is_explosion", this.field_9536);
			this.method_8850(jsonObject, "bypasses_armor", this.field_9532);
			this.method_8850(jsonObject, "bypasses_invulnerability", this.field_9531);
			this.method_8850(jsonObject, "bypasses_magic", this.field_9537);
			this.method_8850(jsonObject, "is_fire", this.field_9540);
			this.method_8850(jsonObject, "is_magic", this.field_9541);
			this.method_8850(jsonObject, "is_lightning", this.field_9538);
			jsonObject.add("direct_entity", this.field_9534.method_8912());
			jsonObject.add("source_entity", this.field_9539.method_8912());
			return jsonObject;
		}
	}

	private void method_8850(JsonObject jsonObject, String string, @Nullable Boolean boolean_) {
		if (boolean_ != null) {
			jsonObject.addProperty(string, boolean_);
		}
	}

	public static class class_2023 {
		private Boolean field_9547;
		private Boolean field_9546;
		private Boolean field_9548;
		private Boolean field_9543;
		private Boolean field_9542;
		private Boolean field_9549;
		private Boolean field_9550;
		private Boolean field_9551;
		private class_2048 field_9544 = class_2048.field_9599;
		private class_2048 field_9545 = class_2048.field_9599;

		public static class_2022.class_2023 method_8855() {
			return new class_2022.class_2023();
		}

		public class_2022.class_2023 method_8852(Boolean boolean_) {
			this.field_9547 = boolean_;
			return this;
		}

		public class_2022.class_2023 method_8853(Boolean boolean_) {
			this.field_9551 = boolean_;
			return this;
		}

		public class_2022.class_2023 method_8854(class_2048.class_2049 arg) {
			this.field_9544 = arg.method_8920();
			return this;
		}

		public class_2022 method_8851() {
			return new class_2022(
				this.field_9547,
				this.field_9546,
				this.field_9548,
				this.field_9543,
				this.field_9542,
				this.field_9549,
				this.field_9550,
				this.field_9551,
				this.field_9544,
				this.field_9545
			);
		}
	}
}
