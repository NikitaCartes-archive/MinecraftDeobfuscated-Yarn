package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_11 {
	private final class_9[] field_52;
	private class_9[] field_57 = new class_9[0];
	private class_9[] field_55 = new class_9[0];
	private class_9 field_56;
	private int field_54;
	private int field_53;

	public class_11(class_9[] args) {
		this.field_52 = args;
		this.field_53 = args.length;
	}

	public void method_44() {
		this.field_54++;
	}

	public boolean method_46() {
		return this.field_54 >= this.field_53;
	}

	@Nullable
	public class_9 method_45() {
		return this.field_53 > 0 ? this.field_52[this.field_53 - 1] : null;
	}

	public class_9 method_40(int i) {
		return this.field_52[i];
	}

	public void method_33(int i, class_9 arg) {
		this.field_52[i] = arg;
	}

	public int method_38() {
		return this.field_53;
	}

	public void method_36(int i) {
		this.field_53 = i;
	}

	public int method_39() {
		return this.field_54;
	}

	public void method_42(int i) {
		this.field_54 = i;
	}

	public class_243 method_47(class_1297 arg, int i) {
		double d = (double)this.field_52[i].field_40 + (double)((int)(arg.field_5998 + 1.0F)) * 0.5;
		double e = (double)this.field_52[i].field_39;
		double f = (double)this.field_52[i].field_38 + (double)((int)(arg.field_5998 + 1.0F)) * 0.5;
		return new class_243(d, e, f);
	}

	public class_243 method_49(class_1297 arg) {
		return this.method_47(arg, this.field_54);
	}

	public class_243 method_35() {
		class_9 lv = this.field_52[this.field_54];
		return new class_243((double)lv.field_40, (double)lv.field_39, (double)lv.field_38);
	}

	public boolean method_41(class_11 arg) {
		if (arg == null) {
			return false;
		} else if (arg.field_52.length != this.field_52.length) {
			return false;
		} else {
			for (int i = 0; i < this.field_52.length; i++) {
				if (this.field_52[i].field_40 != arg.field_52[i].field_40
					|| this.field_52[i].field_39 != arg.field_52[i].field_39
					|| this.field_52[i].field_38 != arg.field_52[i].field_38) {
					return false;
				}
			}

			return true;
		}
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
		class_9[] lvs = new class_9[arg.readInt()];

		for (int j = 0; j < lvs.length; j++) {
			lvs[j] = class_9.method_28(arg);
		}

		class_9[] lvs2 = new class_9[arg.readInt()];

		for (int k = 0; k < lvs2.length; k++) {
			lvs2[k] = class_9.method_28(arg);
		}

		class_9[] lvs3 = new class_9[arg.readInt()];

		for (int l = 0; l < lvs3.length; l++) {
			lvs3[l] = class_9.method_28(arg);
		}

		class_11 lv2 = new class_11(lvs);
		lv2.field_57 = lvs2;
		lv2.field_55 = lvs3;
		lv2.field_56 = lv;
		lv2.field_54 = i;
		return lv2;
	}
}
