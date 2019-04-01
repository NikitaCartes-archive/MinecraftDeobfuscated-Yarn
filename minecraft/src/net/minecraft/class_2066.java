package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class class_2066 implements class_179<class_2066.class_2068> {
	private static final class_2960 field_9625 = new class_2960("inventory_changed");
	private final Map<class_2985, class_2066.class_2067> field_9626 = Maps.<class_2985, class_2066.class_2067>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9625;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2066.class_2068> arg2) {
		class_2066.class_2067 lv = (class_2066.class_2067)this.field_9626.get(arg);
		if (lv == null) {
			lv = new class_2066.class_2067(arg);
			this.field_9626.put(arg, lv);
		}

		lv.method_8953(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2066.class_2068> arg2) {
		class_2066.class_2067 lv = (class_2066.class_2067)this.field_9626.get(arg);
		if (lv != null) {
			lv.method_8955(arg2);
			if (lv.method_8954()) {
				this.field_9626.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9626.remove(arg);
	}

	public class_2066.class_2068 method_8952(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		JsonObject jsonObject2 = class_3518.method_15281(jsonObject, "slots", new JsonObject());
		class_2096.class_2100 lv = class_2096.class_2100.method_9056(jsonObject2.get("occupied"));
		class_2096.class_2100 lv2 = class_2096.class_2100.method_9056(jsonObject2.get("full"));
		class_2096.class_2100 lv3 = class_2096.class_2100.method_9056(jsonObject2.get("empty"));
		class_2073[] lvs = class_2073.method_8972(jsonObject.get("items"));
		return new class_2066.class_2068(lv, lv2, lv3, lvs);
	}

	public void method_8950(class_3222 arg, class_1661 arg2) {
		class_2066.class_2067 lv = (class_2066.class_2067)this.field_9626.get(arg.method_14236());
		if (lv != null) {
			lv.method_8956(arg2);
		}
	}

	static class class_2067 {
		private final class_2985 field_9628;
		private final Set<class_179.class_180<class_2066.class_2068>> field_9627 = Sets.<class_179.class_180<class_2066.class_2068>>newHashSet();

		public class_2067(class_2985 arg) {
			this.field_9628 = arg;
		}

		public boolean method_8954() {
			return this.field_9627.isEmpty();
		}

		public void method_8953(class_179.class_180<class_2066.class_2068> arg) {
			this.field_9627.add(arg);
		}

		public void method_8955(class_179.class_180<class_2066.class_2068> arg) {
			this.field_9627.remove(arg);
		}

		public void method_8956(class_1661 arg) {
			List<class_179.class_180<class_2066.class_2068>> list = null;

			for (class_179.class_180<class_2066.class_2068> lv : this.field_9627) {
				if (lv.method_797().method_8958(arg)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2066.class_2068>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2066.class_2068> lvx : list) {
					lvx.method_796(this.field_9628);
				}
			}
		}
	}

	public static class class_2068 extends class_195 {
		private final class_2096.class_2100 field_9629;
		private final class_2096.class_2100 field_9630;
		private final class_2096.class_2100 field_9631;
		private final class_2073[] field_9632;

		public class_2068(class_2096.class_2100 arg, class_2096.class_2100 arg2, class_2096.class_2100 arg3, class_2073[] args) {
			super(class_2066.field_9625);
			this.field_9629 = arg;
			this.field_9630 = arg2;
			this.field_9631 = arg3;
			this.field_9632 = args;
		}

		public static class_2066.class_2068 method_8957(class_2073... args) {
			return new class_2066.class_2068(class_2096.class_2100.field_9708, class_2096.class_2100.field_9708, class_2096.class_2100.field_9708, args);
		}

		public static class_2066.class_2068 method_8959(class_1935... args) {
			class_2073[] lvs = new class_2073[args.length];

			for (int i = 0; i < args.length; i++) {
				lvs[i] = new class_2073(
					null, args[i].method_8389(), class_2096.class_2100.field_9708, class_2096.class_2100.field_9708, new class_2035[0], null, class_2105.field_9716
				);
			}

			return method_8957(lvs);
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			if (!this.field_9629.method_9041() || !this.field_9630.method_9041() || !this.field_9631.method_9041()) {
				JsonObject jsonObject2 = new JsonObject();
				jsonObject2.add("occupied", this.field_9629.method_9036());
				jsonObject2.add("full", this.field_9630.method_9036());
				jsonObject2.add("empty", this.field_9631.method_9036());
				jsonObject.add("slots", jsonObject2);
			}

			if (this.field_9632.length > 0) {
				JsonArray jsonArray = new JsonArray();

				for (class_2073 lv : this.field_9632) {
					jsonArray.add(lv.method_8971());
				}

				jsonObject.add("items", jsonArray);
			}

			return jsonObject;
		}

		public boolean method_8958(class_1661 arg) {
			int i = 0;
			int j = 0;
			int k = 0;
			List<class_2073> list = Lists.<class_2073>newArrayList(this.field_9632);

			for (int l = 0; l < arg.method_5439(); l++) {
				class_1799 lv = arg.method_5438(l);
				if (lv.method_7960()) {
					j++;
				} else {
					k++;
					if (lv.method_7947() >= lv.method_7914()) {
						i++;
					}

					Iterator<class_2073> iterator = list.iterator();

					while (iterator.hasNext()) {
						class_2073 lv2 = (class_2073)iterator.next();
						if (lv2.method_8970(lv)) {
							iterator.remove();
						}
					}
				}
			}

			if (!this.field_9630.method_9054(i)) {
				return false;
			} else if (!this.field_9631.method_9054(j)) {
				return false;
			} else {
				return !this.field_9629.method_9054(k) ? false : list.isEmpty();
			}
		}
	}
}
