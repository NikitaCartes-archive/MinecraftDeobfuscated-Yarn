package net.minecraft;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_3861 extends class_1874 {
	public class_3861(class_2960 arg, String string, class_1856 arg2, class_1799 arg3, float f, int i) {
		super(arg, string, arg2, arg3, f, i);
	}

	@Override
	public boolean method_8115(class_1263 arg, class_1937 arg2) {
		return arg instanceof class_3866 && this.field_9061.method_8093(arg.method_5438(0));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1799 method_17447() {
		return new class_1799(class_2246.field_10181);
	}

	@Override
	public class_1862<?> method_8119() {
		return class_1865.field_9042;
	}

	public static class class_1875 implements class_1862<class_3861> {
		public class_3861 method_8174(class_2960 arg, JsonObject jsonObject) {
			String string = class_3518.method_15253(jsonObject, "group", "");
			class_1856 lv;
			if (class_3518.method_15264(jsonObject, "ingredient")) {
				lv = class_1856.method_8102(class_3518.method_15261(jsonObject, "ingredient"));
			} else {
				lv = class_1856.method_8102(class_3518.method_15296(jsonObject, "ingredient"));
			}

			String string2 = class_3518.method_15265(jsonObject, "result");
			class_1792 lv2 = class_2378.field_11142.method_10223(new class_2960(string2));
			if (lv2 != null) {
				class_1799 lv3 = new class_1799(lv2);
				float f = class_3518.method_15277(jsonObject, "experience", 0.0F);
				int i = class_3518.method_15282(jsonObject, "cookingtime", 200);
				return new class_3861(arg, string, lv, lv3, f, i);
			} else {
				throw new IllegalStateException(string2 + " did not exist");
			}
		}

		public class_3861 method_8175(class_2960 arg, class_2540 arg2) {
			String string = arg2.method_10800(32767);
			class_1856 lv = class_1856.method_8086(arg2);
			class_1799 lv2 = arg2.method_10819();
			float f = arg2.readFloat();
			int i = arg2.method_10816();
			return new class_3861(arg, string, lv, lv2, f, i);
		}

		public void method_8173(class_2540 arg, class_3861 arg2) {
			arg.method_10814(arg2.field_9062);
			arg2.field_9061.method_8088(arg);
			arg.method_10793(arg2.field_9059);
			arg.writeFloat(arg2.field_9057);
			arg.method_10804(arg2.field_9058);
		}

		@Override
		public String method_8123() {
			return "smelting";
		}
	}
}
