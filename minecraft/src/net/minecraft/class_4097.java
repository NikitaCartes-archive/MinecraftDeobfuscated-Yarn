package net.minecraft;

import java.util.Map;

public abstract class class_4097<E extends class_1309> {
	private final Map<class_4140<?>, class_4141> field_19291;
	private class_4097.class_4098 field_18333 = class_4097.class_4098.field_18337;
	private long field_18334;
	private final int field_18335;
	private final int field_18336;

	public class_4097(Map<class_4140<?>, class_4141> map) {
		this(map, 60);
	}

	public class_4097(Map<class_4140<?>, class_4141> map, int i) {
		this(map, i, i);
	}

	public class_4097(Map<class_4140<?>, class_4141> map, int i, int j) {
		this.field_18335 = i;
		this.field_18336 = j;
		this.field_19291 = map;
	}

	public class_4097.class_4098 method_18921() {
		return this.field_18333;
	}

	public final boolean method_18922(class_3218 arg, E arg2, long l) {
		if (this.method_19546(arg2) && this.method_18919(arg, arg2)) {
			this.field_18333 = class_4097.class_4098.field_18338;
			int i = this.field_18335 + arg.method_8409().nextInt(this.field_18336 + 1 - this.field_18335);
			this.field_18334 = l + (long)i;
			this.method_18920(arg, arg2, l);
			return true;
		} else {
			return false;
		}
	}

	protected void method_18920(class_3218 arg, E arg2, long l) {
	}

	public final void method_18923(class_3218 arg, E arg2, long l) {
		if (!this.method_18915(l) && this.method_18927(arg, arg2, l)) {
			this.method_18924(arg, arg2, l);
		} else {
			this.method_18925(arg, arg2, l);
		}
	}

	protected void method_18924(class_3218 arg, E arg2, long l) {
	}

	public final void method_18925(class_3218 arg, E arg2, long l) {
		this.field_18333 = class_4097.class_4098.field_18337;
		this.method_18926(arg, arg2, l);
	}

	protected void method_18926(class_3218 arg, E arg2, long l) {
	}

	protected boolean method_18927(class_3218 arg, E arg2, long l) {
		return false;
	}

	protected boolean method_18915(long l) {
		return l > this.field_18334;
	}

	protected boolean method_18919(class_3218 arg, E arg2) {
		return true;
	}

	public String toString() {
		return this.getClass().getSimpleName();
	}

	private boolean method_19546(E arg) {
		return this.field_19291.entrySet().stream().allMatch(entry -> {
			class_4140<?> lv = (class_4140<?>)entry.getKey();
			class_4141 lv2 = (class_4141)entry.getValue();
			return arg.method_18868().method_18876(lv, lv2);
		});
	}

	public static enum class_4098 {
		field_18337,
		field_18338;
	}
}
