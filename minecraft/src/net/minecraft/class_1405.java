package net.minecraft;

import javax.annotation.Nullable;

public abstract class class_1405 extends class_1352 {
	protected final class_1314 field_6660;
	protected final boolean field_6658;
	private final boolean field_6663;
	private int field_6662;
	private int field_6661;
	private int field_6659;
	protected class_1309 field_6664;
	protected int field_6657 = 60;

	public class_1405(class_1314 arg, boolean bl) {
		this(arg, bl, false);
	}

	public class_1405(class_1314 arg, boolean bl, boolean bl2) {
		this.field_6660 = arg;
		this.field_6658 = bl;
		this.field_6663 = bl2;
	}

	@Override
	public boolean method_6266() {
		class_1309 lv = this.field_6660.method_5968();
		if (lv == null) {
			lv = this.field_6664;
		}

		if (lv == null) {
			return false;
		} else if (!lv.method_5805()) {
			return false;
		} else {
			class_270 lv2 = this.field_6660.method_5781();
			class_270 lv3 = lv.method_5781();
			if (lv2 != null && lv3 == lv2) {
				return false;
			} else {
				double d = this.method_6326();
				if (this.field_6660.method_5858(lv) > d * d) {
					return false;
				} else {
					if (this.field_6658) {
						if (this.field_6660.method_5985().method_6369(lv)) {
							this.field_6659 = 0;
						} else if (++this.field_6659 > this.field_6657) {
							return false;
						}
					}

					if (lv instanceof class_1657 && ((class_1657)lv).field_7503.field_7480) {
						return false;
					} else {
						this.field_6660.method_5980(lv);
						return true;
					}
				}
			}
		}
	}

	protected double method_6326() {
		class_1324 lv = this.field_6660.method_5996(class_1612.field_7365);
		return lv == null ? 16.0 : lv.method_6194();
	}

	@Override
	public void method_6269() {
		this.field_6662 = 0;
		this.field_6661 = 0;
		this.field_6659 = 0;
	}

	@Override
	public void method_6270() {
		this.field_6660.method_5980(null);
		this.field_6664 = null;
	}

	public static boolean method_6327(class_1308 arg, @Nullable class_1309 arg2, boolean bl, boolean bl2) {
		if (arg2 == null) {
			return false;
		} else if (arg2 == arg) {
			return false;
		} else if (!arg2.method_5805()) {
			return false;
		} else if (!arg.method_5973(arg2.getClass())) {
			return false;
		} else if (arg.method_5722(arg2)) {
			return false;
		} else {
			if (arg instanceof class_1312 && ((class_1312)arg).method_6139() != null) {
				if (arg2 instanceof class_1312 && ((class_1312)arg).method_6139().equals(((class_1312)arg2).method_6139())) {
					return false;
				}

				if (arg2 == ((class_1312)arg).method_6140()) {
					return false;
				}
			} else if (arg2 instanceof class_1657 && !bl && ((class_1657)arg2).field_7503.field_7480) {
				return false;
			}

			return !bl2 || arg.method_5985().method_6369(arg2);
		}
	}

	protected boolean method_6328(@Nullable class_1309 arg, boolean bl) {
		if (!method_6327(this.field_6660, arg, bl, this.field_6658)) {
			return false;
		} else if (!this.field_6660.method_6146(new class_2338(arg))) {
			return false;
		} else {
			if (this.field_6663) {
				if (--this.field_6661 <= 0) {
					this.field_6662 = 0;
				}

				if (this.field_6662 == 0) {
					this.field_6662 = this.method_6329(arg) ? 1 : 2;
				}

				if (this.field_6662 == 2) {
					return false;
				}
			}

			return true;
		}
	}

	private boolean method_6329(class_1309 arg) {
		this.field_6661 = 10 + this.field_6660.method_6051().nextInt(5);
		class_11 lv = this.field_6660.method_5942().method_6349(arg);
		if (lv == null) {
			return false;
		} else {
			class_9 lv2 = lv.method_45();
			if (lv2 == null) {
				return false;
			} else {
				int i = lv2.field_40 - class_3532.method_15357(arg.field_5987);
				int j = lv2.field_38 - class_3532.method_15357(arg.field_6035);
				return (double)(i * i + j * j) <= 2.25;
			}
		}
	}

	public class_1405 method_6330(int i) {
		this.field_6657 = i;
		return this;
	}
}
