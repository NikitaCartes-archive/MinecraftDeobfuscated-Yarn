package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_2953 extends class_3031<class_3111> {
	public class_2953(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	public boolean method_12817(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		for (class_2680 lv = arg.method_8320(arg3);
			(lv.method_11588() || lv.method_11602(class_3481.field_15503)) && arg3.method_10264() > 1;
			lv = arg.method_8320(arg3)
		) {
			arg3 = arg3.method_10074();
		}

		if (arg3.method_10264() < 1) {
			return false;
		} else {
			arg3 = arg3.method_10084();

			for (int i = 0; i < 4; i++) {
				class_2338 lv2 = arg3.method_10069(random.nextInt(4) - random.nextInt(4), random.nextInt(3) - random.nextInt(3), random.nextInt(4) - random.nextInt(4));
				class_2338 lv3 = lv2.method_10074();
				if (arg.method_8623(lv2) && arg.method_8320(lv3).method_11631(arg, lv3)) {
					arg.method_8652(lv2, class_2246.field_10034.method_9564(), 2);
					class_2621.method_11287(arg, random, lv2, class_39.field_850);
					class_2338 lv4 = lv2.method_10078();
					class_2338 lv5 = lv2.method_10067();
					class_2338 lv6 = lv2.method_10095();
					class_2338 lv7 = lv2.method_10072();
					class_2338 lv8 = lv5.method_10074();
					if (arg.method_8623(lv5) && arg.method_8320(lv8).method_11631(arg, lv8)) {
						arg.method_8652(lv5, class_2246.field_10336.method_9564(), 2);
					}

					class_2338 lv9 = lv4.method_10074();
					if (arg.method_8623(lv4) && arg.method_8320(lv9).method_11631(arg, lv9)) {
						arg.method_8652(lv4, class_2246.field_10336.method_9564(), 2);
					}

					class_2338 lv10 = lv6.method_10074();
					if (arg.method_8623(lv6) && arg.method_8320(lv10).method_11631(arg, lv10)) {
						arg.method_8652(lv6, class_2246.field_10336.method_9564(), 2);
					}

					class_2338 lv11 = lv7.method_10074();
					if (arg.method_8623(lv7) && arg.method_8320(lv11).method_11631(arg, lv11)) {
						arg.method_8652(lv7, class_2246.field_10336.method_9564(), 2);
					}

					return true;
				}
			}

			return false;
		}
	}
}
