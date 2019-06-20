package net.minecraft;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;

public class class_2958<C extends class_1263> extends class_2955<C> {
	private boolean field_13351;

	public class_2958(class_1729<C> arg) {
		super(arg);
	}

	@Override
	protected void method_12821(class_1860<C> arg, boolean bl) {
		this.field_13351 = this.field_13348.method_7652(arg);
		int i = this.field_13347.method_7407(arg, null);
		if (this.field_13351) {
			class_1799 lv = this.field_13348.method_7611(0).method_7677();
			if (lv.method_7960() || i <= lv.method_7947()) {
				return;
			}
		}

		int j = this.method_12819(bl, i, this.field_13351);
		IntList intList = new IntArrayList();
		if (this.field_13347.method_7406(arg, intList, j)) {
			if (!this.field_13351) {
				this.method_12820(this.field_13348.method_7655());
				this.method_12820(0);
			}

			this.method_12827(j, intList);
		}
	}

	@Override
	protected void method_12822() {
		this.method_12820(this.field_13348.method_7655());
		super.method_12822();
	}

	protected void method_12827(int i, IntList intList) {
		Iterator<Integer> iterator = intList.iterator();
		class_1735 lv = this.field_13348.method_7611(0);
		class_1799 lv2 = class_1662.method_7405((Integer)iterator.next());
		if (!lv2.method_7960()) {
			int j = Math.min(lv2.method_7914(), i);
			if (this.field_13351) {
				j -= lv.method_7677().method_7947();
			}

			for (int k = 0; k < j; k++) {
				this.method_12824(lv, lv2);
			}
		}
	}
}
