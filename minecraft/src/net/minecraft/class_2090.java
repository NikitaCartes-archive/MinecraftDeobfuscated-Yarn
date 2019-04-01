package net.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;

public class class_2090 {
	public static final class_2090 field_9685 = new class_2090(
		class_2096.class_2099.field_9705, class_2096.class_2099.field_9705, class_2096.class_2099.field_9705, null, null, null
	);
	private final class_2096.class_2099 field_9682;
	private final class_2096.class_2099 field_9684;
	private final class_2096.class_2099 field_9681;
	@Nullable
	private final class_1959 field_9683;
	@Nullable
	private final class_3195<?> field_9687;
	@Nullable
	private final class_2874 field_9686;

	public class_2090(
		class_2096.class_2099 arg,
		class_2096.class_2099 arg2,
		class_2096.class_2099 arg3,
		@Nullable class_1959 arg4,
		@Nullable class_3195<?> arg5,
		@Nullable class_2874 arg6
	) {
		this.field_9682 = arg;
		this.field_9684 = arg2;
		this.field_9681 = arg3;
		this.field_9683 = arg4;
		this.field_9687 = arg5;
		this.field_9686 = arg6;
	}

	public static class_2090 method_9022(class_1959 arg) {
		return new class_2090(class_2096.class_2099.field_9705, class_2096.class_2099.field_9705, class_2096.class_2099.field_9705, arg, null, null);
	}

	public static class_2090 method_9016(class_2874 arg) {
		return new class_2090(class_2096.class_2099.field_9705, class_2096.class_2099.field_9705, class_2096.class_2099.field_9705, null, null, arg);
	}

	public static class_2090 method_9017(class_3195<?> arg) {
		return new class_2090(class_2096.class_2099.field_9705, class_2096.class_2099.field_9705, class_2096.class_2099.field_9705, null, arg, null);
	}

	public boolean method_9018(class_3218 arg, double d, double e, double f) {
		return this.method_9020(arg, (float)d, (float)e, (float)f);
	}

	public boolean method_9020(class_3218 arg, float f, float g, float h) {
		if (!this.field_9682.method_9047(f)) {
			return false;
		} else if (!this.field_9684.method_9047(g)) {
			return false;
		} else if (!this.field_9681.method_9047(h)) {
			return false;
		} else if (this.field_9686 != null && this.field_9686 != arg.field_9247.method_12460()) {
			return false;
		} else {
			class_2338 lv = new class_2338((double)f, (double)g, (double)h);
			return this.field_9683 != null && this.field_9683 != arg.method_8310(lv) ? false : this.field_9687 == null || this.field_9687.method_14024(arg, lv);
		}
	}

	public JsonElement method_9019() {
		if (this == field_9685) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			if (!this.field_9682.method_9041() || !this.field_9684.method_9041() || !this.field_9681.method_9041()) {
				JsonObject jsonObject2 = new JsonObject();
				jsonObject2.add("x", this.field_9682.method_9036());
				jsonObject2.add("y", this.field_9684.method_9036());
				jsonObject2.add("z", this.field_9681.method_9036());
				jsonObject.add("position", jsonObject2);
			}

			if (this.field_9686 != null) {
				jsonObject.addProperty("dimension", class_2874.method_12485(this.field_9686).toString());
			}

			if (this.field_9687 != null) {
				jsonObject.addProperty("feature", (String)class_3031.field_13557.inverse().get(this.field_9687));
			}

			if (this.field_9683 != null) {
				jsonObject.addProperty("biome", class_2378.field_11153.method_10221(this.field_9683).toString());
			}

			return jsonObject;
		}
	}

	public static class_2090 method_9021(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "location");
			JsonObject jsonObject2 = class_3518.method_15281(jsonObject, "position", new JsonObject());
			class_2096.class_2099 lv = class_2096.class_2099.method_9051(jsonObject2.get("x"));
			class_2096.class_2099 lv2 = class_2096.class_2099.method_9051(jsonObject2.get("y"));
			class_2096.class_2099 lv3 = class_2096.class_2099.method_9051(jsonObject2.get("z"));
			class_2874 lv4 = jsonObject.has("dimension") ? class_2874.method_12483(new class_2960(class_3518.method_15265(jsonObject, "dimension"))) : null;
			class_3195<?> lv5 = jsonObject.has("feature") ? (class_3195)class_3031.field_13557.get(class_3518.method_15265(jsonObject, "feature")) : null;
			class_1959 lv6 = null;
			if (jsonObject.has("biome")) {
				class_2960 lv7 = new class_2960(class_3518.method_15265(jsonObject, "biome"));
				lv6 = (class_1959)class_2378.field_11153.method_17966(lv7).orElseThrow(() -> new JsonSyntaxException("Unknown biome '" + lv7 + "'"));
			}

			return new class_2090(lv, lv2, lv3, lv6, lv5, lv4);
		} else {
			return field_9685;
		}
	}

	public static class class_2091 {
		private class_2096.class_2099 field_9693 = class_2096.class_2099.field_9705;
		private class_2096.class_2099 field_9689 = class_2096.class_2099.field_9705;
		private class_2096.class_2099 field_9692 = class_2096.class_2099.field_9705;
		@Nullable
		private class_1959 field_9690;
		@Nullable
		private class_3195<?> field_9688;
		@Nullable
		private class_2874 field_9691;

		public class_2090.class_2091 method_9024(@Nullable class_1959 arg) {
			this.field_9690 = arg;
			return this;
		}

		public class_2090 method_9023() {
			return new class_2090(this.field_9693, this.field_9689, this.field_9692, this.field_9690, this.field_9688, this.field_9691);
		}
	}
}
