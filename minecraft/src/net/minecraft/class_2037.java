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

public class class_2037 implements class_179<class_2037.class_2039> {
	private static final class_2960 field_9572 = new class_2960("enter_block");
	private final Map<class_2985, class_2037.class_2038> field_9573 = Maps.<class_2985, class_2037.class_2038>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9572;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2037.class_2039> arg2) {
		class_2037.class_2038 lv = (class_2037.class_2038)this.field_9573.get(arg);
		if (lv == null) {
			lv = new class_2037.class_2038(arg);
			this.field_9573.put(arg, lv);
		}

		lv.method_8886(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2037.class_2039> arg2) {
		class_2037.class_2038 lv = (class_2037.class_2038)this.field_9573.get(arg);
		if (lv != null) {
			lv.method_8889(arg2);
			if (lv.method_8887()) {
				this.field_9573.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9573.remove(arg);
	}

	public class_2037.class_2039 method_8883(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2248 lv = null;
		if (jsonObject.has("block")) {
			class_2960 lv2 = new class_2960(class_3518.method_15265(jsonObject, "block"));
			lv = (class_2248)class_2378.field_11146.method_17966(lv2).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + lv2 + "'"));
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

		return new class_2037.class_2039(lv, map);
	}

	public void method_8885(class_3222 arg, class_2680 arg2) {
		class_2037.class_2038 lv = (class_2037.class_2038)this.field_9573.get(arg.method_14236());
		if (lv != null) {
			lv.method_8888(arg2);
		}
	}

	static class class_2038 {
		private final class_2985 field_9575;
		private final Set<class_179.class_180<class_2037.class_2039>> field_9574 = Sets.<class_179.class_180<class_2037.class_2039>>newHashSet();

		public class_2038(class_2985 arg) {
			this.field_9575 = arg;
		}

		public boolean method_8887() {
			return this.field_9574.isEmpty();
		}

		public void method_8886(class_179.class_180<class_2037.class_2039> arg) {
			this.field_9574.add(arg);
		}

		public void method_8889(class_179.class_180<class_2037.class_2039> arg) {
			this.field_9574.remove(arg);
		}

		public void method_8888(class_2680 arg) {
			List<class_179.class_180<class_2037.class_2039>> list = null;

			for (class_179.class_180<class_2037.class_2039> lv : this.field_9574) {
				if (lv.method_797().method_8891(arg)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2037.class_2039>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2037.class_2039> lvx : list) {
					lvx.method_796(this.field_9575);
				}
			}
		}
	}

	public static class class_2039 extends class_195 {
		private final class_2248 field_9576;
		private final Map<class_2769<?>, Object> field_9577;

		public class_2039(@Nullable class_2248 arg, @Nullable Map<class_2769<?>, Object> map) {
			super(class_2037.field_9572);
			this.field_9576 = arg;
			this.field_9577 = map;
		}

		public static class_2037.class_2039 method_8890(class_2248 arg) {
			return new class_2037.class_2039(arg, null);
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			if (this.field_9576 != null) {
				jsonObject.addProperty("block", class_2378.field_11146.method_10221(this.field_9576).toString());
				if (this.field_9577 != null && !this.field_9577.isEmpty()) {
					JsonObject jsonObject2 = new JsonObject();

					for (Entry<class_2769<?>, ?> entry : this.field_9577.entrySet()) {
						jsonObject2.addProperty(((class_2769)entry.getKey()).method_11899(), class_156.method_650((class_2769)entry.getKey(), entry.getValue()));
					}

					jsonObject.add("state", jsonObject2);
				}
			}

			return jsonObject;
		}

		public boolean method_8891(class_2680 arg) {
			if (this.field_9576 != null && arg.method_11614() != this.field_9576) {
				return false;
			} else {
				if (this.field_9577 != null) {
					for (Entry<class_2769<?>, Object> entry : this.field_9577.entrySet()) {
						if (arg.method_11654((class_2769)entry.getKey()) != entry.getValue()) {
							return false;
						}
					}
				}

				return true;
			}
		}
	}
}
