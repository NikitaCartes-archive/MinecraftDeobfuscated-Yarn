package net.minecraft;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1843 extends class_1792 {
	public class_1843(class_1792.class_1793 arg) {
		super(arg);
	}

	public static boolean method_8053(@Nullable class_2487 arg) {
		if (!class_1840.method_8047(arg)) {
			return false;
		} else if (!arg.method_10573("title", 8)) {
			return false;
		} else {
			String string = arg.method_10558("title");
			return string.length() > 32 ? false : arg.method_10573("author", 8);
		}
	}

	public static int method_8052(class_1799 arg) {
		return arg.method_7969().method_10550("generation");
	}

	public static int method_17443(class_1799 arg) {
		class_2487 lv = arg.method_7969();
		return lv != null ? lv.method_10554("pages", 8).size() : 0;
	}

	@Override
	public class_2561 method_7864(class_1799 arg) {
		if (arg.method_7985()) {
			class_2487 lv = arg.method_7969();
			String string = lv.method_10558("title");
			if (!class_3544.method_15438(string)) {
				return new class_2585(string);
			}
		}

		return super.method_7864(arg);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_7851(class_1799 arg, @Nullable class_1937 arg2, List<class_2561> list, class_1836 arg3) {
		if (arg.method_7985()) {
			class_2487 lv = arg.method_7969();
			String string = lv.method_10558("author");
			if (!class_3544.method_15438(string)) {
				list.add(new class_2588("book.byAuthor", string).method_10854(class_124.field_1080));
			}

			list.add(new class_2588("book.generation." + lv.method_10550("generation")).method_10854(class_124.field_1080));
		}
	}

	@Override
	public class_1269 method_7884(class_1838 arg) {
		class_1937 lv = arg.method_8045();
		class_2338 lv2 = arg.method_8037();
		class_2680 lv3 = lv.method_8320(lv2);
		if (lv3.method_11614() == class_2246.field_16330) {
			return class_3715.method_17472(lv, lv2, lv3, arg.method_8041()) ? class_1269.field_5812 : class_1269.field_5811;
		} else {
			return class_1269.field_5811;
		}
	}

	@Override
	public class_1271<class_1799> method_7836(class_1937 arg, class_1657 arg2, class_1268 arg3) {
		class_1799 lv = arg2.method_5998(arg3);
		arg2.method_7315(lv, arg3);
		arg2.method_7259(class_3468.field_15372.method_14956(this));
		return new class_1271<>(class_1269.field_5812, lv);
	}

	public static boolean method_8054(class_1799 arg, @Nullable class_2168 arg2, @Nullable class_1657 arg3) {
		class_2487 lv = arg.method_7969();
		if (lv != null && !lv.method_10577("resolved")) {
			lv.method_10556("resolved", true);
			if (!method_8053(lv)) {
				return false;
			} else {
				class_2499 lv2 = lv.method_10554("pages", 8);

				for (int i = 0; i < lv2.size(); i++) {
					String string = lv2.method_10608(i);

					class_2561 lv3;
					try {
						lv3 = class_2561.class_2562.method_10873(string);
						lv3 = class_2564.method_10881(arg2, lv3, arg3);
					} catch (Exception var9) {
						lv3 = new class_2585(string);
					}

					lv2.method_10606(i, new class_2519(class_2561.class_2562.method_10867(lv3)));
				}

				lv.method_10566("pages", lv2);
				return true;
			}
		} else {
			return false;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_7886(class_1799 arg) {
		return true;
	}
}
