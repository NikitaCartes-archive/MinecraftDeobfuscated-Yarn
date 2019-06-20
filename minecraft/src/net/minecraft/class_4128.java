package net.minecraft;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.function.Predicate;

public class class_4128 extends class_4097<class_1309> {
	private final class_4140<class_4208> field_18390;
	private final Predicate<class_4158> field_18391;

	public class_4128(class_4158 arg, class_4140<class_4208> arg2) {
		super(ImmutableMap.of(arg2, class_4141.field_18456));
		this.field_18391 = arg.method_19164();
		this.field_18390 = arg2;
	}

	@Override
	protected boolean method_18919(class_3218 arg, class_1309 arg2) {
		class_4208 lv = (class_4208)arg2.method_18868().method_18904(this.field_18390).get();
		return Objects.equals(arg.method_8597().method_12460(), lv.method_19442()) && lv.method_19446().method_19769(arg2.method_19538(), 5.0);
	}

	@Override
	protected void method_18920(class_3218 arg, class_1309 arg2, long l) {
		class_4095<?> lv = arg2.method_18868();
		class_4208 lv2 = (class_4208)lv.method_18904(this.field_18390).get();
		class_3218 lv3 = arg.method_8503().method_3847(lv2.method_19442());
		if (this.method_20499(lv3, lv2.method_19446()) || this.method_20500(lv3, lv2.method_19446(), arg2)) {
			lv.method_18875(this.field_18390);
		}
	}

	private boolean method_20500(class_3218 arg, class_2338 arg2, class_1309 arg3) {
		class_2680 lv = arg.method_8320(arg2);
		return lv.method_11614().method_9525(class_3481.field_16443) && (Boolean)lv.method_11654(class_2244.field_9968) && !arg3.method_6113();
	}

	private boolean method_20499(class_3218 arg, class_2338 arg2) {
		return !arg.method_19494().method_19116(arg2, this.field_18391);
	}
}
