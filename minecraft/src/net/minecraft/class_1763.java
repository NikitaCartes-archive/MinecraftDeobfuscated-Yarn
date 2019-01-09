package net.minecraft;

import java.util.Collection;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1763 extends class_1792 {
	public class_1763(class_1792.class_1793 arg) {
		super(arg);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_7886(class_1799 arg) {
		return true;
	}

	@Override
	public boolean method_7885(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4) {
		if (!arg2.field_9236) {
			this.method_7759(arg4, arg, arg2, arg3, false, arg4.method_5998(class_1268.field_5808));
		}

		return false;
	}

	@Override
	public class_1269 method_7884(class_1838 arg) {
		class_1657 lv = arg.method_8036();
		class_1937 lv2 = arg.method_8045();
		if (!lv2.field_9236 && lv != null) {
			class_2338 lv3 = arg.method_8037();
			this.method_7759(lv, lv2.method_8320(lv3), lv2, lv3, true, arg.method_8041());
		}

		return class_1269.field_5812;
	}

	private void method_7759(class_1657 arg, class_2680 arg2, class_1936 arg3, class_2338 arg4, boolean bl, class_1799 arg5) {
		if (arg.method_7338()) {
			class_2248 lv = arg2.method_11614();
			class_2689<class_2248, class_2680> lv2 = lv.method_9595();
			Collection<class_2769<?>> collection = lv2.method_11659();
			String string = class_2378.field_11146.method_10221(lv).toString();
			if (collection.isEmpty()) {
				method_7762(arg, new class_2588(this.method_7876() + ".empty", string));
			} else {
				class_2487 lv3 = arg5.method_7911("DebugProperty");
				String string2 = lv3.method_10558(string);
				class_2769<?> lv4 = lv2.method_11663(string2);
				if (bl) {
					if (lv4 == null) {
						lv4 = (class_2769<?>)collection.iterator().next();
					}

					class_2680 lv5 = method_7758(arg2, lv4, arg.method_5715());
					arg3.method_8652(arg4, lv5, 18);
					method_7762(arg, new class_2588(this.method_7876() + ".update", lv4.method_11899(), method_7761(lv5, lv4)));
				} else {
					lv4 = method_7760(collection, lv4, arg.method_5715());
					String string3 = lv4.method_11899();
					lv3.method_10582(string, string3);
					method_7762(arg, new class_2588(this.method_7876() + ".select", string3, method_7761(arg2, lv4)));
				}
			}
		}
	}

	private static <T extends Comparable<T>> class_2680 method_7758(class_2680 arg, class_2769<T> arg2, boolean bl) {
		return arg.method_11657(arg2, method_7760(arg2.method_11898(), arg.method_11654(arg2), bl));
	}

	private static <T> T method_7760(Iterable<T> iterable, @Nullable T object, boolean bl) {
		return bl ? class_156.method_645(iterable, object) : class_156.method_660(iterable, object);
	}

	private static void method_7762(class_1657 arg, class_2561 arg2) {
		((class_3222)arg).method_14254(arg2, class_2556.field_11733);
	}

	private static <T extends Comparable<T>> String method_7761(class_2680 arg, class_2769<T> arg2) {
		return arg2.method_11901(arg.method_11654(arg2));
	}
}
