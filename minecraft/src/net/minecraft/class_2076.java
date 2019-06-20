package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class class_2076 implements class_179<class_2076.class_2078> {
	private static final class_2960 field_9655 = new class_2960("killed_by_crossbow");
	private final Map<class_2985, class_2076.class_2077> field_9656 = Maps.<class_2985, class_2076.class_2077>newHashMap();

	@Override
	public class_2960 method_794() {
		return field_9655;
	}

	@Override
	public void method_792(class_2985 arg, class_179.class_180<class_2076.class_2078> arg2) {
		class_2076.class_2077 lv = (class_2076.class_2077)this.field_9656.get(arg);
		if (lv == null) {
			lv = new class_2076.class_2077(arg);
			this.field_9656.put(arg, lv);
		}

		lv.method_8982(arg2);
	}

	@Override
	public void method_793(class_2985 arg, class_179.class_180<class_2076.class_2078> arg2) {
		class_2076.class_2077 lv = (class_2076.class_2077)this.field_9656.get(arg);
		if (lv != null) {
			lv.method_8985(arg2);
			if (lv.method_8984()) {
				this.field_9656.remove(arg);
			}
		}
	}

	@Override
	public void method_791(class_2985 arg) {
		this.field_9656.remove(arg);
	}

	public class_2076.class_2078 method_8979(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
		class_2048[] lvs = class_2048.method_8910(jsonObject.get("victims"));
		class_2096.class_2100 lv = class_2096.class_2100.method_9056(jsonObject.get("unique_entity_types"));
		return new class_2076.class_2078(lvs, lv);
	}

	public void method_8980(class_3222 arg, Collection<class_1297> collection, int i) {
		class_2076.class_2077 lv = (class_2076.class_2077)this.field_9656.get(arg.method_14236());
		if (lv != null) {
			lv.method_8983(arg, collection, i);
		}
	}

	static class class_2077 {
		private final class_2985 field_9658;
		private final Set<class_179.class_180<class_2076.class_2078>> field_9657 = Sets.<class_179.class_180<class_2076.class_2078>>newHashSet();

		public class_2077(class_2985 arg) {
			this.field_9658 = arg;
		}

		public boolean method_8984() {
			return this.field_9657.isEmpty();
		}

		public void method_8982(class_179.class_180<class_2076.class_2078> arg) {
			this.field_9657.add(arg);
		}

		public void method_8985(class_179.class_180<class_2076.class_2078> arg) {
			this.field_9657.remove(arg);
		}

		public void method_8983(class_3222 arg, Collection<class_1297> collection, int i) {
			List<class_179.class_180<class_2076.class_2078>> list = null;

			for (class_179.class_180<class_2076.class_2078> lv : this.field_9657) {
				if (lv.method_797().method_8988(arg, collection, i)) {
					if (list == null) {
						list = Lists.<class_179.class_180<class_2076.class_2078>>newArrayList();
					}

					list.add(lv);
				}
			}

			if (list != null) {
				for (class_179.class_180<class_2076.class_2078> lvx : list) {
					lvx.method_796(this.field_9658);
				}
			}
		}
	}

	public static class class_2078 extends class_195 {
		private final class_2048[] field_9660;
		private final class_2096.class_2100 field_9659;

		public class_2078(class_2048[] args, class_2096.class_2100 arg) {
			super(class_2076.field_9655);
			this.field_9660 = args;
			this.field_9659 = arg;
		}

		public static class_2076.class_2078 method_8986(class_2048.class_2049... args) {
			class_2048[] lvs = new class_2048[args.length];

			for (int i = 0; i < args.length; i++) {
				class_2048.class_2049 lv = args[i];
				lvs[i] = lv.method_8920();
			}

			return new class_2076.class_2078(lvs, class_2096.class_2100.field_9708);
		}

		public static class_2076.class_2078 method_8987(class_2096.class_2100 arg) {
			class_2048[] lvs = new class_2048[0];
			return new class_2076.class_2078(lvs, arg);
		}

		public boolean method_8988(class_3222 arg, Collection<class_1297> collection, int i) {
			if (this.field_9660.length > 0) {
				List<class_1297> list = Lists.<class_1297>newArrayList(collection);

				for (class_2048 lv : this.field_9660) {
					boolean bl = false;
					Iterator<class_1297> iterator = list.iterator();

					while (iterator.hasNext()) {
						class_1297 lv2 = (class_1297)iterator.next();
						if (lv.method_8914(arg, lv2)) {
							iterator.remove();
							bl = true;
							break;
						}
					}

					if (!bl) {
						return false;
					}
				}
			}

			if (this.field_9659 == class_2096.class_2100.field_9708) {
				return true;
			} else {
				Set<class_1299<?>> set = Sets.<class_1299<?>>newHashSet();

				for (class_1297 lv3 : collection) {
					set.add(lv3.method_5864());
				}

				return this.field_9659.method_9054(set.size()) && this.field_9659.method_9054(i);
			}
		}

		@Override
		public JsonElement method_807() {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("victims", class_2048.method_8911(this.field_9660));
			jsonObject.add("unique_entity_types", this.field_9659.method_9036());
			return jsonObject;
		}
	}
}
