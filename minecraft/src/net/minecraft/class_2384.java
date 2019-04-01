package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;

public class class_2384 extends class_2248 {
	private final class_2248 field_11178;
	private static final Map<class_2248, class_2248> field_11179 = Maps.<class_2248, class_2248>newIdentityHashMap();

	public class_2384(class_2248 arg, class_2248.class_2251 arg2) {
		super(arg2);
		this.field_11178 = arg;
		field_11179.put(arg, this);
	}

	public class_2248 method_10271() {
		return this.field_11178;
	}

	public static boolean method_10269(class_2680 arg) {
		return field_11179.containsKey(arg.method_11614());
	}

	@Override
	public void method_9565(class_2680 arg, class_1937 arg2, class_2338 arg3, class_1799 arg4) {
		super.method_9565(arg, arg2, arg3, arg4);
		if (!arg2.field_9236 && arg2.method_8450().method_8355("doTileDrops") && class_1890.method_8225(class_1893.field_9099, arg4) == 0) {
			class_1614 lv = class_1299.field_6125.method_5883(arg2);
			lv.method_5808((double)arg3.method_10263() + 0.5, (double)arg3.method_10264(), (double)arg3.method_10260() + 0.5, 0.0F, 0.0F);
			arg2.method_8649(lv);
			lv.method_5990();
		}
	}

	public static class_2680 method_10270(class_2248 arg) {
		return ((class_2248)field_11179.get(arg)).method_9564();
	}
}
