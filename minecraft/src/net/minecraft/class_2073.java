package net.minecraft;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class class_2073 {
	public static final class_2073 field_9640 = new class_2073();
	@Nullable
	private final class_3494<class_1792> field_9643;
	@Nullable
	private final class_1792 field_9644;
	private final class_2096.class_2100 field_9641;
	private final class_2096.class_2100 field_9646;
	private final class_2035[] field_9647;
	@Nullable
	private final class_1842 field_9642;
	private final class_2105 field_9645;

	public class_2073() {
		this.field_9643 = null;
		this.field_9644 = null;
		this.field_9642 = null;
		this.field_9641 = class_2096.class_2100.field_9708;
		this.field_9646 = class_2096.class_2100.field_9708;
		this.field_9647 = new class_2035[0];
		this.field_9645 = class_2105.field_9716;
	}

	public class_2073(
		@Nullable class_3494<class_1792> arg,
		@Nullable class_1792 arg2,
		class_2096.class_2100 arg3,
		class_2096.class_2100 arg4,
		class_2035[] args,
		@Nullable class_1842 arg5,
		class_2105 arg6
	) {
		this.field_9643 = arg;
		this.field_9644 = arg2;
		this.field_9641 = arg3;
		this.field_9646 = arg4;
		this.field_9647 = args;
		this.field_9642 = arg5;
		this.field_9645 = arg6;
	}

	public boolean method_8970(class_1799 arg) {
		if (this == field_9640) {
			return true;
		} else if (this.field_9643 != null && !this.field_9643.method_15141(arg.method_7909())) {
			return false;
		} else if (this.field_9644 != null && arg.method_7909() != this.field_9644) {
			return false;
		} else if (!this.field_9641.method_9054(arg.method_7947())) {
			return false;
		} else if (!this.field_9646.method_9041() && !arg.method_7963()) {
			return false;
		} else if (!this.field_9646.method_9054(arg.method_7936() - arg.method_7919())) {
			return false;
		} else if (!this.field_9645.method_9074(arg)) {
			return false;
		} else {
			Map<class_1887, Integer> map = class_1890.method_8222(arg);

			for (int i = 0; i < this.field_9647.length; i++) {
				if (!this.field_9647[i].method_8880(map)) {
					return false;
				}
			}

			class_1842 lv = class_1844.method_8063(arg);
			return this.field_9642 == null || this.field_9642 == lv;
		}
	}

	public static class_2073 method_8969(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = class_3518.method_15295(jsonElement, "item");
			class_2096.class_2100 lv = class_2096.class_2100.method_9056(jsonObject.get("count"));
			class_2096.class_2100 lv2 = class_2096.class_2100.method_9056(jsonObject.get("durability"));
			if (jsonObject.has("data")) {
				throw new JsonParseException("Disallowed data tag found");
			} else {
				class_2105 lv3 = class_2105.method_9073(jsonObject.get("nbt"));
				class_1792 lv4 = null;
				if (jsonObject.has("item")) {
					class_2960 lv5 = new class_2960(class_3518.method_15265(jsonObject, "item"));
					lv4 = class_2378.field_11142.method_10223(lv5);
					if (lv4 == null) {
						throw new JsonSyntaxException("Unknown item id '" + lv5 + "'");
					}
				}

				class_3494<class_1792> lv6 = null;
				if (jsonObject.has("tag")) {
					class_2960 lv7 = new class_2960(class_3518.method_15265(jsonObject, "tag"));
					lv6 = class_3489.method_15106().method_15193(lv7);
					if (lv6 == null) {
						throw new JsonSyntaxException("Unknown item tag '" + lv7 + "'");
					}
				}

				class_2035[] lvs = class_2035.method_8879(jsonObject.get("enchantments"));
				class_1842 lv8 = null;
				if (jsonObject.has("potion")) {
					class_2960 lv9 = new class_2960(class_3518.method_15265(jsonObject, "potion"));
					if (!class_2378.field_11143.method_10250(lv9)) {
						throw new JsonSyntaxException("Unknown potion '" + lv9 + "'");
					}

					lv8 = class_2378.field_11143.method_10223(lv9);
				}

				return new class_2073(lv6, lv4, lv, lv2, lvs, lv8, lv3);
			}
		} else {
			return field_9640;
		}
	}

	public JsonElement method_8971() {
		if (this == field_9640) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			if (this.field_9644 != null) {
				jsonObject.addProperty("item", class_2378.field_11142.method_10221(this.field_9644).toString());
			}

			if (this.field_9643 != null) {
				jsonObject.addProperty("tag", this.field_9643.method_15143().toString());
			}

			jsonObject.add("count", this.field_9641.method_9036());
			jsonObject.add("durability", this.field_9646.method_9036());
			jsonObject.add("nbt", this.field_9645.method_9075());
			if (this.field_9647.length > 0) {
				JsonArray jsonArray = new JsonArray();

				for (class_2035 lv : this.field_9647) {
					jsonArray.add(lv.method_8881());
				}

				jsonObject.add("enchantments", jsonArray);
			}

			if (this.field_9642 != null) {
				jsonObject.addProperty("potion", class_2378.field_11143.method_10221(this.field_9642).toString());
			}

			return jsonObject;
		}
	}

	public static class_2073[] method_8972(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonArray jsonArray = class_3518.method_15252(jsonElement, "items");
			class_2073[] lvs = new class_2073[jsonArray.size()];

			for (int i = 0; i < lvs.length; i++) {
				lvs[i] = method_8969(jsonArray.get(i));
			}

			return lvs;
		} else {
			return new class_2073[0];
		}
	}

	public static class class_2074 {
		private final List<class_2035> field_9649 = Lists.<class_2035>newArrayList();
		@Nullable
		private class_1792 field_9650;
		@Nullable
		private class_3494<class_1792> field_9652;
		private class_2096.class_2100 field_9648 = class_2096.class_2100.field_9708;
		private class_2096.class_2100 field_9653 = class_2096.class_2100.field_9708;
		@Nullable
		private class_1842 field_9651;
		private class_2105 field_9654 = class_2105.field_9716;

		private class_2074() {
		}

		public static class_2073.class_2074 method_8973() {
			return new class_2073.class_2074();
		}

		public class_2073.class_2074 method_8977(class_1935 arg) {
			this.field_9650 = arg.method_8389();
			return this;
		}

		public class_2073.class_2074 method_8975(class_3494<class_1792> arg) {
			this.field_9652 = arg;
			return this;
		}

		public class_2073.class_2074 method_8974(class_2096.class_2100 arg) {
			this.field_9648 = arg;
			return this;
		}

		public class_2073.class_2074 method_8978(class_2035 arg) {
			this.field_9649.add(arg);
			return this;
		}

		public class_2073 method_8976() {
			return new class_2073(
				this.field_9652,
				this.field_9650,
				this.field_9648,
				this.field_9653,
				(class_2035[])this.field_9649.toArray(new class_2035[0]),
				this.field_9651,
				this.field_9654
			);
		}
	}
}
