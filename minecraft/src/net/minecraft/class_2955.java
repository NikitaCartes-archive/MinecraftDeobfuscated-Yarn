package net.minecraft;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_2955<C extends class_1263> implements class_2952<Integer> {
	protected static final Logger field_13349 = LogManager.getLogger();
	protected final class_1662 field_13347 = new class_1662();
	protected class_1661 field_13350;
	protected class_1729<C> field_13348;

	public class_2955(class_1729<C> arg) {
		this.field_13348 = arg;
	}

	public void method_12826(class_3222 arg, @Nullable class_1860<C> arg2, boolean bl) {
		if (arg2 != null && arg.method_14253().method_14878(arg2)) {
			this.field_13350 = arg.field_7514;
			if (this.method_12825() || arg.method_7337()) {
				this.field_13347.method_7409();
				arg.field_7514.method_7387(this.field_13347);
				this.field_13348.method_7654(this.field_13347);
				if (this.field_13347.method_7402(arg2, null)) {
					this.method_12821(arg2, bl);
				} else {
					this.method_12822();
					arg.field_13987.method_14364(new class_2695(arg.field_7512.field_7763, arg2));
				}

				arg.field_7514.method_5431();
			}
		}
	}

	protected void method_12822() {
		for (int i = 0; i < this.field_13348.method_7653() * this.field_13348.method_7656() + 1; i++) {
			if (i != this.field_13348.method_7655() || !(this.field_13348 instanceof class_1714) && !(this.field_13348 instanceof class_1723)) {
				this.method_12820(i);
			}
		}

		this.field_13348.method_7657();
	}

	protected void method_12820(int i) {
		class_1799 lv = this.field_13348.method_7611(i).method_7677();
		if (!lv.method_7960()) {
			for (; lv.method_7947() > 0; this.field_13348.method_7611(i).method_7671(1)) {
				int j = this.field_13350.method_7390(lv);
				if (j == -1) {
					j = this.field_13350.method_7376();
				}

				class_1799 lv2 = lv.method_7972();
				lv2.method_7939(1);
				if (!this.field_13350.method_7367(j, lv2)) {
					field_13349.error("Can't find any space for item in the inventory");
				}
			}
		}
	}

	protected void method_12821(class_1860<C> arg, boolean bl) {
		boolean bl2 = this.field_13348.method_7652(arg);
		int i = this.field_13347.method_7407(arg, null);
		if (bl2) {
			for (int j = 0; j < this.field_13348.method_7656() * this.field_13348.method_7653() + 1; j++) {
				if (j != this.field_13348.method_7655()) {
					class_1799 lv = this.field_13348.method_7611(j).method_7677();
					if (!lv.method_7960() && Math.min(i, lv.method_7914()) < lv.method_7947() + 1) {
						return;
					}
				}
			}
		}

		int jx = this.method_12819(bl, i, bl2);
		IntList intList = new IntArrayList();
		if (this.field_13347.method_7406(arg, intList, jx)) {
			int k = jx;

			for (int l : intList) {
				int m = class_1662.method_7405(l).method_7914();
				if (m < k) {
					k = m;
				}
			}

			if (this.field_13347.method_7406(arg, intList, k)) {
				this.method_12822();
				this.method_12816(this.field_13348.method_7653(), this.field_13348.method_7656(), this.field_13348.method_7655(), arg, intList.iterator(), k);
			}
		}
	}

	@Override
	public void method_12815(Iterator<Integer> iterator, int i, int j, int k, int l) {
		class_1735 lv = this.field_13348.method_7611(i);
		class_1799 lv2 = class_1662.method_7405((Integer)iterator.next());
		if (!lv2.method_7960()) {
			for (int m = 0; m < j; m++) {
				this.method_12824(lv, lv2);
			}
		}
	}

	protected int method_12819(boolean bl, int i, boolean bl2) {
		int j = 1;
		if (bl) {
			j = i;
		} else if (bl2) {
			j = 64;

			for (int k = 0; k < this.field_13348.method_7653() * this.field_13348.method_7656() + 1; k++) {
				if (k != this.field_13348.method_7655()) {
					class_1799 lv = this.field_13348.method_7611(k).method_7677();
					if (!lv.method_7960() && j > lv.method_7947()) {
						j = lv.method_7947();
					}
				}
			}

			if (j < 64) {
				j++;
			}
		}

		return j;
	}

	protected void method_12824(class_1735 arg, class_1799 arg2) {
		int i = this.field_13350.method_7371(arg2);
		if (i != -1) {
			class_1799 lv = this.field_13350.method_5438(i).method_7972();
			if (!lv.method_7960()) {
				if (lv.method_7947() > 1) {
					this.field_13350.method_5434(i, 1);
				} else {
					this.field_13350.method_5441(i);
				}

				lv.method_7939(1);
				if (arg.method_7677().method_7960()) {
					arg.method_7673(lv);
				} else {
					arg.method_7677().method_7933(1);
				}
			}
		}
	}

	private boolean method_12825() {
		List<class_1799> list = Lists.<class_1799>newArrayList();
		int i = this.method_12823();

		for (int j = 0; j < this.field_13348.method_7653() * this.field_13348.method_7656() + 1; j++) {
			if (j != this.field_13348.method_7655()) {
				class_1799 lv = this.field_13348.method_7611(j).method_7677().method_7972();
				if (!lv.method_7960()) {
					int k = this.field_13350.method_7390(lv);
					if (k == -1 && list.size() <= i) {
						for (class_1799 lv2 : list) {
							if (lv2.method_7962(lv) && lv2.method_7947() != lv2.method_7914() && lv2.method_7947() + lv.method_7947() <= lv2.method_7914()) {
								lv2.method_7933(lv.method_7947());
								lv.method_7939(0);
								break;
							}
						}

						if (!lv.method_7960()) {
							if (list.size() >= i) {
								return false;
							}

							list.add(lv);
						}
					} else if (k == -1) {
						return false;
					}
				}
			}
		}

		return true;
	}

	private int method_12823() {
		int i = 0;

		for (class_1799 lv : this.field_13350.field_7547) {
			if (lv.method_7960()) {
				i++;
			}
		}

		return i;
	}
}
