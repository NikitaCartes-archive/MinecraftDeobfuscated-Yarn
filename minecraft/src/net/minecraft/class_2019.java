package net.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public class class_2019 {
	public static final class_2019 field_9520 = class_2019.class_2020.method_8844().method_8843();
	private final class_2096.class_2099 field_9523;
	private final class_2096.class_2099 field_9524;
	private final class_2048 field_9521;
	private final Boolean field_9522;
	private final class_2022 field_9525;

	public class_2019() {
		this.field_9523 = class_2096.class_2099.field_9705;
		this.field_9524 = class_2096.class_2099.field_9705;
		this.field_9521 = class_2048.field_9599;
		this.field_9522 = null;
		this.field_9525 = class_2022.field_9533;
	}

	public class_2019(class_2096.class_2099 arg, class_2096.class_2099 arg2, class_2048 arg3, @Nullable Boolean boolean_, class_2022 arg4) {
		this.field_9523 = arg;
		this.field_9524 = arg2;
		this.field_9521 = arg3;
		this.field_9522 = boolean_;
		this.field_9525 = arg4;
	}

	public boolean method_8838(class_3222 arg, class_1282 arg2, float f, float g, boolean bl) {
		if (this == field_9520) {
			return true;
		} else if (!this.field_9523.method_9047(f)) {
			return false;
		} else if (!this.field_9524.method_9047(g)) {
			return false;
		} else if (!this.field_9521.method_8914(arg, arg2.method_5529())) {
			return false;
		} else {
			return this.field_9522 != null && this.field_9522 != bl ? false : this.field_9525.method_8847(arg, arg2);
		}
	}

	public static class_2019 method_8839(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "damage");
			class_2096.class_2099 lv = class_2096.class_2099.method_9051(jsonObject.get("dealt"));
			class_2096.class_2099 lv2 = class_2096.class_2099.method_9051(jsonObject.get("taken"));
			Boolean boolean_ = jsonObject.has("blocked") ? class_3518.method_15270(jsonObject, "blocked") : null;
			class_2048 lv3 = class_2048.method_8913(jsonObject.get("source_entity"));
			class_2022 lv4 = class_2022.method_8846(jsonObject.get("type"));
			return new class_2019(lv, lv2, lv3, boolean_, lv4);
		} else {
			return field_9520;
		}
	}

	public JsonElement method_8840() {
		if (this == field_9520) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("dealt", this.field_9523.method_9036());
			jsonObject.add("taken", this.field_9524.method_9036());
			jsonObject.add("source_entity", this.field_9521.method_8912());
			jsonObject.add("type", this.field_9525.method_8848());
			if (this.field_9522 != null) {
				jsonObject.addProperty("blocked", this.field_9522);
			}

			return jsonObject;
		}
	}

	public static class class_2020 {
		private class_2096.class_2099 field_9530 = class_2096.class_2099.field_9705;
		private class_2096.class_2099 field_9527 = class_2096.class_2099.field_9705;
		private class_2048 field_9528 = class_2048.field_9599;
		private Boolean field_9526;
		private class_2022 field_9529 = class_2022.field_9533;

		public static class_2019.class_2020 method_8844() {
			return new class_2019.class_2020();
		}

		public class_2019.class_2020 method_8841(Boolean boolean_) {
			this.field_9526 = boolean_;
			return this;
		}

		public class_2019.class_2020 method_8842(class_2022.class_2023 arg) {
			this.field_9529 = arg.method_8851();
			return this;
		}

		public class_2019 method_8843() {
			return new class_2019(this.field_9530, this.field_9527, this.field_9528, this.field_9526, this.field_9529);
		}
	}
}
