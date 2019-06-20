package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_11 {
	private final List<class_9> field_52;
	private class_9[] field_57 = new class_9[0];
	private class_9[] field_55 = new class_9[0];
	private class_9 field_56;
	private int field_54;

	public class_11(List<class_9> list) {
		this.field_52 = list;
	}

	public void method_44() {
		this.field_54++;
	}

	public boolean method_46() {
		return this.field_54 >= this.field_52.size();
	}

	@Nullable
	public class_9 method_45() {
		return !this.field_52.isEmpty() ? (class_9)this.field_52.get(this.field_52.size() - 1) : null;
	}

	public class_9 method_40(int i) {
		return (class_9)this.field_52.get(i);
	}

	public List<class_9> method_19314() {
		return this.field_52;
	}

	public void method_36(int i) {
		if (this.field_52.size() > i) {
			this.field_52.subList(i, this.field_52.size()).clear();
		}
	}

	public void method_33(int i, class_9 arg) {
		this.field_52.set(i, arg);
	}

	public int method_38() {
		return this.field_52.size();
	}

	public int method_39() {
		return this.field_54;
	}

	public void method_42(int i) {
		this.field_54 = i;
	}

	public class_243 method_47(class_1297 arg, int i) {
		class_9 lv = (class_9)this.field_52.get(i);
		double d = (double)lv.field_40 + (double)((int)(arg.method_17681() + 1.0F)) * 0.5;
		double e = (double)lv.field_39;
		double f = (double)lv.field_38 + (double)((int)(arg.method_17681() + 1.0F)) * 0.5;
		return new class_243(d, e, f);
	}

	public class_243 method_49(class_1297 arg) {
		return this.method_47(arg, this.field_54);
	}

	public class_243 method_35() {
		class_9 lv = (class_9)this.field_52.get(this.field_54);
		return new class_243((double)lv.field_40, (double)lv.field_39, (double)lv.field_38);
	}

	public boolean method_41(@Nullable class_11 arg) {
		if (arg == null) {
			return false;
		} else if (arg.field_52.size() != this.field_52.size()) {
			return false;
		} else {
			for (int i = 0; i < this.field_52.size(); i++) {
				class_9 lv = (class_9)this.field_52.get(i);
				class_9 lv2 = (class_9)arg.field_52.get(i);
				if (lv.field_40 != lv2.field_40 || lv.field_39 != lv2.field_39 || lv.field_38 != lv2.field_38) {
					return false;
				}
			}

			return true;
		}
	}

	public boolean method_19313(class_2338 arg) {
		class_9 lv = this.method_45();
		return lv == null ? false : arg.method_19771(new class_2382(lv.field_40, lv.field_39, lv.field_38), 2.0);
	}

	@Environment(EnvType.CLIENT)
	public class_9[] method_43() {
		return this.field_57;
	}

	@Environment(EnvType.CLIENT)
	public class_9[] method_37() {
		return this.field_55;
	}

	@Nullable
	public class_9 method_48() {
		return this.field_56;
	}

	@Environment(EnvType.CLIENT)
	public static class_11 method_34(class_2540 arg) {
		int i = arg.readInt();
		class_9 lv = class_9.method_28(arg);
		List<class_9> list = Lists.<class_9>newArrayList();
		int j = arg.readInt();

		for (int k = 0; k < j; k++) {
			list.add(class_9.method_28(arg));
		}

		class_9[] lvs = new class_9[arg.readInt()];

		for (int l = 0; l < lvs.length; l++) {
			lvs[l] = class_9.method_28(arg);
		}

		class_9[] lvs2 = new class_9[arg.readInt()];

		for (int m = 0; m < lvs2.length; m++) {
			lvs2[m] = class_9.method_28(arg);
		}

		class_11 lv2 = new class_11(list);
		lv2.field_57 = lvs;
		lv2.field_55 = lvs2;
		lv2.field_56 = lv;
		lv2.field_54 = i;
		return lv2;
	}

	public String toString() {
		return "Path(length=" + this.field_52.size() + ")";
	}
}
