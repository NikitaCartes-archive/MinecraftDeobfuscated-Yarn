package net.minecraft;

import java.io.IOException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_2637 implements class_2596<class_2602> {
	private class_1923 field_12116;
	private class_2637.class_2638[] field_12117;

	public class_2637() {
	}

	public class_2637(int i, short[] ss, class_2818 arg) {
		this.field_12116 = arg.method_12004();
		this.field_12117 = new class_2637.class_2638[i];

		for (int j = 0; j < this.field_12117.length; j++) {
			this.field_12117[j] = new class_2637.class_2638(ss[j], arg);
		}
	}

	@Override
	public void method_11053(class_2540 arg) throws IOException {
		this.field_12116 = new class_1923(arg.readInt(), arg.readInt());
		this.field_12117 = new class_2637.class_2638[arg.method_10816()];

		for (int i = 0; i < this.field_12117.length; i++) {
			this.field_12117[i] = new class_2637.class_2638(arg.readShort(), class_2248.field_10651.method_10200(arg.method_10816()));
		}
	}

	@Override
	public void method_11052(class_2540 arg) throws IOException {
		arg.writeInt(this.field_12116.field_9181);
		arg.writeInt(this.field_12116.field_9180);
		arg.method_10804(this.field_12117.length);

		for (class_2637.class_2638 lv : this.field_12117) {
			arg.writeShort(lv.method_11396());
			arg.method_10804(class_2248.method_9507(lv.method_11395()));
		}
	}

	public void method_11392(class_2602 arg) {
		arg.method_11100(this);
	}

	@Environment(EnvType.CLIENT)
	public class_2637.class_2638[] method_11391() {
		return this.field_12117;
	}

	public class class_2638 {
		private final short field_12118;
		private final class_2680 field_12119;

		public class_2638(short s, class_2680 arg2) {
			this.field_12118 = s;
			this.field_12119 = arg2;
		}

		public class_2638(short s, class_2818 arg2) {
			this.field_12118 = s;
			this.field_12119 = arg2.method_8320(this.method_11394());
		}

		public class_2338 method_11394() {
			return new class_2338(class_2637.this.field_12116.method_8330(this.field_12118 >> 12 & 15, this.field_12118 & 255, this.field_12118 >> 8 & 15));
		}

		public short method_11396() {
			return this.field_12118;
		}

		public class_2680 method_11395() {
			return this.field_12119;
		}
	}
}
