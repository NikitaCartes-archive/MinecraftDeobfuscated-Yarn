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

public class class_2080 implements class_179<class_2080.class_2083> {
	private final Map<class_2985, class_2080.class_2082> field_9662 = Maps.<class_2985, class_2080.class_2082>newHashMap();
	private final class_2960 field_9661;

	public class_2080(class_2960 arg) {
		this.field_9661 = arg;
	}

	@Override
	public class_2960 method_794() {
		return this.field_9661;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2080.class_2083> arg2) {
		class_2080.class_2082 lv = (class_2080.class_2082)this.field_9662.get(arg);
		if (lv == null) {
			lv = new class_2080.class_2082(arg);
			this.field_9662.put(arg, lv);
		}

		lv.method_8993(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2080.class_2083> arg2) {
		class_2080.class_2082 lv = (class_2080.class_2082)this.field_9662.get(arg);
		if (lv != null) {
			lv.method_8996(arg2);
			if (lv.method_8995()) {
				this.field_9662.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9662.remove(arg);
	}

	public class_2080.class_2083 method_8989(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		return new class_2080.class_2083(this.field_9661, class_2048.method_8913(jsonObject.get("entity")), class_2022.method_8846(jsonObject.get("killing_blow")));
	}

	public void method_8990(class_3222 arg, class_1297 arg2, class_1282 arg3) {
		class_2080.class_2082 lv = (class_2080.class_2082)this.field_9662.get(arg.method_14236());
		if (lv != null) {
			lv.method_8994(arg, arg2, arg3);
		}
	}

	static class class_2082 {
		private final class_2985 field_9666;
		private final Set<class_179.class_180<class_2080.class_2083>> field_9665 = Sets.<class_179.class_180<class_2080.class_2083>>newHashSet();

		public class_2082(class_2985 arg) {
			this.field_9666 = arg;
		}

		public boolean method_8995() {
			return this.field_9665.isEmpty();
		}

		public void method_8993(class_179.class_180<class_2080.class_2083> arg) {
			this.field_9665.add(arg);
		}

		public void method_8996(class_179.class_180<class_2080.class_2083> arg) {
			this.field_9665.remove(arg);
		}

		public void method_8994(class_3222 arg, class_1297 arg2, class_1282 arg3) {
			List<class_179.class_180<class_2080.class_2083>> list = null;

			for (class_179.class_180<class_2080.class_2083> lv : this.field_9665) {
				if (lv.method_797().method_9000(arg, arg2, arg3)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2080.class_2083>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2080.class_2083> lvx : list) {
					lvx.method_796(this.field_9666);
				}
			}
		}
	}

	public static class class_2083 extends class_195 {
		private final class_2048 field_9668;
		private final class_2022 field_9667;

		public class_2083(class_2960 arg, class_2048 arg2, class_2022 arg3) {
			super(arg);
			this.field_9668 = arg2;
			this.field_9667 = arg3;
		}

		public static class_2080.class_2083 method_8997(class_2048.class_2049 arg) {
			return new class_2080.class_2083(class_174.field_1192.field_9661, arg.method_8920(), class_2022.field_9533);
		}

		public static class_2080.class_2083 method_8999() {
			return new class_2080.class_2083(class_174.field_1192.field_9661, class_2048.field_9599, class_2022.field_9533);
		}

		public static class_2080.class_2083 method_9001(class_2048.class_2049 arg, class_2022.class_2023 arg2) {
			return new class_2080.class_2083(class_174.field_1192.field_9661, arg.method_8920(), arg2.method_8851());
		}

		public static class_2080.class_2083 method_8998() {
			return new class_2080.class_2083(class_174.field_1188.field_9661, class_2048.field_9599, class_2022.field_9533);
		}

		public boolean method_9000(class_3222 arg, class_1297 arg2, class_1282 arg3) {
			return !this.field_9667.method_8847(arg, arg3) ? false : this.field_9668.method_8914(arg, arg2);
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("entity", this.field_9668.method_8912());
			jsonObject.add("killing_blow", this.field_9667.method_8848());
			return jsonObject;
		}
	}
}
