package net.minecraft;

import javax.annotation.Nullable;

public class class_1822 extends class_1827 {
	public class_1822(class_1792.class_1793 arg, class_2248 arg2, class_2248 arg3) {
		super(arg2, arg3, arg);
	}

	@Override
	protected boolean method_7710(class_2338 arg, class_1937 arg2, @Nullable class_1657 arg3, class_1799 arg4, class_2680 arg5) {
		boolean bl = super.method_7710(arg, arg2, arg3, arg4, arg5);
		if (!arg2.field_9236 && !bl && arg3 != null) {
			arg3.method_7311((class_2625)arg2.method_8321(arg));
		}

		return bl;
	}
}
