package net.minecraft;

import java.util.Map.Entry;

public class class_1719 extends class_1735 {
	private final class_1657 field_7818;
	private int field_7819;

	public class_1719(class_1657 arg, class_1263 arg2, int i, int j, int k) {
		super(arg2, i, j, k);
		this.field_7818 = arg;
	}

	@Override
	public boolean method_7680(class_1799 arg) {
		return false;
	}

	@Override
	public class_1799 method_7671(int i) {
		if (this.method_7681()) {
			this.field_7819 = this.field_7819 + Math.min(i, this.method_7677().method_7947());
		}

		return super.method_7671(i);
	}

	@Override
	public class_1799 method_7667(class_1657 arg, class_1799 arg2) {
		this.method_7669(arg2);
		super.method_7667(arg, arg2);
		return arg2;
	}

	@Override
	protected void method_7678(class_1799 arg, int i) {
		this.field_7819 += i;
		this.method_7669(arg);
	}

	@Override
	protected void method_7669(class_1799 arg) {
		arg.method_7982(this.field_7818.field_6002, this.field_7818, this.field_7819);
		if (!this.field_7818.field_6002.field_9236) {
			for (Entry<class_2960, Integer> entry : ((class_2609)this.field_7871).method_11198().entrySet()) {
				class_1874 lv = (class_1874)this.field_7818.field_6002.method_8433().method_8130((class_2960)entry.getKey());
				float f;
				if (lv != null) {
					f = lv.method_8171();
				} else {
					f = 0.0F;
				}

				int i = (Integer)entry.getValue();
				if (f == 0.0F) {
					i = 0;
				} else if (f < 1.0F) {
					int j = class_3532.method_15375((float)i * f);
					if (j < class_3532.method_15386((float)i * f) && Math.random() < (double)((float)i * f - (float)j)) {
						j++;
					}

					i = j;
				}

				while (i > 0) {
					int j = class_1303.method_5918(i);
					i -= j;
					this.field_7818
						.field_6002
						.method_8649(
							new class_1303(this.field_7818.field_6002, this.field_7818.field_5987, this.field_7818.field_6010 + 0.5, this.field_7818.field_6035 + 0.5, j)
						);
				}
			}

			((class_1732)this.field_7871).method_7664(this.field_7818);
		}

		this.field_7819 = 0;
	}
}
