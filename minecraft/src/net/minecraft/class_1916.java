package net.minecraft;

import java.util.ArrayList;
import javax.annotation.Nullable;

public class class_1916 extends ArrayList<class_1914> {
	public class_1916() {
	}

	public class_1916(class_2487 arg) {
		class_2499 lv = arg.method_10554("Recipes", 10);

		for (int i = 0; i < lv.size(); i++) {
			this.add(new class_1914(lv.method_10602(i)));
		}
	}

	@Nullable
	public class_1914 method_8267(class_1799 arg, class_1799 arg2, int i) {
		if (i > 0 && i < this.size()) {
			class_1914 lv = (class_1914)this.get(i);
			return lv.method_16952(arg, arg2) ? lv : null;
		} else {
			for (int j = 0; j < this.size(); j++) {
				class_1914 lv2 = (class_1914)this.get(j);
				if (lv2.method_16952(arg, arg2)) {
					return lv2;
				}
			}

			return null;
		}
	}

	public void method_8270(class_2540 arg) {
		arg.writeByte((byte)(this.size() & 0xFF));

		for (int i = 0; i < this.size(); i++) {
			class_1914 lv = (class_1914)this.get(i);
			arg.method_10793(lv.method_8246());
			arg.method_10793(lv.method_8250());
			class_1799 lv2 = lv.method_8247();
			arg.writeBoolean(!lv2.method_7960());
			if (!lv2.method_7960()) {
				arg.method_10793(lv2);
			}

			arg.writeBoolean(lv.method_8255());
			arg.writeInt(lv.method_8249());
			arg.writeInt(lv.method_8248());
		}
	}

	public static class_1916 method_8265(class_2540 arg) {
		class_1916 lv = new class_1916();
		int i = arg.readByte() & 255;

		for (int j = 0; j < i; j++) {
			class_1799 lv2 = arg.method_10819();
			class_1799 lv3 = arg.method_10819();
			class_1799 lv4 = class_1799.field_8037;
			if (arg.readBoolean()) {
				lv4 = arg.method_10819();
			}

			boolean bl = arg.readBoolean();
			int k = arg.readInt();
			int l = arg.readInt();
			class_1914 lv5 = new class_1914(lv2, lv4, lv3, k, l);
			if (bl) {
				lv5.method_8254();
			}

			lv.add(lv5);
		}

		return lv;
	}

	public class_2487 method_8268() {
		class_2487 lv = new class_2487();
		class_2499 lv2 = new class_2499();

		for (int i = 0; i < this.size(); i++) {
			class_1914 lv3 = (class_1914)this.get(i);
			lv2.method_10606(lv3.method_8251());
		}

		lv.method_10566("Recipes", lv2);
		return lv;
	}
}
