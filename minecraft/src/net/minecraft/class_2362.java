package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2362 extends class_2248 {
	private static final Map<class_2248, class_2248> field_11103 = Maps.<class_2248, class_2248>newHashMap();
	protected static final class_265 field_11102 = class_2248.method_9541(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);
	private final class_2248 field_11101;

	public class_2362(class_2248 arg, class_2248.class_2251 arg2) {
		super(arg2);
		this.field_11101 = arg;
		field_11103.put(arg, this);
	}

	@Override
	public class_265 method_9529(class_2680 arg, class_1922 arg2, class_2338 arg3) {
		return field_11102;
	}

	@Override
	public class_2464 method_9604(class_2680 arg) {
		return class_2464.field_11458;
	}

	@Override
	public boolean method_9534(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1657 arg4, class_1268 arg5, class_2350 arg6, float f, float g, float h) {
		class_1799 lv = arg4.method_5998(arg5);
		class_1792 lv2 = lv.method_7909();
		class_2248 lv3 = lv2 instanceof class_1747
			? (class_2248)field_11103.getOrDefault(((class_1747)lv2).method_7711(), class_2246.field_10124)
			: class_2246.field_10124;
		boolean bl = lv3 == class_2246.field_10124;
		boolean bl2 = this.field_11101 == class_2246.field_10124;
		if (bl != bl2) {
			if (bl2) {
				arg2.method_8652(arg3, lv3.method_9564(), 3);
				arg4.method_7281(class_3468.field_15412);
				if (!arg4.field_7503.field_7477) {
					lv.method_7934(1);
				}
			} else {
				class_1799 lv4 = new class_1799(this.field_11101);
				if (lv.method_7960()) {
					arg4.method_6122(arg5, lv4);
				} else if (!arg4.method_7270(lv4)) {
					arg4.method_7328(lv4, false);
				}

				arg2.method_8652(arg3, class_2246.field_10495.method_9564(), 3);
			}
		}

		return true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_1799 method_9574(class_1922 arg, class_2338 arg2, class_2680 arg3) {
		return this.field_11101 == class_2246.field_10124 ? super.method_9574(arg, arg2, arg3) : new class_1799(this.field_11101);
	}

	@Override
	public class_2680 method_9559(class_2680 arg, class_2350 arg2, class_2680 arg3, class_1936 arg4, class_2338 arg5, class_2338 arg6) {
		return arg2 == class_2350.field_11033 && !arg.method_11591(arg4, arg5)
			? class_2246.field_10124.method_9564()
			: super.method_9559(arg, arg2, arg3, arg4, arg5, arg6);
	}

	@Override
	public class_1921 method_9551() {
		return class_1921.field_9174;
	}

	public class_2248 method_16231() {
		return this.field_11101;
	}
}
