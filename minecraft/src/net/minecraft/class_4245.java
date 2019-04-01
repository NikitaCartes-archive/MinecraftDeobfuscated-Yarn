package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;

public class class_4245 extends class_4097<class_1308> {
	private final float field_18988;
	@Nullable
	private class_2338 field_18989;
	private int field_18990;
	private int field_18991;
	private int field_18992;

	public class_4245(float f) {
		this.field_18988 = f;
	}

	@Override
	protected Set<Pair<class_4140<?>, class_4141>> method_18914() {
		return ImmutableSet.of(Pair.of(class_4140.field_19007, class_4141.field_18456), Pair.of(class_4140.field_18445, class_4141.field_18457));
	}

	protected boolean method_19971(class_3218 arg, class_1308 arg2) {
		return arg2.method_6109() && this.method_19975(arg, arg2);
	}

	protected void method_19972(class_3218 arg, class_1308 arg2, long l) {
		super.method_18920(arg, arg2, l);
		this.method_19969(arg2).ifPresent(arg3 -> {
			this.field_18989 = arg3;
			this.field_18990 = 100;
			this.field_18991 = 3 + arg.field_9229.nextInt(4);
			this.field_18992 = 0;
			this.method_19970(arg2, arg3);
		});
	}

	protected void method_19976(class_3218 arg, class_1308 arg2, long l) {
		super.method_18926(arg, arg2, l);
		this.field_18989 = null;
		this.field_18990 = 0;
		this.field_18991 = 0;
		this.field_18992 = 0;
	}

	protected boolean method_19978(class_3218 arg, class_1308 arg2, long l) {
		return arg2.method_6109()
			&& this.field_18989 != null
			&& this.method_19974(arg, this.field_18989)
			&& !this.method_19981(arg, arg2)
			&& !this.method_19982(arg, arg2);
	}

	@Override
	protected boolean method_18915(long l) {
		return false;
	}

	protected void method_19980(class_3218 arg, class_1308 arg2, long l) {
		if (!this.method_19977(arg, arg2)) {
			this.field_18990--;
		} else if (this.field_18992 > 0) {
			this.field_18992--;
		} else {
			if (this.method_19979(arg, arg2)) {
				arg2.method_5993().method_6233();
				this.field_18991--;
				this.field_18992 = 5;
			}
		}
	}

	private void method_19970(class_1308 arg, class_2338 arg2) {
		arg.method_18868().method_18878(class_4140.field_18445, new class_4142(arg2, this.field_18988, 0));
	}

	private boolean method_19975(class_3218 arg, class_1308 arg2) {
		return this.method_19977(arg, arg2) || this.method_19969(arg2).isPresent();
	}

	private boolean method_19977(class_3218 arg, class_1308 arg2) {
		class_2338 lv = new class_2338(arg2);
		class_2338 lv2 = lv.method_10074();
		return this.method_19974(arg, lv) || this.method_19974(arg, lv2);
	}

	private boolean method_19979(class_3218 arg, class_1308 arg2) {
		return this.method_19974(arg, new class_2338(arg2));
	}

	private boolean method_19974(class_3218 arg, class_2338 arg2) {
		return arg.method_8320(arg2).method_11602(class_3481.field_16443);
	}

	private Optional<class_2338> method_19969(class_1308 arg) {
		return arg.method_18868().method_19543(class_4140.field_19007);
	}

	private boolean method_19981(class_3218 arg, class_1308 arg2) {
		return !this.method_19977(arg, arg2) && this.field_18990 <= 0;
	}

	private boolean method_19982(class_3218 arg, class_1308 arg2) {
		return this.method_19977(arg, arg2) && this.field_18991 <= 0;
	}
}
