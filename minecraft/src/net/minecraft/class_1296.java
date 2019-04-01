package net.minecraft;

import javax.annotation.Nullable;

public abstract class class_1296 extends class_1314 {
	private static final class_2940<Boolean> field_5949 = class_2945.method_12791(class_1296.class, class_2943.field_13323);
	protected int field_5950;
	protected int field_5948;
	protected int field_5947;

	protected class_1296(class_1299<? extends class_1296> arg, class_1937 arg2) {
		super(arg, arg2);
	}

	@Nullable
	public abstract class_1296 method_5613(class_1296 arg);

	protected void method_18249(class_1657 arg, class_1296 arg2) {
	}

	@Override
	public boolean method_5992(class_1657 arg, class_1268 arg2) {
		class_1799 lv = arg.method_5998(arg2);
		class_1792 lv2 = lv.method_7909();
		if (lv2 instanceof class_1826 && ((class_1826)lv2).method_8018(lv.method_7969(), this.method_5864())) {
			if (!this.field_6002.field_9236) {
				class_1296 lv3 = this.method_5613(this);
				if (lv3 != null) {
					lv3.method_5614(-24000);
					lv3.method_5808(this.field_5987, this.field_6010, this.field_6035, 0.0F, 0.0F);
					this.field_6002.method_8649(lv3);
					if (lv.method_7938()) {
						lv3.method_5665(lv.method_7964());
					}

					this.method_18249(arg, lv3);
					if (!arg.field_7503.field_7477) {
						lv.method_7934(1);
					}
				}
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_5949, false);
	}

	public int method_5618() {
		if (this.field_6002.field_9236) {
			return this.field_6011.method_12789(field_5949) ? -1 : 1;
		} else {
			return this.field_5950;
		}
	}

	public void method_5620(int i, boolean bl) {
		int j = this.method_5618();
		j += i * 20;
		if (j > 0) {
			j = 0;
		}

		int l = j - j;
		this.method_5614(j);
		if (bl) {
			this.field_5948 += l;
			if (this.field_5947 == 0) {
				this.field_5947 = 40;
			}
		}

		if (this.method_5618() == 0) {
			this.method_5614(this.field_5948);
		}
	}

	public void method_5615(int i) {
		this.method_5620(i, false);
	}

	public void method_5614(int i) {
		int j = this.field_5950;
		this.field_5950 = i;
		if (j < 0 && i >= 0 || j >= 0 && i < 0) {
			this.field_6011.method_12778(field_5949, i < 0);
			this.method_5619();
		}
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10569("Age", this.method_5618());
		arg.method_10569("ForcedAge", this.field_5948);
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.method_5614(arg.method_10550("Age"));
		this.field_5948 = arg.method_10550("ForcedAge");
	}

	@Override
	public void method_5674(class_2940<?> arg) {
		if (field_5949.equals(arg)) {
			this.method_18382();
		}

		super.method_5674(arg);
	}

	@Override
	public void method_6007() {
		super.method_6007();
		if (this.field_6002.field_9236) {
			if (this.field_5947 > 0) {
				if (this.field_5947 % 4 == 0) {
					this.field_6002
						.method_8406(
							class_2398.field_11211,
							this.field_5987 + (double)(this.field_5974.nextFloat() * this.method_17681() * 2.0F) - (double)this.method_17681(),
							this.field_6010 + 0.5 + (double)(this.field_5974.nextFloat() * this.method_17682()),
							this.field_6035 + (double)(this.field_5974.nextFloat() * this.method_17681() * 2.0F) - (double)this.method_17681(),
							0.0,
							0.0,
							0.0
						);
				}

				this.field_5947--;
			}
		} else if (this.method_5805()) {
			int i = this.method_5618();
			if (i < 0) {
				this.method_5614(++i);
			} else if (i > 0) {
				this.method_5614(--i);
			}
		}
	}

	protected void method_5619() {
	}

	@Override
	public boolean method_6109() {
		return this.method_5618() < 0;
	}
}
