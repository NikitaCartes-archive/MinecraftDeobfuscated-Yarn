package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;

public class class_1368 extends class_1352 {
	protected final class_1314 field_6525;
	private final double field_6520;
	private class_11 field_6523;
	protected class_1417 field_6522;
	private final boolean field_6524;
	protected final List<class_1417> field_6521 = Lists.<class_1417>newArrayList();

	public class_1368(class_1314 arg, double d, boolean bl) {
		this.field_6525 = arg;
		this.field_6520 = d;
		this.field_6524 = bl;
		this.method_6265(1);
		if (!(arg.method_5942() instanceof class_1409)) {
			throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
		}
	}

	@Override
	public boolean method_6264() {
		this.method_6297();
		if (this.field_6524 && this.field_6525.field_6002.method_8530()) {
			return false;
		} else {
			class_1415 lv = this.field_6525.field_6002.method_8557().method_6438(new class_2338(this.field_6525), 0);
			if (lv == null) {
				return false;
			} else {
				this.field_6522 = this.method_6298(lv);
				if (this.field_6522 == null) {
					return false;
				} else {
					class_1409 lv2 = (class_1409)this.field_6525.method_5942();
					boolean bl = lv2.method_6366();
					lv2.method_6363(false);
					this.field_6523 = lv2.method_6348(this.field_6522.method_6429());
					lv2.method_6363(bl);
					if (this.field_6523 != null) {
						return true;
					} else {
						class_243 lv3 = class_1414.method_6373(
							this.field_6525,
							10,
							7,
							new class_243(
								(double)this.field_6522.method_6429().method_10263(),
								(double)this.field_6522.method_6429().method_10264(),
								(double)this.field_6522.method_6429().method_10260()
							)
						);
						if (lv3 == null) {
							return false;
						} else {
							lv2.method_6363(false);
							this.field_6523 = this.field_6525.method_5942().method_6352(lv3.field_1352, lv3.field_1351, lv3.field_1350);
							lv2.method_6363(bl);
							return this.field_6523 != null;
						}
					}
				}
			}
		}
	}

	@Override
	public boolean method_6266() {
		if (this.field_6525.method_5942().method_6357()) {
			return false;
		} else {
			float f = this.field_6525.field_5998 + 4.0F;
			return this.field_6525.method_5831(this.field_6522.method_6429()) > (double)(f * f);
		}
	}

	@Override
	public void method_6269() {
		this.field_6525.method_5942().method_6334(this.field_6523, this.field_6520);
	}

	@Override
	public void method_6270() {
		if (this.field_6525.method_5942().method_6357() || this.field_6525.method_5831(this.field_6522.method_6429()) < 16.0) {
			this.field_6521.add(this.field_6522);
		}
	}

	private class_1417 method_6298(class_1415 arg) {
		class_1417 lv = null;
		int i = Integer.MAX_VALUE;

		for (class_1417 lv2 : arg.method_6405()) {
			int j = lv2.method_6415(
				class_3532.method_15357(this.field_6525.field_5987),
				class_3532.method_15357(this.field_6525.field_6010),
				class_3532.method_15357(this.field_6525.field_6035)
			);
			if (j < i && !this.method_6299(lv2)) {
				lv = lv2;
				i = j;
			}
		}

		return lv;
	}

	private boolean method_6299(class_1417 arg) {
		for (class_1417 lv : this.field_6521) {
			if (arg.method_6429().equals(lv.method_6429())) {
				return true;
			}
		}

		return false;
	}

	private void method_6297() {
		if (this.field_6521.size() > 15) {
			this.field_6521.remove(0);
		}
	}
}
