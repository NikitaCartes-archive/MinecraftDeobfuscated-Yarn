package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class class_2006 implements class_179<class_2006.class_2008> {
	private static final class_2960 field_9504 = new class_2960("construct_beacon");
	private final Map<class_2985, class_2006.class_2007> field_9505 = Maps.<class_2985, class_2006.class_2007>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9504;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2006.class_2008> arg2) {
		class_2006.class_2007 lv = (class_2006.class_2007)this.field_9505.get(arg);
		if (lv == null) {
			lv = new class_2006.class_2007(arg);
			this.field_9505.put(arg, lv);
		}

		lv.method_8813(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2006.class_2008> arg2) {
		class_2006.class_2007 lv = (class_2006.class_2007)this.field_9505.get(arg);
		if (lv != null) {
			lv.method_8816(arg2);
			if (lv.method_8815()) {
				this.field_9505.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9505.remove(arg);
	}

	public class_2006.class_2008 method_8811(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2096.class_2100 lv = class_2096.class_2100.method_9056(jsonObject.get("level"));
		return new class_2006.class_2008(lv);
	}

	public void method_8812(class_3222 arg, class_2580 arg2) {
		class_2006.class_2007 lv = (class_2006.class_2007)this.field_9505.get(arg.method_14236());
		if (lv != null) {
			lv.method_8814(arg2);
		}
	}

	static class class_2007 {
		private final class_2985 field_9507;
		private final Set<class_179.class_180<class_2006.class_2008>> field_9506 = Sets.<class_179.class_180<class_2006.class_2008>>newHashSet();

		public class_2007(class_2985 arg) {
			this.field_9507 = arg;
		}

		public boolean method_8815() {
			return this.field_9506.isEmpty();
		}

		public void method_8813(class_179.class_180<class_2006.class_2008> arg) {
			this.field_9506.add(arg);
		}

		public void method_8816(class_179.class_180<class_2006.class_2008> arg) {
			this.field_9506.remove(arg);
		}

		public void method_8814(class_2580 arg) {
			List<class_179.class_180<class_2006.class_2008>> list = null;

			for (class_179.class_180<class_2006.class_2008> lv : this.field_9506) {
				if (lv.method_797().method_8817(arg)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2006.class_2008>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2006.class_2008> lvx : list) {
					lvx.method_796(this.field_9507);
				}
			}
		}
	}

	public static class class_2008 extends class_195 {
		private final class_2096.class_2100 field_9508;

		public class_2008(class_2096.class_2100 arg) {
			super(class_2006.field_9504);
			this.field_9508 = arg;
		}

		public static class_2006.class_2008 method_8818(class_2096.class_2100 arg) {
			return new class_2006.class_2008(arg);
		}

		public boolean method_8817(class_2580 arg) {
			return this.field_9508.method_9054(arg.method_10939());
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("level", this.field_9508.method_9036());
			return jsonObject;
		}
	}
}
