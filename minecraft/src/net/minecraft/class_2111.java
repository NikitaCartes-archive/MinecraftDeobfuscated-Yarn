package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public class class_2111 implements class_179<class_2111.class_2113> {
	private static final class_2960 field_9724 = new class_2960("placed_block");
	private final Map<class_2985, class_2111.class_2112> field_9725 = Maps.<class_2985, class_2111.class_2112>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9724;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2111.class_2113> arg2) {
		class_2111.class_2112 lv = (class_2111.class_2112)this.field_9725.get(arg);
		if (lv == null) {
			lv = new class_2111.class_2112(arg);
			this.field_9725.put(arg, lv);
		}

		lv.method_9090(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2111.class_2113> arg2) {
		class_2111.class_2112 lv = (class_2111.class_2112)this.field_9725.get(arg);
		if (lv != null) {
			lv.method_9093(arg2);
			if (lv.method_9091()) {
				this.field_9725.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9725.remove(arg);
	}

	public class_2111.class_2113 method_9088(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2248 lv = null;
		if (jsonObject.has("block")) {
			class_2960 lv2 = new class_2960(class_3518.method_15265(jsonObject, "block"));
			if (!class_2378.field_11146.method_10250(lv2)) {
				throw new JsonSyntaxException("Unknown block type '" + lv2 + "'");
			}

			lv = class_2378.field_11146.method_10223(lv2);
		}

		Map<class_2769<?>, Object> map = null;
		if (jsonObject.has("state")) {
			if (lv == null) {
				throw new JsonSyntaxException("Can't define block state without a specific block type");
			}

			class_2689<class_2248, class_2680> lv3 = lv.method_9595();

			for (Entry<String, JsonElement> entry : class_3518.method_15296(jsonObject, "state").entrySet()) {
				class_2769<?> lv4 = lv3.method_11663((String)entry.getKey());
				if (lv4 == null) {
					throw new JsonSyntaxException("Unknown block state property '" + (String)entry.getKey() + "' for block '" + class_2378.field_11146.method_10221(lv) + "'");
				}

				String string = class_3518.method_15287((JsonElement)entry.getValue(), (String)entry.getKey());
				Optional<?> optional = lv4.method_11900(string);
				if (!optional.isPresent()) {
					throw new JsonSyntaxException(
						"Invalid block state value '" + string + "' for property '" + (String)entry.getKey() + "' on block '" + class_2378.field_11146.method_10221(lv) + "'"
					);
				}

				if (map == null) {
					map = Maps.<class_2769<?>, Object>newHashMap();
				}

				map.put(lv4, optional.get());
			}
		}

		class_2090 lv5 = class_2090.method_9021(jsonObject.get("location"));
		class_2073 lv6 = class_2073.method_8969(jsonObject.get("item"));
		return new class_2111.class_2113(lv, map, lv5, lv6);
	}

	public void method_9087(class_3222 arg, class_2338 arg2, class_1799 arg3) {
		class_2680 lv = arg.field_6002.method_8320(arg2);
		class_2111.class_2112 lv2 = (class_2111.class_2112)this.field_9725.get(arg.method_14236());
		if (lv2 != null) {
			lv2.method_9092(lv, arg2, arg.method_14220(), arg3);
		}
	}

	static class class_2112 {
		private final class_2985 field_9727;
		private final Set<class_179.class_180<class_2111.class_2113>> field_9726 = Sets.<class_179.class_180<class_2111.class_2113>>newHashSet();

		public class_2112(class_2985 arg) {
			this.field_9727 = arg;
		}

		public boolean method_9091() {
			return this.field_9726.isEmpty();
		}

		public void method_9090(class_179.class_180<class_2111.class_2113> arg) {
			this.field_9726.add(arg);
		}

		public void method_9093(class_179.class_180<class_2111.class_2113> arg) {
			this.field_9726.remove(arg);
		}

		public void method_9092(class_2680 arg, class_2338 arg2, class_3218 arg3, class_1799 arg4) {
			List<class_179.class_180<class_2111.class_2113>> list = null;

			for (class_179.class_180<class_2111.class_2113> lv : this.field_9726) {
				if (lv.method_797().method_9094(arg, arg2, arg3, arg4)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2111.class_2113>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2111.class_2113> lvx : list) {
					lvx.method_796(this.field_9727);
				}
			}
		}
	}

	public static class class_2113 extends class_195 {
		private final class_2248 field_9728;
		private final Map<class_2769<?>, Object> field_9730;
		private final class_2090 field_9729;
		private final class_2073 field_9731;

		public class_2113(@Nullable class_2248 arg, @Nullable Map<class_2769<?>, Object> map, class_2090 arg2, class_2073 arg3) {
			super(class_2111.field_9724);
			this.field_9728 = arg;
			this.field_9730 = map;
			this.field_9729 = arg2;
			this.field_9731 = arg3;
		}

		public static class_2111.class_2113 method_9095(class_2248 arg) {
			return new class_2111.class_2113(arg, null, class_2090.field_9685, class_2073.field_9640);
		}

		public boolean method_9094(class_2680 arg, class_2338 arg2, class_3218 arg3, class_1799 arg4) {
			if (this.field_9728 != null && arg.method_11614() != this.field_9728) {
				return false;
			} else {
				if (this.field_9730 != null) {
					for (Entry<class_2769<?>, Object> entry : this.field_9730.entrySet()) {
						if (arg.method_11654((class_2769)entry.getKey()) != entry.getValue()) {
							return false;
						}
					}
				}

				return !this.field_9729.method_9020(arg3, (float)arg2.method_10263(), (float)arg2.method_10264(), (float)arg2.method_10260())
					? false
					: this.field_9731.method_8970(arg4);
			}
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			if (this.field_9728 != null) {
				jsonObject.addProperty("block", class_2378.field_11146.method_10221(this.field_9728).toString());
			}

			if (this.field_9730 != null) {
				JsonObject jsonObject2 = new JsonObject();

				for (Entry<class_2769<?>, Object> entry : this.field_9730.entrySet()) {
					jsonObject2.addProperty(((class_2769)entry.getKey()).method_11899(), class_156.method_650((class_2769)entry.getKey(), entry.getValue()));
				}

				jsonObject.add("state", jsonObject2);
			}

			jsonObject.add("location", this.field_9729.method_9019());
			jsonObject.add("item", this.field_9731.method_8971());
			return jsonObject;
		}
	}
}
