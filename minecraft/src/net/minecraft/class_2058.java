package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class class_2058 implements class_179<class_2058.class_2060> {
	private static final class_2960 field_9617 = new class_2960("fishing_rod_hooked");
	private final Map<class_2985, class_2058.class_2059> field_9618 = Maps.<class_2985, class_2058.class_2059>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9617;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2058.class_2060> arg2) {
		class_2058.class_2059 lv = (class_2058.class_2059)this.field_9618.get(arg);
		if (lv == null) {
			lv = new class_2058.class_2059(arg);
			this.field_9618.put(arg, lv);
		}

		lv.method_8943(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2058.class_2060> arg2) {
		class_2058.class_2059 lv = (class_2058.class_2059)this.field_9618.get(arg);
		if (lv != null) {
			lv.method_8945(arg2);
			if (lv.method_8944()) {
				this.field_9618.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9618.remove(arg);
	}

	public class_2058.class_2060 method_8941(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2073 lv = class_2073.method_8969(jsonObject.get("rod"));
		class_2048 lv2 = class_2048.method_8913(jsonObject.get("entity"));
		class_2073 lv3 = class_2073.method_8969(jsonObject.get("item"));
		return new class_2058.class_2060(lv, lv2, lv3);
	}

	public void method_8939(class_3222 arg, class_1799 arg2, class_1536 arg3, Collection<class_1799> collection) {
		class_2058.class_2059 lv = (class_2058.class_2059)this.field_9618.get(arg.method_14236());
		if (lv != null) {
			lv.method_8942(arg, arg2, arg3, collection);
		}
	}

	static class class_2059 {
		private final class_2985 field_9620;
		private final Set<class_179.class_180<class_2058.class_2060>> field_9619 = Sets.<class_179.class_180<class_2058.class_2060>>newHashSet();

		public class_2059(class_2985 arg) {
			this.field_9620 = arg;
		}

		public boolean method_8944() {
			return this.field_9619.isEmpty();
		}

		public void method_8943(class_179.class_180<class_2058.class_2060> arg) {
			this.field_9619.add(arg);
		}

		public void method_8945(class_179.class_180<class_2058.class_2060> arg) {
			this.field_9619.remove(arg);
		}

		public void method_8942(class_3222 arg, class_1799 arg2, class_1536 arg3, Collection<class_1799> collection) {
			List<class_179.class_180<class_2058.class_2060>> list = null;

			for (class_179.class_180<class_2058.class_2060> lv : this.field_9619) {
				if (lv.method_797().method_8946(arg, arg2, arg3, collection)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2058.class_2060>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2058.class_2060> lvx : list) {
					lvx.method_796(this.field_9620);
				}
			}
		}
	}

	public static class class_2060 extends class_195 {
		private final class_2073 field_9621;
		private final class_2048 field_9622;
		private final class_2073 field_9623;

		public class_2060(class_2073 arg, class_2048 arg2, class_2073 arg3) {
			super(class_2058.field_9617);
			this.field_9621 = arg;
			this.field_9622 = arg2;
			this.field_9623 = arg3;
		}

		public static class_2058.class_2060 method_8947(class_2073 arg, class_2048 arg2, class_2073 arg3) {
			return new class_2058.class_2060(arg, arg2, arg3);
		}

		public boolean method_8946(class_3222 arg, class_1799 arg2, class_1536 arg3, Collection<class_1799> collection) {
			if (!this.field_9621.method_8970(arg2)) {
				return false;
			} else if (!this.field_9622.method_8914(arg, arg3.field_7165)) {
				return false;
			} else {
				if (this.field_9623 != class_2073.field_9640) {
					boolean bl = false;
					if (arg3.field_7165 instanceof class_1542) {
						class_1542 lv = (class_1542)arg3.field_7165;
						if (this.field_9623.method_8970(lv.method_6983())) {
							bl = true;
						}
					}

					for (class_1799 lv2 : collection) {
						if (this.field_9623.method_8970(lv2)) {
							bl = true;
							break;
						}
					}

					if (!bl) {
						return false;
					}
				}

				return true;
			}
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("rod", this.field_9621.method_8971());
			jsonObject.add("entity", this.field_9622.method_8912());
			jsonObject.add("item", this.field_9623.method_8971());
			return jsonObject;
		}
	}
}
