package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;

public class class_2386 extends class_2373 {
	public class_2386(class_2248.class_2251 arg) {
		super(arg);
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9179;
	}

	@Override
	public void method_9556(class_1937 arg, class_1657 arg2, class_2338 arg3, class_2680 arg4, @Nullable class_2586 arg5, class_1799 arg6) {
		super.method_9556(arg, arg2, arg3, arg4, arg5, arg6);
		if (class_1890.method_8225(class_1893.field_9099, arg6) == 0) {
			if (arg.field_9247.method_12465()) {
				arg.method_8650(arg3);
				return;
			}

			class_3614 lv = arg.method_8320(arg3.method_10074()).method_11620();
			if (lv.method_15801() || lv.method_15797()) {
				arg.method_8501(arg3, class_2246.field_10382.method_9564());
			}
		}
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (arg2.method_8314(class_1944.field_9282, arg3) > 11 - arg.method_11581(arg2, arg3)) {
			this.method_10275(arg, arg2, arg3);
		}
	}

	protected void method_10275(class_2680 arg, class_1937 arg2, class_2338 arg3) {
		if (arg2.field_9247.method_12465()) {
			arg2.method_8650(arg3);
		} else {
			arg2.method_8501(arg3, class_2246.field_10382.method_9564());
			arg2.method_8492(arg3, class_2246.field_10382, arg3);
		}
	}

	@Override
	public class_3619 method_9527(class_2680 arg) {
		return class_3619.field_15974;
	}
}
