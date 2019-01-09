package net.minecraft;

import java.util.Map;
import javax.annotation.Nullable;

public class class_1827 extends class_1747 {
	protected final class_2248 field_8918;

	public class_1827(class_2248 arg, class_2248 arg2, class_1792.class_1793 arg3) {
		super(arg, arg3);
		this.field_8918 = arg2;
	}

	@Nullable
	@Override
	protected class_2680 method_7707(class_1750 arg) {
		class_2680 lv = this.field_8918.method_9605(arg);
		class_2680 lv2 = null;
		class_1941 lv3 = arg.method_8045();
		class_2338 lv4 = arg.method_8037();

		for (class_2350 lv5 : arg.method_7718()) {
			if (lv5 != class_2350.field_11036) {
				class_2680 lv6 = lv5 == class_2350.field_11033 ? this.method_7711().method_9605(arg) : lv;
				if (lv6 != null && lv6.method_11591(lv3, lv4)) {
					lv2 = lv6;
					break;
				}
			}
		}

		return lv2 != null && lv3.method_8628(lv2, lv4) ? lv2 : null;
	}

	@Override
	public void method_7713(Map<class_2248, class_1792> map, class_1792 arg) {
		super.method_7713(map, arg);
		map.put(this.field_8918, arg);
	}
}
