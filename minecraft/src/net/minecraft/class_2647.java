package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;

public abstract class class_2647 {
	@Nullable
	protected abstract class_2944<class_3111> method_11430(Random random);

	public boolean method_11431(class_1936 arg, class_2338 arg2, class_2680 arg3, Random random) {
		class_2944<class_3111> lv = this.method_11430(random);
		if (lv == null) {
			return false;
		} else {
			arg.method_8652(arg2, class_2246.field_10124.method_9564(), 4);
			if (lv.method_13151(arg, (class_2794<? extends class_2888>)arg.method_8398().method_12129(), random, arg2, class_3037.field_13603)) {
				return true;
			} else {
				arg.method_8652(arg2, arg3, 4);
				return false;
			}
		}
	}
}
