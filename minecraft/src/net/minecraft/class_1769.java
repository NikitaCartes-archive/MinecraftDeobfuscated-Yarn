package net.minecraft;

import com.google.common.collect.Maps;
import java.util.Map;

public class class_1769 extends class_1792 {
	private static final Map<class_1767, class_1769> field_7968 = Maps.newEnumMap(class_1767.class);
	private final class_1767 field_7969;

	public class_1769(class_1767 arg, class_1792.class_1793 arg2) {
		super(arg2);
		this.field_7969 = arg;
		field_7968.put(arg, this);
	}

	@Override
	public boolean method_7847(class_1799 arg, class_1657 arg2, class_1309 arg3, class_1268 arg4) {
		if (arg3 instanceof class_1472) {
			class_1472 lv = (class_1472)arg3;
			if (lv.method_5805() && !lv.method_6629() && lv.method_6633() != this.field_7969) {
				lv.method_6631(this.field_7969);
				arg.method_7934(1);
			}

			return true;
		} else {
			return false;
		}
	}

	public class_1767 method_7802() {
		return this.field_7969;
	}

	public static class_1769 method_7803(class_1767 arg) {
		return (class_1769)field_7968.get(arg);
	}
}
