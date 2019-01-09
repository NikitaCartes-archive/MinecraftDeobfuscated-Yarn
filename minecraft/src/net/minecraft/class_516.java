package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_516 {
	private final List<class_1860> field_3144 = Lists.<class_1860>newArrayList();
	private final Set<class_1860> field_3146 = Sets.<class_1860>newHashSet();
	private final Set<class_1860> field_3145 = Sets.<class_1860>newHashSet();
	private final Set<class_1860> field_3147 = Sets.<class_1860>newHashSet();
	private boolean field_3148 = true;

	public boolean method_2652() {
		return !this.field_3147.isEmpty();
	}

	public void method_2647(class_3439 arg) {
		for (class_1860 lv : this.field_3144) {
			if (arg.method_14878(lv)) {
				this.field_3147.add(lv);
			}
		}
	}

	public void method_2649(class_1662 arg, int i, int j, class_3439 arg2) {
		for (int k = 0; k < this.field_3144.size(); k++) {
			class_1860 lv = (class_1860)this.field_3144.get(k);
			boolean bl = lv.method_8113(i, j) && arg2.method_14878(lv);
			if (bl) {
				this.field_3145.add(lv);
			} else {
				this.field_3145.remove(lv);
			}

			if (bl && arg.method_7402(lv, null)) {
				this.field_3146.add(lv);
			} else {
				this.field_3146.remove(lv);
			}
		}
	}

	public boolean method_2653(class_1860 arg) {
		return this.field_3146.contains(arg);
	}

	public boolean method_2655() {
		return !this.field_3146.isEmpty();
	}

	public boolean method_2657() {
		return !this.field_3145.isEmpty();
	}

	public List<class_1860> method_2650() {
		return this.field_3144;
	}

	public List<class_1860> method_2651(boolean bl) {
		List<class_1860> list = Lists.<class_1860>newArrayList();
		Set<class_1860> set = bl ? this.field_3146 : this.field_3145;

		for (class_1860 lv : this.field_3144) {
			if (set.contains(lv)) {
				list.add(lv);
			}
		}

		return list;
	}

	public List<class_1860> method_2648(boolean bl) {
		List<class_1860> list = Lists.<class_1860>newArrayList();

		for (class_1860 lv : this.field_3144) {
			if (this.field_3145.contains(lv) && this.field_3146.contains(lv) == bl) {
				list.add(lv);
			}
		}

		return list;
	}

	public void method_2654(class_1860 arg) {
		this.field_3144.add(arg);
		if (this.field_3148) {
			class_1799 lv = ((class_1860)this.field_3144.get(0)).method_8110();
			class_1799 lv2 = arg.method_8110();
			this.field_3148 = class_1799.method_7984(lv, lv2) && class_1799.method_7975(lv, lv2);
		}
	}

	public boolean method_2656() {
		return this.field_3148;
	}
}
