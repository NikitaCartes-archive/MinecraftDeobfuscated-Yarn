package net.minecraft;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;

public class class_1368 extends class_1352 {
	protected final class_1314 field_6525;
	private final double field_6520;
	private class_11 field_6523;
	private class_2338 field_18412;
	private final boolean field_6524;
	private final List<class_2338> field_18413 = Lists.<class_2338>newArrayList();
	private final int field_18414;
	private final BooleanSupplier field_18415;

	public class_1368(class_1314 arg, double d, boolean bl, int i, BooleanSupplier booleanSupplier) {
		this.field_6525 = arg;
		this.field_6520 = d;
		this.field_6524 = bl;
		this.field_18414 = i;
		this.field_18415 = booleanSupplier;
		this.method_6265(EnumSet.of(class_1352.class_4134.field_18405));
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
			class_3218 lv = (class_3218)this.field_6525.field_6002;
			class_2338 lv2 = new class_2338(this.field_6525);
			class_243 lv3 = class_1414.method_19108(this.field_6525, 15, 7, arg3 -> {
				if (!lv.method_19500(arg3)) {
					return Double.NEGATIVE_INFINITY;
				} else {
					Optional<class_2338> optionalx = lv.method_19494().method_19127(class_4158.field_18501, this::method_19052, arg3, 10, class_4153.class_4155.field_18488);
					return !optionalx.isPresent() ? Double.NEGATIVE_INFINITY : -((class_2338)optionalx.get()).method_10262(lv2);
				}
			});
			if (lv3 == null) {
				return false;
			} else {
				Optional<class_2338> optional = lv.method_19494()
					.method_19127(class_4158.field_18501, this::method_19052, new class_2338(lv3), 10, class_4153.class_4155.field_18488);
				if (!optional.isPresent()) {
					return false;
				} else {
					this.field_18412 = ((class_2338)optional.get()).method_10062();
					class_1409 lv4 = (class_1409)this.field_6525.method_5942();
					boolean bl = lv4.method_6366();
					lv4.method_6363(this.field_18415.getAsBoolean());
					this.field_6523 = lv4.method_6348(this.field_18412);
					lv4.method_6363(bl);
					if (this.field_6523 == null) {
						class_243 lv5 = class_1414.method_6373(
							this.field_6525,
							10,
							7,
							new class_243((double)this.field_18412.method_10263(), (double)this.field_18412.method_10264(), (double)this.field_18412.method_10260())
						);
						if (lv5 == null) {
							return false;
						}

						lv4.method_6363(this.field_18415.getAsBoolean());
						this.field_6523 = this.field_6525.method_5942().method_6352(lv5.field_1352, lv5.field_1351, lv5.field_1350);
						lv4.method_6363(bl);
						if (this.field_6523 == null) {
							return false;
						}
					}

					for (int i = 0; i < this.field_6523.method_38(); i++) {
						class_9 lv6 = this.field_6523.method_40(i);
						class_2338 lv7 = new class_2338(lv6.field_40, lv6.field_39 + 1, lv6.field_38);
						if (class_1343.method_6254(this.field_6525.field_6002, lv7)) {
							this.field_6523 = this.field_6525.method_5942().method_6352((double)lv6.field_40, (double)lv6.field_39, (double)lv6.field_38);
							break;
						}
					}

					return this.field_6523 != null;
				}
			}
		}
	}

	@Override
	public boolean method_6266() {
		return this.field_6525.method_5942().method_6357()
			? false
			: !this.field_18412.method_19769(this.field_6525.method_19538(), (double)(this.field_6525.method_17681() + (float)this.field_18414));
	}

	@Override
	public void method_6269() {
		this.field_6525.method_5942().method_6334(this.field_6523, this.field_6520);
	}

	@Override
	public void method_6270() {
		if (this.field_6525.method_5942().method_6357() || this.field_18412.method_19769(this.field_6525.method_19538(), (double)this.field_18414)) {
			this.field_18413.add(this.field_18412);
		}
	}

	private boolean method_19052(class_2338 arg) {
		for (class_2338 lv : this.field_18413) {
			if (Objects.equals(arg, lv)) {
				return false;
			}
		}

		return true;
	}

	private void method_6297() {
		if (this.field_18413.size() > 15) {
			this.field_18413.remove(0);
		}
	}
}
