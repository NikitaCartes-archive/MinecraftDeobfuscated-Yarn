package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;

public class class_4130 extends class_4097<class_1646> {
	@Nullable
	private class_1799 field_18392;
	private final List<class_1799> field_18393 = Lists.<class_1799>newArrayList();
	private int field_18394;
	private int field_18395;
	private int field_18396;

	public class_4130(int i, int j) {
		super(ImmutableMap.of(class_4140.field_18447, class_4141.field_18456), i, j);
	}

	public boolean method_19599(class_3218 arg, class_1646 arg2) {
		class_4095<?> lv = arg2.method_18868();
		if (!lv.method_18904(class_4140.field_18447).isPresent()) {
			return false;
		} else {
			class_1309 lv2 = (class_1309)lv.method_18904(class_4140.field_18447).get();
			return lv2.method_5864() == class_1299.field_6097 && arg2.method_5805() && lv2.method_5805() && !arg2.method_6109() && arg2.method_5858(lv2) <= 17.0;
		}
	}

	public boolean method_19600(class_3218 arg, class_1646 arg2, long l) {
		return this.method_19599(arg, arg2) && this.field_18396 > 0 && arg2.method_18868().method_18904(class_4140.field_18447).isPresent();
	}

	public void method_19602(class_3218 arg, class_1646 arg2, long l) {
		super.method_18920(arg, arg2, l);
		this.method_19603(arg2);
		this.field_18394 = 0;
		this.field_18395 = 0;
		this.field_18396 = 40;
	}

	public void method_19604(class_3218 arg, class_1646 arg2, long l) {
		class_1309 lv = this.method_19603(arg2);
		this.method_19027(lv, arg2);
		if (!this.field_18393.isEmpty()) {
			this.method_19026(arg2);
		} else {
			arg2.method_5673(class_1304.field_6173, class_1799.field_8037);
			this.field_18396 = Math.min(this.field_18396, 40);
		}

		this.field_18396--;
	}

	public void method_19605(class_3218 arg, class_1646 arg2, long l) {
		super.method_18926(arg, arg2, l);
		arg2.method_18868().method_18875(class_4140.field_18447);
		arg2.method_5673(class_1304.field_6173, class_1799.field_8037);
		this.field_18392 = null;
	}

	private void method_19027(class_1309 arg, class_1646 arg2) {
		boolean bl = false;
		class_1799 lv = arg.method_6047();
		if (this.field_18392 == null || !class_1799.method_7984(this.field_18392, lv)) {
			this.field_18392 = lv;
			bl = true;
			this.field_18393.clear();
		}

		if (bl && !this.field_18392.method_7960()) {
			this.method_19601(arg2);
			if (!this.field_18393.isEmpty()) {
				this.field_18396 = 900;
				this.method_19598(arg2);
			}
		}
	}

	private void method_19598(class_1646 arg) {
		arg.method_5673(class_1304.field_6173, (class_1799)this.field_18393.get(0));
	}

	private void method_19601(class_1646 arg) {
		for (class_1914 lv : arg.method_8264()) {
			if (!lv.method_8255() && this.method_19028(lv)) {
				this.field_18393.add(lv.method_8250());
			}
		}
	}

	private boolean method_19028(class_1914 arg) {
		return class_1799.method_7984(this.field_18392, arg.method_19272()) || class_1799.method_7984(this.field_18392, arg.method_8247());
	}

	private class_1309 method_19603(class_1646 arg) {
		class_4095<?> lv = arg.method_18868();
		class_1309 lv2 = (class_1309)lv.method_18904(class_4140.field_18447).get();
		lv.method_18878(class_4140.field_18446, new class_4102(lv2));
		return lv2;
	}

	private void method_19026(class_1646 arg) {
		if (this.field_18393.size() >= 2 && ++this.field_18394 >= 40) {
			this.field_18395++;
			this.field_18394 = 0;
			if (this.field_18395 > this.field_18393.size() - 1) {
				this.field_18395 = 0;
			}

			arg.method_5673(class_1304.field_6173, (class_1799)this.field_18393.get(this.field_18395));
		}
	}
}
