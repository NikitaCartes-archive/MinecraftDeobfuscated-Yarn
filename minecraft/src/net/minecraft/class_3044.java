package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3044 extends class_3031<class_3111> {
	private static final class_2960 field_13624 = new class_2960("fossil/spine_1");
	private static final class_2960 field_13615 = new class_2960("fossil/spine_2");
	private static final class_2960 field_13627 = new class_2960("fossil/spine_3");
	private static final class_2960 field_13619 = new class_2960("fossil/spine_4");
	private static final class_2960 field_13620 = new class_2960("fossil/spine_1_coal");
	private static final class_2960 field_13618 = new class_2960("fossil/spine_2_coal");
	private static final class_2960 field_13625 = new class_2960("fossil/spine_3_coal");
	private static final class_2960 field_13616 = new class_2960("fossil/spine_4_coal");
	private static final class_2960 field_13611 = new class_2960("fossil/skull_1");
	private static final class_2960 field_13621 = new class_2960("fossil/skull_2");
	private static final class_2960 field_13612 = new class_2960("fossil/skull_3");
	private static final class_2960 field_13622 = new class_2960("fossil/skull_4");
	private static final class_2960 field_13614 = new class_2960("fossil/skull_1_coal");
	private static final class_2960 field_13613 = new class_2960("fossil/skull_2_coal");
	private static final class_2960 field_13623 = new class_2960("fossil/skull_3_coal");
	private static final class_2960 field_13610 = new class_2960("fossil/skull_4_coal");
	private static final class_2960[] field_13617 = new class_2960[]{
		field_13624, field_13615, field_13627, field_13619, field_13611, field_13621, field_13612, field_13622
	};
	private static final class_2960[] field_13626 = new class_2960[]{
		field_13620, field_13618, field_13625, field_13616, field_13614, field_13613, field_13623, field_13610
	};

	public class_3044(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	public boolean method_13236(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		Random random2 = arg.method_8409();
		class_2470[] lvs = class_2470.values();
		class_2470 lv = lvs[random2.nextInt(lvs.length)];
		int i = random2.nextInt(field_13617.length);
		class_3485 lv2 = ((class_3218)arg.method_8410()).method_17982().method_134();
		class_3499 lv3 = lv2.method_15091(field_13617[i]);
		class_3499 lv4 = lv2.method_15091(field_13626[i]);
		class_1923 lv5 = new class_1923(arg3);
		class_3341 lv6 = new class_3341(lv5.method_8326(), 0, lv5.method_8328(), lv5.method_8327(), 256, lv5.method_8329());
		class_3492 lv7 = new class_3492().method_15123(lv).method_15126(lv6).method_15112(random2).method_16184(class_3793.field_16721);
		class_2338 lv8 = lv3.method_15166(lv);
		int j = random2.nextInt(16 - lv8.method_10263());
		int k = random2.nextInt(16 - lv8.method_10260());
		int l = 256;

		for (int m = 0; m < lv8.method_10263(); m++) {
			for (int n = 0; n < lv8.method_10263(); n++) {
				l = Math.min(l, arg.method_8589(class_2902.class_2903.field_13195, arg3.method_10263() + m + j, arg3.method_10260() + n + k));
			}
		}

		int m = Math.max(l - 15 - random2.nextInt(10), 10);
		class_2338 lv9 = lv3.method_15167(arg3.method_10069(j, m, k), class_2415.field_11302, lv);
		class_3488 lv10 = new class_3488(0.9F);
		lv7.method_16183().method_16184(lv10);
		lv3.method_15172(arg, lv9, lv7, 4);
		lv7.method_16664(lv10);
		class_3488 lv11 = new class_3488(0.1F);
		lv7.method_16183().method_16184(lv11);
		lv4.method_15172(arg, lv9, lv7, 4);
		return true;
	}
}
