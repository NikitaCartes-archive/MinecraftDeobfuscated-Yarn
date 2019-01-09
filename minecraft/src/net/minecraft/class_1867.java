package net.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1867 implements class_1860 {
	private final class_2960 field_9048;
	private final String field_9049;
	private final class_1799 field_9050;
	private final class_2371<class_1856> field_9047;

	public class_1867(class_2960 arg, String string, class_1799 arg2, class_2371<class_1856> arg3) {
		this.field_9048 = arg;
		this.field_9049 = string;
		this.field_9050 = arg2;
		this.field_9047 = arg3;
	}

	@Override
	public class_2960 method_8114() {
		return this.field_9048;
	}

	@Override
	public class_1862<?> method_8119() {
		return class_1865.field_9031;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public String method_8112() {
		return this.field_9049;
	}

	@Override
	public class_1799 method_8110() {
		return this.field_9050;
	}

	@Override
	public class_2371<class_1856> method_8117() {
		return this.field_9047;
	}

	@Override
	public boolean method_8115(class_1263 arg, class_1937 arg2) {
		if (!(arg instanceof class_1715)) {
			return false;
		} else {
			class_1715 lv = (class_1715)arg;
			class_1662 lv2 = new class_1662();
			int i = 0;

			for (int j = 0; j < lv.method_17397(); j++) {
				for (int k = 0; k < lv.method_17398(); k++) {
					class_1799 lv3 = arg.method_5438(k + j * lv.method_17398());
					if (!lv3.method_7960()) {
						i++;
						lv2.method_7400(new class_1799(lv3.method_7909()));
					}
				}
			}

			return i == this.field_9047.size() && lv2.method_7402(this, null);
		}
	}

	@Override
	public class_1799 method_8116(class_1263 arg) {
		return this.field_9050.method_7972();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_8113(int i, int j) {
		return i * j >= this.field_9047.size();
	}

	public static class class_1868 implements class_1862<class_1867> {
		public class_1867 method_8142(class_2960 arg, JsonObject jsonObject) {
			String string = class_3518.method_15253(jsonObject, "group", "");
			class_2371<class_1856> lv = method_8144(class_3518.method_15261(jsonObject, "ingredients"));
			if (lv.isEmpty()) {
				throw new JsonParseException("No ingredients for shapeless recipe");
			} else if (lv.size() > 9) {
				throw new JsonParseException("Too many ingredients for shapeless recipe");
			} else {
				class_1799 lv2 = class_1869.method_8155(class_3518.method_15296(jsonObject, "result"));
				return new class_1867(arg, string, lv2, lv);
			}
		}

		private static class_2371<class_1856> method_8144(JsonArray jsonArray) {
			class_2371<class_1856> lv = class_2371.method_10211();

			for (int i = 0; i < jsonArray.size(); i++) {
				class_1856 lv2 = class_1856.method_8102(jsonArray.get(i));
				if (!lv2.method_8103()) {
					lv.add(lv2);
				}
			}

			return lv;
		}

		@Override
		public String method_8123() {
			return "crafting_shapeless";
		}

		public class_1867 method_8141(class_2960 arg, class_2540 arg2) {
			String string = arg2.method_10800(32767);
			int i = arg2.method_10816();
			class_2371<class_1856> lv = class_2371.method_10213(i, class_1856.field_9017);

			for (int j = 0; j < lv.size(); j++) {
				lv.set(j, class_1856.method_8086(arg2));
			}

			class_1799 lv2 = arg2.method_10819();
			return new class_1867(arg, string, lv2, lv);
		}

		public void method_8143(class_2540 arg, class_1867 arg2) {
			arg.method_10814(arg2.field_9049);
			arg.method_10804(arg2.field_9047.size());

			for (class_1856 lv : arg2.field_9047) {
				lv.method_8088(arg);
			}

			arg.method_10793(arg2.field_9050);
		}
	}
}
