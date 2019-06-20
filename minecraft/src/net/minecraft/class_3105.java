package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3105 extends class_3031<class_3061> {
	private static final class_2680 field_13702 = class_2246.field_10515.method_9564();

	public class_3105(Function<Dynamic<?>, ? extends class_3061> function) {
		super(function);
	}

	public boolean method_13555(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3061 arg4) {
		if (arg.method_8320(arg3.method_10084()) != field_13702) {
			return false;
		} else if (!arg.method_8320(arg3).method_11588() && arg.method_8320(arg3) != field_13702) {
			return false;
		} else {
			int i = 0;
			if (arg.method_8320(arg3.method_10067()) == field_13702) {
				i++;
			}

			if (arg.method_8320(arg3.method_10078()) == field_13702) {
				i++;
			}

			if (arg.method_8320(arg3.method_10095()) == field_13702) {
				i++;
			}

			if (arg.method_8320(arg3.method_10072()) == field_13702) {
				i++;
			}

			if (arg.method_8320(arg3.method_10074()) == field_13702) {
				i++;
			}

			int j = 0;
			if (arg.method_8623(arg3.method_10067())) {
				j++;
			}

			if (arg.method_8623(arg3.method_10078())) {
				j++;
			}

			if (arg.method_8623(arg3.method_10095())) {
				j++;
			}

			if (arg.method_8623(arg3.method_10072())) {
				j++;
			}

			if (arg.method_8623(arg3.method_10074())) {
				j++;
			}

			if (!arg4.field_13661 && i == 4 && j == 1 || i == 5) {
				arg.method_8652(arg3, class_2246.field_10164.method_9564(), 2);
				arg.method_8405().method_8676(arg3, class_3612.field_15908, 0);
			}

			return true;
		}
	}
}
