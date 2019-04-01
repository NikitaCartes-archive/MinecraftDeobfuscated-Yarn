package net.minecraft;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public abstract class class_3972 implements class_1860<class_1263> {
	protected final class_1856 field_17642;
	protected final class_1799 field_17643;
	private final class_3956<?> field_17646;
	private final class_1865<?> field_17647;
	protected final class_2960 field_17644;
	protected final String field_17645;

	public class_3972(class_3956<?> arg, class_1865<?> arg2, class_2960 arg3, String string, class_1856 arg4, class_1799 arg5) {
		this.field_17646 = arg;
		this.field_17647 = arg2;
		this.field_17644 = arg3;
		this.field_17645 = string;
		this.field_17642 = arg4;
		this.field_17643 = arg5;
	}

	@Override
	public class_3956<?> method_17716() {
		return this.field_17646;
	}

	@Override
	public class_1865<?> method_8119() {
		return this.field_17647;
	}

	@Override
	public class_2960 method_8114() {
		return this.field_17644;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public String method_8112() {
		return this.field_17645;
	}

	@Override
	public class_1799 method_8110() {
		return this.field_17643;
	}

	@Override
	public class_2371<class_1856> method_8117() {
		class_2371<class_1856> lv = class_2371.method_10211();
		lv.add(this.field_17642);
		return lv;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_8113(int i, int j) {
		return true;
	}

	@Override
	public class_1799 method_8116(class_1263 arg) {
		return this.field_17643.method_7972();
	}

	public static class class_3973<T extends class_3972> implements class_1865<T> {
		final class_3972.class_3973.class_3974<T> field_17648;

		protected class_3973(class_3972.class_3973.class_3974<T> arg) {
			this.field_17648 = arg;
		}

		public T method_17881(class_2960 arg, JsonObject jsonObject) {
			String string = class_3518.method_15253(jsonObject, "group", "");
			class_1856 lv;
			if (class_3518.method_15264(jsonObject, "ingredient")) {
				lv = class_1856.method_8102(class_3518.method_15261(jsonObject, "ingredient"));
			} else {
				lv = class_1856.method_8102(class_3518.method_15296(jsonObject, "ingredient"));
			}

			String string2 = class_3518.method_15265(jsonObject, "result");
			int i = class_3518.method_15260(jsonObject, "count");
			class_1799 lv2 = new class_1799(class_2378.field_11142.method_10223(new class_2960(string2)), i);
			return this.field_17648.create(arg, string, lv, lv2);
		}

		public T method_17882(class_2960 arg, class_2540 arg2) {
			String string = arg2.method_10800(32767);
			class_1856 lv = class_1856.method_8086(arg2);
			class_1799 lv2 = arg2.method_10819();
			return this.field_17648.create(arg, string, lv, lv2);
		}

		public void method_17880(class_2540 arg, T arg2) {
			arg.method_10814(arg2.field_17645);
			arg2.field_17642.method_8088(arg);
			arg.method_10793(arg2.field_17643);
		}

		interface class_3974<T extends class_3972> {
			T create(class_2960 arg, String string, class_1856 arg2, class_1799 arg3);
		}
	}
}
