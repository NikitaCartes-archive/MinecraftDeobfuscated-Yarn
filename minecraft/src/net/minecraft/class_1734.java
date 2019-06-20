package net.minecraft;

public class class_1734 extends class_1735 {
	private final class_1715 field_7870;
	private final class_1657 field_7868;
	private int field_7869;

	public class_1734(class_1657 arg, class_1715 arg2, class_1263 arg3, int i, int j, int k) {
		super(arg3, i, j, k);
		this.field_7868 = arg;
		this.field_7870 = arg2;
	}

	@Override
	public boolean method_7680(class_1799 arg) {
		return false;
	}

	@Override
	public class_1799 method_7671(int i) {
		if (this.method_7681()) {
			this.field_7869 = this.field_7869 + Math.min(i, this.method_7677().method_7947());
		}

		return super.method_7671(i);
	}

	@Override
	protected void method_7678(class_1799 arg, int i) {
		this.field_7869 += i;
		this.method_7669(arg);
	}

	@Override
	protected void method_7672(int i) {
		this.field_7869 += i;
	}

	@Override
	protected void method_7669(class_1799 arg) {
		if (this.field_7869 > 0) {
			arg.method_7982(this.field_7868.field_6002, this.field_7868, this.field_7869);
		}

		if (this.field_7871 instanceof class_1732) {
			((class_1732)this.field_7871).method_7664(this.field_7868);
		}

		this.field_7869 = 0;
	}

	@Override
	public class_1799 method_7667(class_1657 arg, class_1799 arg2) {
		this.method_7669(arg2);
		class_2371<class_1799> lv = arg.field_6002.method_8433().method_8128(class_3956.field_17545, this.field_7870, arg.field_6002);

		for (int i = 0; i < lv.size(); i++) {
			class_1799 lv2 = this.field_7870.method_5438(i);
			class_1799 lv3 = lv.get(i);
			if (!lv2.method_7960()) {
				this.field_7870.method_5434(i, 1);
				lv2 = this.field_7870.method_5438(i);
			}

			if (!lv3.method_7960()) {
				if (lv2.method_7960()) {
					this.field_7870.method_5447(i, lv3);
				} else if (class_1799.method_7984(lv2, lv3) && class_1799.method_7975(lv2, lv3)) {
					lv3.method_7933(lv2.method_7947());
					this.field_7870.method_5447(i, lv3);
				} else if (!this.field_7868.field_7514.method_7394(lv3)) {
					this.field_7868.method_7328(lv3, false);
				}
			}
		}

		return arg2;
	}
}
