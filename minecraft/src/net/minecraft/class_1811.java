package net.minecraft;

import java.util.function.Predicate;

public abstract class class_1811 extends class_1792 {
	public static final Predicate<class_1799> field_18281 = arg -> arg.method_7909().method_7855(class_3489.field_18317);
	public static final Predicate<class_1799> field_18282 = field_18281.or(arg -> arg.method_7909() == class_1802.field_8639);

	public class_1811(class_1792.class_1793 arg) {
		super(arg);
	}

	public Predicate<class_1799> method_20310() {
		return this.method_19268();
	}

	public abstract Predicate<class_1799> method_19268();

	public static class_1799 method_18815(class_1309 arg, Predicate<class_1799> predicate) {
		if (predicate.test(arg.method_5998(class_1268.field_5810))) {
			return arg.method_5998(class_1268.field_5810);
		} else {
			return predicate.test(arg.method_5998(class_1268.field_5808)) ? arg.method_5998(class_1268.field_5808) : class_1799.field_8037;
		}
	}

	@Override
	public int method_7837() {
		return 1;
	}
}
