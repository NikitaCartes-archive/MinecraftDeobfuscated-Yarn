package net.minecraft;

public abstract class class_1492 extends class_1496 {
	private static final class_2940<Boolean> field_6943 = class_2945.method_12791(class_1492.class, class_2943.field_13323);

	protected class_1492(class_1299<?> arg, class_1937 arg2) {
		super(arg, arg2);
		this.field_6964 = false;
	}

	@Override
	protected void method_5693() {
		super.method_5693();
		this.field_6011.method_12784(field_6943, false);
	}

	@Override
	protected void method_6001() {
		super.method_6001();
		this.method_5996(class_1612.field_7359).method_6192((double)this.method_6754());
		this.method_5996(class_1612.field_7357).method_6192(0.175F);
		this.method_5996(field_6974).method_6192(0.5);
	}

	public boolean method_6703() {
		return this.field_6011.method_12789(field_6943);
	}

	public void method_6704(boolean bl) {
		this.field_6011.method_12778(field_6943, bl);
	}

	@Override
	protected int method_6750() {
		return this.method_6703() ? 17 : super.method_6750();
	}

	@Override
	public double method_5621() {
		return super.method_5621() - 0.25;
	}

	@Override
	protected class_3414 method_6747() {
		super.method_6747();
		return class_3417.field_14661;
	}

	@Override
	protected void method_16078() {
		super.method_16078();
		if (this.method_6703()) {
			if (!this.field_6002.field_9236) {
				this.method_5706(class_2246.field_10034);
			}

			this.method_6704(false);
		}
	}

	@Override
	public void method_5652(class_2487 arg) {
		super.method_5652(arg);
		arg.method_10556("ChestedHorse", this.method_6703());
		if (this.method_6703()) {
			class_2499 lv = new class_2499();

			for (int i = 2; i < this.field_6962.method_5439(); i++) {
				class_1799 lv2 = this.field_6962.method_5438(i);
				if (!lv2.method_7960()) {
					class_2487 lv3 = new class_2487();
					lv3.method_10567("Slot", (byte)i);
					lv2.method_7953(lv3);
					lv.method_10606(lv3);
				}
			}

			arg.method_10566("Items", lv);
		}
	}

	@Override
	public void method_5749(class_2487 arg) {
		super.method_5749(arg);
		this.method_6704(arg.method_10577("ChestedHorse"));
		if (this.method_6703()) {
			class_2499 lv = arg.method_10554("Items", 10);
			this.method_6721();

			for (int i = 0; i < lv.size(); i++) {
				class_2487 lv2 = lv.method_10602(i);
				int j = lv2.method_10571("Slot") & 255;
				if (j >= 2 && j < this.field_6962.method_5439()) {
					this.field_6962.method_5447(j, class_1799.method_7915(lv2));
				}
			}
		}

		this.method_6731();
	}

	@Override
	public boolean method_5758(int i, class_1799 arg) {
		if (i == 499) {
			if (this.method_6703() && arg.method_7960()) {
				this.method_6704(false);
				this.method_6721();
				return true;
			}

			if (!this.method_6703() && arg.method_7909() == class_2246.field_10034.method_8389()) {
				this.method_6704(true);
				this.method_6721();
				return true;
			}
		}

		return super.method_5758(i, arg);
	}

	@Override
	public boolean method_5992(class_1657 arg, class_1268 arg2) {
		class_1799 lv = arg.method_5998(arg2);
		if (lv.method_7909() instanceof class_1826) {
			return super.method_5992(arg, arg2);
		} else {
			if (!this.method_6109()) {
				if (this.method_6727() && arg.method_5715()) {
					this.method_6722(arg);
					return true;
				}

				if (this.method_5782()) {
					return super.method_5992(arg, arg2);
				}
			}

			if (!lv.method_7960()) {
				boolean bl = this.method_6742(arg, lv);
				if (!bl) {
					if (!this.method_6727() || lv.method_7909() == class_1802.field_8448) {
						if (lv.method_7920(arg, this, arg2)) {
							return true;
						} else {
							this.method_6757();
							return true;
						}
					}

					if (!this.method_6703() && lv.method_7909() == class_2246.field_10034.method_8389()) {
						this.method_6704(true);
						this.method_6705();
						bl = true;
						this.method_6721();
					}

					if (!this.method_6109() && !this.method_6725() && lv.method_7909() == class_1802.field_8175) {
						this.method_6722(arg);
						return true;
					}
				}

				if (bl) {
					if (!arg.field_7503.field_7477) {
						lv.method_7934(1);
					}

					return true;
				}
			}

			if (this.method_6109()) {
				return super.method_5992(arg, arg2);
			} else {
				this.method_6726(arg);
				return true;
			}
		}
	}

	protected void method_6705() {
		this.method_5783(class_3417.field_14598, 1.0F, (this.field_5974.nextFloat() - this.field_5974.nextFloat()) * 0.2F + 1.0F);
	}

	public int method_6702() {
		return 5;
	}
}
