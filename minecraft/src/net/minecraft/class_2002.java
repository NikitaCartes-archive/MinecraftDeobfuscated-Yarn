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

public class class_2002 implements class_179<class_2002.class_2004> {
	private static final class_2960 field_9499 = new class_2960("channeled_lightning");
	private final Map<class_2985, class_2002.class_2003> field_9500 = Maps.<class_2985, class_2002.class_2003>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9499;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2002.class_2004> arg2) {
		class_2002.class_2003 lv = (class_2002.class_2003)this.field_9500.get(arg);
		if (lv == null) {
			lv = new class_2002.class_2003(arg);
			this.field_9500.put(arg, lv);
		}

		lv.method_8804(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2002.class_2004> arg2) {
		class_2002.class_2003 lv = (class_2002.class_2003)this.field_9500.get(arg);
		if (lv != null) {
			lv.method_8807(arg2);
			if (lv.method_8805()) {
				this.field_9500.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9500.remove(arg);
	}

	public class_2002.class_2004 method_8801(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2048[] lvs = class_2048.method_8910(jsonObject.get("victims"));
		return new class_2002.class_2004(lvs);
	}

	public void method_8803(class_3222 arg, Collection<? extends class_1297> collection) {
		class_2002.class_2003 lv = (class_2002.class_2003)this.field_9500.get(arg.method_14236());
		if (lv != null) {
			lv.method_8806(arg, collection);
		}
	}

	static class class_2003 {
		private final class_2985 field_9502;
		private final Set<class_179.class_180<class_2002.class_2004>> field_9501 = Sets.<class_179.class_180<class_2002.class_2004>>newHashSet();

		public class_2003(class_2985 arg) {
			this.field_9502 = arg;
		}

		public boolean method_8805() {
			return this.field_9501.isEmpty();
		}

		public void method_8804(class_179.class_180<class_2002.class_2004> arg) {
			this.field_9501.add(arg);
		}

		public void method_8807(class_179.class_180<class_2002.class_2004> arg) {
			this.field_9501.remove(arg);
		}

		public void method_8806(class_3222 arg, Collection<? extends class_1297> collection) {
			List<class_179.class_180<class_2002.class_2004>> list = null;

			for (class_179.class_180<class_2002.class_2004> lv : this.field_9501) {
				if (lv.method_797().method_8808(arg, collection)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2002.class_2004>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2002.class_2004> lvx : list) {
					lvx.method_796(this.field_9502);
				}
			}
		}
	}

	public static class class_2004 extends class_195 {
		private final class_2048[] field_9503;

		public class_2004(class_2048[] args) {
			super(class_2002.field_9499);
			this.field_9503 = args;
		}

		public static class_2002.class_2004 method_8809(class_2048... args) {
			return new class_2002.class_2004(args);
		}

		public boolean method_8808(class_3222 arg, Collection<? extends class_1297> collection) {
			for (class_2048 lv : this.field_9503) {
				boolean bl = false;

				for (class_1297 lv2 : collection) {
					if (lv.method_8914(arg, lv2)) {
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

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("victims", class_2048.method_8911(this.field_9503));
			return jsonObject;
		}
	}
}
