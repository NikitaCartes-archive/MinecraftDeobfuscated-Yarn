package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;

public class class_2674 {
	private final class_1937 field_12249;
	private final class_2338 field_12250;
	private final boolean field_12247;
	private final class_2338 field_12244;
	private final class_2350 field_12243;
	private final List<class_2338> field_12245 = Lists.<class_2338>newArrayList();
	private final List<class_2338> field_12246 = Lists.<class_2338>newArrayList();
	private final class_2350 field_12248;

	public class_2674(class_1937 arg, class_2338 arg2, class_2350 arg3, boolean bl) {
		this.field_12249 = arg;
		this.field_12250 = arg2;
		this.field_12248 = arg3;
		this.field_12247 = bl;
		if (bl) {
			this.field_12243 = arg3;
			this.field_12244 = arg2.method_10093(arg3);
		} else {
			this.field_12243 = arg3.method_10153();
			this.field_12244 = arg2.method_10079(arg3, 2);
		}
	}

	public boolean method_11537() {
		this.field_12245.clear();
		this.field_12246.clear();
		class_2680 lv = this.field_12249.method_8320(this.field_12244);
		if (!class_2665.method_11484(lv, this.field_12249, this.field_12244, this.field_12243, false, this.field_12248)) {
			if (this.field_12247 && lv.method_11586() == class_3619.field_15971) {
				this.field_12246.add(this.field_12244);
				return true;
			} else {
				return false;
			}
		} else if (!this.method_11540(this.field_12244, this.field_12243)) {
			return false;
		} else {
			for (int i = 0; i < this.field_12245.size(); i++) {
				class_2338 lv2 = (class_2338)this.field_12245.get(i);
				if (this.field_12249.method_8320(lv2).method_11614() == class_2246.field_10030 && !this.method_11538(lv2)) {
					return false;
				}
			}

			return true;
		}
	}

	private boolean method_11540(class_2338 arg, class_2350 arg2) {
		class_2680 lv = this.field_12249.method_8320(arg);
		class_2248 lv2 = lv.method_11614();
		if (lv.method_11588()) {
			return true;
		} else if (!class_2665.method_11484(lv, this.field_12249, arg, this.field_12243, false, arg2)) {
			return true;
		} else if (arg.equals(this.field_12250)) {
			return true;
		} else if (this.field_12245.contains(arg)) {
			return true;
		} else {
			int i = 1;
			if (i + this.field_12245.size() > 12) {
				return false;
			} else {
				while (lv2 == class_2246.field_10030) {
					class_2338 lv3 = arg.method_10079(this.field_12243.method_10153(), i);
					lv = this.field_12249.method_8320(lv3);
					lv2 = lv.method_11614();
					if (lv.method_11588()
						|| !class_2665.method_11484(lv, this.field_12249, lv3, this.field_12243, false, this.field_12243.method_10153())
						|| lv3.equals(this.field_12250)) {
						break;
					}

					if (++i + this.field_12245.size() > 12) {
						return false;
					}
				}

				int j = 0;

				for (int k = i - 1; k >= 0; k--) {
					this.field_12245.add(arg.method_10079(this.field_12243.method_10153(), k));
					j++;
				}

				int k = 1;

				while (true) {
					class_2338 lv4 = arg.method_10079(this.field_12243, k);
					int l = this.field_12245.indexOf(lv4);
					if (l > -1) {
						this.method_11539(j, l);

						for (int m = 0; m <= l + j; m++) {
							class_2338 lv5 = (class_2338)this.field_12245.get(m);
							if (this.field_12249.method_8320(lv5).method_11614() == class_2246.field_10030 && !this.method_11538(lv5)) {
								return false;
							}
						}

						return true;
					}

					lv = this.field_12249.method_8320(lv4);
					if (lv.method_11588()) {
						return true;
					}

					if (!class_2665.method_11484(lv, this.field_12249, lv4, this.field_12243, true, this.field_12243) || lv4.equals(this.field_12250)) {
						return false;
					}

					if (lv.method_11586() == class_3619.field_15971) {
						this.field_12246.add(lv4);
						return true;
					}

					if (this.field_12245.size() >= 12) {
						return false;
					}

					this.field_12245.add(lv4);
					j++;
					k++;
				}
			}
		}
	}

	private void method_11539(int i, int j) {
		List<class_2338> list = Lists.<class_2338>newArrayList();
		List<class_2338> list2 = Lists.<class_2338>newArrayList();
		List<class_2338> list3 = Lists.<class_2338>newArrayList();
		list.addAll(this.field_12245.subList(0, j));
		list2.addAll(this.field_12245.subList(this.field_12245.size() - i, this.field_12245.size()));
		list3.addAll(this.field_12245.subList(j, this.field_12245.size() - i));
		this.field_12245.clear();
		this.field_12245.addAll(list);
		this.field_12245.addAll(list2);
		this.field_12245.addAll(list3);
	}

	private boolean method_11538(class_2338 arg) {
		for (class_2350 lv : class_2350.values()) {
			if (lv.method_10166() != this.field_12243.method_10166() && !this.method_11540(arg.method_10093(lv), lv)) {
				return false;
			}
		}

		return true;
	}

	public List<class_2338> method_11541() {
		return this.field_12245;
	}

	public List<class_2338> method_11536() {
		return this.field_12246;
	}
}
