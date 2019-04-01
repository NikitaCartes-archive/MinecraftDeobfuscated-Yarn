package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3029 extends class_3031<class_3018> {
	public class_3029(Function<Dynamic<?>, ? extends class_3018> function) {
		super(function);
	}

	public boolean method_13142(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3018 arg4) {
		for (class_2338 lv : class_2338.method_10097(arg3.method_10069(-1, -2, -1), arg3.method_10069(1, 2, 1))) {
			boolean bl = lv.method_10263() == arg3.method_10263();
			boolean bl2 = lv.method_10264() == arg3.method_10264();
			boolean bl3 = lv.method_10260() == arg3.method_10260();
			boolean bl4 = Math.abs(lv.method_10264() - arg3.method_10264()) == 2;
			if (bl && bl2 && bl3) {
				class_2338 lv2 = lv.method_10062();
				this.method_13153(arg, lv2, class_2246.field_10613.method_9564());
				arg4.method_18036().ifPresent(arg4x -> {
					class_2586 lvx = arg.method_8321(lv2);
					if (lvx instanceof class_2643) {
						class_2643 lv2x = (class_2643)lvx;
						lv2x.method_11418(arg4x, arg4.method_13026());
						lvx.method_5431();
					}
				});
			} else if (bl2) {
				this.method_13153(arg, lv, class_2246.field_10124.method_9564());
			} else if (bl4 && bl && bl3) {
				this.method_13153(arg, lv, class_2246.field_9987.method_9564());
			} else if ((bl || bl3) && !bl4) {
				this.method_13153(arg, lv, class_2246.field_9987.method_9564());
			} else {
				this.method_13153(arg, lv, class_2246.field_10124.method_9564());
			}
		}

		return true;
	}
}
