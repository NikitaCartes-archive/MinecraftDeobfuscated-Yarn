package net.minecraft;

import java.util.Random;
import javax.annotation.Nullable;

public class class_2298 extends class_2248 {
	private final class_2248 field_10818;

	public class_2298(class_2248 arg, class_2248.class_2251 arg2) {
		super(arg2);
		this.field_10818 = arg;
	}

	@Override
	public void method_9588(class_2680 arg, class_1937 arg2, class_2338 arg3, Random random) {
		if (!this.method_9808(arg2, arg3)) {
			arg2.method_8652(arg3, this.field_10818.method_9564(), 2);
		}
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		if (!this.method_9808(arg4, arg5)) {
			arg4.method_8397().method_8676(arg5, this, 60 + arg4.method_8409().nextInt(40));
		}

		return super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	protected boolean method_9808(class_1922 arg, class_2338 arg2) {
		for (class_2350 lv : class_2350.values()) {
			class_3610 lv2 = arg.method_8316(arg2.method_10093(lv));
			if (lv2.method_15767(class_3486.field_15517)) {
				return true;
			}
		}

		return false;
	}

	@Nullable
	@Override
	public class_2680 method_9605(class_1750 arg) {
		if (!this.method_9808(arg.method_8045(), arg.method_8037())) {
			arg.method_8045().method_8397().method_8676(arg.method_8037(), this, 60 + arg.method_8045().method_8409().nextInt(40));
		}

		return this.method_9564();
	}
}
