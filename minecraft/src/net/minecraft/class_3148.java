package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;

public class class_3148 extends class_3031<class_3111> {
	public class_3148(Function<Dynamic<?>, ? extends class_3111> function) {
		super(function);
	}

	public boolean method_13782(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, class_3111 arg4) {
		int i = 0;

		for (int j = 0; j < 20; j++) {
			class_2338 lv = arg3.method_10069(random.nextInt(4) - random.nextInt(4), 0, random.nextInt(4) - random.nextInt(4));
			if (arg.method_8623(lv)) {
				class_2338 lv2 = lv.method_10074();
				if (arg.method_8316(lv2.method_10067()).method_15767(class_3486.field_15517)
					|| arg.method_8316(lv2.method_10078()).method_15767(class_3486.field_15517)
					|| arg.method_8316(lv2.method_10095()).method_15767(class_3486.field_15517)
					|| arg.method_8316(lv2.method_10072()).method_15767(class_3486.field_15517)) {
					int k = 2 + random.nextInt(random.nextInt(3) + 1);

					for (int l = 0; l < k; l++) {
						if (class_2246.field_10424.method_9564().method_11591(arg, lv)) {
							arg.method_8652(lv.method_10086(l), class_2246.field_10424.method_9564(), 2);
							i++;
						}
					}
				}
			}
		}

		return i > 0;
	}
}
